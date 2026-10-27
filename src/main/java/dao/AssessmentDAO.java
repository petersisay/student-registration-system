package dao;

import model.Assessment;
import model.AssessmentScore;
import util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Academic Assessments and Mark Entry.
 */
public class AssessmentDAO {

    public int createAssessment(Assessment a) {
        String sql = "INSERT INTO assessments (class_section_id, subject_id, term_id, teacher_id, title, assessment_type, max_score, weight_percent, assessment_date, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, a.getClassSectionId());
            ps.setInt(2, a.getSubjectId());
            ps.setInt(3, a.getTermId());
            ps.setInt(4, a.getTeacherId());
            ps.setString(5, a.getTitle());
            ps.setString(6, a.getAssessmentType());
            ps.setBigDecimal(7, a.getMaxScore() != null ? a.getMaxScore() : BigDecimal.valueOf(100));
            ps.setBigDecimal(8, a.getWeightPercent() != null ? a.getWeightPercent() : BigDecimal.valueOf(20));
            ps.setDate(9, a.getAssessmentDate() != null ? a.getAssessmentDate() : new Date(System.currentTimeMillis()));
            ps.setString(10, a.getStatus() != null ? a.getStatus() : "DRAFT");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    a.setId(rs.getInt(1));
                    return a.getId();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public Assessment getAssessmentById(int id) {
        String sql = "SELECT a.*, s.name_en AS subject_name, cs.section_name, gl.name_en AS grade_name, " +
                     "at.name AS term_name, t.full_name_en AS teacher_name, " +
                     "(SELECT COUNT(*) FROM assessment_scores sc WHERE sc.assessment_id = a.id) AS scores_count " +
                     "FROM assessments a " +
                     "JOIN subjects s ON a.subject_id = s.id " +
                     "JOIN class_sections cs ON a.class_section_id = cs.id " +
                     "JOIN grade_levels gl ON cs.grade_level_id = gl.id " +
                     "JOIN academic_terms at ON a.term_id = at.id " +
                     "JOIN teachers t ON a.teacher_id = t.id " +
                     "WHERE a.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Assessment a = new Assessment();
                    a.setId(rs.getInt("id"));
                    a.setClassSectionId(rs.getInt("class_section_id"));
                    a.setSubjectId(rs.getInt("subject_id"));
                    a.setTermId(rs.getInt("term_id"));
                    a.setTeacherId(rs.getInt("teacher_id"));
                    a.setTitle(rs.getString("title"));
                    a.setAssessmentType(rs.getString("assessment_type"));
                    a.setMaxScore(rs.getBigDecimal("max_score"));
                    a.setWeightPercent(rs.getBigDecimal("weight_percent"));
                    a.setAssessmentDate(rs.getDate("assessment_date"));
                    a.setStatus(rs.getString("status"));
                    a.setCreatedAt(rs.getTimestamp("created_at"));
                    a.setSubjectName(rs.getString("subject_name"));
                    a.setClassName(rs.getString("grade_name") + " - " + rs.getString("section_name"));
                    a.setTermName(rs.getString("term_name"));
                    a.setTeacherName(rs.getString("teacher_name"));
                    a.setScoresCount(rs.getInt("scores_count"));
                    return a;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Assessment> getAssessmentsForClassAndSubject(int classSectionId, int subjectId, int termId) {
        List<Assessment> list = new ArrayList<>();
        String sql = "SELECT a.*, s.name_en AS subject_name, cs.section_name, gl.name_en AS grade_name, " +
                     "at.name AS term_name, t.full_name_en AS teacher_name, " +
                     "(SELECT COUNT(*) FROM assessment_scores sc WHERE sc.assessment_id = a.id) AS scores_count " +
                     "FROM assessments a " +
                     "JOIN subjects s ON a.subject_id = s.id " +
                     "JOIN class_sections cs ON a.class_section_id = cs.id " +
                     "JOIN grade_levels gl ON cs.grade_level_id = gl.id " +
                     "JOIN academic_terms at ON a.term_id = at.id " +
                     "JOIN teachers t ON a.teacher_id = t.id " +
                     "WHERE a.class_section_id = ? AND a.subject_id = ? AND a.term_id = ? " +
                     "ORDER BY a.assessment_date DESC, a.id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classSectionId);
            ps.setInt(2, subjectId);
            ps.setInt(3, termId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Assessment a = new Assessment();
                    a.setId(rs.getInt("id"));
                    a.setClassSectionId(rs.getInt("class_section_id"));
                    a.setSubjectId(rs.getInt("subject_id"));
                    a.setTermId(rs.getInt("term_id"));
                    a.setTeacherId(rs.getInt("teacher_id"));
                    a.setTitle(rs.getString("title"));
                    a.setAssessmentType(rs.getString("assessment_type"));
                    a.setMaxScore(rs.getBigDecimal("max_score"));
                    a.setWeightPercent(rs.getBigDecimal("weight_percent"));
                    a.setAssessmentDate(rs.getDate("assessment_date"));
                    a.setStatus(rs.getString("status"));
                    a.setCreatedAt(rs.getTimestamp("created_at"));
                    a.setSubjectName(rs.getString("subject_name"));
                    a.setClassName(rs.getString("grade_name") + " - " + rs.getString("section_name"));
                    a.setTermName(rs.getString("term_name"));
                    a.setTeacherName(rs.getString("teacher_name"));
                    a.setScoresCount(rs.getInt("scores_count"));
                    list.add(a);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<AssessmentScore> getScoresForAssessment(int assessmentId) {
        List<AssessmentScore> list = new ArrayList<>();
        String sql = "SELECT sc.*, s.full_name_en, s.full_name_am, s.name, s.student_id_number " +
                     "FROM assessment_scores sc " +
                     "JOIN students s ON sc.student_id = s.id " +
                     "WHERE sc.assessment_id = ? " +
                     "ORDER BY s.full_name_en ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assessmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AssessmentScore sc = new AssessmentScore();
                    sc.setId(rs.getInt("id"));
                    sc.setAssessmentId(rs.getInt("assessment_id"));
                    sc.setStudentId(rs.getInt("student_id"));
                    sc.setScore(rs.getBigDecimal("score"));
                    sc.setComments(rs.getString("comments"));
                    sc.setEnteredAt(rs.getTimestamp("entered_at"));
                    sc.setUpdatedAt(rs.getTimestamp("updated_at"));

                    String fn = rs.getString("full_name_en");
                    sc.setStudentName(fn != null && !fn.isEmpty() ? fn : rs.getString("name"));
                    sc.setStudentIdNumber(rs.getString("student_id_number"));
                    list.add(sc);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean recordScores(int assessmentId, List<AssessmentScore> scores) {
        if (scores == null || scores.isEmpty()) return true;
        String sql = "INSERT INTO assessment_scores (assessment_id, student_id, score, comments) " +
                     "VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE score = VALUES(score), comments = VALUES(comments)";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (AssessmentScore sc : scores) {
                    ps.setInt(1, assessmentId);
                    ps.setInt(2, sc.getStudentId());
                    ps.setBigDecimal(3, sc.getScore());
                    ps.setString(4, sc.getComments());
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

    /**
     * Calculates the weighted final score of a student in a specific subject and term.
     */
    public BigDecimal calculateStudentSubjectTotal(int studentId, int classSectionId, int subjectId, int termId) {
        String sql = "SELECT a.max_score, a.weight_percent, sc.score " +
                     "FROM assessments a " +
                     "JOIN assessment_scores sc ON a.id = sc.assessment_id " +
                     "WHERE sc.student_id = ? AND a.class_section_id = ? AND a.subject_id = ? AND a.term_id = ?";
        BigDecimal totalWeighted = BigDecimal.ZERO;
        BigDecimal totalWeights = BigDecimal.ZERO;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, classSectionId);
            ps.setInt(3, subjectId);
            ps.setInt(4, termId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BigDecimal maxScore = rs.getBigDecimal("max_score");
                    BigDecimal weight = rs.getBigDecimal("weight_percent");
                    BigDecimal rawScore = rs.getBigDecimal("score");

                    if (maxScore != null && maxScore.compareTo(BigDecimal.ZERO) > 0 &&
                        weight != null && rawScore != null) {
                        // (score / maxScore) * weight
                        BigDecimal normalized = rawScore.divide(maxScore, 4, java.math.RoundingMode.HALF_UP).multiply(weight);
                        totalWeighted = totalWeighted.add(normalized);
                        totalWeights = totalWeights.add(weight);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // If total weights are configured as 100%, return totalWeighted scaled to 2 decimal places
        if (totalWeights.compareTo(BigDecimal.ZERO) > 0) {
            return totalWeighted.setScale(2, java.math.RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }
}
