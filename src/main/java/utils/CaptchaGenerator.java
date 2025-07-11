package utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public final class CaptchaGenerator {
    public static BufferedImage render(String text) {
        int w = 160, h = 60;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.WHITE); g.fillRect(0,0,w,h);
        Random rand = new Random();
        for (int i = 0; i < 1200; i++) {
            g.setColor(new Color(rand.nextInt()));
            g.drawRect(rand.nextInt(w), rand.nextInt(h), 1,1);
        }
        g.setFont(new Font("SansSerif", Font.BOLD, 38));
        g.setColor(Color.BLACK);
        FontMetrics fm = g.getFontMetrics();
        int x = (w - fm.stringWidth(text)) / 2;
        int y = (h - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(text, x, y);
        g.dispose();
        return img;
    }
}
