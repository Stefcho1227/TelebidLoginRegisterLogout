package utils;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class HttpUtil {
    public static void redirect(HttpExchange ex, String location) throws IOException {
        ex.getResponseHeaders().add("Location", location);
        ex.sendResponseHeaders(302, -1);
        ex.close();
    }
    public static void setCookie(HttpExchange ex, String name, String value) {
        ex.getResponseHeaders().add("Set-Cookie", String.format("%s=%s; Path=/; HttpOnly", name, value));
    }
    public static String getCookie(HttpExchange ex, String name) {
        var cookies = ex.getRequestHeaders().getFirst("Cookie");
        if (cookies == null){
            return null;
        }
        for (String c : cookies.split(";")) {
            String[] nv = c.trim().split("=",2);
            if (nv.length==2 && nv[0].equals(name)){
                return nv[1];
            }
        }
        return null;
    }

    public static void deleteCookie(HttpExchange ex, String name) {
        ex.getResponseHeaders().add("Set-Cookie", String.format("%s=; Path=/; Max-Age=0", name));
    }

    public static String queryParam(HttpExchange ex, String key) {
        String q = ex.getRequestURI().getQuery();
        if (q == null) {
            return null;
        }
        for (String p : q.split("&")) {
            String[] kv = p.split("=",2);
            if (kv.length==2 && kv[0].equals(key)){
                return kv[1];
            }
        }
        return null;
    }

    public static void renderTemplate(HttpExchange ex, String htmlFile, Map<String,Object> context)
            throws IOException {
        String raw = new String(HttpUtil.class.getResourceAsStream("/web/"+htmlFile).readAllBytes(),
                StandardCharsets.UTF_8);
        for (var e : context.entrySet()){
            raw = raw.replace("{{"+e.getKey()+"}}", String.valueOf(e.getValue()));
        }
        byte[] bytes = raw.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
        ex.sendResponseHeaders(200, bytes.length);
        ex.getResponseBody().write(bytes);
        ex.close();
    }
    public static void writeJson(HttpExchange ex, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().add("Content-Type", "application/json");
        ex.sendResponseHeaders(200, bytes.length);
        ex.getResponseBody().write(bytes);
        ex.close();
    }
}
