package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import utils.*;

import java.io.IOException;

public class LogoutHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange ex) throws IOException {
        String sid = HttpUtil.getCookie(ex, "SID");
        SessionManager.invalidate(sid);
        HttpUtil.deleteCookie(ex, "SID");
        HttpUtil.redirect(ex, "/login.html?loggedout=1");
    }
}
