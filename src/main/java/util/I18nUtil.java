package util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Utility for loading and serving internationalized messages (English and Amharic).
 * Supports UTF-8 resource bundle parsing and dynamic locale resolution.
 */
public class I18nUtil {

    private static final Map<String, Properties> BUNDLES = new HashMap<>();

    static {
        loadBundle("en", "/messages_en.properties");
        loadBundle("am", "/messages_am.properties");
    }

    private static void loadBundle(String locale, String path) {
        Properties props = new Properties();
        try (InputStream is = I18nUtil.class.getResourceAsStream(path)) {
            if (is != null) {
                try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                    props.load(reader);
                    BUNDLES.put(locale, props);
                }
            } else {
                System.err.println("[I18nUtil] Could not locate resource file: " + path);
            }
        } catch (Exception e) {
            System.err.println("[I18nUtil] Error loading bundle for " + locale + ": " + e.getMessage());
        }
    }

    public static String getMessage(String locale, String key, Object... args) {
        String lang = (locale != null && locale.equalsIgnoreCase("am")) ? "am" : "en";
        Properties props = BUNDLES.get(lang);
        String val = null;
        if (props != null) {
            val = props.getProperty(key);
        }

        // Fallback to English if missing in Amharic
        if (val == null && !"en".equals(lang)) {
            Properties enProps = BUNDLES.get("en");
            if (enProps != null) {
                val = enProps.getProperty(key);
            }
        }

        if (val == null) {
            return key;
        }

        if (args != null && args.length > 0) {
            return MessageFormat.format(val, args);
        }
        return val;
    }

    public static String getMessage(HttpServletRequest request, String key, Object... args) {
        String locale = "en";
        if (request != null) {
            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute("currentLocale") != null) {
                locale = (String) session.getAttribute("currentLocale");
            }
        }
        return getMessage(locale, key, args);
    }
}
