package model;

/**
 * Domain model representing elementary Grade Levels (Grades 1 through 8).
 */
public class GradeLevel {
    private int id;
    private int gradeNumber;
    private String nameEn;
    private String nameAm;

    public GradeLevel() {}

    public GradeLevel(int id, int gradeNumber, String nameEn, String nameAm) {
        this.id = id;
        this.gradeNumber = gradeNumber;
        this.nameEn = nameEn;
        this.nameAm = nameAm;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getGradeNumber() { return gradeNumber; }
    public void setGradeNumber(int gradeNumber) { this.gradeNumber = gradeNumber; }

    public String getNameEn() { return nameEn; }
    public void setNameEn(String nameEn) { this.nameEn = nameEn; }

    public String getNameAm() { return nameAm; }
    public void setNameAm(String nameAm) { this.nameAm = nameAm; }

    public String getDisplayName(String locale) {
        return "am".equalsIgnoreCase(locale) ? nameAm : nameEn;
    }
}
