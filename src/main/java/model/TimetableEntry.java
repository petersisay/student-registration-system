package model;

/**
 * Domain model representing a scheduled period in the school timetable.
 */
public class TimetableEntry {
    private int id;
    private int classSectionId;
    private int subjectId;
    private int teacherId;
    private String dayOfWeek; // MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY
    private int periodNumber;
    private String startTime;
    private String endTime;

    // Joined display fields
    private String className;
    private String subjectName;
    private String teacherName;

    public TimetableEntry() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getClassSectionId() { return classSectionId; }
    public void setClassSectionId(int classSectionId) { this.classSectionId = classSectionId; }

    public int getSubjectId() { return subjectId; }
    public void setSubjectId(int subjectId) { this.subjectId = subjectId; }

    public int getTeacherId() { return teacherId; }
    public void setTeacherId(int teacherId) { this.teacherId = teacherId; }

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public int getPeriodNumber() { return periodNumber; }
    public void setPeriodNumber(int periodNumber) { this.periodNumber = periodNumber; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

    public String getLocalizedDay(String locale) {
        if ("am".equalsIgnoreCase(locale)) {
            switch (dayOfWeek != null ? dayOfWeek : "") {
                case "MONDAY": return "ሰኞ";
                case "TUESDAY": return "ማክሰኞ";
                case "WEDNESDAY": return "ረቡዕ";
                case "THURSDAY": return "ሐሙስ";
                case "FRIDAY": return "አርብ";
                default: return dayOfWeek;
            }
        }
        return dayOfWeek;
    }
}
