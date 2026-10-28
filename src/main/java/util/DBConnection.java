package util;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Enterprise Database Connection Manager.
 * Supports environment variables (DATABASE_URL, MYSQL_URL, DB_URL, DB_USER, DB_PASSWORD, DB_HOST, DB_PORT, DB_NAME),
 * SSL modes for cloud providers (Aiven, TiDB, Railway, Render),
 * connection parameters for utf8mb4 and timezone support, and auto-creation of local databases.
 */
public class DBConnection {

    private static String dbUrl;
    private static String dbUser;
    private static String dbPassword;
    private static boolean isRemote = false;
    private static boolean initialized = false;

    static {
        loadConfiguration();
    }

    private static synchronized void loadConfiguration() {
        // Support standard cloud provider connection URIs (Render, Railway, Fly.io, Heroku, Aiven)
        String cloudDatabaseUrl = System.getenv("DATABASE_URL");
        if (cloudDatabaseUrl == null || cloudDatabaseUrl.trim().isEmpty()) {
            cloudDatabaseUrl = System.getenv("MYSQL_URL");
        }

        if (cloudDatabaseUrl != null && !cloudDatabaseUrl.trim().isEmpty()) {
            String uri = cloudDatabaseUrl.trim();
            if (uri.startsWith("mysql://")) {
                try {
                    URI parsed = new URI(uri);
                    String userInfo = parsed.getUserInfo();
                    if (userInfo != null && userInfo.contains(":")) {
                        String[] parts = userInfo.split(":", 2);
                        dbUser = parts[0];
                        dbPassword = parts[1];
                    } else if (userInfo != null) {
                        dbUser = userInfo;
                        dbPassword = "";
                    }
                    String path = parsed.getPath();
                    if (path != null && path.startsWith("/")) {
                        path = path.substring(1);
                    }
                    if (path == null || path.isEmpty()) {
                        path = "defaultdb";
                    }
                    int p = parsed.getPort() > 0 ? parsed.getPort() : 3306;
                    String h = parsed.getHost();
                    isRemote = h != null && !h.equalsIgnoreCase("localhost") && !h.equals("127.0.0.1");

                    String sslParams = isRemote 
                            ? "useSSL=true&trustServerCertificate=true&allowPublicKeyRetrieval=true"
                            : "useSSL=false&allowPublicKeyRetrieval=true";

                    dbUrl = "jdbc:mysql://" + h + ":" + p + "/" + path
                            + "?" + sslParams + "&serverTimezone=UTC&characterEncoding=UTF-8";
                    return;
                } catch (Exception ignored) {
                }
            }
        }

        String envUrl = System.getenv("DB_URL");
        String host = System.getenv("DB_HOST");
        String port = System.getenv("DB_PORT");
        String dbName = System.getenv("DB_NAME");

        if (envUrl != null && !envUrl.trim().isEmpty()) {
            dbUrl = envUrl.trim();
            isRemote = !dbUrl.contains("localhost") && !dbUrl.contains("127.0.0.1");
        } else if (host != null && !host.trim().isEmpty()) {
            String p = (port != null && !port.trim().isEmpty()) ? port.trim() : "3306";
            String d = (dbName != null && !dbName.trim().isEmpty()) ? dbName.trim() : "student_db";
            isRemote = !host.trim().equalsIgnoreCase("localhost") && !host.trim().equals("127.0.0.1");

            String sslParams = isRemote 
                    ? "useSSL=true&trustServerCertificate=true&allowPublicKeyRetrieval=true"
                    : "useSSL=false&allowPublicKeyRetrieval=true";

            dbUrl = "jdbc:mysql://" + host.trim() + ":" + p + "/" + d
                    + "?" + sslParams + "&serverTimezone=UTC&characterEncoding=UTF-8";
        } else {
            // Default local setup
            isRemote = false;
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

        // On first connection attempt, ensure the target database exists (for local environments)
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
        if (initialized || isRemote) {
            initialized = true;
            return;
        }
        try {
            // Connect to local MySQL server root without database specified
            String serverUrl = "jdbc:mysql://localhost:3306/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Africa/Addis_Ababa";
            try (Connection conn = DriverManager.getConnection(serverUrl, dbUser, dbPassword);
                 Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS student_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");
            } catch (Exception ignored) {
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
