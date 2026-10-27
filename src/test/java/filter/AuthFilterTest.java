package filter;

import model.Role;
import model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthFilterTest {

    @Test
    public void testRolePermissionsMatrix() {
        User admin = new User(1, "admin", "Admin", Role.ADMIN, "admin@test.com", "en");
        User teacher = new User(2, "teacher", "Teacher", Role.TEACHER, "teacher@test.com", "en");
        User student = new User(3, "student", "Student", Role.STUDENT, "student@test.com", "en");

        assertEquals(Role.ADMIN, admin.getRole());
        assertEquals(Role.TEACHER, teacher.getRole());
        assertEquals(Role.STUDENT, student.getRole());

        // Test Role conversions
        assertEquals(Role.ADMIN, Role.fromString("ADMIN"));
        assertEquals(Role.TEACHER, Role.fromString("TEACHER"));
        assertEquals(Role.STUDENT, Role.fromString("STUDENT"));
        assertEquals(Role.PRINCIPAL, Role.fromString("PRINCIPAL"));
        assertEquals(Role.REGISTRAR, Role.fromString("REGISTRAR"));
        assertEquals(Role.ATTENDANCE_OFFICER, Role.fromString("ATTENDANCE_OFFICER"));

        // Bilingual labels
        assertEquals("Administrator", Role.ADMIN.getLocalizedLabel("en"));
        assertEquals("አስተዳዳሪ", Role.ADMIN.getLocalizedLabel("am"));
        assertEquals("Teacher", Role.TEACHER.getLocalizedLabel("en"));
        assertEquals("መምህር/ት", Role.TEACHER.getLocalizedLabel("am"));
        assertEquals("Student", Role.STUDENT.getLocalizedLabel("en"));
        assertEquals("ተማሪ", Role.STUDENT.getLocalizedLabel("am"));
    }
}
