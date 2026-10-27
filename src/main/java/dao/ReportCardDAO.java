package dao;

import model.*;
import util.DBConnection;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for Report Cards.
 * Implements draft, review, approval, and publication workflows with attendance summaries.
 */
public class ReportCardDAO {

    private final AssessmentDAO assessmentDAO = new AssessmentDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final AcademicDAO academicDAO = new AcademicDAO();

    public ReportCard getReportCard(int studentId, int termId) {
        String sql = "SELECT rc.*, s.full_name_en, s.full_name_am, s.student_id_number, " +
                     "gl.name_en AS grade_name, cs.section_name, at.name AS term_name, ay.name AS year_name " +
                     "FROM report_cards rc " +
                     "JOIN students s ON rc.student_id = s.id " +
                     "JOIN student_enrollments se ON rc.enrollment_id = se.id " +
                     "JOIN grade_levels gl ON se.grade_level_id = gl.id " +
                     "JOIN class_sections cs ON se.class_section_id = cs.id " +
                     "JOIN academic_terms at ON rc.term_id = at.id " +
                     "JOIN academic_years ay ON at.academic_year_id = ay.id " +
                     "WHERE rc.student_id = ? AND rc.term_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, termId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ReportCard rc = mapReportCard(rs);
                    rc.setItems(getReportCardItems(rc.getId(), conn));
                    return rc;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ReportCard> getReportCardsForClass(int classSectionId, int termId) {
        List<ReportCard> list = new ArrayList<>();
        String sql = "SELECT rc.*, s.full_name_en, s.full_name_am, s.student_id_number, " +
                     "gl.name_en AS grade_name, cs.section_name, at.name AS term_name, ay.name AS year_name " +
                     "FROM report_cards rc " +
                     "JOIN students s ON rc.student_id = s.id " +
                     "JOIN student_enrollments se ON rc.enrollment_id = se.id " +
                     "JOIN grade_levels gl ON se.grade_level_id = gl.id " +
                     "JOIN class_sections cs ON se.class_section_id = cs.id " +
                     "JOIN academic_terms at ON rc.term_id = at.id " +
                     "JOIN academic_years ay ON at.academic_year_id = ay.id " +
                     "WHERE se.class_section_id = ? AND rc.term_id = ? " +
                     "ORDER BY rc.average_score DESC, s.full_name_en ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classSectionId);
            ps.setInt(2, termId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReportCard rc = mapReportCard(rs);
                    list.add(rc);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Generates or recalculates a report card from actual assessment scores and attendance.
     */
    public ReportCard generateOrRecalculateReportCard(int studentId, int enrollmentId, int classSectionId, int gradeLevelId, int termId, int academicYearId) {
        // Fetch subjects for this grade
        List<Subject> subjects = academicDAO.getSubjectsForGrade(gradeLevelId);
        List<ReportCardItem> items = new ArrayList<>();

        BigDecimal totalSum = BigDecimal.ZERO;
        int subjectCount = 0;

        for (Subject sub : subjects) {
            BigDecimal finalScore = assessmentDAO.calculateStudentSubjectTotal(studentId, classSectionId, sub.getId(), termId);
            String letterGrade = computeGradeLetter(finalScore);
            String remark = computeRemark(letterGrade);

            ReportCardItem item = new ReportCardItem(sub.getId(), finalScore, finalScore, letterGrade, remark);
            item.setSubjectNameEn(sub.getNameEn());
            item.setSubjectNameAm(sub.getNameAm());
            item.setSubjectCode(sub.getCode());
            items.add(item);

            totalSum = totalSum.add(finalScore);
            subjectCount++;
        }

        BigDecimal avgScore = BigDecimal.ZERO;
        if (subjectCount > 0) {
            avgScore = totalSum.divide(BigDecimal.valueOf(subjectCount), 2, RoundingMode.HALF_UP);
        }

        // Attendance stats
        Map<String, Integer> attSummary = attendanceDAO.getStudentAttendanceSummary(studentId, academicYearId);
        int present = attSummary.getOrDefault("PRESENT", 0);
        int absent = attSummary.getOrDefault("ABSENT", 0);
        int late = attSummary.getOrDefault("LATE", 0);

        // Upsert into report_cards
        String upsertCard = "INSERT INTO report_cards (student_id, enrollment_id, term_id, total_score, average_score, rank_in_class, conduct, attendance_present, attendance_absent, attendance_late, status) " +
                            "VALUES (?, ?, ?, ?, ?, 1, 'A', ?, ?, ?, 'DRAFT') " +
                            "ON DUPLICATE KEY UPDATE total_score = VALUES(total_score), average_score = VALUES(average_score), " +
                            "attendance_present = VALUES(attendance_present), attendance_absent = VALUES(attendance_absent), attendance_late = VALUES(attendance_late)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            int reportCardId = 0;
            try (PreparedStatement ps = conn.prepareStatement(upsertCard, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, studentId);
                ps.setInt(2, enrollmentId);
                ps.setInt(3, termId);
                ps.setBigDecimal(4, totalSum);
                ps.setBigDecimal(5, avgScore);
                ps.setInt(6, present);
                ps.setInt(7, absent);
                ps.setInt(8, late);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        reportCardId = rs.getInt(1);
                    }
                }
            }

            // If updated rather than inserted, query ID
            if (reportCardId == 0) {
                String selectId = "SELECT id FROM report_cards WHERE student_id = ? AND term_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(selectId)) {
                    ps.setInt(1, studentId);
                    ps.setInt(2, termId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) reportCardId = rs.getInt(1);
                    }
                }
            }

            // Delete old items and insert fresh
            String deleteItems = "DELETE FROM report_card_items WHERE report_card_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteItems)) {
                ps.setInt(1, reportCardId);
                ps.executeUpdate();
            }

            String insertItem = "INSERT INTO report_card_items (report_card_id, subject_id, assessment_total, final_score, letter_grade, remark) " +
                                "VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertItem)) {
                for (ReportCardItem it : items) {
                    ps.setInt(1, reportCardId);
                    ps.setInt(2, it.getSubjectId());
                    ps.setBigDecimal(3, it.getAssessmentTotal());
                    ps.setBigDecimal(4, it.getFinalScore());
                    ps.setString(5, it.getLetterGrade());
                    ps.setString(6, it.getRemark());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();
            return getReportCard(studentId, termId);
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
        return null;
    }

    public boolean publishReportCard(int reportCardId) {
        String sql = "UPDATE report_cards SET status = 'PUBLISHED', published_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reportCardId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateComments(int reportCardId, String teacherComment, String principalComment) {
        String sql = "UPDATE report_cards SET teacher_comment = ?, principal_comment = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, teacherComment);
            ps.setString(2, principalComment);
            ps.setInt(3, reportCardId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private List<ReportCardItem> getReportCardItems(int reportCardId, Connection conn) throws SQLException {
        List<ReportCardItem> items = new ArrayList<>();
        String sql = "SELECT rci.*, s.code, s.name_en, s.name_am FROM report_card_items rci " +
                     "JOIN subjects s ON rci.subject_id = s.id " +
                     "WHERE rci.report_card_id = ? ORDER BY s.code ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reportCardId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReportCardItem it = new ReportCardItem();
                    it.setId(rs.getInt("id"));
                    it.setReportCardId(rs.getInt("report_card_id"));
                    it.setSubjectId(rs.getInt("subject_id"));
                    it.setAssessmentTotal(rs.getBigDecimal("assessment_total"));
                    it.setFinalScore(rs.getBigDecimal("final_score"));
                    it.setLetterGrade(rs.getString("letter_grade"));
                    it.setRemark(rs.getString("remark"));
                    it.setSubjectCode(rs.getString("code"));
                    it.setSubjectNameEn(rs.getString("name_en"));
                    it.setSubjectNameAm(rs.getString("name_am"));
                    items.add(it);
                }
            }
        }
        return items;
    }

    private String computeGradeLetter(BigDecimal score) {
        if (score == null) return "F";
        double val = score.doubleValue();
        if (val >= 90.0) return "A";
        if (val >= 80.0) return "B";
        if (val >= 65.0) return "C";
        if (val >= 50.0) return "D";
        return "F";
    }

    private String computeRemark(String letterGrade) {
        switch (letterGrade) {
            case "A": return "Excellent (እጅግ በጣም ጥሩ)";
            case "B": return "Very Good (በጣም ጥሩ)";
            case "C": return "Good (ጥሩ)";
            case "D": return "Satisfactory (አጥጋቢ)";
            default: return "Needs Improvement (ትኩረት ያስፈልገዋል)";
        }
    }

    private ReportCard mapReportCard(ResultSet rs) throws SQLException {
        ReportCard rc = new ReportCard();
        rc.setId(rs.getInt("id"));
        rc.setStudentId(rs.getInt("student_id"));
        rc.setEnrollmentId(rs.getInt("enrollment_id"));
        rc.setTermId(rs.getInt("term_id"));
        rc.setTotalScore(rs.getBigDecimal("total_score"));
        rc.setAverageScore(rs.getBigDecimal("average_score"));
        rc.setRankInClass(rs.getInt("rank_in_class"));
        rc.setConduct(rs.getString("conduct"));
        rc.setAttendancePresent(rs.getInt("attendance_present"));
        rc.setAttendanceAbsent(rs.getInt("attendance_absent"));
        rc.setAttendanceLate(rs.getInt("attendance_late"));
        rc.setTeacherComment(rs.getString("teacher_comment"));
        rc.setPrincipalComment(rs.getString("principal_comment"));
        rc.setStatus(rs.getString("status"));
        rc.setPublishedAt(rs.getTimestamp("published_at"));
        rc.setCreatedAt(rs.getTimestamp("created_at"));

        rc.setStudentName(rs.getString("full_name_en"));
        rc.setStudentNameAm(rs.getString("full_name_am"));
        rc.setStudentIdNumber(rs.getString("student_id_number"));
        rc.setClassName(rs.getString("grade_name") + " - " + rs.getString("section_name"));
        rc.setGradeName(rs.getString("grade_name"));
        rc.setTermName(rs.getString("term_name"));
        rc.setAcademicYearName(rs.getString("year_name"));
        return rc;
    }
}
