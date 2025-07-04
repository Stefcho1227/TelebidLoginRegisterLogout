package dao;

import model.User;

import java.sql.*;

public class UserDao implements AutoCloseable {
    private final Connection c = ConnectionPool.take();
    public boolean emailExists(String mail) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT 1 FROM users WHERE email=?")) {
            ps.setString(1, mail);
            return ps.executeQuery().next();
        }
    }
    public void insertUser(String mail, String first, String last, String hash)
            throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO users(email, first_name, last_name, password_hash) VALUES(?,?,?,?)")) {
            ps.setString(1, mail);
            ps.setString(2, first);
            ps.setString(3, last);
            ps.setString(4, hash);
            ps.executeUpdate();
        }
    }
    public User findByEmail(String mail) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT * FROM users WHERE email=?")) {
            ps.setString(1, mail);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? User.of(rs) : null;
        }
    }
    public User findById(int id) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT * FROM users WHERE id=?")) {
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? User.of(rs) : null;
        }
    }
    public void updateNames(int uid, String f, String l) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("UPDATE users SET first_name=?, last_name=? WHERE id=?")) {
            ps.setString(1,f);
            ps.setString(2,l);
            ps.setInt(3,uid);
            ps.executeUpdate();
        }
    }
    public void updatePassword(int uid, String hash) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("UPDATE users SET password_hash=? WHERE id=?")) {
            ps.setString(1,hash);
            ps.setInt(2,uid);
            ps.executeUpdate();
        }
    }
    @Override public void close() { ConnectionPool.release(c); }
}
