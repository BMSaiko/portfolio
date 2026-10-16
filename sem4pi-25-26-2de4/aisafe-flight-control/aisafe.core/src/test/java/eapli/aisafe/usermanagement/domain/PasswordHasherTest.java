package eapli.aisafe.usermanagement.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PasswordHasherTest {

    @Test
    void hashPasswordReturnsNonNull() {
        String hash = PasswordHasher.hashPassword("MyPass123");
        assertNotNull(hash);
        String[] parts = hash.split(":");
        assertEquals(3, parts.length);
    }

    @Test
    void samePasswordProducesDifferentHashes() {
        String pwd = "MyPass123";
        String hash1 = PasswordHasher.hashPassword(pwd);
        String hash2 = PasswordHasher.hashPassword(pwd);
        assertNotEquals(hash1, hash2);
    }

    @Test
    void verifyPasswordCorrect() {
        String pwd = "MyPass123";
        String hash = PasswordHasher.hashPassword(pwd);
        assertTrue(PasswordHasher.verifyPassword(pwd, hash));
    }

    @Test
    void verifyPasswordIncorrect() {
        String hash = PasswordHasher.hashPassword("MyPass123");
        assertFalse(PasswordHasher.verifyPassword("WrongPass", hash));
    }

    @Test
    void verifyPasswordWithMalformedHashThrows() {
        assertThrows(RuntimeException.class, () ->
                PasswordHasher.verifyPassword("pass", "invalid:format")
        );
    }
}