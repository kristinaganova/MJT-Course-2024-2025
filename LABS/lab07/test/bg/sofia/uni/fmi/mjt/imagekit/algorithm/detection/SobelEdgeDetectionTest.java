package bg.sofia.uni.fmi.mjt.imagekit.algorithm.detection;

import bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale.LuminosityGrayscale;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

class SobelEdgeDetectionTest {

    @Test
    void testSobelEdgeDetectionWithSimpleImage() {
        BufferedImage originalImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);

        for (int x = 20; x < 80; x++) {
            for (int y = 20; y < 80; y++) {
                originalImage.setRGB(x, y, 0xFFFFFF);
            }
        }

        SobelEdgeDetection sobel = new SobelEdgeDetection(new LuminosityGrayscale());
        BufferedImage edgeDetectedImage = sobel.process(originalImage);

        boolean hasEdges = false;
        for (int x = 0; x < edgeDetectedImage.getWidth(); x++) {
            for (int y = 0; y < edgeDetectedImage.getHeight(); y++) {
                if (edgeDetectedImage.getRGB(x, y) != 0x000000) {
                    hasEdges = true;
                    break;
                }
            }
        }

        assertTrue(hasEdges, "Edge detection should detect edges in the input image.");
    }
}
