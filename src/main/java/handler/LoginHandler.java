package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.UserDao;
import utils.*;
import web.FormParser;

import java.io.IOException;
import java.util.Map;

public class LoginHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) {
            HttpUtil.renderTemplate(ex, "login.html", Map.of("error", "", "msg", ""));
            return;
        }
        Map<String, String> form = FormParser.parse(ex.getRequestBody());
        String email = form.get("email");
        String pw = form.get("password");
        try (UserDao dao = new UserDao()) {
            var user = dao.findByEmail(email);
            if (user == null || !PasswordHasher.matches(pw, user.getPasswordHash())) {
                HttpUtil.renderTemplate(ex, "login.html",
                        Map.of("error", "Wrong credentials"));
                return;
            }
            String sid = SessionManager.createSession(user.getId());
            HttpUtil.setCookie(ex, "SID", sid);
            HttpUtil.redirect(ex, "/profile");
        } catch (Exception e) {
            e.printStackTrace();
            HttpUtil.renderTemplate(ex, "error.html", Map.of("message", "DB error"));
        }
    }
}
