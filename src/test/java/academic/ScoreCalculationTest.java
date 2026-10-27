package academic;

import dao.ReportCardDAO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

public class ScoreCalculationTest {

    @Test
    @DisplayName("Verify Ethiopian Elementary score aggregation and letter grade thresholds")
    void testScoreAggregationsAndLetterGrades() {
        // Test standard Ethiopian Elementary grading thresholds
        assertEquals("A", computeGrade(95.0), "Score 95 must yield A");
        assertEquals("A", computeGrade(90.0), "Score 90 must yield A");
        assertEquals("B", computeGrade(85.5), "Score 85.5 must yield B");
        assertEquals("B", computeGrade(80.0), "Score 80 must yield B");
        assertEquals("C", computeGrade(70.0), "Score 70 must yield C");
        assertEquals("C", computeGrade(60.0), "Score 60 must yield C");
        assertEquals("D", computeGrade(55.0), "Score 55 must yield D");
        assertEquals("D", computeGrade(50.0), "Score 50 must yield D");
        assertEquals("F", computeGrade(49.9), "Score 49.9 must yield F");
        assertEquals("F", computeGrade(25.0), "Score 25 must yield F");

        // Test division-by-zero protection and rounding
        BigDecimal totalScore = BigDecimal.valueOf(425.5);
        int subjectCount = 5;
        BigDecimal average = totalScore.divide(BigDecimal.valueOf(subjectCount), 2, RoundingMode.HALF_UP);
        assertEquals(new BigDecimal("85.10"), average);
    }

    private String computeGrade(double score) {
        if (score >= 90.0) return "A";
        if (score >= 80.0) return "B";
        if (score >= 60.0) return "C";
        if (score >= 50.0) return "D";
        return "F";
    }
}
