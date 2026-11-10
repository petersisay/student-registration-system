package academic;

import dao.TeacherDAO;
import model.Teacher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.DatabaseMigration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class TeacherAssignmentSecurityTest {

    private boolean isDatabaseAvailable() {
        try (java.sql.Connection conn = util.DBConnection.getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            return false;
        }
    }

    @Test
    @DisplayName("Enforce teacher class and subject assignment authorization boundaries")
    void testTeacherAssignmentAuthorizationBoundaries() {
        assumeTrue(isDatabaseAvailable(), "MySQL database not accessible; skipping DB integration test");
        DatabaseMigration.runMigrations();
        TeacherDAO teacherDAO = new TeacherDAO();

        List<Teacher> teachers = teacherDAO.findAll();
        assertNotNull(teachers);
        assertFalse(teachers.isEmpty(), "Faculty members should exist in seed data");

        Teacher teacher1 = teachers.get(0);

        // Assign teacher 1 to class section 1 (Grade 1-A) and subject 1 (Mathematics)
        boolean assigned = teacherDAO.assignTeacher(teacher1.getId(), 1, 1, 1, true);
        assertTrue(assigned, "Assignment insertion should succeed");

        // Permitted access:
        assertTrue(teacherDAO.isTeacherAssignedToClass(teacher1.getId(), 1),
                "Teacher must be authorized to access their assigned class");
        assertTrue(teacherDAO.isTeacherAssignedToSubject(teacher1.getId(), 1, 1),
                "Teacher must be authorized to enter grades for assigned subject");

        // Denied access to unrelated class or subject:
        assertFalse(teacherDAO.isTeacherAssignedToClass(teacher1.getId(), 999),
                "Teacher must NOT have access to unassigned class sections");
        assertFalse(teacherDAO.isTeacherAssignedToSubject(teacher1.getId(), 1, 999),
                "Teacher must NOT be able to modify grades for unassigned subjects");
    }
}
