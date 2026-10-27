package dao;

import model.Teacher;
import model.TeacherAssignment;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Teacher profiles and authorization boundaries.
 * Strictly verifies that teachers can ONLY access their assigned classes and subjects.
 */
public class TeacherDAO {

    public Teacher findById(int id) {
        String sql = "SELECT * FROM teachers WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapTeacher(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Teacher findByUserId(int userId) {
        String sql = "SELECT * FROM teachers WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapTeacher(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Teacher> findAll() {
        List<Teacher> list = new ArrayList<>();
        String sql = "SELECT * FROM teachers ORDER BY full_name_en ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapTeacher(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int create(Teacher t) {
        String sql = "INSERT INTO teachers (user_id, employee_id, full_name_en, full_name_am, specialization, qualification, phone, email, hire_date, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (t.getUserId() != null && t.getUserId() > 0) {
                ps.setInt(1, t.getUserId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setString(2, t.getEmployeeId());
            ps.setString(3, t.getFullNameEn());
            ps.setString(4, t.getFullNameAm());
            ps.setString(5, t.getSpecialization());
            ps.setString(6, t.getQualification());
            ps.setString(7, t.getPhone());
            ps.setString(8, t.getEmail());
            ps.setDate(9, t.getHireDate());
            ps.setString(10, t.getStatus() != null ? t.getStatus() : "ACTIVE");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    t.setId(rs.getInt(1));
                    return t.getId();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<TeacherAssignment> getAssignments(int teacherId, int academicYearId) {
        List<TeacherAssignment> list = new ArrayList<>();
        String sql = "SELECT tca.*, t.full_name_en AS teacher_name, t.employee_id, cs.section_name, " +
                     "gl.name_en AS grade_name, s.name_en AS subject_name, ay.name AS year_name " +
                     "FROM teacher_class_assignments tca " +
                     "JOIN teachers t ON tca.teacher_id = t.id " +
                     "JOIN class_sections cs ON tca.class_section_id = cs.id " +
                     "JOIN grade_levels gl ON cs.grade_level_id = gl.id " +
                     "JOIN academic_years ay ON tca.academic_year_id = ay.id " +
                     "LEFT JOIN subjects s ON tca.subject_id = s.id " +
                     "WHERE tca.teacher_id = ? AND tca.academic_year_id = ? " +
                     "ORDER BY gl.grade_number, cs.section_name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            ps.setInt(2, academicYearId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TeacherAssignment a = new TeacherAssignment();
                    a.setId(rs.getInt("id"));
                    a.setTeacherId(rs.getInt("teacher_id"));
                    a.setClassSectionId(rs.getInt("class_section_id"));
                    int sId = rs.getInt("subject_id");
                    if (!rs.wasNull()) a.setSubjectId(sId);
                    a.setAcademicYearId(rs.getInt("academic_year_id"));
                    a.setPrimaryHomeroom(rs.getBoolean("is_primary_homeroom"));
                    a.setAssignedAt(rs.getTimestamp("assigned_at"));
                    a.setTeacherName(rs.getString("teacher_name"));
                    a.setTeacherEmployeeId(rs.getString("employee_id"));
                    a.setSectionName(rs.getString("section_name"));
                    a.setGradeName(rs.getString("grade_name"));
                    a.setSubjectName(rs.getString("subject_name"));
                    a.setAcademicYearName(rs.getString("year_name"));
                    list.add(a);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean assignTeacher(int teacherId, int classSectionId, Integer subjectId, int academicYearId, boolean isHomeroom) {
        String sql = "INSERT INTO teacher_class_assignments (teacher_id, class_section_id, subject_id, academic_year_id, is_primary_homeroom) " +
                     "VALUES (?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE is_primary_homeroom = VALUES(is_primary_homeroom)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            ps.setInt(2, classSectionId);
            if (subjectId != null && subjectId > 0) {
                ps.setInt(3, subjectId);
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            ps.setInt(4, academicYearId);
            ps.setBoolean(5, isHomeroom);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean removeAssignment(int assignmentId) {
        String sql = "DELETE FROM teacher_class_assignments WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assignmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Strictly verifies that the given teacher is authorized for the given class section.
     */
    public boolean isTeacherAssignedToClass(int teacherId, int classSectionId) {
        String sql = "SELECT COUNT(*) FROM teacher_class_assignments WHERE teacher_id = ? AND class_section_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            ps.setInt(2, classSectionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Strictly verifies that the given teacher is authorized for the given class section AND subject.
     */
    public boolean isTeacherAssignedToSubject(int teacherId, int classSectionId, int subjectId) {
        String sql = "SELECT COUNT(*) FROM teacher_class_assignments WHERE teacher_id = ? AND class_section_id = ? AND subject_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            ps.setInt(2, classSectionId);
            ps.setInt(3, subjectId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Teacher mapTeacher(ResultSet rs) throws SQLException {
        Teacher t = new Teacher();
        t.setId(rs.getInt("id"));
        int uId = rs.getInt("user_id");
        if (!rs.wasNull()) t.setUserId(uId);
        t.setEmployeeId(rs.getString("employee_id"));
        t.setFullNameEn(rs.getString("full_name_en"));
        t.setFullNameAm(rs.getString("full_name_am"));
        t.setSpecialization(rs.getString("specialization"));
        t.setQualification(rs.getString("qualification"));
        t.setPhone(rs.getString("phone"));
        t.setEmail(rs.getString("email"));
        t.setHireDate(rs.getDate("hire_date"));
        t.setStatus(rs.getString("status"));
        return t;
    }
}
