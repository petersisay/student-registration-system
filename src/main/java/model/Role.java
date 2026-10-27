package model;

/**
 * Enumeration of system roles with localized display names and authority levels.
 */
public enum Role {
    ADMIN("Administrator", "አስተዳዳሪ"),
    PRINCIPAL("Principal", "ርዕሰ መምህር"),
    REGISTRAR("Registrar", "ሬጅስትራር"),
    TEACHER("Teacher", "መምህር/ት"),
    ATTENDANCE_OFFICER("Attendance Officer", "የክትትል ኃላፊ"),
    STUDENT("Student", "ተማሪ"),
    GUARDIAN("Guardian", "ወላጅ/አሳዳጊ");

    private final String labelEn;
    private final String labelAm;

    Role(String labelEn, String labelAm) {
        this.labelEn = labelEn;
        this.labelAm = labelAm;
    }

    public String getLabelEn() {
        return labelEn;
    }

    public String getLabelAm() {
        return labelAm;
    }

    public String getLocalizedLabel(String locale) {
        return "am".equalsIgnoreCase(locale) ? labelAm : labelEn;
    }

    public static Role fromString(String roleStr) {
        if (roleStr == null) return null;
        for (Role r : values()) {
            if (r.name().equalsIgnoreCase(roleStr.trim())) {
                return r;
            }
        }
        return null;
    }
}
