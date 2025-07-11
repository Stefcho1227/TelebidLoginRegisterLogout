package util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public final class PasswordHasher {
    private static final SecureRandom RAND = new SecureRandom();
    private static final int SALT_LEN = 16;
    private static final int HASH_LEN = 32;
    private static final int ITERATIONS = 100000;
    public static String hash(String password) {
        byte[] salt = new byte[SALT_LEN];
        RAND.nextBytes(salt);
        byte[] hash = pbkdf2(password.toCharArray(), salt);
        return Base64.getEncoder().encodeToString(salt) + ':' + Base64.getEncoder().encodeToString(hash);
    }
    public static boolean matches(String password, String stored) {
        String[] parts = stored.split(":");
        if (parts.length != 2) {
            return false;
        }
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] hash = Base64.getDecoder().decode(parts[1]);
        byte[] test = pbkdf2(password.toCharArray(), salt);
        return constantTimeEquals(hash, test);
    }


    private static byte[] pbkdf2(char[] pwd, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(pwd, salt, ITERATIONS, HASH_LEN * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return skf.generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException("PBKDF2 failure", e);
        }
    }

    private static boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a.length != b.length) return false;
        int res = 0;
        for (int i = 0; i < a.length; i++) res |= a[i] ^ b[i];
        return res == 0;
    }

    private PasswordHasher() {}
}
