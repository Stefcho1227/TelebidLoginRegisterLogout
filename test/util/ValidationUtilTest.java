// src/test/java/util/ValidationUtilTest.java
package util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilTest {
    @Test
    void validEmails() {
        assertTrue(ValidationUtil.isValidEmail("john.doe@example.com"));
        assertTrue(ValidationUtil.isValidEmail("a_b-c+1@sub.domain.co.uk"));
        assertTrue(ValidationUtil.isValidEmail("x@x.io"));
    }
    @Test
    void invalidEmails() {
        assertFalse(ValidationUtil.isValidEmail(null));
        assertFalse(ValidationUtil.isValidEmail(""));
        assertFalse(ValidationUtil.isValidEmail("plainaddress"));
        assertFalse(ValidationUtil.isValidEmail("missing-at-sign.com"));
        assertFalse(ValidationUtil.isValidEmail("user@bad@host.com"));
        assertFalse(ValidationUtil.isValidEmail("user@host"));
    }
    @Test
    void validNames() {
        assertTrue(ValidationUtil.isValidName("Иван", 2, 30));
        assertTrue(ValidationUtil.isValidName("Jean-Luc Picard", 2, 30));
        assertTrue(ValidationUtil.isValidName("O'Connor", 2, 30));
    }
    @Test
    void invalidNames() {
        assertFalse(ValidationUtil.isValidName(null, 2, 20));
        assertFalse(ValidationUtil.isValidName("A", 2, 20));
        assertFalse(ValidationUtil.isValidName("ThisNameIsWayTooLongForLimit", 2, 20));
        assertFalse(ValidationUtil.isValidName("Bad#Name", 2, 20));
        assertFalse(ValidationUtil.isValidName("   ", 2, 20));
    }
    @Test
    void strongPasswords() {
        assertTrue(ValidationUtil.isStrongPassword("Abcdef1!"));
        assertTrue(ValidationUtil.isStrongPassword("Str0ng#Pass"));
        assertTrue(ValidationUtil.isStrongPassword("Qwerty1%"));
    }
    @Test
    void weakPasswords() {
        assertFalse(ValidationUtil.isStrongPassword(null));
        assertFalse(ValidationUtil.isStrongPassword("short1!"));
        assertFalse(ValidationUtil.isStrongPassword("nouppercase1!"));
        assertFalse(ValidationUtil.isStrongPassword("NOLOWERCASE1!"));
        assertFalse(ValidationUtil.isStrongPassword("NoNumber!"));
        assertFalse(ValidationUtil.isStrongPassword("NoSpecial1"));
    }
}
