package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility to load environment variables and local .env configuration files.
 * Protects credentials, API keys, and sensitive secrets by enabling them
 * to be stored in an untracked local .env file instead of hardcoded in source.
 */
public class EnvUtil {

    private static final Map<String, String> DOTENV = new HashMap<>();
    private static boolean loaded = false;

    private static synchronized void loadDotEnv() {
        if (loaded) {
            return;
        }
        loaded = true;

        File[] candidates = new File[] {
            new File(".env"),
            new File(System.getProperty("user.dir"), ".env")
        };

        for (File envFile : candidates) {
            if (envFile.exists() && envFile.isFile()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(envFile, StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty() || line.startsWith("#")) {
                            continue;
                        }
                        int eqIdx = line.indexOf('=');
                        if (eqIdx > 0) {
                            String key = line.substring(0, eqIdx).trim();
                            String val = line.substring(eqIdx + 1).trim();
                            if ((val.startsWith("\"") && val.endsWith("\"")) || (val.startsWith("'") && val.endsWith("'"))) {
                                if (val.length() >= 2) {
                                    val = val.substring(1, val.length() - 1);
                                }
                            }
                            DOTENV.putIfAbsent(key, val);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("[EnvUtil] Warning: Unable to parse .env file: " + e.getMessage());
                }
                break;
            }
        }
    }

    /**
     * Resolves a configuration value by key.
     * Precedence:
     * 1. OS System Environment Variable (System.getenv)
     * 2. JVM System Property (System.getProperty)
     * 3. Local .env file
     */
    public static String get(String key) {
        String sysEnv = System.getenv(key);
        if (sysEnv != null && !sysEnv.trim().isEmpty()) {
            return sysEnv.trim();
        }
        String sysProp = System.getProperty(key);
        if (sysProp != null && !sysProp.trim().isEmpty()) {
            return sysProp.trim();
        }
        loadDotEnv();
        return DOTENV.get(key);
    }

    /**
     * Resolves a configuration value by key with a default fallback.
     */
    public static String get(String key, String defaultValue) {
        String val = get(key);
        return (val != null && !val.trim().isEmpty()) ? val : defaultValue;
    }

    /**
     * Checks if a boolean flag is set to true.
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
        String val = get(key);
        if (val == null || val.trim().isEmpty()) {
            return defaultValue;
        }
        return "true".equalsIgnoreCase(val.trim()) || "1".equals(val.trim());
    }
}
