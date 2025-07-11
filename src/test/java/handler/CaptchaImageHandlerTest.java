package handler;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import utils.CaptchaGenerator;
import utils.CaptchaStore;
import utils.HttpUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mockStatic;
class CaptchaImageHandlerTest {

    private final CaptchaImageHandler handler = new CaptchaImageHandler();
    @Test
    void handle_noId_generatesCaptchaAndReturnsJson() throws Exception {
        HttpExchange ex = mockExchange("/captcha");

        try (MockedStatic<HttpUtil>     httpUtil = mockStatic(HttpUtil.class);
             MockedStatic<CaptchaStore> store    = mockStatic(CaptchaStore.class)) {
            httpUtil.when(() -> HttpUtil.queryParam(ex, "id")).thenReturn(null);
            final StringBuilder jsonHolder = new StringBuilder();
            httpUtil.when(() -> HttpUtil.writeJson(eq(ex), anyString()))
                    .thenAnswer(inv -> {
                        jsonHolder.append(inv.getArgument(1, String.class));
                        return null;
                    });
            handler.handle(ex);
            String json = jsonHolder.toString();
            assertTrue(json.matches("\\{\"id\":\"[0-9a-fA-F\\-]{36}\"}"));
            String id = json.substring(7, 43);          // грубо изрязване
            store.verify(() -> CaptchaStore.generate(id));
        }
    }
    @Test
    void handle_unknownId_returns404() throws Exception {
        HttpExchange ex = mockExchange("/captcha?id=abc");
        try (MockedStatic<HttpUtil>     httpUtil  = mockStatic(HttpUtil.class);
             MockedStatic<CaptchaStore> storeMock = mockStatic(CaptchaStore.class)) {
            httpUtil.when(() -> HttpUtil.queryParam(ex, "id")).thenReturn("abc");
            storeMock.when(() -> CaptchaStore.peek("abc")).thenReturn(null);
            handler.handle(ex);
            verify(ex).sendResponseHeaders(404, -1);
            verify(ex).close();
        }
    }
    @Test
    void handle_validId_returnsImage() throws Exception {
        HttpExchange ex = mockExchange("/captcha?id=xyz");
        BufferedImage fakeImg = new BufferedImage(1, 1,
                BufferedImage.TYPE_INT_RGB);
        try (MockedStatic<HttpUtil>         httpUtil = mockStatic(HttpUtil.class);
             MockedStatic<CaptchaStore>     store    = mockStatic(CaptchaStore.class);
             MockedStatic<CaptchaGenerator> gen      = mockStatic(CaptchaGenerator.class)) {
            httpUtil.when(() -> HttpUtil.queryParam(ex, "id")).thenReturn("xyz");
            store.when(()   -> CaptchaStore.peek("xyz")).thenReturn("ABC123");
            gen.when(()     -> CaptchaGenerator.render("ABC123")).thenReturn(fakeImg);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            when(ex.getResponseBody()).thenReturn(os);
            handler.handle(ex);
            Headers h = ex.getResponseHeaders();
            assertEquals("image/png", h.getFirst("Content-Type"));
            verify(ex).sendResponseHeaders(200, 0);
            assertTrue(os.size() > 0,
                    "PNG bytes should be written to response body");
            assertNotNull(ImageIO.read(new java.io.ByteArrayInputStream(os.toByteArray())));
        }
    }
    private static HttpExchange mockExchange(String uri) throws Exception {
        HttpExchange ex = mock(HttpExchange.class);
        when(ex.getRequestURI()).thenReturn(new URI(uri));
        when(ex.getResponseHeaders()).thenReturn(new Headers());
        return ex;
    }
}
