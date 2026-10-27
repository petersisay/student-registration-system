package util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordUtilTest {

    @Test
    @DisplayName("Verify PBKDF2WithHmacSHA256 hashing, unique salts, and verification")
    void testPbkdf2PasswordHashing() {
        String password = "SecureAdmin@2026!";
        String salt1 = PasswordUtil.generateSalt();
        String salt2 = PasswordUtil.generateSalt();

        assertNotNull(salt1);
        assertNotNull(salt2);
        assertNotEquals(salt1, salt2, "Cryptographic salts must be unique");

        String hash1 = PasswordUtil.hashPassword(password, salt1);
        String hash2 = PasswordUtil.hashPassword(password, salt2);

        assertNotNull(hash1);
        assertNotNull(hash2);
        assertNotEquals(hash1, hash2, "Hashes of the same password with different salts must differ");

        // Verify correct password succeeds
        assertTrue(PasswordUtil.verifyPassword(password, hash1, salt1), "Valid password must verify successfully");

        // Verify incorrect password fails
        assertFalse(PasswordUtil.verifyPassword("WrongPassword123", hash1, salt1), "Incorrect password must be rejected");

        // Verify null handling
        assertFalse(PasswordUtil.verifyPassword(null, hash1, salt1));
        assertFalse(PasswordUtil.verifyPassword(password, null, salt1));
        assertFalse(PasswordUtil.verifyPassword(password, hash1, null));
    }
}
