package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class UserTest {
    @Test
    @DisplayName("Constructor populates getters and fullName()")
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
    void equalsAndHashCode() {
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
    void ofBuildsUserFromResultSet() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getInt   ("id"))            .thenReturn(10);
        when(rs.getString("email"))         .thenReturn("a@b.com");
        when(rs.getString("first_name"))    .thenReturn("Jane");
        when(rs.getString("last_name"))     .thenReturn("Smith");
        when(rs.getString("password_hash")) .thenReturn("hashXYZ");
        User u = User.of(rs);
        assertAll(
                () -> assertEquals(10,          u.getId()),
                () -> assertEquals("a@b.com",   u.getEmail()),
                () -> assertEquals("Jane",      u.getFirstName()),
                () -> assertEquals("Smith",     u.getLastName()),
                () -> assertEquals("hashXYZ",   u.getPasswordHash())
        );
    }
}
