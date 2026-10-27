package dao;

import model.Announcement;
import model.AuditLog;
import model.TimetableEntry;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for School Operations: Timetable scheduling, Announcements,
 * Audit logs, and School-wide dashboard metrics.
 */
public class SchoolOperationsDAO {

    public List<TimetableEntry> getTimetableForClass(int classSectionId) {
        List<TimetableEntry> list = new ArrayList<>();
        String sql = "SELECT tt.*, s.name_en AS subject_name, t.full_name_en AS teacher_name, " +
                     "cs.section_name, gl.name_en AS grade_name " +
                     "FROM timetables tt " +
                     "JOIN subjects s ON tt.subject_id = s.id " +
                     "JOIN teachers t ON tt.teacher_id = t.id " +
                     "JOIN class_sections cs ON tt.class_section_id = cs.id " +
                     "JOIN grade_levels gl ON cs.grade_level_id = gl.id " +
                     "WHERE tt.class_section_id = ? " +
                     "ORDER BY FIELD(tt.day_of_week, 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY'), tt.period_number ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classSectionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapTimetable(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<TimetableEntry> getTimetableForTeacher(int teacherId) {
        List<TimetableEntry> list = new ArrayList<>();
        String sql = "SELECT tt.*, s.name_en AS subject_name, t.full_name_en AS teacher_name, " +
                     "cs.section_name, gl.name_en AS grade_name " +
                     "FROM timetables tt " +
                     "JOIN subjects s ON tt.subject_id = s.id " +
                     "JOIN teachers t ON tt.teacher_id = t.id " +
                     "JOIN class_sections cs ON tt.class_section_id = cs.id " +
                     "JOIN grade_levels gl ON cs.grade_level_id = gl.id " +
                     "WHERE tt.teacher_id = ? " +
                     "ORDER BY FIELD(tt.day_of_week, 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY'), tt.period_number ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapTimetable(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean saveTimetableEntry(TimetableEntry e) {
        String sql = "INSERT INTO timetables (class_section_id, subject_id, teacher_id, day_of_week, period_number, start_time, end_time) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE subject_id = VALUES(subject_id), teacher_id = VALUES(teacher_id), " +
                     "start_time = VALUES(start_time), end_time = VALUES(end_time)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, e.getClassSectionId());
            ps.setInt(2, e.getSubjectId());
            ps.setInt(3, e.getTeacherId());
            ps.setString(4, e.getDayOfWeek());
            ps.setInt(5, e.getPeriodNumber());
            ps.setString(6, e.getStartTime());
            ps.setString(7, e.getEndTime());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public List<Announcement> getActiveAnnouncements(String role) {
        List<Announcement> list = new ArrayList<>();
        String sql = "SELECT a.*, u.full_name AS author_name FROM announcements a " +
                     "LEFT JOIN users u ON a.author_id = u.id " +
                     "WHERE a.is_active = TRUE AND (a.target_role = 'ALL' OR a.target_role = ?) " +
                     "ORDER BY a.published_date DESC, a.id DESC LIMIT 10";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role != null ? role : "ALL");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Announcement a = new Announcement();
                    a.setId(rs.getInt("id"));
                    a.setTitleEn(rs.getString("title_en"));
                    a.setTitleAm(rs.getString("title_am"));
                    a.setContentEn(rs.getString("content_en"));
                    a.setContentAm(rs.getString("content_am"));
                    a.setTargetRole(rs.getString("target_role"));
                    int auth = rs.getInt("author_id");
                    if (!rs.wasNull()) a.setAuthorId(auth);
                    a.setPublishedDate(rs.getDate("published_date"));
                    a.setExpiryDate(rs.getDate("expiry_date"));
                    a.setActive(rs.getBoolean("is_active"));
                    a.setCreatedAt(rs.getTimestamp("created_at"));
                    a.setAuthorName(rs.getString("author_name"));
                    list.add(a);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean createAnnouncement(Announcement a) {
        String sql = "INSERT INTO announcements (title_en, title_am, content_en, content_am, target_role, author_id, published_date, expiry_date, is_active) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getTitleEn());
            ps.setString(2, a.getTitleAm());
            ps.setString(3, a.getContentEn());
            ps.setString(4, a.getContentAm());
            ps.setString(5, a.getTargetRole() != null ? a.getTargetRole() : "ALL");
            if (a.getAuthorId() != null && a.getAuthorId() > 0) {
                ps.setInt(6, a.getAuthorId());
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            ps.setDate(7, a.getPublishedDate() != null ? a.getPublishedDate() : new Date(System.currentTimeMillis()));
            ps.setDate(8, a.getExpiryDate());
            ps.setBoolean(9, a.isActive());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void logAudit(AuditLog log) {
        String sql = "INSERT INTO audit_logs (user_id, action, entity_type, entity_id, details, ip_address) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (log.getUserId() != null && log.getUserId() > 0) {
                ps.setInt(1, log.getUserId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setString(2, log.getAction());
            ps.setString(3, log.getEntityType());
            ps.setString(4, log.getEntityId());
            ps.setString(5, log.getDetails());
            ps.setString(6, log.getIpAddress());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<AuditLog> getRecentAuditLogs(int limit) {
        List<AuditLog> list = new ArrayList<>();
        String sql = "SELECT al.*, u.username FROM audit_logs al " +
                     "LEFT JOIN users u ON al.user_id = u.id " +
                     "ORDER BY al.created_at DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit > 0 ? limit : 20);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AuditLog al = new AuditLog();
                    al.setId(rs.getInt("id"));
                    int uId = rs.getInt("user_id");
                    if (!rs.wasNull()) al.setUserId(uId);
                    al.setAction(rs.getString("action"));
                    al.setEntityType(rs.getString("entity_type"));
                    al.setEntityId(rs.getString("entity_id"));
                    al.setDetails(rs.getString("details"));
                    al.setIpAddress(rs.getString("ip_address"));
                    al.setCreatedAt(rs.getTimestamp("created_at"));
                    al.setUsername(rs.getString("username"));
                    list.add(al);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Map<String, Object> getSchoolStats() {
        Map<String, Object> stats = new HashMap<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM students WHERE status = 'ACTIVE'")) {
                if (rs.next()) stats.put("totalStudents", rs.getInt(1));
            }
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM teachers WHERE status = 'ACTIVE'")) {
                if (rs.next()) stats.put("totalTeachers", rs.getInt(1));
            }
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM class_sections")) {
                if (rs.next()) stats.put("totalClasses", rs.getInt(1));
            }
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM subjects")) {
                if (rs.next()) stats.put("totalSubjects", rs.getInt(1));
            }

            // Student distribution by grade
            Map<Integer, Integer> gradeDist = new HashMap<>();
            String gradeSql = "SELECT gl.grade_number, COUNT(se.id) AS cnt FROM grade_levels gl " +
                              "LEFT JOIN student_enrollments se ON gl.id = se.grade_level_id AND se.status = 'ACTIVE' " +
                              "GROUP BY gl.grade_number ORDER BY gl.grade_number";
            try (ResultSet rs = stmt.executeQuery(gradeSql)) {
                while (rs.next()) {
                    gradeDist.put(rs.getInt("grade_number"), rs.getInt("cnt"));
                }
            }
            stats.put("gradeDistribution", gradeDist);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    private TimetableEntry mapTimetable(ResultSet rs) throws SQLException {
        TimetableEntry tt = new TimetableEntry();
        tt.setId(rs.getInt("id"));
        tt.setClassSectionId(rs.getInt("class_section_id"));
        tt.setSubjectId(rs.getInt("subject_id"));
        tt.setTeacherId(rs.getInt("teacher_id"));
        tt.setDayOfWeek(rs.getString("day_of_week"));
        tt.setPeriodNumber(rs.getInt("period_number"));
        tt.setStartTime(rs.getString("start_time"));
        tt.setEndTime(rs.getString("end_time"));
        tt.setSubjectName(rs.getString("subject_name"));
        tt.setTeacherName(rs.getString("teacher_name"));
        tt.setClassName(rs.getString("grade_name") + " - " + rs.getString("section_name"));
        return tt;
    }
}
