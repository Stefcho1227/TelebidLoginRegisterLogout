// src/test/java/utils/HttpUtilTest.java
package utils;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class HttpUtilTest {
    @Test
    void redirect_sets302_andLocationHeader() throws Exception {
        HttpExchange ex = mockExchange("/any");
        HttpUtil.redirect(ex, "/target");
        assertThat(ex.getResponseHeaders().getFirst("Location")).isEqualTo("/target");
        verify(ex).sendResponseHeaders(302, -1);
        verify(ex).close();
    }
    @Test
    void cookies_roundTrip() {
        HttpExchange ex = mockExchange("/");
        HttpUtil.setCookie(ex, "SID", "ABC");
        assertThat(ex.getResponseHeaders().getFirst("Set-Cookie")).contains("SID=ABC");
        ex.getRequestHeaders().add("Cookie", "SID=ABC");
        assertThat(HttpUtil.getCookie(ex, "SID")).isEqualTo("ABC");
        HttpUtil.deleteCookie(ex, "SID");
        var setCookies = ex.getResponseHeaders().get("Set-Cookie");
        assertThat(setCookies).hasSize(2);
        assertThat(setCookies.get(1)).contains("Max-Age=0");
    }
    @Test
    void queryParam_returnsValueOrNull() throws Exception {
        HttpExchange ex = mockExchange("/path?x=1&y=2");
        assertThat(HttpUtil.queryParam(ex, "x")).isEqualTo("1");
        assertThat(HttpUtil.queryParam(ex, "y")).isEqualTo("2");
        assertThat(HttpUtil.queryParam(ex, "z")).isNull();
    }
    @Test
    void writeJson_setsContentType_andWritesBody() throws Exception {
        HttpExchange ex = mockExchange("/api");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        when(ex.getResponseBody()).thenReturn(os);
        HttpUtil.writeJson(ex, "{\"ok\":1}");
        assertThat(ex.getResponseHeaders().getFirst("Content-Type")).isEqualTo("application/json");
        verify(ex).sendResponseHeaders(200, 8);
        assertThat(os.toString(StandardCharsets.UTF_8)).isEqualTo("{\"ok\":1}");
        verify(ex).close();
    }
    private static HttpExchange mockExchange(String uri) {
        HttpExchange ex = mock(HttpExchange.class, withSettings().lenient());
        when(ex.getRequestURI()).thenReturn(URI.create(uri));
        when(ex.getResponseHeaders()).thenReturn(new Headers());
        when(ex.getRequestHeaders()).thenReturn(new Headers());
        return ex;
    }
}
