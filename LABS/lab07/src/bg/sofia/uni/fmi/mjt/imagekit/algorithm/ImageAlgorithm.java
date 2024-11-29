package bg.sofia.uni.fmi.mjt.imagekit.algorithm;

import java.awt.image.BufferedImage;

/**
 * Represents an algorithm that processes images.
 */
public interface ImageAlgorithm {

    int BLOCK_SIZE = 16;
    int MAX_PIXEL_VALUE = 0xff;

    /**
     * Applies the image processing algorithm to the given image.
     *
     * @param image the image to be processed
     * @return BufferedImage the processed image of type (TYPE_INT_RGB)
     */
    BufferedImage process(BufferedImage image);

    /**
     * Extracts the RGB components from an integer-packed pixel value.
     *
     * @param rgb the integer-packed RGB value
     * @return an array of three integers: {red, green, blue}
     */
    static int[] getRGBComponents(int rgb) {
        int r = (rgb >> BLOCK_SIZE) & MAX_PIXEL_VALUE;
        int g = (rgb >> (BLOCK_SIZE / 2)) & MAX_PIXEL_VALUE;
        int b = rgb & MAX_PIXEL_VALUE;
        return new int[]{r, g, b};
    }

    /**
     * Combines the red, green, and blue components into a single integer-packed RGB value.
     *
     * @param r red component (0-255)
     * @param g green component (0-255)
     * @param b blue component (0-255)
     * @return the integer-packed RGB value
     */
    static int combineRGB(int r, int g, int b) {
        return (r << BLOCK_SIZE) | (g << (BLOCK_SIZE / 2)) | b;
    }

    /**
     * Clamps a value to ensure it stays within the range [0, MAX_PIXEL_VALUE].
     *
     * @param value the value to clamp
     * @return the clamped value
     */
    static int clamp(int value) {
        return Math.min(MAX_PIXEL_VALUE, Math.max(0, value));
    }
}