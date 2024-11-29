package bg.sofia.uni.fmi.mjt.imagekit.algorithm.blur;

import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GaussianBlurTest {

    @Test
    void testGaussianBlurPreservesImageSize() {
        GaussianBlur gaussianBlur = new GaussianBlur();
        BufferedImage inputImage = createTestImage(10, 10);
        BufferedImage outputImage = gaussianBlur.process(inputImage);

        assertNotNull(outputImage, "The processed image should not be null.");
        assertEquals(inputImage.getWidth(), outputImage.getWidth(), "The width of the output image should match the input image.");
        assertEquals(inputImage.getHeight(), outputImage.getHeight(), "The height of the output image should match the input image.");
    }

    @Test
    void testGaussianBlurEffect() {
        GaussianBlur gaussianBlur = new GaussianBlur();
        BufferedImage inputImage = createSimpleTestPattern();
        BufferedImage outputImage = gaussianBlur.process(inputImage);

        assertFalse(imagesAreIdentical(inputImage, outputImage), "The blurred image should differ from the input image.");
    }

    @Test
    void testGaussianBlurEdgeCases() {
        GaussianBlur gaussianBlur = new GaussianBlur();

        BufferedImage smallImage = createTestImage(1, 1);
        BufferedImage smallBlurred = gaussianBlur.process(smallImage);
        assertNotNull(smallBlurred, "The processed image should not be null for a 1x1 input.");
        assertEquals(1, smallBlurred.getWidth(), "The width should remain 1.");
        assertEquals(1, smallBlurred.getHeight(), "The height should remain 1.");

        BufferedImage largeImage = createTestImage(1000, 1000);
        BufferedImage largeBlurred = gaussianBlur.process(largeImage);
        assertNotNull(largeBlurred, "The processed image should not be null for a large input.");
        assertEquals(1000, largeBlurred.getWidth(), "The width should remain 1000.");
        assertEquals(1000, largeBlurred.getHeight(), "The height should remain 1000.");
    }

    private BufferedImage createTestImage(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgb = (x * y) % 256;
                image.setRGB(x, y, (rgb << 16) | (rgb << 8) | rgb);
            }
        }
        return image;
    }

    private BufferedImage createSimpleTestPattern() {
        BufferedImage image = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                int rgb = ((x + y) % 2 == 0) ? 0xFFFFFF : 0x000000;
                image.setRGB(x, y, rgb);
            }
        }
        return image;
    }

    private boolean imagesAreIdentical(BufferedImage img1, BufferedImage img2) {
        if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) {
            return false;
        }
        for (int x = 0; x < img1.getWidth(); x++) {
            for (int y = 0; y < img1.getHeight(); y++) {
                if (img1.getRGB(x, y) != img2.getRGB(x, y)) {
                    return false;
                }
            }
        }
        return true;
    }
}
