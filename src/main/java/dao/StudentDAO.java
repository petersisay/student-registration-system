package dao;

import model.Student;
import model.StudentEnrollment;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Students and historical enrollment records.
 * Ensures that advancing grades or changing sections NEVER overwrites academic history.
 */
public class StudentDAO {

    public int create(Student s) {
        String sql = "INSERT INTO students (student_id_number, full_name_en, full_name_am, name, email, year, date_of_birth, gender, emergency_contact_name, emergency_contact_phone, admission_date, status, user_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getStudentIdNumber());
            ps.setString(2, s.getFullNameEn());
            ps.setString(3, s.getFullNameAm());
            ps.setString(4, s.getName());
            ps.setString(5, s.getEmail());
            ps.setInt(6, s.getYear());
            ps.setDate(7, s.getDateOfBirth());
            ps.setString(8, s.getGender());
            ps.setString(9, s.getEmergencyContactName());
            ps.setString(10, s.getEmergencyContactPhone());
            ps.setDate(11, s.getAdmissionDate());
            ps.setString(12, s.getStatus());
            if (s.getUserId() != null && s.getUserId() > 0) {
                ps.setInt(13, s.getUserId());
            } else {
                ps.setNull(13, Types.INTEGER);
            }
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    s.setId(rs.getInt(1));
                    return s.getId();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public Student findById(int id) {
        String sql = "SELECT s.*, gl.name_en AS grade_name, gl.grade_number, cs.section_name " +
                     "FROM students s " +
                     "LEFT JOIN student_enrollments se ON s.id = se.student_id AND se.status = 'ACTIVE' " +
                     "LEFT JOIN grade_levels gl ON se.grade_level_id = gl.id " +
                     "LEFT JOIN class_sections cs ON se.class_section_id = cs.id " +
                     "WHERE s.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapStudent(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Student findByUserId(int userId) {
        String sql = "SELECT s.*, gl.name_en AS grade_name, gl.grade_number, cs.section_name " +
                     "FROM students s " +
                     "LEFT JOIN student_enrollments se ON s.id = se.student_id AND se.status = 'ACTIVE' " +
                     "LEFT JOIN grade_levels gl ON se.grade_level_id = gl.id " +
                     "LEFT JOIN class_sections cs ON se.class_section_id = cs.id " +
                     "WHERE s.user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapStudent(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Student> findAll(String search, Integer gradeNumber, int offset, int limit) {
        List<Student> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT s.*, gl.name_en AS grade_name, gl.grade_number, cs.section_name ")
           .append("FROM students s ")
           .append("LEFT JOIN student_enrollments se ON s.id = se.student_id AND se.status = 'ACTIVE' ")
           .append("LEFT JOIN grade_levels gl ON se.grade_level_id = gl.id ")
           .append("LEFT JOIN class_sections cs ON se.class_section_id = cs.id ")
           .append("WHERE 1=1 ");

        List<Object> params = new ArrayList<>();
        if (search != null && !search.trim().isEmpty()) {
            sql.append("AND (s.full_name_en LIKE ? OR s.full_name_am LIKE ? OR s.student_id_number LIKE ? OR s.name LIKE ?) ");
            String term = "%" + search.trim() + "%";
            params.add(term);
            params.add(term);
            params.add(term);
            params.add(term);
        }
        if (gradeNumber != null && gradeNumber > 0) {
            sql.append("AND (gl.grade_number = ? OR s.year = ?) ");
            params.add(gradeNumber);
            params.add(gradeNumber);
        }
        sql.append("ORDER BY s.id DESC LIMIT ? OFFSET ?");
        params.add(limit > 0 ? limit : 20);
        params.add(offset >= 0 ? offset : 0);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapStudent(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countAll(String search, Integer gradeNumber) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) FROM students s ")
           .append("LEFT JOIN student_enrollments se ON s.id = se.student_id AND se.status = 'ACTIVE' ")
           .append("LEFT JOIN grade_levels gl ON se.grade_level_id = gl.id ")
           .append("WHERE 1=1 ");

        List<Object> params = new ArrayList<>();
        if (search != null && !search.trim().isEmpty()) {
            sql.append("AND (s.full_name_en LIKE ? OR s.full_name_am LIKE ? OR s.student_id_number LIKE ? OR s.name LIKE ?) ");
            String term = "%" + search.trim() + "%";
            params.add(term);
            params.add(term);
            params.add(term);
            params.add(term);
        }
        if (gradeNumber != null && gradeNumber > 0) {
            sql.append("AND (gl.grade_number = ? OR s.year = ?) ");
            params.add(gradeNumber);
            params.add(gradeNumber);
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean update(Student s) {
        String sql = "UPDATE students SET student_id_number = ?, full_name_en = ?, full_name_am = ?, name = ?, email = ?, year = ?, " +
                     "date_of_birth = ?, gender = ?, emergency_contact_name = ?, emergency_contact_phone = ?, admission_date = ?, status = ? " +
                     "WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getStudentIdNumber());
            ps.setString(2, s.getFullNameEn());
            ps.setString(3, s.getFullNameAm());
            ps.setString(4, s.getName());
            ps.setString(5, s.getEmail());
            ps.setInt(6, s.getYear());
            ps.setDate(7, s.getDateOfBirth());
            ps.setString(8, s.getGender());
            ps.setString(9, s.getEmergencyContactName());
            ps.setString(10, s.getEmergencyContactPhone());
            ps.setDate(11, s.getAdmissionDate());
            ps.setString(12, s.getStatus());
            ps.setInt(13, s.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean enrollStudent(int studentId, int academicYearId, int gradeLevelId, int classSectionId, String status) {
        String sql = "INSERT INTO student_enrollments (student_id, academic_year_id, grade_level_id, class_section_id, enrollment_date, status) " +
                     "VALUES (?, ?, ?, ?, CURRENT_DATE, ?) " +
                     "ON DUPLICATE KEY UPDATE grade_level_id = VALUES(grade_level_id), class_section_id = VALUES(class_section_id), status = VALUES(status)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, academicYearId);
            ps.setInt(3, gradeLevelId);
            ps.setInt(4, classSectionId);
            ps.setString(5, status != null ? status : "ACTIVE");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean promoteStudent(int studentId, int currentYearId, int nextYearId, int nextGradeId, int nextSectionId) {
        String closeCurrentSql = "UPDATE student_enrollments SET status = 'PROMOTED' WHERE student_id = ? AND academic_year_id = ?";
        String enrollNextSql = "INSERT INTO student_enrollments (student_id, academic_year_id, grade_level_id, class_section_id, enrollment_date, status) " +
                               "VALUES (?, ?, ?, ?, CURRENT_DATE, 'ACTIVE') " +
                               "ON DUPLICATE KEY UPDATE grade_level_id = VALUES(grade_level_id), class_section_id = VALUES(class_section_id), status = 'ACTIVE'";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement(closeCurrentSql)) {
                ps1.setInt(1, studentId);
                ps1.setInt(2, currentYearId);
                ps1.executeUpdate();
            }

            try (PreparedStatement ps2 = conn.prepareStatement(enrollNextSql)) {
                ps2.setInt(1, studentId);
                ps2.setInt(2, nextYearId);
                ps2.setInt(3, nextGradeId);
                ps2.setInt(4, nextSectionId);
                ps2.executeUpdate();
            }

            // Also update legacy year on student for display
            String updateStudentSql = "UPDATE students SET year = (SELECT grade_number FROM grade_levels WHERE id = ?) WHERE id = ?";
            try (PreparedStatement ps3 = conn.prepareStatement(updateStudentSql)) {
                ps3.setInt(1, nextGradeId);
                ps3.setInt(2, studentId);
                ps3.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
        return false;
    }

    public List<StudentEnrollment> getEnrollmentHistory(int studentId) {
        List<StudentEnrollment> list = new ArrayList<>();
        String sql = "SELECT se.*, ay.name AS year_name, gl.name_en AS grade_name, gl.grade_number, cs.section_name " +
                     "FROM student_enrollments se " +
                     "JOIN academic_years ay ON se.academic_year_id = ay.id " +
                     "JOIN grade_levels gl ON se.grade_level_id = gl.id " +
                     "JOIN class_sections cs ON se.class_section_id = cs.id " +
                     "WHERE se.student_id = ? " +
                     "ORDER BY ay.start_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StudentEnrollment se = new StudentEnrollment();
                    se.setId(rs.getInt("id"));
                    se.setStudentId(rs.getInt("student_id"));
                    se.setAcademicYearId(rs.getInt("academic_year_id"));
                    se.setGradeLevelId(rs.getInt("grade_level_id"));
                    se.setClassSectionId(rs.getInt("class_section_id"));
                    se.setEnrollmentDate(rs.getDate("enrollment_date"));
                    se.setStatus(rs.getString("status"));
                    se.setAcademicNotes(rs.getString("academic_notes"));
                    se.setAcademicYearName(rs.getString("year_name"));
                    se.setGradeName(rs.getString("grade_name"));
                    se.setGradeNumber(rs.getInt("grade_number"));
                    se.setSectionName(rs.getString("section_name"));
                    list.add(se);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Student> findEnrolledByClass(int classSectionId) {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT s.*, gl.name_en AS grade_name, gl.grade_number, cs.section_name " +
                     "FROM students s " +
                     "JOIN student_enrollments se ON s.id = se.student_id AND se.status = 'ACTIVE' " +
                     "JOIN grade_levels gl ON se.grade_level_id = gl.id " +
                     "JOIN class_sections cs ON se.class_section_id = cs.id " +
                     "WHERE se.class_section_id = ? " +
                     "ORDER BY s.full_name_en ASC, s.name ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classSectionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapStudent(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Student mapStudent(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setId(rs.getInt("id"));
        int uId = rs.getInt("user_id");
        if (!rs.wasNull()) {
            s.setUserId(uId);
        }
        s.setStudentIdNumber(rs.getString("student_id_number"));
        s.setFullNameEn(rs.getString("full_name_en"));
        s.setFullNameAm(rs.getString("full_name_am"));
        s.setName(rs.getString("name"));
        s.setEmail(rs.getString("email"));
        s.setYear(rs.getInt("year"));
        s.setDateOfBirth(rs.getDate("date_of_birth"));
        s.setGender(rs.getString("gender"));
        s.setEmergencyContactName(rs.getString("emergency_contact_name"));
        s.setEmergencyContactPhone(rs.getString("emergency_contact_phone"));
        s.setAdmissionDate(rs.getDate("admission_date"));
        s.setStatus(rs.getString("status"));
        s.setCreatedAt(rs.getTimestamp("created_at"));

        try {
            s.setCurrentGradeName(rs.getString("grade_name"));
            s.setCurrentGradeNumber(rs.getInt("grade_number"));
            s.setCurrentSectionName(rs.getString("section_name"));
        } catch (SQLException ignored) {}

        return s;
    }

    public List<Student> getStudentsByClass(int classSectionId, int academicYearId) {
        return findEnrolledByClass(classSectionId);
    }
}
