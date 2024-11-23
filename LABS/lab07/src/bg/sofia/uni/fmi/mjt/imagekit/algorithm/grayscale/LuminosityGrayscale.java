package bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale;

import java.awt.image.BufferedImage;

public class LuminosityGrayscale implements GrayscaleAlgorithm {

    private static final double RED_WEIGHT = 0.21;
    private static final double GREEN_WEIGHT = 0.72;
    private static final double BLUE_WEIGHT = 0.07;

    @Override
    public BufferedImage process(BufferedImage image) {

        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage grayscleImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgb = image.getRGB(x, y);
                int r = (rgb >> BLOCK_SIZE) & MAX_PIXEL_VALUE;
                int g = (rgb >> (BLOCK_SIZE / 2)) & MAX_PIXEL_VALUE;
                int b = rgb & MAX_PIXEL_VALUE;

                int gray = (int)(RED_WEIGHT * r + GREEN_WEIGHT * g + BLUE_WEIGHT * b);
                int grayRgb = (gray << BLOCK_SIZE) | (gray << (BLOCK_SIZE / 2)) | gray;

                grayscleImage.setRGB(x, y, grayRgb);
            }
        }

        return grayscleImage;

    }
}
