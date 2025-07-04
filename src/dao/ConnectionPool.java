package dao;

import java.sql.*;
import java.util.concurrent.*;
public final class ConnectionPool implements AutoCloseable {
    private static final int SIZE = 10;
    private static final BlockingQueue<Connection> pool = new LinkedBlockingQueue<>(SIZE);
    static {
        try {
            for (int i = 0; i < SIZE; i++) {
                pool.put(DriverManager.getConnection(System.getenv("DB_URL"),
                        System.getenv("DB_USER"),
                        System.getenv("DB_PASS")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
    }
    public static Connection take() {
        try {
            return pool.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for DB connection", e);
        }
    }
    public static void release(Connection c) {
        pool.offer(c);
    }
    @Override public void close() {}
}
