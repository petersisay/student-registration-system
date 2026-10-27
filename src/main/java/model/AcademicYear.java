package model;

import java.sql.Date;

/**
 * Domain model representing an Ethiopian Academic Year (e.g. 2026/2027 or 2019 E.C.).
 */
public class AcademicYear {
    private int id;
    private String name;
    private Date startDate;
    private Date endDate;
    private boolean isCurrent;
    private String academicYearAmharic;

    public AcademicYear() {}

    public AcademicYear(int id, String name, Date startDate, Date endDate, boolean isCurrent) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isCurrent = isCurrent;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public boolean isCurrent() { return isCurrent; }
    public void setCurrent(boolean current) { isCurrent = current; }

    public String getAcademicYearAmharic() {
        return academicYearAmharic != null && !academicYearAmharic.isEmpty() ? academicYearAmharic : name;
    }
    public void setAcademicYearAmharic(String academicYearAmharic) {
        this.academicYearAmharic = academicYearAmharic;
    }
}
