// src/test/java/handler/ProfileHandlerTest.java
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
import java.net.URI;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
class ProfileHandlerTest {

    private final ProfileHandler handler = new ProfileHandler();
    @Test
    void handle_noSession_redirectsToLogin() throws Exception {
        HttpExchange ex = mockExchange("GET", null);
        try (MockedStatic<HttpUtil>  http = mockStatic(HttpUtil.class);
             MockedStatic<SessionManager> sm = mockStatic(SessionManager.class)) {
            http.when(() -> HttpUtil.getCookie(ex, "SID")).thenReturn(null);
            sm.when(() -> SessionManager.getUserId(null)).thenReturn(null); // ← FIX
            handler.handle(ex);
            http.verify(() -> HttpUtil.redirect(ex, "/login.html"));
        }
    }
    @Test
    void handle_getProfile_rendersPage() throws Exception {
        HttpExchange ex = mockExchange("GET", null);
        User dbUser = new User(7, "a@b.com", "John", "Doe", "hash");
        try (MockedStatic<HttpUtil> http = mockStatic(HttpUtil.class);
             MockedStatic<SessionManager> sm = mockStatic(SessionManager.class);
             MockedConstruction<UserDao> daoC =
                     mockConstruction(UserDao.class, (mock, ctx) -> when(mock.findById(7)).thenReturn(dbUser))) {
            http.when(() -> HttpUtil.getCookie(ex, "SID")).thenReturn("SID7");
            sm.when(() -> SessionManager.getUserId("SID7")).thenReturn(7);
            http.when(() -> HttpUtil.queryParam(ex, "updated")).thenReturn(null);
            handler.handle(ex);
            http.verify(() -> HttpUtil.renderTemplate(
                    eq(ex), eq("profile.html"),
                    argThat(ctx -> "John".equals(ctx.get("firstName")) && "Doe".equals(ctx.get("lastName"))
                            && "".equals(ctx.get("msg")) && "".equals(ctx.get("error")))));
        }
    }
    @Test
    void handle_post_invalidInput_showsValidationError() throws Exception {
        HttpExchange ex = mockExchange("POST", "firstName=A&lastName=B&password=pw");
        try (MockedStatic<HttpUtil> http = mockStatic(HttpUtil.class);
             MockedStatic<SessionManager> sm = mockStatic(SessionManager.class);
             MockedStatic<FormParser> fp = mockStatic(FormParser.class);
             MockedStatic<ValidationUtil> val = mockStatic(ValidationUtil.class)) {
            http.when(() -> HttpUtil.getCookie(ex, "SID")).thenReturn("X");
            sm.when(() -> SessionManager.getUserId("X")).thenReturn(1);
            fp.when(() -> FormParser.parse(any())).thenReturn(
                    Map.of("firstName", "A", "lastName", "B", "password", "pw"));
            val.when(() -> ValidationUtil.isValidName(anyString(), anyInt(), anyInt()))
                    .thenReturn(false);
            handler.handle(ex);
            http.verify(() -> HttpUtil.renderTemplate(
                    eq(ex), eq("profile.html"), eq(Map.of("error", "Validation failed"))));
        }
    }
    @Test
    void handle_post_updateNames_only() throws Exception {
        HttpExchange ex = mockExchange("POST", "firstName=John&lastName=Doe&password=");
        try (MockedStatic<HttpUtil> http = mockStatic(HttpUtil.class);
             MockedStatic<SessionManager> sm = mockStatic(SessionManager.class);
             MockedStatic<FormParser> fp     = mockStatic(FormParser.class);
             MockedStatic<ValidationUtil> val = mockStatic(ValidationUtil.class);
             MockedConstruction<UserDao> daoC = mockConstruction(UserDao.class)) {
            http.when(() -> HttpUtil.getCookie(ex, "SID")).thenReturn("SID1");
            sm.when(() -> SessionManager.getUserId("SID1")).thenReturn(1);
            fp.when(() -> FormParser.parse(any())).thenReturn(
                    Map.of("firstName", "John", "lastName", "Doe", "password", ""));
            val.when(() -> ValidationUtil.isValidName(anyString(), anyInt(), anyInt())).thenReturn(true);
            handler.handle(ex);
            UserDao dao = daoC.constructed().get(0);
            verify(dao).updateNames(1, "John", "Doe");
            verify(dao, never()).updatePassword(anyInt(), anyString());
            http.verify(() -> HttpUtil.redirect(ex, "/profile?updated=1"));
        }
    }
    @Test
    void handle_post_updateNamesAndPassword() throws Exception {
        HttpExchange ex = mockExchange("POST", "firstName=J&lastName=D&password=secret");
        try (MockedStatic<HttpUtil>  http = mockStatic(HttpUtil.class);
             MockedStatic<SessionManager> sm = mockStatic(SessionManager.class);
             MockedStatic<FormParser> fp     = mockStatic(FormParser.class);
             MockedStatic<ValidationUtil> val = mockStatic(ValidationUtil.class);
             MockedStatic<PasswordHasher> ph  = mockStatic(PasswordHasher.class);
             MockedConstruction<UserDao> daoC = mockConstruction(UserDao.class)) {
            http.when(() -> HttpUtil.getCookie(ex, "SID")).thenReturn("SID2");
            sm.when(() -> SessionManager.getUserId("SID2")).thenReturn(2);
            fp.when(() -> FormParser.parse(any())).thenReturn(
                    Map.of("firstName", "J", "lastName", "D", "password", "secret"));
            val.when(() -> ValidationUtil.isValidName(anyString(), anyInt(), anyInt())).thenReturn(true);
            val.when(() -> ValidationUtil.isStrongPassword("secret")).thenReturn(true);
            ph.when(() -> PasswordHasher.hash("secret")).thenReturn("hashed");
            handler.handle(ex);
            UserDao dao = daoC.constructed().get(0);
            verify(dao).updateNames(2, "J", "D");
            verify(dao).updatePassword(2, "hashed");
            http.verify(() -> HttpUtil.redirect(ex, "/profile?updated=1"));
        }
    }
    private static HttpExchange mockExchange(String method, String body) throws Exception {
        HttpExchange ex = mock(HttpExchange.class, withSettings().lenient());
        when(ex.getRequestMethod()).thenReturn(method);
        when(ex.getRequestURI()).thenReturn(new URI("/profile"));
        when(ex.getResponseHeaders()).thenReturn(new Headers());
        if (body != null) {
            when(ex.getRequestBody()).thenReturn(new ByteArrayInputStream(body.getBytes(UTF_8)));
        }
        return ex;
    }
}
