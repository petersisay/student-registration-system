package model;

import java.sql.Timestamp;

/**
 * Domain model representing a configurable grading policy.
 */
public class GradingPolicy {
    private int id;
    private int schoolId;
    private int academicYearId;
    private String name;
    private String policyJson;
    private boolean isActive;
    private Timestamp createdAt;

    public GradingPolicy() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSchoolId() { return schoolId; }
    public void setSchoolId(int schoolId) { this.schoolId = schoolId; }

    public int getAcademicYearId() { return academicYearId; }
    public void setAcademicYearId(int academicYearId) { this.academicYearId = academicYearId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPolicyJson() { return policyJson; }
    public void setPolicyJson(String policyJson) { this.policyJson = policyJson; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
