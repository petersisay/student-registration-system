package model;

/**
 * Domain model representing a class section (e.g., Grade 1-A, Grade 1-B).
 */
public class ClassSection {
    private int id;
    private int gradeLevelId;
    private int academicYearId;
    private String sectionName;
    private String roomNumber;
    private int maxCapacity;

    // Joined display fields
    private String gradeName;
    private String gradeNameAmharic;
    private int gradeNumber;
    private String academicYearName;
    private int studentCount;

    public ClassSection() {}

    public ClassSection(int id, int gradeLevelId, int academicYearId, String sectionName, String roomNumber) {
        this.id = id;
        this.gradeLevelId = gradeLevelId;
        this.academicYearId = academicYearId;
        this.sectionName = sectionName;
        this.roomNumber = roomNumber;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getGradeLevelId() { return gradeLevelId; }
    public void setGradeLevelId(int gradeLevelId) { this.gradeLevelId = gradeLevelId; }

    public int getAcademicYearId() { return academicYearId; }
    public void setAcademicYearId(int academicYearId) { this.academicYearId = academicYearId; }

    public String getSectionName() { return sectionName; }
    public void setSectionName(String sectionName) { this.sectionName = sectionName; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public int getMaxCapacity() { return maxCapacity; }
    public void setMaxCapacity(int maxCapacity) { this.maxCapacity = maxCapacity; }

    public int getCapacity() { return maxCapacity; }
    public void setCapacity(int capacity) { this.maxCapacity = capacity; }

    public String getGradeName() { return gradeName; }
    public void setGradeName(String gradeName) { this.gradeName = gradeName; }

    public String getGradeNameAmharic() {
        return gradeNameAmharic != null && !gradeNameAmharic.isEmpty() ? gradeNameAmharic : (gradeNumber > 0 ? gradeNumber + "ኛ ክፍል" : (gradeName != null ? gradeName : ""));
    }
    public void setGradeNameAmharic(String gradeNameAmharic) { this.gradeNameAmharic = gradeNameAmharic; }

    public int getGradeNumber() { return gradeNumber; }
    public void setGradeNumber(int gradeNumber) { this.gradeNumber = gradeNumber; }

    public String getAcademicYearName() { return academicYearName; }
    public void setAcademicYearName(String academicYearName) { this.academicYearName = academicYearName; }

    public int getStudentCount() { return studentCount; }
    public void setStudentCount(int studentCount) { this.studentCount = studentCount; }

    public String getFullClassName() {
        return (gradeName != null ? gradeName : "Grade " + gradeNumber) + " - " + sectionName;
    }
}
