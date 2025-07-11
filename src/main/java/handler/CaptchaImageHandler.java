package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import utils.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.UUID;

public class CaptchaImageHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange ex) throws IOException {
        String id = HttpUtil.queryParam(ex, "id");
        if (id == null) {
            String newId  = UUID.randomUUID().toString();
            CaptchaStore.generate(newId);
            HttpUtil.writeJson(ex, "{\"id\":\"" + newId + "\"}");
            return;
        }
        String txt = CaptchaStore.peek(id);
        if (txt == null) {
            ex.sendResponseHeaders(404, -1);
            ex.close();
            return;
        }
        BufferedImage img = CaptchaGenerator.render(txt);
        ex.getResponseHeaders().add("Content-Type", "image/png");
        ex.sendResponseHeaders(200, 0);
        ImageIO.write(img, "PNG", ex.getResponseBody());
        ex.close();
    }
}
