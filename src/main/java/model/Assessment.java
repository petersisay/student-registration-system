package model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Domain model representing an academic assessment (quiz, exam, project).
 */
public class Assessment {
    private int id;
    private int classSectionId;
    private int subjectId;
    private int termId;
    private int teacherId;
    private String title;
    private String assessmentType; // QUIZ, ASSIGNMENT, MIDTERM, FINAL_EXAM, PROJECT
    private BigDecimal maxScore;
    private BigDecimal weightPercent;
    private Date assessmentDate;
    private String status; // DRAFT, SUBMITTED, APPROVED
    private Timestamp createdAt;

    // Joined display fields
    private String subjectName;
    private String className;
    private String termName;
    private String teacherName;
    private int scoresCount;

    public Assessment() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getClassSectionId() { return classSectionId; }
    public void setClassSectionId(int classSectionId) { this.classSectionId = classSectionId; }

    public int getSubjectId() { return subjectId; }
    public void setSubjectId(int subjectId) { this.subjectId = subjectId; }

    public int getTermId() { return termId; }
    public void setTermId(int termId) { this.termId = termId; }

    public int getTeacherId() { return teacherId; }
    public void setTeacherId(int teacherId) { this.teacherId = teacherId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAssessmentType() { return assessmentType; }
    public void setAssessmentType(String assessmentType) { this.assessmentType = assessmentType; }

    public BigDecimal getMaxScore() { return maxScore; }
    public void setMaxScore(BigDecimal maxScore) { this.maxScore = maxScore; }

    public BigDecimal getWeightPercent() { return weightPercent; }
    public void setWeightPercent(BigDecimal weightPercent) { this.weightPercent = weightPercent; }

    public Date getAssessmentDate() { return assessmentDate; }
    public void setAssessmentDate(Date assessmentDate) { this.assessmentDate = assessmentDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getTermName() { return termName; }
    public void setTermName(String termName) { this.termName = termName; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

    public int getScoresCount() { return scoresCount; }
    public void setScoresCount(int scoresCount) { this.scoresCount = scoresCount; }
}
