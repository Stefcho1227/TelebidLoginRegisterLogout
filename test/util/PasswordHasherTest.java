package util;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class PasswordHasherTest {
    @Test
    void hashAndMatch_positive() {
        String pwd   = "Abcdef1!";
        String hash  = PasswordHasher.hash(pwd);
        assertTrue(PasswordHasher.matches(pwd, hash),
                "The password must match the hash just generated.");
    }
    @Test
    void hashAndMatch_negative() {
        String hash = PasswordHasher.hash("Correct1!");
        assertFalse(PasswordHasher.matches("Wrong1!", hash),
                "Wrong password should not match the hash");
    }
    @RepeatedTest(3)
    void samePasswordProducesDifferentHashes() {
        String pwd   = "SamePass1!";
        String hash1 = PasswordHasher.hash(pwd);
        String hash2 = PasswordHasher.hash(pwd);
        assertNotEquals(hash1, hash2,
                "The same password should result in different hashes (different salt)");
    }
    @Test
    void constantTimeEquals_trueForEqualArrays() throws Exception {
        byte[] a = {1,2,3};
        byte[] b = {1,2,3};
        assertTrue(invokeConstEq(a,b));
    }
    @Test
    void constantTimeEquals_falseForDiffArrays() throws Exception {
        byte[] a = {1,2,3};
        byte[] b = {1,2,4};
        assertFalse(invokeConstEq(a,b));
    }
    private static boolean invokeConstEq(byte[] a, byte[] b) throws Exception {
        Method m = PasswordHasher.class.getDeclaredMethod("constantTimeEquals", byte[].class, byte[].class);
        m.setAccessible(true);
        return (boolean) m.invoke(null, a, b);
    }
}
