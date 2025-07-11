// src/test/java/handler/LogoutHandlerTest.java
package handler;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import utils.HttpUtil;
import utils.SessionManager;

import java.net.URI;
import static org.mockito.Mockito.*;

class LogoutHandlerTest {
    private final LogoutHandler handler = new LogoutHandler();
    @Test
    void handle_withSidCookie() throws Exception {
        HttpExchange ex = mockExchange();
        try (MockedStatic<HttpUtil>     http = mockStatic(HttpUtil.class);
             MockedStatic<SessionManager> sm = mockStatic(SessionManager.class)) {
            http.when(() -> HttpUtil.getCookie(ex, "SID")).thenReturn("SID123");
            handler.handle(ex);
            sm.verify(() -> SessionManager.invalidate("SID123"));
            http.verify(() -> HttpUtil.deleteCookie(ex, "SID"));
            http.verify(() -> HttpUtil.redirect(ex, "/login.html?loggedout=1"));
        }
    }
    @Test
    void handle_withoutSidCookie() throws Exception {
        HttpExchange ex = mockExchange();
        try (MockedStatic<HttpUtil>     http = mockStatic(HttpUtil.class);
             MockedStatic<SessionManager> sm = mockStatic(SessionManager.class)) {
            http.when(() -> HttpUtil.getCookie(ex, "SID")).thenReturn(null);
            handler.handle(ex);
            sm.verify(() -> SessionManager.invalidate(null));
            http.verify(() -> HttpUtil.deleteCookie(ex, "SID"));
            http.verify(() -> HttpUtil.redirect(ex, "/login.html?loggedout=1"));
        }
    }
    private static HttpExchange mockExchange() throws Exception {
        HttpExchange ex = mock(HttpExchange.class);
        when(ex.getRequestURI()).thenReturn(new URI("/logout"));
        when(ex.getResponseHeaders()).thenReturn(new Headers());
        return ex;
    }
}
