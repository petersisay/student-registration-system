package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Enterprise Database Connection Manager.
 * Supports environment variables (DB_URL, DB_USER, DB_PASSWORD, DB_HOST, DB_PORT, DB_NAME),
 * connection parameters for utf8mb4 and timezone support, and auto-creation of the database.
 */
public class DBConnection {

    private static String dbUrl;
    private static String dbUser;
    private static String dbPassword;
    private static boolean initialized = false;

    static {
        loadConfiguration();
    }

    private static synchronized void loadConfiguration() {
        String envUrl = System.getenv("DB_URL");
        String host = System.getenv("DB_HOST");
        String port = System.getenv("DB_PORT");
        String dbName = System.getenv("DB_NAME");

        if (envUrl != null && !envUrl.trim().isEmpty()) {
            dbUrl = envUrl.trim();
        } else if (host != null && !host.trim().isEmpty()) {
            String p = (port != null && !port.trim().isEmpty()) ? port.trim() : "3306";
            String d = (dbName != null && !dbName.trim().isEmpty()) ? dbName.trim() : "student_db";
            dbUrl = "jdbc:mysql://" + host.trim() + ":" + p + "/" + d
                    + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Africa/Addis_Ababa&characterEncoding=UTF-8";
        } else {
            // Default local setup
            dbUrl = "jdbc:mysql://localhost:3306/student_db"
                    + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Africa/Addis_Ababa&characterEncoding=UTF-8";
        }

        String envUser = System.getenv("DB_USER");
        dbUser = (envUser != null && !envUser.trim().isEmpty()) ? envUser.trim() : "root";

        String envPass = System.getenv("DB_PASSWORD");
        // On local WAMP / XAMPP, default root password is empty string
        dbPassword = (envPass != null) ? envPass : "";
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }

        // On first connection attempt, ensure the target database exists
        ensureDatabaseExists();

        try {
            return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        } catch (SQLException ex) {
            // Fallback for standard MySQL instances where password might be 'root' or empty
            if (dbPassword.isEmpty()) {
                try {
                    return DriverManager.getConnection(dbUrl, dbUser, "root");
                } catch (SQLException ignored) {
                }
            }
            throw ex;
        }
    }

    private static synchronized void ensureDatabaseExists() {
        if (initialized) {
            return;
        }
        try {
            // Connect to MySQL server root without database specified
            String serverUrl = "jdbc:mysql://localhost:3306/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Africa/Addis_Ababa";
            try (Connection conn = DriverManager.getConnection(serverUrl, dbUser, dbPassword);
                 Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS student_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");
            } catch (Exception e) {
                // Ignore if running in a restricted cloud environment where database is already created
            }
            initialized = true;
        } catch (Exception e) {
            initialized = true;
        }
    }

    public static String getDbUrl() {
        return dbUrl;
    }

    public static String getDbUser() {
        return dbUser;
    }
}
