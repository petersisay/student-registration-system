package util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * Enterprise cryptographic utility for password hashing and verification.
 * Implements PBKDF2WithHmacSHA256 with 65,536 iterations and 128-bit cryptographic salt
 * conforming to NIST SP 800-63B guidelines, with constant-time equality checks and
 * transparent backwards-compatible verification for legacy SHA-256 hashes.
 */
public class PasswordUtil {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    /**
     * Generates a 16-byte (128-bit) cryptographically strong random salt, Base64-encoded.
     */
    public static String generateSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Hashes a password using PBKDF2WithHmacSHA256 with 65,536 iterations and 256-bit output.
     */
    public static String hashPassword(String password, String salt) {
        if (password == null || salt == null) {
            throw new IllegalArgumentException("Password and salt must not be null");
        }
        try {
            byte[] saltBytes = Base64.getDecoder().decode(salt);
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("PBKDF2 algorithm configuration error", e);
        }
    }

    /**
     * Verifies that the entered password matches the stored hash given its salt.
     * Uses constant-time comparison to prevent side-channel timing attacks.
     * Supports both PBKDF2 and legacy SHA-256 hashes for seamless migration.
     */
    public static boolean verifyPassword(String enteredPassword, String storedHash, String salt) {
        if (enteredPassword == null || storedHash == null || salt == null) {
            return false;
        }

        // 1. Try PBKDF2 verification first
        try {
            String calculatedPbkdf2 = hashPassword(enteredPassword, salt);
            if (MessageDigest.isEqual(calculatedPbkdf2.getBytes(StandardCharsets.UTF_8),
                                      storedHash.getBytes(StandardCharsets.UTF_8))) {
                return true;
            }
        } catch (Exception ignored) {}

        // 2. Fallback to legacy SHA-256 for existing records
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] legacyHash = digest.digest(enteredPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : legacyHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return MessageDigest.isEqual(hexString.toString().getBytes(StandardCharsets.UTF_8),
                                         storedHash.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            return false;
        }
    }
}
