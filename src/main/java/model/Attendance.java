package model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Domain model representing student attendance.
 */
public class Attendance {
    private int id;
    private int studentId;
    private int classSectionId;
    private int academicYearId;
    private Date attendanceDate;
    private String status; // PRESENT, ABSENT, LATE, EXCUSED
    private Integer recordedByUserId;
    private String notes;
    private Timestamp recordedAt;

    // Joined display fields
    private String studentName;
    private String studentIdNumber;
    private String recorderName;

    public Attendance() {}

    public Attendance(int studentId, int classSectionId, int academicYearId, Date attendanceDate, String status) {
        this.studentId = studentId;
        this.classSectionId = classSectionId;
        this.academicYearId = academicYearId;
        this.attendanceDate = attendanceDate;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getClassSectionId() { return classSectionId; }
    public void setClassSectionId(int classSectionId) { this.classSectionId = classSectionId; }

    public int getAcademicYearId() { return academicYearId; }
    public void setAcademicYearId(int academicYearId) { this.academicYearId = academicYearId; }

    public Date getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(Date attendanceDate) { this.attendanceDate = attendanceDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getRecordedByUserId() { return recordedByUserId; }
    public void setRecordedByUserId(Integer recordedByUserId) { this.recordedByUserId = recordedByUserId; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Timestamp getRecordedAt() { return recordedAt; }
    public void setRecordedAt(Timestamp recordedAt) { this.recordedAt = recordedAt; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentIdNumber() { return studentIdNumber; }
    public void setStudentIdNumber(String studentIdNumber) { this.studentIdNumber = studentIdNumber; }

    public String getRecorderName() { return recorderName; }
    public void setRecorderName(String recorderName) { this.recorderName = recorderName; }

    public String getLocalizedStatus(String locale) {
        if ("am".equalsIgnoreCase(locale)) {
            switch (status != null ? status : "") {
                case "PRESENT": return "ተገኝቷል";
                case "ABSENT": return "ቀሪ";
                case "LATE": return "አርፍዷል";
                case "EXCUSED": return "ፈቃድ";
                default: return status;
            }
        }
        return status;
    }
}
