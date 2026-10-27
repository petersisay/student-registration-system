package model;

import java.sql.Timestamp;

/**
 * Domain model representing explicit teacher-to-class and teacher-to-subject assignments.
 */
public class TeacherAssignment {
    private int id;
    private int teacherId;
    private int classSectionId;
    private Integer subjectId;
    private int academicYearId;
    private boolean isPrimaryHomeroom;
    private Timestamp assignedAt;

    // Joined display fields
    private String teacherName;
    private String teacherEmployeeId;
    private String sectionName;
    private String gradeName;
    private String subjectName;
    private String academicYearName;

    public TeacherAssignment() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTeacherId() { return teacherId; }
    public void setTeacherId(int teacherId) { this.teacherId = teacherId; }

    public int getClassSectionId() { return classSectionId; }
    public void setClassSectionId(int classSectionId) { this.classSectionId = classSectionId; }

    public Integer getSubjectId() { return subjectId; }
    public void setSubjectId(Integer subjectId) { this.subjectId = subjectId; }

    public int getAcademicYearId() { return academicYearId; }
    public void setAcademicYearId(int academicYearId) { this.academicYearId = academicYearId; }

    public boolean isPrimaryHomeroom() { return isPrimaryHomeroom; }
    public void setPrimaryHomeroom(boolean primaryHomeroom) { isPrimaryHomeroom = primaryHomeroom; }

    public Timestamp getAssignedAt() { return assignedAt; }
    public void setAssignedAt(Timestamp assignedAt) { this.assignedAt = assignedAt; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

    public String getTeacherEmployeeId() { return teacherEmployeeId; }
    public void setTeacherEmployeeId(String teacherEmployeeId) { this.teacherEmployeeId = teacherEmployeeId; }

    public String getSectionName() { return sectionName; }
    public void setSectionName(String sectionName) { this.sectionName = sectionName; }

    public String getGradeName() { return gradeName; }
    public void setGradeName(String gradeName) { this.gradeName = gradeName; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getAcademicYearName() { return academicYearName; }
    public void setAcademicYearName(String academicYearName) { this.academicYearName = academicYearName; }

    public String getFullClassName() {
        return (gradeName != null ? gradeName : "") + " - " + sectionName;
    }

    public boolean isHomeroom() { return isPrimaryHomeroom; }
    public String getSubjectNameAmharic() { return subjectName; }
}
