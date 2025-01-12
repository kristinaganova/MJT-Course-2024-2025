package bg.sofia.uni.fmi.mjt.imagekit.algorithm;

import java.awt.image.BufferedImage;

/**
 * Represents an algorithm that processes images.
 */
public interface ImageAlgorithm {

    static final int BLOCK_SIZE = 16;
    static final int MAX_PIXEL_VALUE = 0xff;
    /**
     * Applies the image processing algorithm to the given image.
     *
     * @param image the image to be processed
     * @return BufferedImage the processed image of type (TYPE_INT_RGB)
     */
    BufferedImage process(BufferedImage image);
}