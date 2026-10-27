package model;

import java.sql.Timestamp;

/**
 * Domain model representing an authenticated user account.
 */
public class User {
    private int id;
    private String username;
    private String passwordHash;
    private String salt;
    private String fullName;
    private Role role;
    private String email;
    private String phone;
    private String preferredLocale;
    private boolean isActive;
    private Timestamp createdAt;

    public User() {}

    public User(int id, String username, String fullName, Role role, String email, String preferredLocale) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
        this.email = email;
        this.preferredLocale = preferredLocale;
        this.isActive = true;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getSalt() { return salt; }
    public void setSalt(String salt) { this.salt = salt; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPreferredLocale() {
        return (preferredLocale != null && !preferredLocale.isEmpty()) ? preferredLocale : "en";
    }
    public void setPreferredLocale(String preferredLocale) { this.preferredLocale = preferredLocale; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
