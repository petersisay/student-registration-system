package model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Domain model representing a student in the Ethiopian elementary school.
 * Backward compatible with legacy fields (name, email, year) while supporting
 * complete profile details, Ethiopian naming conventions, and enrollment history.
 */
public class Student {
    private int id;
    private Integer userId;
    private String studentIdNumber;
    private String fullNameEn;
    private String fullNameAm;
    private String name; // Legacy
    private String email; // Legacy
    private int year; // Legacy
    private Date dateOfBirth;
    private String gender;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private Date admissionDate;
    private String status; // ACTIVE, TRANSFERRED, GRADUATED, SUSPENDED
    private Timestamp createdAt;

    // Joined / transient display fields
    private String currentGradeName;
    private String currentSectionName;
    private int currentGradeNumber;

    public Student() {}

    // Legacy constructor for backward compatibility
    public Student(String name, String email, int year) {
        this.name = name;
        this.fullNameEn = name;
        this.email = email;
        this.year = year;
        this.status = "ACTIVE";
    }

    public Student(int id, String studentIdNumber, String fullNameEn, String fullNameAm, String email, int year, String status) {
        this.id = id;
        this.studentIdNumber = studentIdNumber;
        this.fullNameEn = fullNameEn;
        this.fullNameAm = fullNameAm;
        this.name = fullNameEn;
        this.email = email;
        this.year = year;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getStudentIdNumber() { return studentIdNumber; }
    public void setStudentIdNumber(String studentIdNumber) { this.studentIdNumber = studentIdNumber; }

    public String getFullNameEn() {
        return (fullNameEn != null && !fullNameEn.isEmpty()) ? fullNameEn : name;
    }
    public void setFullNameEn(String fullNameEn) {
        this.fullNameEn = fullNameEn;
        if (this.name == null) this.name = fullNameEn;
    }

    public String getFullNameAm() { return fullNameAm; }
    public void setFullNameAm(String fullNameAm) { this.fullNameAm = fullNameAm; }

    public String getName() {
        return (name != null && !name.isEmpty()) ? name : fullNameEn;
    }
    public void setName(String name) {
        this.name = name;
        if (this.fullNameEn == null) this.fullNameEn = name;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public Date getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getEmergencyContactName() { return emergencyContactName; }
    public void setEmergencyContactName(String emergencyContactName) { this.emergencyContactName = emergencyContactName; }

    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public void setEmergencyContactPhone(String emergencyContactPhone) { this.emergencyContactPhone = emergencyContactPhone; }

    public Date getAdmissionDate() { return admissionDate; }
    public void setAdmissionDate(Date admissionDate) { this.admissionDate = admissionDate; }

    public String getStatus() { return (status != null) ? status : "ACTIVE"; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getCurrentGradeName() { return currentGradeName; }
    public void setCurrentGradeName(String currentGradeName) { this.currentGradeName = currentGradeName; }

    public String getCurrentSectionName() { return currentSectionName; }
    public void setCurrentSectionName(String currentSectionName) { this.currentSectionName = currentSectionName; }

    public int getCurrentGradeNumber() { return currentGradeNumber; }
    public void setCurrentGradeNumber(int currentGradeNumber) { this.currentGradeNumber = currentGradeNumber; }

    public String getDisplayName(String locale) {
        if ("am".equalsIgnoreCase(locale) && fullNameAm != null && !fullNameAm.trim().isEmpty()) {
            return fullNameAm;
        }
        return getFullNameEn();
    }
}
