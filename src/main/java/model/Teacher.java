package model;

import java.sql.Date;

/**
 * Domain model representing a teacher and their qualifications.
 */
public class Teacher {
    private int id;
    private Integer userId;
    private String employeeId;
    private String fullNameEn;
    private String fullNameAm;
    private String specialization;
    private String qualification;
    private String phone;
    private String email;
    private Date hireDate;
    private String status;

    public Teacher() {}

    public Teacher(int id, String employeeId, String fullNameEn, String fullNameAm, String specialization, String email) {
        this.id = id;
        this.employeeId = employeeId;
        this.fullNameEn = fullNameEn;
        this.fullNameAm = fullNameAm;
        this.specialization = specialization;
        this.email = email;
        this.status = "ACTIVE";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getFullNameEn() { return fullNameEn; }
    public void setFullNameEn(String fullNameEn) { this.fullNameEn = fullNameEn; }

    public String getFullNameAm() { return fullNameAm; }
    public void setFullNameAm(String fullNameAm) { this.fullNameAm = fullNameAm; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Date getHireDate() { return hireDate; }
    public void setHireDate(Date hireDate) { this.hireDate = hireDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDisplayName(String locale) {
        if ("am".equalsIgnoreCase(locale) && fullNameAm != null && !fullNameAm.trim().isEmpty()) {
            return fullNameAm;
        }
        return fullNameEn;
    }

    public String getFullName() { return fullNameEn; }
    public String getNameAmharic() { return fullNameAm; }
    public String getTeacherCode() { return employeeId; }
    public boolean isActive() { return "ACTIVE".equalsIgnoreCase(status); }
}
