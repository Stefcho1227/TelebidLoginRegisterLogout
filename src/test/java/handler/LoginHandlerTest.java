package handler;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import dao.UserDao;
import model.User;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import utils.*;
import web.FormParser;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.net.URI;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
class LoginHandlerTest {

    private final LoginHandler handler = new LoginHandler();
    @Test
    void handle_get_rendersLoginTemplate() throws Exception {
        HttpExchange ex = mockExchange("GET", null);
        try (MockedStatic<HttpUtil> http = mockStatic(HttpUtil.class)) {
            handler.handle(ex);

            http.verify(() -> HttpUtil.renderTemplate(
                    eq(ex),
                    eq("login.html"),
                    eq(Map.of("error", "", "msg", ""))));
        }
    }
    @Test
    void handle_post_wrongCredentials() throws Exception {
        HttpExchange ex = mockExchange("POST", "email=a@b.com&password=123");
        try (MockedStatic<FormParser>  parser   = mockStatic(FormParser.class);
             MockedStatic<HttpUtil>    http     = mockStatic(HttpUtil.class);
             MockedStatic<PasswordHasher> pw    = mockStatic(PasswordHasher.class);
             MockedConstruction<UserDao> daoCon = mockConstruction(
                     UserDao.class, (mock, ctx) -> when(mock.findByEmail("a@b.com")).thenReturn(null))) {
            parser.when(() -> FormParser.parse(any())).thenReturn(
                    Map.of("email", "a@b.com", "password", "123"));
            handler.handle(ex);
            http.verify(() -> HttpUtil.renderTemplate(
                    eq(ex),
                    eq("login.html"),
                    eq(Map.of("error", "Wrong credentials"))));
        }
    }
    @Test
    void handle_post_success_redirects() throws Exception {
        HttpExchange ex = mockExchange("POST", "email=a@b.com&password=123");
        User dbUser = new User(7, "a@b.com", "F", "L", "hashABC");
        try (MockedStatic<FormParser> parser = mockStatic(FormParser.class);
             MockedStatic<HttpUtil>   http   = mockStatic(HttpUtil.class);
             MockedStatic<PasswordHasher> pwh = mockStatic(PasswordHasher.class);
             MockedStatic<SessionManager> ses = mockStatic(SessionManager.class);
             MockedConstruction<UserDao> daoC = mockConstruction(
                     UserDao.class, (mock, ctx) -> when(mock.findByEmail("a@b.com")).thenReturn(dbUser))) {
            parser.when(() -> FormParser.parse(any())).thenReturn(
                    Map.of("email", "a@b.com", "password", "123"));
            pwh.when(() -> PasswordHasher.matches("123", "hashABC")).thenReturn(true);
            ses.when(() -> SessionManager.createSession(7)).thenReturn("SID123");
            handler.handle(ex);
            http.verify(() -> HttpUtil.setCookie(ex, "SID", "SID123"));
            http.verify(() -> HttpUtil.redirect(ex, "/profile"));
        }
    }
    @Test
    void handle_post_dbThrows_rendersError() throws Exception {
        HttpExchange ex = mockExchange("POST", "email=x@x.com&password=p");
        try (MockedStatic<FormParser> parser = mockStatic(FormParser.class);
             MockedStatic<HttpUtil>   http   = mockStatic(HttpUtil.class);
             MockedConstruction<UserDao> daoC = mockConstruction(UserDao.class,
                     (mock, ctx) -> when(mock.findByEmail(anyString())).thenThrow(new RuntimeException("boom")))) {
            parser.when(() -> FormParser.parse(any()))
                    .thenReturn(Map.of("email", "x@x.com", "password", "p"));
            handler.handle(ex);
            http.verify(() -> HttpUtil.renderTemplate(eq(ex), eq("error.html"),
                    eq(Map.of("message", "DB error"))));
        }
    }
    private static HttpExchange mockExchange(String method, String body) throws Exception {
        HttpExchange ex = mock(HttpExchange.class);
        when(ex.getRequestMethod()).thenReturn(method);
        when(ex.getRequestURI()).thenReturn(new URI("/login"));
        when(ex.getResponseHeaders()).thenReturn(new Headers());
        if (body != null) {when(ex.getRequestBody()).thenReturn(new ByteArrayInputStream(body.getBytes(UTF_8)));
        }
        return ex;
    }
}
