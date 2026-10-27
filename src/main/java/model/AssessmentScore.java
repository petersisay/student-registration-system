package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Domain model representing a student's score on a specific assessment.
 */
public class AssessmentScore {
    private int id;
    private int assessmentId;
    private int studentId;
    private BigDecimal score;
    private String comments;
    private Timestamp enteredAt;
    private Timestamp updatedAt;

    // Joined display fields
    private String studentName;
    private String studentIdNumber;

    public AssessmentScore() {}

    public AssessmentScore(int assessmentId, int studentId, BigDecimal score, String comments) {
        this.assessmentId = assessmentId;
        this.studentId = studentId;
        this.score = score;
        this.comments = comments;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getAssessmentId() { return assessmentId; }
    public void setAssessmentId(int assessmentId) { this.assessmentId = assessmentId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) { this.score = score; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public Timestamp getEnteredAt() { return enteredAt; }
    public void setEnteredAt(Timestamp enteredAt) { this.enteredAt = enteredAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentIdNumber() { return studentIdNumber; }
    public void setStudentIdNumber(String studentIdNumber) { this.studentIdNumber = studentIdNumber; }
}
