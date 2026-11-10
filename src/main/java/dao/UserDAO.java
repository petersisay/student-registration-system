package dao;

import model.Role;
import model.User;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User accounts and authentication credentials.
 */
public class UserDAO {

    public User findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) return null;
        try {
            util.DatabaseMigration.runMigrations();
        } catch (Exception ignored) {}

        String sql = "SELECT * FROM users WHERE LOWER(TRIM(username)) = LOWER(TRIM(?)) AND is_active = TRUE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error querying user by username '" + username + "': " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public User findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int create(User user) {
        String sql = "INSERT INTO users (username, password_hash, salt, full_name, role, email, phone, preferred_locale, is_active) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername().trim());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getSalt());
            ps.setString(4, user.getFullName().trim());
            ps.setString(5, user.getRole().name());
            ps.setString(6, user.getEmail());
            ps.setString(7, user.getPhone());
            ps.setString(8, user.getPreferredLocale());
            ps.setBoolean(9, user.isActive());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                    return user.getId();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean updateProfile(int userId, String fullName, String email, String phone, String preferredLocale) {
        String sql = "UPDATE users SET full_name = ?, email = ?, phone = ?, preferred_locale = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setString(4, preferredLocale);
            ps.setInt(5, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePassword(int userId, String passwordHash, String salt) {
        String sql = "UPDATE users SET password_hash = ?, salt = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, passwordHash);
            ps.setString(2, salt);
            ps.setInt(3, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<User> findAllStaff() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role NOT IN ('STUDENT', 'GUARDIAN') ORDER BY role, full_name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<User> findAllUsers(String search, String roleStr) {
        List<User> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM users WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (search != null && !search.trim().isEmpty()) {
            sql.append("AND (LOWER(username) LIKE ? OR LOWER(full_name) LIKE ? OR LOWER(email) LIKE ?) ");
            String term = "%" + search.trim().toLowerCase() + "%";
            params.add(term);
            params.add(term);
            params.add(term);
        }

        if (roleStr != null && !roleStr.trim().isEmpty() && !"ALL".equalsIgnoreCase(roleStr.trim())) {
            sql.append("AND role = ? ");
            params.add(roleStr.trim().toUpperCase());
        }

        sql.append("ORDER BY created_at DESC, full_name ASC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapUser(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean existsByUsername(String username) {
        if (username == null || username.trim().isEmpty()) return false;
        String sql = "SELECT COUNT(*) FROM users WHERE LOWER(username) = LOWER(?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean existsByUsernameExcept(String username, int excludeUserId) {
        if (username == null || username.trim().isEmpty()) return false;
        String sql = "SELECT COUNT(*) FROM users WHERE LOWER(username) = LOWER(?) AND id != ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            ps.setInt(2, excludeUserId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateUserAdmin(int id, String username, String fullName, Role role, String email, String phone, boolean isActive) {
        String sql = "UPDATE users SET username = ?, full_name = ?, role = ?, email = ?, phone = ?, is_active = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            ps.setString(2, fullName.trim());
            ps.setString(3, role.name());
            ps.setString(4, email != null ? email.trim() : null);
            ps.setString(5, phone != null ? phone.trim() : null);
            ps.setBoolean(6, isActive);
            ps.setInt(7, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean resetUserPassword(int userId, String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) return false;
        String salt = util.PasswordUtil.generateSalt();
        String hash = util.PasswordUtil.hashPassword(plainPassword, salt);
        return updatePassword(userId, hash, salt);
    }

    public int countAllUsers() {
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setSalt(rs.getString("salt"));
        u.setFullName(rs.getString("full_name"));
        u.setRole(Role.fromString(rs.getString("role")));
        u.setEmail(rs.getString("email"));
        u.setPhone(rs.getString("phone"));
        u.setPreferredLocale(rs.getString("preferred_locale"));
        u.setActive(rs.getBoolean("is_active"));
        u.setCreatedAt(rs.getTimestamp("created_at"));
        return u;
    }
}
