package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.UserDao;
import util.*;
import web.FormParser;

import java.io.IOException;
import java.util.Map;

public class ProfileHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange ex) throws IOException {
        String sid = HttpUtil.getCookie(ex, "SID");
        Integer uid = SessionManager.getUserId(sid);
        if (uid == null) {
            HttpUtil.redirect(ex, "/login.html");
            return;
        }
        if ("GET".equalsIgnoreCase(ex.getRequestMethod())) {
            showProfile(ex, uid);
        } else if ("POST".equalsIgnoreCase(ex.getRequestMethod())) {
            updateProfile(ex, uid);
        }
    }
    private void showProfile(HttpExchange ex, int uid) throws IOException {
        try (UserDao dao = new UserDao()) {
            var user = dao.findById(uid);

            Map<String,Object> ctx = Map.of(
                    "firstName", user.getFirstName(),
                    "lastName",  user.getLastName(),
                    "msg",  HttpUtil.queryParam(ex,"updated") != null ? "Changes saved" : "",
                    "error",""
            );

            HttpUtil.renderTemplate(ex, "profile.html", ctx);
        } catch (Exception e) {
            e.printStackTrace();
            HttpUtil.renderTemplate(ex, "error.html", Map.of("message", "DB error"));
        }
    }
    private void updateProfile(HttpExchange ex, int uid) throws IOException {
        Map<String, String> form = FormParser.parse(ex.getRequestBody());
        String first = form.get("firstName");
        String last  = form.get("lastName");
        String pw    = form.get("password");
        if (!ValidationUtil.isValidName(first,2,30) ||
                !ValidationUtil.isValidName(last,2,30) ||
                (pw != null && !pw.isEmpty() && !ValidationUtil.isStrongPassword(pw))) {
            HttpUtil.renderTemplate(ex, "profile.html",
                    Map.of("error", "Validation failed"));
            return;
        }
        try (UserDao dao = new UserDao()) {
            dao.updateNames(uid, first, last);
            if (pw != null && !pw.isEmpty())
                dao.updatePassword(uid, PasswordHasher.hash(pw));
            HttpUtil.redirect(ex, "/profile?updated=1");
        } catch (Exception e) {
            e.printStackTrace();
            HttpUtil.renderTemplate(ex, "error.html", Map.of("message", "DB error"));
        }
    }
}
