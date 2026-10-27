package model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Domain model representing annual student enrollment.
 * Preserves complete multi-year academic progression history.
 */
public class StudentEnrollment {
    private int id;
    private int studentId;
    private int academicYearId;
    private int gradeLevelId;
    private int classSectionId;
    private Date enrollmentDate;
    private String status; // ACTIVE, PROMOTED, REPEATING, TRANSFERRED, COMPLETED
    private String academicNotes;
    private Timestamp createdAt;

    // Joined display fields
    private String studentName;
    private String studentIdNumber;
    private String academicYearName;
    private String gradeName;
    private int gradeNumber;
    private String sectionName;

    public StudentEnrollment() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getAcademicYearId() { return academicYearId; }
    public void setAcademicYearId(int academicYearId) { this.academicYearId = academicYearId; }

    public int getGradeLevelId() { return gradeLevelId; }
    public void setGradeLevelId(int gradeLevelId) { this.gradeLevelId = gradeLevelId; }

    public int getClassSectionId() { return classSectionId; }
    public void setClassSectionId(int classSectionId) { this.classSectionId = classSectionId; }

    public Date getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(Date enrollmentDate) { this.enrollmentDate = enrollmentDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAcademicNotes() { return academicNotes; }
    public void setAcademicNotes(String academicNotes) { this.academicNotes = academicNotes; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentIdNumber() { return studentIdNumber; }
    public void setStudentIdNumber(String studentIdNumber) { this.studentIdNumber = studentIdNumber; }

    public String getAcademicYearName() { return academicYearName; }
    public void setAcademicYearName(String academicYearName) { this.academicYearName = academicYearName; }

    public String getGradeName() { return gradeName; }
    public void setGradeName(String gradeName) { this.gradeName = gradeName; }

    public int getGradeNumber() { return gradeNumber; }
    public void setGradeNumber(int gradeNumber) { this.gradeNumber = gradeNumber; }

    public String getSectionName() { return sectionName; }
    public void setSectionName(String sectionName) { this.sectionName = sectionName; }

    public String getFullClassName() {
        return (gradeName != null ? gradeName : "Grade " + gradeNumber) + " - " + sectionName;
    }
}
