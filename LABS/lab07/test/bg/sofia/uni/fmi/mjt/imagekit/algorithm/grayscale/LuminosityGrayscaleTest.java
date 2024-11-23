package bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale;

import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

class LuminosityGrayscaleTest {

    @Test
    void testGrayscaleConversion() {
        BufferedImage colorImage = new BufferedImage(3, 3, BufferedImage.TYPE_INT_RGB);
        colorImage.setRGB(0, 0, 0xFF0000); // Червено
        colorImage.setRGB(1, 0, 0x00FF00); // Зелено
        colorImage.setRGB(2, 0, 0x0000FF); // Синьо

        LuminosityGrayscale grayscale = new LuminosityGrayscale();
        BufferedImage grayscaleImage = grayscale.process(colorImage);

        for (int x = 0; x < colorImage.getWidth(); x++) {
            for (int y = 0; y < colorImage.getHeight(); y++) {
                int rgb = grayscaleImage.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                assertEquals(r, g, "Red and Green components should be equal in grayscale image.");
                assertEquals(g, b, "Green and Blue components should be equal in grayscale image.");
            }
        }
    }
}
