package dao;

import model.Attendance;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for daily attendance operations.
 */
public class AttendanceDAO {

    public boolean recordAttendance(int studentId, int classSectionId, int academicYearId, Date date, String status, Integer recordedBy, String notes) {
        String sql = "INSERT INTO attendances (student_id, class_section_id, academic_year_id, attendance_date, status, recorded_by_user_id, notes) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE status = VALUES(status), recorded_by_user_id = VALUES(recorded_by_user_id), notes = VALUES(notes), recorded_at = CURRENT_TIMESTAMP";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, classSectionId);
            ps.setInt(3, academicYearId);
            ps.setDate(4, date);
            ps.setString(5, status);
            if (recordedBy != null && recordedBy > 0) {
                ps.setInt(6, recordedBy);
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            ps.setString(7, notes);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean batchRecord(List<Attendance> list) {
        if (list == null || list.isEmpty()) return true;
        String sql = "INSERT INTO attendances (student_id, class_section_id, academic_year_id, attendance_date, status, recorded_by_user_id, notes) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE status = VALUES(status), recorded_by_user_id = VALUES(recorded_by_user_id), notes = VALUES(notes), recorded_at = CURRENT_TIMESTAMP";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (Attendance a : list) {
                    ps.setInt(1, a.getStudentId());
                    ps.setInt(2, a.getClassSectionId());
                    ps.setInt(3, a.getAcademicYearId());
                    ps.setDate(4, a.getAttendanceDate());
                    ps.setString(5, a.getStatus());
                    if (a.getRecordedByUserId() != null && a.getRecordedByUserId() > 0) {
                        ps.setInt(6, a.getRecordedByUserId());
                    } else {
                        ps.setNull(6, Types.INTEGER);
                    }
                    ps.setString(7, a.getNotes());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
        return false;
    }

    public List<Attendance> getAttendanceForClassAndDate(int classSectionId, Date date) {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT a.*, s.full_name_en, s.full_name_am, s.name, s.student_id_number, u.full_name AS recorder_name " +
                     "FROM attendances a " +
                     "JOIN students s ON a.student_id = s.id " +
                     "LEFT JOIN users u ON a.recorded_by_user_id = u.id " +
                     "WHERE a.class_section_id = ? AND a.attendance_date = ? " +
                     "ORDER BY s.full_name_en ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classSectionId);
            ps.setDate(2, date);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Attendance a = new Attendance();
                    a.setId(rs.getInt("id"));
                    a.setStudentId(rs.getInt("student_id"));
                    a.setClassSectionId(rs.getInt("class_section_id"));
                    a.setAcademicYearId(rs.getInt("academic_year_id"));
                    a.setAttendanceDate(rs.getDate("attendance_date"));
                    a.setStatus(rs.getString("status"));
                    int rec = rs.getInt("recorded_by_user_id");
                    if (!rs.wasNull()) a.setRecordedByUserId(rec);
                    a.setNotes(rs.getString("notes"));
                    a.setRecordedAt(rs.getTimestamp("recorded_at"));

                    String fn = rs.getString("full_name_en");
                    a.setStudentName(fn != null && !fn.isEmpty() ? fn : rs.getString("name"));
                    a.setStudentIdNumber(rs.getString("student_id_number"));
                    a.setRecorderName(rs.getString("recorder_name"));
                    list.add(a);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Map<String, Integer> getStudentAttendanceSummary(int studentId, int academicYearId) {
        Map<String, Integer> summary = new HashMap<>();
        summary.put("PRESENT", 0);
        summary.put("ABSENT", 0);
        summary.put("LATE", 0);
        summary.put("EXCUSED", 0);

        String sql = "SELECT status, COUNT(*) AS cnt FROM attendances " +
                     "WHERE student_id = ? AND academic_year_id = ? GROUP BY status";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, academicYearId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    summary.put(rs.getString("status"), rs.getInt("cnt"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return summary;
    }

    public Map<String, Integer> getTodayClassOverview(int academicYearId, Date date) {
        Map<String, Integer> summary = new HashMap<>();
        summary.put("PRESENT", 0);
        summary.put("ABSENT", 0);
        summary.put("LATE", 0);
        summary.put("EXCUSED", 0);

        String sql = "SELECT status, COUNT(*) AS cnt FROM attendances " +
                     "WHERE academic_year_id = ? AND attendance_date = ? GROUP BY status";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, academicYearId);
            ps.setDate(2, date);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    summary.put(rs.getString("status"), rs.getInt("cnt"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return summary;
    }
}
