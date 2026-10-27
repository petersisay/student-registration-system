package model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Domain model representing an official school report card.
 */
public class ReportCard {
    private int id;
    private int studentId;
    private int enrollmentId;
    private int termId;
    private BigDecimal totalScore;
    private BigDecimal averageScore;
    private int rankInClass;
    private String conduct;
    private int attendancePresent;
    private int attendanceAbsent;
    private int attendanceLate;
    private String teacherComment;
    private String principalComment;
    private String status; // DRAFT, APPROVED, PUBLISHED
    private Timestamp publishedAt;
    private Timestamp createdAt;

    // Joined display fields
    private String studentName;
    private String studentIdNumber;
    private String studentNameAm;
    private String className;
    private String gradeName;
    private String termName;
    private String academicYearName;

    private List<ReportCardItem> items = new ArrayList<>();

    public ReportCard() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(int enrollmentId) { this.enrollmentId = enrollmentId; }

    public int getTermId() { return termId; }
    public void setTermId(int termId) { this.termId = termId; }

    public BigDecimal getTotalScore() { return totalScore; }
    public void setTotalScore(BigDecimal totalScore) { this.totalScore = totalScore; }

    public BigDecimal getAverageScore() { return averageScore; }
    public void setAverageScore(BigDecimal averageScore) { this.averageScore = averageScore; }

    public int getRankInClass() { return rankInClass; }
    public void setRankInClass(int rankInClass) { this.rankInClass = rankInClass; }

    public String getConduct() { return conduct; }
    public void setConduct(String conduct) { this.conduct = conduct; }

    public int getAttendancePresent() { return attendancePresent; }
    public void setAttendancePresent(int attendancePresent) { this.attendancePresent = attendancePresent; }

    public int getAttendanceAbsent() { return attendanceAbsent; }
    public void setAttendanceAbsent(int attendanceAbsent) { this.attendanceAbsent = attendanceAbsent; }

    public int getAttendanceLate() { return attendanceLate; }
    public void setAttendanceLate(int attendanceLate) { this.attendanceLate = attendanceLate; }

    public String getTeacherComment() { return teacherComment; }
    public void setTeacherComment(String teacherComment) { this.teacherComment = teacherComment; }

    public String getPrincipalComment() { return principalComment; }
    public void setPrincipalComment(String principalComment) { this.principalComment = principalComment; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getPublishedAt() { return publishedAt; }
    public void setPublishedAt(Timestamp publishedAt) { this.publishedAt = publishedAt; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentIdNumber() { return studentIdNumber; }
    public void setStudentIdNumber(String studentIdNumber) { this.studentIdNumber = studentIdNumber; }

    public String getStudentNameAm() { return studentNameAm; }
    public void setStudentNameAm(String studentNameAm) { this.studentNameAm = studentNameAm; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getGradeName() { return gradeName; }
    public void setGradeName(String gradeName) { this.gradeName = gradeName; }

    public String getTermName() { return termName; }
    public void setTermName(String termName) { this.termName = termName; }

    public String getAcademicYearName() { return academicYearName; }
    public void setAcademicYearName(String academicYearName) { this.academicYearName = academicYearName; }

    public List<ReportCardItem> getItems() { return items; }
    public void setItems(List<ReportCardItem> items) { this.items = items; }

    public boolean isPublished() {
        return "PUBLISHED".equalsIgnoreCase(status);
    }
}
