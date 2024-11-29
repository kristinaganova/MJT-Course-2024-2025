package bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale;

import java.awt.image.BufferedImage;
import bg.sofia.uni.fmi.mjt.imagekit.algorithm.ImageAlgorithm;

public class LuminosityGrayscale implements ImageAlgorithm {

    private static final double RED_WEIGHT = 0.21;
    private static final double GREEN_WEIGHT = 0.72;
    private static final double BLUE_WEIGHT = 0.07;

    @Override
    public BufferedImage process(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage grayscaleImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgb = image.getRGB(x, y);
                int[] components = ImageAlgorithm.getRGBComponents(rgb);

                int gray = (int) (RED_WEIGHT * components[0] + GREEN_WEIGHT * components[1]
                                + BLUE_WEIGHT * components[2]);
                gray = ImageAlgorithm.clamp(gray);

                int grayRgb = ImageAlgorithm.combineRGB(gray, gray, gray);
                grayscaleImage.setRGB(x, y, grayRgb);
            }
        }

        return grayscaleImage;
    }
}