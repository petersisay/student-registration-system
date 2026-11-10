package util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EnvUtilTest {

    @Test
    void testEnvResolutionAndDefaults() {
        // Test fallback defaults
        String testVal = EnvUtil.get("NON_EXISTENT_TEST_KEY_12345", "default_val");
        assertEquals("default_val", testVal);

        boolean boolVal = EnvUtil.getBoolean("NON_EXISTENT_BOOL_KEY", true);
        assertTrue(boolVal);

        boolean boolValFalse = EnvUtil.getBoolean("NON_EXISTENT_BOOL_KEY", false);
        assertFalse(boolValFalse);

        // Test known JVM/OS property
        String path = EnvUtil.get("PATH");
        assertNotNull(path, "PATH environment variable should be readable");
    }
}
