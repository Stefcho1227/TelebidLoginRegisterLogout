package utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

class PasswordHasherTest {
    @Test
    void hashAndMatch_positive() {
        String pwd   = "Abcdef1!";
        String hash  = PasswordHasher.hash(pwd);
        Assertions.assertTrue(PasswordHasher.matches(pwd, hash),
                "The password must match the hash just generated.");
    }
    @Test
    void hashAndMatch_negative() {
        String hash = PasswordHasher.hash("Correct1!");
        Assertions.assertFalse(PasswordHasher.matches("Wrong1!", hash),
                "Wrong password should not match the hash");
    }
    @RepeatedTest(3)
    void samePasswordProducesDifferentHashes() {
        String pwd   = "SamePass1!";
        String hash1 = PasswordHasher.hash(pwd);
        String hash2 = PasswordHasher.hash(pwd);
        Assertions.assertNotEquals(hash1, hash2,
                "The same password should result in different hashes (different salt)");
    }
    @Test
    void constantTimeEquals_trueForEqualArrays() throws Exception {
        byte[] a = {1,2,3};
        byte[] b = {1,2,3};
        Assertions.assertTrue(invokeConstEq(a,b));
    }
    @Test
    void constantTimeEquals_falseForDiffArrays() throws Exception {
        byte[] a = {1,2,3};
        byte[] b = {1,2,4};
        Assertions.assertFalse(invokeConstEq(a,b));
    }
    private static boolean invokeConstEq(byte[] a, byte[] b) throws Exception {
        Method m = PasswordHasher.class.getDeclaredMethod("constantTimeEquals", byte[].class, byte[].class);
        m.setAccessible(true);
        return (boolean) m.invoke(null, a, b);
    }
}
