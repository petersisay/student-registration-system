package model;

import java.math.BigDecimal;

/**
 * Domain model representing elementary curriculum subjects.
 */
public class Subject {
    private int id;
    private String code;
    private String nameEn;
    private String nameAm;
    private int gradeLevelId;
    private BigDecimal passMark;
    private BigDecimal maxMark;

    // Joined fields
    private String gradeName;

    public Subject() {}

    public Subject(int id, String code, String nameEn, String nameAm, int gradeLevelId) {
        this.id = id;
        this.code = code;
        this.nameEn = nameEn;
        this.nameAm = nameAm;
        this.gradeLevelId = gradeLevelId;
        this.passMark = BigDecimal.valueOf(50);
        this.maxMark = BigDecimal.valueOf(100);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getNameEn() { return nameEn; }
    public void setNameEn(String nameEn) { this.nameEn = nameEn; }

    public String getNameAm() { return nameAm; }
    public void setNameAm(String nameAm) { this.nameAm = nameAm; }

    public int getGradeLevelId() { return gradeLevelId; }
    public void setGradeLevelId(int gradeLevelId) { this.gradeLevelId = gradeLevelId; }

    public BigDecimal getPassMark() { return passMark; }
    public void setPassMark(BigDecimal passMark) { this.passMark = passMark; }

    public BigDecimal getMaxMark() { return maxMark; }
    public void setMaxMark(BigDecimal maxMark) { this.maxMark = maxMark; }

    public String getGradeName() { return gradeName; }
    public void setGradeName(String gradeName) { this.gradeName = gradeName; }

    public String getDisplayName(String locale) {
        return "am".equalsIgnoreCase(locale) ? nameAm : nameEn;
    }
}
