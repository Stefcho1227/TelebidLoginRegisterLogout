// src/test/java/model/UserTest.java
package model;

import org.junit.jupiter.api.*;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    @Test
    void constructorAndGetters() {
        User u = new User(1, "john@doe.com", "John", "Doe", "hash123");

        assertAll(
                () -> assertEquals(1,              u.getId()),
                () -> assertEquals("john@doe.com", u.getEmail()),
                () -> assertEquals("John",         u.getFirstName()),
                () -> assertEquals("Doe",          u.getLastName()),
                () -> assertEquals("hash123",      u.getPasswordHash()),
                () -> assertEquals("John Doe",     u.fullName())
        );
    }
    @Test
    void equalsAndHashCodeBasedOnIdAndEmail() {
        User ref   = new User(5, "eq@test", "A", "B", "h1");
        User same  = new User(5, "eq@test", "X", "Y", "h2");
        User diff1 = new User(6, "eq@test", "A", "B", "h1");
        User diff2 = new User(5, "other@test", "A", "B", "h1");
        assertEquals(ref, same);
        assertEquals(ref.hashCode(), same.hashCode());
        assertNotEquals(ref, diff1);
        assertNotEquals(ref, diff2);
    }
    @Test
    void toStringContainsIdAndEmail() {
        User u = new User(7, "x@y.com", "F", "L", "h");
        String s = u.toString();
        assertTrue(s.contains("7"));
        assertTrue(s.contains("x@y.com"));
    }
    @Test
    void ofBuildsUserFromResultSet() throws Exception {
        try (Connection c = DriverManager.getConnection("jdbc:h2:mem:one;DB_CLOSE_DELAY=-1");
             Statement  st = c.createStatement()) {
            ResultSet rs = st.executeQuery("""
                    SELECT  10  AS id,
                            'a@b.com'     AS email,
                            'Jane'        AS first_name,
                            'Smith'       AS last_name,
                            'hashXYZ'     AS password_hash
                    """);
            assertTrue(rs.next(), "ResultSet трябва да има един ред");
            User u = User.of(rs);
            assertAll(
                    () -> assertEquals(10,      u.getId()),
                    () -> assertEquals("a@b.com", u.getEmail()),
                    () -> assertEquals("Jane",  u.getFirstName()),
                    () -> assertEquals("Smith", u.getLastName()),
                    () -> assertEquals("hashXYZ", u.getPasswordHash())
            );
        }
    }
}
