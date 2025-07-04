package web;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class FormParser {
    public static Map<String,String> parse(InputStream is) throws IOException {
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        Map<String,String> map = new HashMap<>();
        for (String pair : body.split("&")) {
            int eq = pair.indexOf('=');
            if (eq < 0) {
                continue;
            }
            String k = URLDecoder.decode(pair.substring(0, eq), "UTF-8");
            String v = URLDecoder.decode(pair.substring(eq+1), "UTF-8");
            map.put(k, v);
        }
        return map;
    }
}
