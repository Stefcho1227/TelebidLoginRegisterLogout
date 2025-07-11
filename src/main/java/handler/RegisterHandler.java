package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.UserDao;
import util.*;
import web.FormParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RegisterHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) {
            HttpUtil.renderTemplate(ex, "register.html", Map.of("errors", List.of()));
            return;
        }
        Map<String, String> form = FormParser.parse(ex.getRequestBody());
        String email = form.get("email");
        String first = form.get("firstName");
        String last = form.get("lastName");
        String pw = form.get("password");
        String capId = form.get("captchaId");
        String capText = form.get("captchaAnswer");
        List<String> errors = new ArrayList<>();
        if (!ValidationUtil.isValidEmail(email)){
            errors.add("Invalid email");
        }
        if (!ValidationUtil.isValidName(first, 2, 30)) {
            errors.add("Invalid first name");
        }
        if (!ValidationUtil.isValidName(last, 2, 30)){
            errors.add("Invalid last name");
        }
        if (!ValidationUtil.isStrongPassword(pw))  {
            errors.add("Weak password");
        }
        if (!CaptchaStore.verify(capId, capText)) {
            errors.add("Wrong captcha");
        }
        if (!errors.isEmpty()) {
            System.out.println("VALIDATION ERRORS = " + errors);
            HttpUtil.renderTemplate(ex,"register.html",
                    Map.of("errors", errors.stream().map(e->"<li>"+e+"</li>").collect(Collectors.joining())));
            return;
        }
        try (UserDao dao = new UserDao()) {
            if (dao.emailExists(email)) {
                errors.add("Email already exists");
                HttpUtil.renderTemplate(ex, "register.html", Map.of("errors", errors));
            } else {
                dao.insertUser(email, first, last, PasswordHasher.hash(pw));
                HttpUtil.redirect(ex, "/login.html?registered=1");
            }
        } catch (Exception e) {
            e.printStackTrace();
            HttpUtil.renderTemplate(ex,"error.html",
                    Map.of("message","DB error: "+e.getMessage()));
        }
    }
}
