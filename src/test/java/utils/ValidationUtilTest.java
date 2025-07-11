package utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ValidationUtilTest {
    @Test
    void validEmails() {
        Assertions.assertTrue(ValidationUtil.isValidEmail("john.doe@example.com"));
        Assertions.assertTrue(ValidationUtil.isValidEmail("a_b-c+1@sub.domain.co.uk"));
        Assertions.assertTrue(ValidationUtil.isValidEmail("x@x.io"));
    }
    @Test
    void invalidEmails() {
        Assertions.assertFalse(ValidationUtil.isValidEmail(null));
        Assertions.assertFalse(ValidationUtil.isValidEmail(""));
        Assertions.assertFalse(ValidationUtil.isValidEmail("plainaddress"));
        Assertions.assertFalse(ValidationUtil.isValidEmail("missing-at-sign.com"));
        Assertions.assertFalse(ValidationUtil.isValidEmail("user@bad@host.com"));
        Assertions.assertFalse(ValidationUtil.isValidEmail("user@host"));
    }
    @Test
    void validNames() {
        Assertions.assertTrue(ValidationUtil.isValidName("Иван", 2, 30));
        Assertions.assertTrue(ValidationUtil.isValidName("Jean-Luc Picard", 2, 30));
        Assertions.assertTrue(ValidationUtil.isValidName("O'Connor", 2, 30));
    }
    @Test
    void invalidNames() {
        Assertions.assertFalse(ValidationUtil.isValidName(null, 2, 20));
        Assertions.assertFalse(ValidationUtil.isValidName("A", 2, 20));
        Assertions.assertFalse(ValidationUtil.isValidName("ThisNameIsWayTooLongForLimit", 2, 20));
        Assertions.assertFalse(ValidationUtil.isValidName("Bad#Name", 2, 20));
        Assertions.assertFalse(ValidationUtil.isValidName("   ", 2, 20));
    }
    @Test
    void strongPasswords() {
        Assertions.assertTrue(ValidationUtil.isStrongPassword("Abcdef1!"));
        Assertions.assertTrue(ValidationUtil.isStrongPassword("Str0ng#Pass"));
        Assertions.assertTrue(ValidationUtil.isStrongPassword("Qwerty1%"));
    }
    @Test
    void weakPasswords() {
        Assertions.assertFalse(ValidationUtil.isStrongPassword(null));
        Assertions.assertFalse(ValidationUtil.isStrongPassword("short1!"));
        Assertions.assertFalse(ValidationUtil.isStrongPassword("nouppercase1!"));
        Assertions.assertFalse(ValidationUtil.isStrongPassword("NOLOWERCASE1!"));
        Assertions.assertFalse(ValidationUtil.isStrongPassword("NoNumber!"));
        Assertions.assertFalse(ValidationUtil.isStrongPassword("NoSpecial1"));
    }
}
