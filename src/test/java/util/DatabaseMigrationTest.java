package util;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class DatabaseMigrationTest {

    @Test
    public void testDatabaseMigrationAndSeedData() {
        assumeTrue(util.DBConnection.isAvailable(), "Skipping test: Database is not reachable in current environment.");
        // Run migration
        DatabaseMigration.runMigrations();

        // Verify connection and verify tables exist
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            assertNotNull(conn, "Database connection should not be null");

            // Verify schools table
            try (ResultSet rs = stmt.executeQuery("SELECT count(*) FROM schools")) {
                assertTrue(rs.next());
                assertTrue(rs.getInt(1) >= 1, "Should have at least 1 school record");
            }

            // Verify grades 1 to 8
            try (ResultSet rs = stmt.executeQuery("SELECT count(*) FROM grade_levels")) {
                assertTrue(rs.next());
                assertEquals(8, rs.getInt(1), "Should have exactly 8 grade levels (Grades 1-8)");
            }

            // Verify users seeded
            try (ResultSet rs = stmt.executeQuery("SELECT count(*) FROM users WHERE username = 'admin'")) {
                assertTrue(rs.next());
                assertEquals(1, rs.getInt(1), "Should have seeded admin user");
            }

            // Verify subjects seeded
            try (ResultSet rs = stmt.executeQuery("SELECT count(*) FROM subjects")) {
                assertTrue(rs.next());
                assertTrue(rs.getInt(1) >= 10, "Should have seeded curriculum subjects");
            }

        } catch (Exception e) {
            System.err.println("Migration test notice (MySQL may not be running or credentials differ): " + e.getMessage());
            // Fail if an unexpected error occurs
            fail("Migration test encountered error: " + e.getMessage());
        }
    }
}
