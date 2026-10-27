package dao;

import model.*;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Academic structure: Years, Terms, Grades (1-8), Sections,
 * Subjects, and Configurable Grading Policies.
 */
public class AcademicDAO {

    public AcademicYear getCurrentAcademicYear() {
        String sql = "SELECT * FROM academic_years WHERE is_current = TRUE LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new AcademicYear(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDate("start_date"),
                    rs.getDate("end_date"),
                    rs.getBoolean("is_current")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<AcademicYear> getAllAcademicYears() {
        List<AcademicYear> list = new ArrayList<>();
        String sql = "SELECT * FROM academic_years ORDER BY start_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new AcademicYear(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDate("start_date"),
                    rs.getDate("end_date"),
                    rs.getBoolean("is_current")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public AcademicTerm getCurrentTerm() {
        String sql = "SELECT * FROM academic_terms WHERE is_current = TRUE LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new AcademicTerm(
                    rs.getInt("id"),
                    rs.getInt("academic_year_id"),
                    rs.getString("name"),
                    rs.getInt("term_number"),
                    rs.getDate("start_date"),
                    rs.getDate("end_date"),
                    rs.getBoolean("is_current")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<AcademicTerm> getTermsForYear(int academicYearId) {
        List<AcademicTerm> list = new ArrayList<>();
        String sql = "SELECT * FROM academic_terms WHERE academic_year_id = ? ORDER BY term_number ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, academicYearId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new AcademicTerm(
                        rs.getInt("id"),
                        rs.getInt("academic_year_id"),
                        rs.getString("name"),
                        rs.getInt("term_number"),
                        rs.getDate("start_date"),
                        rs.getDate("end_date"),
                        rs.getBoolean("is_current")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<GradeLevel> getAllGradeLevels() {
        List<GradeLevel> list = new ArrayList<>();
        String sql = "SELECT * FROM grade_levels ORDER BY grade_number ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new GradeLevel(
                    rs.getInt("id"),
                    rs.getInt("grade_number"),
                    rs.getString("name_en"),
                    rs.getString("name_am")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<ClassSection> getClassSections(int academicYearId, Integer gradeLevelId) {
        List<ClassSection> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT cs.*, gl.name_en AS grade_name, gl.grade_number, ay.name AS year_name, ")
           .append("(SELECT COUNT(*) FROM student_enrollments se WHERE se.class_section_id = cs.id AND se.status = 'ACTIVE') AS student_count ")
           .append("FROM class_sections cs ")
           .append("JOIN grade_levels gl ON cs.grade_level_id = gl.id ")
           .append("JOIN academic_years ay ON cs.academic_year_id = ay.id ")
           .append("WHERE cs.academic_year_id = ? ");

        if (gradeLevelId != null && gradeLevelId > 0) {
            sql.append("AND cs.grade_level_id = ? ");
        }
        sql.append("ORDER BY gl.grade_number, cs.section_name");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setInt(1, academicYearId);
            if (gradeLevelId != null && gradeLevelId > 0) {
                ps.setInt(2, gradeLevelId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ClassSection cs = new ClassSection();
                    cs.setId(rs.getInt("id"));
                    cs.setGradeLevelId(rs.getInt("grade_level_id"));
                    cs.setAcademicYearId(rs.getInt("academic_year_id"));
                    cs.setSectionName(rs.getString("section_name"));
                    cs.setRoomNumber(rs.getString("room_number"));
                    cs.setMaxCapacity(rs.getInt("max_capacity"));
                    cs.setGradeName(rs.getString("grade_name"));
                    cs.setGradeNumber(rs.getInt("grade_number"));
                    cs.setAcademicYearName(rs.getString("year_name"));
                    cs.setStudentCount(rs.getInt("student_count"));
                    list.add(cs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ClassSection getClassSectionById(int id) {
        String sql = "SELECT cs.*, gl.name_en AS grade_name, gl.grade_number, ay.name AS year_name, " +
                     "(SELECT COUNT(*) FROM student_enrollments se WHERE se.class_section_id = cs.id AND se.status = 'ACTIVE') AS student_count " +
                     "FROM class_sections cs " +
                     "JOIN grade_levels gl ON cs.grade_level_id = gl.id " +
                     "JOIN academic_years ay ON cs.academic_year_id = ay.id " +
                     "WHERE cs.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ClassSection cs = new ClassSection();
                    cs.setId(rs.getInt("id"));
                    cs.setGradeLevelId(rs.getInt("grade_level_id"));
                    cs.setAcademicYearId(rs.getInt("academic_year_id"));
                    cs.setSectionName(rs.getString("section_name"));
                    cs.setRoomNumber(rs.getString("room_number"));
                    cs.setMaxCapacity(rs.getInt("max_capacity"));
                    cs.setGradeName(rs.getString("grade_name"));
                    cs.setGradeNumber(rs.getInt("grade_number"));
                    cs.setAcademicYearName(rs.getString("year_name"));
                    cs.setStudentCount(rs.getInt("student_count"));
                    return cs;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Subject> getSubjectsForGrade(int gradeLevelId) {
        List<Subject> list = new ArrayList<>();
        String sql = "SELECT s.*, gl.name_en AS grade_name FROM subjects s " +
                     "JOIN grade_levels gl ON s.grade_level_id = gl.id " +
                     "WHERE s.grade_level_id = ? ORDER BY s.code ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, gradeLevelId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Subject s = new Subject();
                    s.setId(rs.getInt("id"));
                    s.setCode(rs.getString("code"));
                    s.setNameEn(rs.getString("name_en"));
                    s.setNameAm(rs.getString("name_am"));
                    s.setGradeLevelId(rs.getInt("grade_level_id"));
                    s.setPassMark(rs.getBigDecimal("pass_mark"));
                    s.setMaxMark(rs.getBigDecimal("max_mark"));
                    s.setGradeName(rs.getString("grade_name"));
                    list.add(s);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Subject getSubjectById(int id) {
        String sql = "SELECT s.*, gl.name_en AS grade_name FROM subjects s " +
                     "JOIN grade_levels gl ON s.grade_level_id = gl.id " +
                     "WHERE s.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Subject s = new Subject();
                    s.setId(rs.getInt("id"));
                    s.setCode(rs.getString("code"));
                    s.setNameEn(rs.getString("name_en"));
                    s.setNameAm(rs.getString("name_am"));
                    s.setGradeLevelId(rs.getInt("grade_level_id"));
                    s.setPassMark(rs.getBigDecimal("pass_mark"));
                    s.setMaxMark(rs.getBigDecimal("max_mark"));
                    s.setGradeName(rs.getString("grade_name"));
                    return s;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public GradingPolicy getActiveGradingPolicy(int academicYearId) {
        String sql = "SELECT * FROM grading_policies WHERE academic_year_id = ? AND is_active = TRUE LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, academicYearId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    GradingPolicy gp = new GradingPolicy();
                    gp.setId(rs.getInt("id"));
                    gp.setSchoolId(rs.getInt("school_id"));
                    gp.setAcademicYearId(rs.getInt("academic_year_id"));
                    gp.setName(rs.getString("name"));
                    gp.setPolicyJson(rs.getString("policy_json"));
                    gp.setActive(rs.getBoolean("is_active"));
                    gp.setCreatedAt(rs.getTimestamp("created_at"));
                    return gp;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean saveGradingPolicy(GradingPolicy policy) {
        String sql = "INSERT INTO grading_policies (school_id, academic_year_id, name, policy_json, is_active) " +
                     "VALUES (?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE name = VALUES(name), policy_json = VALUES(policy_json), is_active = VALUES(is_active)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, policy.getSchoolId());
            ps.setInt(2, policy.getAcademicYearId());
            ps.setString(3, policy.getName());
            ps.setString(4, policy.getPolicyJson());
            ps.setBoolean(5, policy.isActive());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public AcademicYear getActiveAcademicYear() {
        return getCurrentAcademicYear();
    }

    public List<ClassSection> getAllClassSections() {
        AcademicYear year = getCurrentAcademicYear();
        int yearId = (year != null) ? year.getId() : 1;
        return getClassSections(yearId, null);
    }

    public List<Subject> getAllSubjects() {
        List<Subject> list = new ArrayList<>();
        String sql = "SELECT s.*, gl.name_en AS grade_name FROM subjects s " +
                     "JOIN grade_levels gl ON s.grade_level_id = gl.id " +
                     "ORDER BY gl.grade_number ASC, s.code ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Subject s = new Subject();
                s.setId(rs.getInt("id"));
                s.setCode(rs.getString("code"));
                s.setNameEn(rs.getString("name_en"));
                s.setNameAm(rs.getString("name_am"));
                s.setGradeLevelId(rs.getInt("grade_level_id"));
                s.setPassMark(rs.getBigDecimal("pass_mark"));
                s.setMaxMark(rs.getBigDecimal("max_mark"));
                s.setGradeName(rs.getString("grade_name"));
                list.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
