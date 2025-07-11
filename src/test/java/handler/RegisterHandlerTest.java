// src/test/java/handler/RegisterHandlerTest.java
package handler;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import dao.UserDao;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import utils.*;
import web.FormParser;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
class RegisterHandlerTest {

    private final RegisterHandler handler = new RegisterHandler();
    @Test
    void handle_get_rendersEmptyForm() throws Exception {
        HttpExchange ex = mockExchange("GET", null);
        try (MockedStatic<HttpUtil> http = mockStatic(HttpUtil.class)) {
            handler.handle(ex);
            http.verify(() -> HttpUtil.renderTemplate(eq(ex), eq("register.html"),
                    eq(Map.of("errors", List.of()))));
        }
    }
    @Test
    void handle_post_validationErrors() throws Exception {
        HttpExchange ex = mockExchange("POST",
                "email=bad&firstName=A&lastName=B&password=123&" + "captchaId=cid&captchaAnswer=x");
        try (MockedStatic<FormParser> fp   = mockStatic(FormParser.class);
             MockedStatic<ValidationUtil> val = mockStatic(ValidationUtil.class);
             MockedStatic<CaptchaStore> cap  = mockStatic(CaptchaStore.class);
             MockedStatic<HttpUtil> http     = mockStatic(HttpUtil.class)) {
            fp.when(() -> FormParser.parse(any())).thenReturn(
                    Map.of("email", "bad", "firstName", "A", "lastName", "B",
                            "password", "123", "captchaId", "cid", "captchaAnswer", "x"));
            val.when(() -> ValidationUtil.isValidEmail("bad")).thenReturn(false);
            val.when(() -> ValidationUtil.isValidName(anyString(), anyInt(), anyInt())).thenReturn(false);
            val.when(() -> ValidationUtil.isStrongPassword("123")).thenReturn(false);
            cap.when(() -> CaptchaStore.verify("cid", "x")).thenReturn(false);
            handler.handle(ex);
            http.verify(() -> HttpUtil.renderTemplate(eq(ex), eq("register.html"),
                    argThat(ctx -> ctx.get("errors").toString().contains("Invalid"))));
        }
    }
    @Test
    void handle_post_emailAlreadyExists() throws Exception {
        HttpExchange ex = mockExchange("POST",
                "email=a@b.com&firstName=John&lastName=Doe&password=Secret1!" + "&captchaId=cid&captchaAnswer=ok");
        try (MockedStatic<FormParser> fp      = mockStatic(FormParser.class);
             MockedStatic<ValidationUtil> val = mockStatic(ValidationUtil.class);
             MockedStatic<CaptchaStore> cap   = mockStatic(CaptchaStore.class);
             MockedConstruction<UserDao> daoC = mockConstruction(UserDao.class,
                     (mock, ctx) -> when(mock.emailExists("a@b.com")).thenReturn(true));
             MockedStatic<PasswordHasher> ph  = mockStatic(PasswordHasher.class);
             MockedStatic<HttpUtil> http      = mockStatic(HttpUtil.class)) {
            fp.when(() -> FormParser.parse(any())).thenReturn(
                    Map.of("email", "a@b.com", "firstName", "John", "lastName", "Doe",
                            "password", "Secret1!", "captchaId", "cid", "captchaAnswer", "ok"));
            val.when(() -> ValidationUtil.isValidEmail("a@b.com")).thenReturn(true);
            val.when(() -> ValidationUtil.isValidName(anyString(), anyInt(), anyInt())).thenReturn(true);
            val.when(() -> ValidationUtil.isStrongPassword("Secret1!")).thenReturn(true);
            cap.when(() -> CaptchaStore.verify("cid", "ok")).thenReturn(true);
            handler.handle(ex);
            http.verify(() -> HttpUtil.renderTemplate(eq(ex), eq("register.html"),
                    argThat(ctx -> ctx.get("errors").toString().contains("Email already exists"))));
        }
    }
    @Test
    void handle_post_success_redirects() throws Exception {
        HttpExchange ex = mockExchange("POST",
                "email=a@c.com&firstName=Jane&lastName=Doe&password=Secret1!" + "&captchaId=cid&captchaAnswer=yes");
        try (MockedStatic<FormParser> fp      = mockStatic(FormParser.class);
             MockedStatic<ValidationUtil> val = mockStatic(ValidationUtil.class);
             MockedStatic<CaptchaStore> cap   = mockStatic(CaptchaStore.class);
             MockedStatic<PasswordHasher> ph  = mockStatic(PasswordHasher.class);
             MockedConstruction<UserDao> daoC = mockConstruction(UserDao.class,
                     (mock, ctx) -> when(mock.emailExists("a@c.com")).thenReturn(false));
             MockedStatic<HttpUtil> http      = mockStatic(HttpUtil.class)) {
            fp.when(() -> FormParser.parse(any())).thenReturn(
                    Map.of("email", "a@c.com", "firstName", "Jane", "lastName", "Doe",
                            "password", "Secret1!", "captchaId", "cid", "captchaAnswer", "yes"));
            val.when(() -> ValidationUtil.isValidEmail("a@c.com")).thenReturn(true);
            val.when(() -> ValidationUtil.isValidName(anyString(), anyInt(), anyInt())).thenReturn(true);
            val.when(() -> ValidationUtil.isStrongPassword("Secret1!")).thenReturn(true);
            cap.when(() -> CaptchaStore.verify("cid", "yes")).thenReturn(true);
            ph.when(() -> PasswordHasher.hash("Secret1!")).thenReturn("HASHED");
            handler.handle(ex);
            UserDao dao = daoC.constructed().get(0);
            verify(dao).insertUser("a@c.com", "Jane", "Doe", "HASHED");
            http.verify(() -> HttpUtil.redirect(ex, "/login.html?registered=1"));
        }
    }
    private static HttpExchange mockExchange(String method, String body) throws Exception {
        HttpExchange ex = mock(HttpExchange.class, withSettings().lenient());
        when(ex.getRequestMethod()).thenReturn(method);
        when(ex.getRequestURI()).thenReturn(new URI("/register"));
        when(ex.getResponseHeaders()).thenReturn(new Headers());
        if (body != null) {
            when(ex.getRequestBody()).thenReturn(new ByteArrayInputStream(body.getBytes(UTF_8)));
        }
        return ex;
    }
}
