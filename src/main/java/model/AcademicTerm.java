package model;

import java.sql.Date;

/**
 * Domain model representing an academic term/semester.
 */
public class AcademicTerm {
    private int id;
    private int academicYearId;
    private String name;
    private int termNumber;
    private Date startDate;
    private Date endDate;
    private boolean isCurrent;

    public AcademicTerm() {}

    public AcademicTerm(int id, int academicYearId, String name, int termNumber, Date startDate, Date endDate, boolean isCurrent) {
        this.id = id;
        this.academicYearId = academicYearId;
        this.name = name;
        this.termNumber = termNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isCurrent = isCurrent;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getAcademicYearId() { return academicYearId; }
    public void setAcademicYearId(int academicYearId) { this.academicYearId = academicYearId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getTermNumber() { return termNumber; }
    public void setTermNumber(int termNumber) { this.termNumber = termNumber; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public boolean isCurrent() { return isCurrent; }
    public void setCurrent(boolean current) { isCurrent = current; }
}
