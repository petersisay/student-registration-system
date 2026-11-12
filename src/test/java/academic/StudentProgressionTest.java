package academic;

import dao.StudentDAO;
import model.Student;
import model.StudentEnrollment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.DatabaseMigration;

import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class StudentProgressionTest {

    @Test
    @DisplayName("Preserve historical enrollment records when student is promoted from Grade 1 to Grade 2")
    void testStudentPromotionPreservesHistoricalEnrollments() {
        assumeTrue(util.DBConnection.isAvailable(), "Skipping test: Database is not reachable in current environment.");
        DatabaseMigration.runMigrations();

        StudentDAO studentDAO = new StudentDAO();
        
        // Create an isolated test student for this progression test
        Student testStudent = new Student();
        testStudent.setStudentIdNumber("TEST-STU-" + System.currentTimeMillis() % 10000);
        testStudent.setFullNameEn("Abebe Bikila");
        testStudent.setFullNameAm("አበበ ቢቂላ");
        testStudent.setName("Abebe Bikila");
        testStudent.setEmail("abebe." + System.currentTimeMillis() % 10000 + "@example.com");
        testStudent.setYear(1);
        testStudent.setDateOfBirth(Date.valueOf("2016-05-12"));
        testStudent.setGender("MALE");
        testStudent.setEmergencyContactName("Bikila Demo");
        testStudent.setEmergencyContactPhone("+251911223344");
        testStudent.setAdmissionDate(Date.valueOf("2024-09-01"));
        testStudent.setStatus("ACTIVE");

        int studentId = studentDAO.create(testStudent);
        assertTrue(studentId > 0, "Student creation should return generated ID");

        // Enroll in Grade 1, Section 1 (2024/2025 Academic Year ID 1)
        boolean enrolled = studentDAO.enrollStudent(studentId, 1, 1, 1, "ACTIVE");
        assertTrue(enrolled, "Initial enrollment must succeed");

        // Verify initial enrollment
        List<StudentEnrollment> initialHistory = studentDAO.getEnrollmentHistory(studentId);
        assertNotNull(initialHistory);
        assertEquals(1, initialHistory.size(), "Student should have exactly 1 active enrollment");
        assertEquals("ACTIVE", initialHistory.get(0).getStatus());

        // Promote student: 2026/2027 (Year 1) -> 2027/2028 (Year 2), Grade 1 -> Grade 2, Section A (10)
        boolean promoted = studentDAO.promoteStudent(studentId, 1, 2, 2, 10);
        assertTrue(promoted, "Student promotion workflow should succeed");

        // Verify updated enrollment history
        List<StudentEnrollment> postPromotionHistory = studentDAO.getEnrollmentHistory(studentId);
        assertNotNull(postPromotionHistory);
        assertEquals(2, postPromotionHistory.size(),
                "Progression must add a new enrollment without deleting previous records");

        // Verify that the historical record is preserved as PROMOTED and new record is ACTIVE
        boolean foundPromoted = false;
        boolean foundActiveNextYear = false;

        for (StudentEnrollment se : postPromotionHistory) {
            if (se.getAcademicYearId() == 1) {
                assertEquals("PROMOTED", se.getStatus(), "Previous academic year enrollment must be marked PROMOTED");
                foundPromoted = true;
            }
            if (se.getAcademicYearId() == 2) {
                assertEquals("ACTIVE", se.getStatus(), "Next academic year enrollment must be ACTIVE");
                foundActiveNextYear = true;
            }
        }

        assertTrue(foundPromoted, "Historical Grade 1 enrollment was safely preserved as PROMOTED");
        assertTrue(foundActiveNextYear, "New Grade 2 enrollment is active for the new academic year");
    }
}
