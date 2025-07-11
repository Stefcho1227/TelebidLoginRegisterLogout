package utils;

import util.CaptchaGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

class CaptchaGeneratorTest {

    private static final int W = 160;
    private static final int H = 60;
    @Test
    void render_returnsImageWithRightSize() {
        BufferedImage img = CaptchaGenerator.render("ABC123");

        Assertions.assertNotNull(img, "Render should return an image");
        Assertions.assertEquals(W, img.getWidth());
        Assertions.assertEquals(H, img.getHeight());
    }
    @Test
    void render_drawsSomeBlackTextPixels() {
        BufferedImage img = CaptchaGenerator.render("TEST12");

        int blackRgb = Color.BLACK.getRGB();
        int yMid     = H / 2;
        boolean foundBlack = false;

        for (int x = 0; x < W; x++) {
            if (img.getRGB(x, yMid) == blackRgb) {
                foundBlack = true;
                break;
            }
        }
        Assertions.assertTrue(foundBlack,
                "We expect at least a few accurate black pixels from the text in the center line");
    }
}
