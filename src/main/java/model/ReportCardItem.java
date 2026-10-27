package model;

import java.math.BigDecimal;

/**
 * Domain model representing an individual subject outcome on a student's report card.
 */
public class ReportCardItem {
    private int id;
    private int reportCardId;
    private int subjectId;
    private BigDecimal assessmentTotal;
    private BigDecimal finalScore;
    private String letterGrade;
    private String remark;

    // Joined display fields
    private String subjectNameEn;
    private String subjectNameAm;
    private String subjectCode;

    public ReportCardItem() {}

    public ReportCardItem(int subjectId, BigDecimal assessmentTotal, BigDecimal finalScore, String letterGrade, String remark) {
        this.subjectId = subjectId;
        this.assessmentTotal = assessmentTotal;
        this.finalScore = finalScore;
        this.letterGrade = letterGrade;
        this.remark = remark;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getReportCardId() { return reportCardId; }
    public void setReportCardId(int reportCardId) { this.reportCardId = reportCardId; }

    public int getSubjectId() { return subjectId; }
    public void setSubjectId(int subjectId) { this.subjectId = subjectId; }

    public BigDecimal getAssessmentTotal() { return assessmentTotal; }
    public void setAssessmentTotal(BigDecimal assessmentTotal) { this.assessmentTotal = assessmentTotal; }

    public BigDecimal getFinalScore() { return finalScore; }
    public void setFinalScore(BigDecimal finalScore) { this.finalScore = finalScore; }

    public String getLetterGrade() { return letterGrade; }
    public void setLetterGrade(String letterGrade) { this.letterGrade = letterGrade; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public String getSubjectNameEn() { return subjectNameEn; }
    public void setSubjectNameEn(String subjectNameEn) { this.subjectNameEn = subjectNameEn; }

    public String getSubjectNameAm() { return subjectNameAm; }
    public void setSubjectNameAm(String subjectNameAm) { this.subjectNameAm = subjectNameAm; }

    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    public String getSubjectDisplayName(String locale) {
        return "am".equalsIgnoreCase(locale) && subjectNameAm != null && !subjectNameAm.isEmpty()
                ? subjectNameAm : subjectNameEn;
    }
}
