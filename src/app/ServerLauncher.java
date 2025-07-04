package app;

import com.sun.net.httpserver.HttpServer;
import handler.*;
import util.SessionManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Driver;
import java.util.Enumeration;

public class ServerLauncher {
    public static void main(String[] args) throws IOException {
        Enumeration<Driver> e = java.sql.DriverManager.getDrivers();
        while (e.hasMoreElements()) {
            System.out.println(e.nextElement().getClass().getName());
        }
        int PORT = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        SessionManager.init();
        server.createContext("/register", new RegisterHandler());
        server.createContext("/login",    new LoginHandler());
        server.createContext("/logout",   new LogoutHandler());
        server.createContext("/profile",  new ProfileHandler());
        server.createContext("/captcha",  new CaptchaImageHandler());
        server.setExecutor(null);
        server.start();
        System.out.printf("Server started on http://localhost:%d%n", PORT);
    }
}
