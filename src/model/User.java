package model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public final class User {
    private final int id;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String passwordHash;
    public User(int id, String email, String firstName, String lastName, String passwordHash) {
        this.id = id;
        this.email = Objects.requireNonNull(email);
        this.firstName = Objects.requireNonNull(firstName);
        this.lastName = Objects.requireNonNull(lastName);
        this.passwordHash = Objects.requireNonNull(passwordHash);
    }
    public static User of(ResultSet rs) throws SQLException {
        return new User(rs.getInt("id"), rs.getString("email"), rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("password_hash"));
    }
    public int getId() {
        return id;
    }
    public String getEmail() {
        return email;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getPasswordHash() {
        return passwordHash;
    }
    public String fullName() {
        return firstName + " " + lastName;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)){
            return false;
        }
        User other = (User) o;
        return id == other.id && email.equals(other.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return "User{id=" + id +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
