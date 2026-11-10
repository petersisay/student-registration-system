package util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Executes versioned database migrations and verifies default seed accounts.
 * Safe to execute on every application startup.
 */
public class DatabaseMigration {

    private static boolean migrated = false;

    public static synchronized void runMigrations() {
        if (migrated) {
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            executeSqlFile(conn, "/db/migrations/V1__init_schema.sql");
            executeSqlFile(conn, "/db/migrations/V2__seed_ethiopian_elementary_data.sql");
            seedDefaultUserAccounts(conn);
            migrated = true;
            System.out.println("[DatabaseMigration] Database schema and seed data successfully initialized.");
        } catch (Exception e) {
            System.err.println("[DatabaseMigration] Warning during migration: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void executeSqlFile(Connection conn, String resourcePath) throws Exception {
        InputStream is = DatabaseMigration.class.getResourceAsStream(resourcePath);
        if (is == null) {
            System.err.println("[DatabaseMigration] Migration file not found on classpath: " + resourcePath);
            return;
        }

        StringBuilder currentStatement = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
             Statement stmt = conn.createStatement()) {

            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                // Skip empty lines or pure comment lines
                if (trimmed.isEmpty() || trimmed.startsWith("--") || trimmed.startsWith("#") || trimmed.startsWith("/*")) {
                    continue;
                }

                // Strip inline comment if present
                int commentIdx = line.indexOf("--");
                if (commentIdx != -1) {
                    line = line.substring(0, commentIdx);
                    trimmed = line.trim();
                }

                if (trimmed.isEmpty()) {
                    continue;
                }

                currentStatement.append(line).append("\n");

                if (trimmed.endsWith(";")) {
                    String sql = currentStatement.toString().trim();
                    if (sql.endsWith(";")) {
                        sql = sql.substring(0, sql.length() - 1).trim();
                    }
                    if (!sql.isEmpty()) {
                        try {
                            stmt.execute(sql);
                        } catch (Exception e) {
                            System.err.println("[DatabaseMigration] Error executing statement: " + e.getMessage() + "\nSQL: " + sql);
                            throw e;
                        }
                    }
                    currentStatement.setLength(0);
                }
            }
        }
    }

    private static void seedDefaultUserAccounts(Connection conn) throws Exception {
        boolean seedDemoAccounts = EnvUtil.getBoolean("SEED_DEFAULT_ACCOUNTS", true);
        String adminUser = EnvUtil.get("INITIAL_ADMIN_USERNAME", "admin");
        String adminPass = EnvUtil.get("INITIAL_ADMIN_PASSWORD", "admin123");

        // List of accounts to initialize. If seedDemoAccounts is disabled, only the admin account is provisioned.
        String[][] defaultUsers;
        if (seedDemoAccounts) {
            defaultUsers = new String[][] {
                {adminUser, adminPass, "System Administrator (አስተዳዳሪ)", "ADMIN", "admin@addistesfa.edu.et"},
                {"principal", "principal123", "Dr. Solomon Haile (ርዕሰ መምህር)", "PRINCIPAL", "principal@addistesfa.edu.et"},
                {"registrar", "registrar123", "Marta Lemma (ሬጅስትራር)", "REGISTRAR", "registrar@addistesfa.edu.et"},
                {"teacher.abebe", "teacher123", "Abebe Bikila (መምህር አበበ)", "TEACHER", "abebe@addistesfa.edu.et"},
                {"teacher.almaz", "teacher123", "Almaz Ayana (መምህርት አልማዝ)", "TEACHER", "almaz@addistesfa.edu.et"},
                {"attendance.officer", "officer123", "Tadesse Kebede (የክትትል ኃላፊ)", "ATTENDANCE_OFFICER", "attendance@addistesfa.edu.et"},
                {"student.dawit", "student123", "Dawit Yohannes (ተማሪ ዳዊት)", "STUDENT", "dawit@addistesfa.edu.et"}
            };
        } else {
            defaultUsers = new String[][] {
                {adminUser, adminPass, "System Administrator (አስተዳዳሪ)", "ADMIN", "admin@addistesfa.edu.et"}
            };
        }

        String checkSql = "SELECT id, password_hash, salt FROM users WHERE LOWER(TRIM(username)) = LOWER(TRIM(?))";
        String insertSql = "INSERT INTO users (username, password_hash, salt, full_name, role, email, preferred_locale) " +
                           "VALUES (?, ?, ?, ?, ?, ?, 'en')";
        String updateSql = "UPDATE users SET password_hash = ?, salt = ?, is_active = TRUE WHERE id = ?";

        for (String[] u : defaultUsers) {
            String username = u[0];
            String password = u[1];
            String fullName = u[2];
            String role = u[3];
            String email = u[4];

            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, username);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (!rs.next()) {
                        String salt = PasswordUtil.generateSalt();
                        String hash = PasswordUtil.hashPassword(password, salt);
                        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                            insertStmt.setString(1, username);
                            insertStmt.setString(2, hash);
                            insertStmt.setString(3, salt);
                            insertStmt.setString(4, fullName);
                            insertStmt.setString(5, role);
                            insertStmt.setString(6, email);
                            insertStmt.executeUpdate();

                            int userId = 0;
                            try (ResultSet keys = insertStmt.getGeneratedKeys()) {
                                if (keys.next()) {
                                    userId = keys.getInt(1);
                                }
                            }

                            // If this was a teacher account, link to teachers table
                            if ("TEACHER".equals(role) && userId > 0) {
                                provisionTeacherProfile(conn, userId, username, fullName, email);
                            } else if ("STUDENT".equals(role) && userId > 0) {
                                provisionStudentProfile(conn, userId, fullName);
                            }
                        }
                    } else {
                        // User exists: verify password hash. If broken/legacy/mismatched, synchronize with known credential
                        int userId = rs.getInt("id");
                        String storedHash = rs.getString("password_hash");
                        String storedSalt = rs.getString("salt");
                        if (!PasswordUtil.verifyPassword(password, storedHash, storedSalt)) {
                            String newSalt = PasswordUtil.generateSalt();
                            String newHash = PasswordUtil.hashPassword(password, newSalt);
                            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                                updateStmt.setString(1, newHash);
                                updateStmt.setString(2, newSalt);
                                updateStmt.setInt(3, userId);
                                updateStmt.executeUpdate();
                                System.out.println("[DatabaseMigration] Re-synchronized credentials for user: " + username);
                            }
                        }
                    }
                }
            }
        }
    }

    private static void provisionTeacherProfile(Connection conn, int userId, String username, String fullName, String email) {
        String empId = "TCH-" + username.replace("teacher.", "").toUpperCase();
        String sql = "INSERT INTO teachers (user_id, employee_id, full_name_en, full_name_am, specialization, qualification, phone, email, status) " +
                     "VALUES (?, ?, ?, ?, 'Mathematics & Science', 'B.Ed. in Elementary Education', '+251 91 123 4567', ?, 'ACTIVE') " +
                     "ON DUPLICATE KEY UPDATE user_id=VALUES(user_id)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, empId);
            ps.setString(3, fullName);
            ps.setString(4, fullName);
            ps.setString(5, email);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("[DatabaseMigration] Could not link teacher: " + e.getMessage());
        }
    }

    private static void provisionStudentProfile(Connection conn, int userId, String fullName) {
        String studentIdNum = "STD-2026-001";
        String checkSql = "SELECT id FROM students WHERE student_id_number = ?";
        try (PreparedStatement check = conn.prepareStatement(checkSql)) {
            check.setString(1, studentIdNum);
            try (ResultSet rs = check.executeQuery()) {
                int studentDbId = 0;
                if (!rs.next()) {
                    String insertStudent = "INSERT INTO students (user_id, student_id_number, full_name_en, full_name_am, name, email, year, date_of_birth, gender, emergency_contact_name, emergency_contact_phone, admission_date, status) " +
                                           "VALUES (?, ?, ?, ?, ?, 'dawit@addistesfa.edu.et', 4, '2016-04-12', 'MALE', 'Yohannes Tefera', '+251 92 345 6789', '2026-09-15', 'ACTIVE')";
                    try (PreparedStatement ps = conn.prepareStatement(insertStudent, Statement.RETURN_GENERATED_KEYS)) {
                        ps.setInt(1, userId);
                        ps.setString(2, studentIdNum);
                        ps.setString(3, fullName);
                        ps.setString(4, "ዳዊት ዮሐንስ");
                        ps.setString(5, fullName);
                        ps.executeUpdate();
                        try (ResultSet gk = ps.getGeneratedKeys()) {
                            if (gk.next()) {
                                studentDbId = gk.getInt(1);
                            }
                        }
                    }
                } else {
                    studentDbId = rs.getInt("id");
                }

                // Ensure initial enrollment in Grade 4, Section A for 2026/2027
                if (studentDbId > 0) {
                    String enrollSql = "INSERT IGNORE INTO student_enrollments (student_id, academic_year_id, grade_level_id, class_section_id, enrollment_date, status) " +
                                       "VALUES (?, 1, 4, 5, '2026-09-15', 'ACTIVE')";
                    try (PreparedStatement eps = conn.prepareStatement(enrollSql)) {
                        eps.setInt(1, studentDbId);
                        eps.executeUpdate();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[DatabaseMigration] Could not link student: " + e.getMessage());
        }
    }
}
