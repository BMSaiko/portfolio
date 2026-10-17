package eapli.aisafe.usermanagement.domain;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * Utility class responsible for hashing and verifying passwords.
 *
 * <p>
 * This class supports US030 - Authentication and Authorization by ensuring that
 * user passwords are not stored in plain text.
 * </p>
 *
 * <p>
 * Passwords are hashed using PBKDF2 with HMAC SHA-256, a random salt and a
 * fixed number of iterations. The stored format is:
 * </p>
 *
 * <pre>
 * iterations:salt:hash
 * </pre>
 */
public final class PasswordHasher {

    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;

    // Reutilizar uma única instância de SecureRandom (thread-safe) para melhor
    // desempenho e aleatoriedade.
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private PasswordHasher() {}

    /**
     * Hashes a plain text password.
     *
     * @param password plain text password
     * @return encoded password hash in the format iterations:salt:hash
     * @throws IllegalStateException if the hashing algorithm is not available
     */
    public static String hashPassword(String password) {
        try {
            byte[] salt = generateSalt();
            byte[] hash = pbkdf2(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            return ITERATIONS + ":" + encode(salt) + ":" + encode(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            // A falha do algoritmo PBKDF2 é um erro de configuração do ambiente Java.
            throw new IllegalStateException("Password hashing algorithm not available", e);
        }
    }

    /**
     * Verifies a plain text password against a stored hash.
     *
     * @param password plain text password
     * @param storedHash stored password hash
     * @return {@code true} if the password matches the stored hash
     * @throws IllegalStateException if the hashing algorithm is not available
     * @throws IllegalArgumentException if the stored hash has an invalid format
     */
    public static boolean verifyPassword(String password, String storedHash) {
        try {
            String[] parts = storedHash.split(":");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid stored hash format");
            }
            int iterations = Integer.parseInt(parts[0]);
            byte[] salt = decode(parts[1]);
            byte[] hash = decode(parts[2]);
            byte[] testHash = pbkdf2(password.toCharArray(), salt, iterations, KEY_LENGTH);
            return slowEquals(hash, testHash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Password verification algorithm not available", e);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid iteration count in stored hash", e);
        }
    }

    /**
     * Generates a PBKDF2 hash.
     *
     * @param password password characters
     * @param salt random salt
     * @param iterations number of iterations
     * @param keyLength key length in bits
     * @return generated hash bytes
     * @throws NoSuchAlgorithmException if the PBKDF2 algorithm is unavailable
     * @throws InvalidKeySpecException if the key specification is invalid
     */
    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLength)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return skf.generateSecret(spec).getEncoded();
    }

    /**
     * Generates a random salt.
     *
     * @return random salt bytes
     */
    private static byte[] generateSalt() {
        byte[] salt = new byte[16];
        SECURE_RANDOM.nextBytes(salt);
        return salt;
    }

    /**
     * Encodes bytes using Base64.
     *
     * @param bytes bytes to encode
     * @return Base64 text
     */
    private static String encode(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Decodes Base64 text into bytes.
     *
     * @param str Base64 text
     * @return decoded bytes
     */
    private static byte[] decode(String str) {
        return Base64.getDecoder().decode(str);
    }

    /**
     * Compares two byte arrays in a way that reduces timing attack risks.
     *
     * @param a first byte array
     * @param b second byte array
     * @return {@code true} if both byte arrays have the same content
     */
    private static boolean slowEquals(byte[] a, byte[] b) {
        if (a.length != b.length) return false;
        int diff = 0;
        for (int i = 0; i < a.length; i++) {
            diff |= a[i] ^ b[i];
        }
        return diff == 0;
    }
}