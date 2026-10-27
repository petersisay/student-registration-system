package util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class I18nUtilTest {

    @Test
    public void testEnglishAndAmharicTranslations() {
        // Test English
        assertEquals("Sign In", I18nUtil.getMessage("en", "auth.login"));
        assertEquals("Dashboard", I18nUtil.getMessage("en", "nav.dashboard"));
        assertEquals("Register Student", I18nUtil.getMessage("en", "nav.student_register"));
        assertEquals("Present", I18nUtil.getMessage("en", "attendance.present"));

        // Test Amharic (Ethiopic characters UTF-8 verification)
        assertEquals("ግባ", I18nUtil.getMessage("am", "auth.login"));
        assertEquals("ዋና ዳሽቦርድ", I18nUtil.getMessage("am", "nav.dashboard"));
        assertEquals("አዲስ ተማሪ መዝግብ", I18nUtil.getMessage("am", "nav.student_register"));
        assertEquals("ተገኝቷል", I18nUtil.getMessage("am", "attendance.present"));

        // Fallback test on non-existent key
        assertEquals("non.existent.key", I18nUtil.getMessage("en", "non.existent.key"));
    }
}
