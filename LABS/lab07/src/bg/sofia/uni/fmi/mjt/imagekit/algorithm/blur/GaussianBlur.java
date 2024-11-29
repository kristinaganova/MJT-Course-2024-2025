package bg.sofia.uni.fmi.mjt.imagekit.algorithm.blur;

import bg.sofia.uni.fmi.mjt.imagekit.algorithm.ImageAlgorithm;

import java.awt.image.BufferedImage;

public class GaussianBlur implements ImageAlgorithm {

    private static final double[][] GAUSSIAN_KERNEL = {
            {1, 4, 7, 4, 1},
            {4, 16, 26, 16, 4},
            {7, 26, 41, 26, 7},
            {4, 16, 26, 16, 4},
            {1, 4, 7, 4, 1}
    };

    private static final double KERNEL_SUM = 256.0;

    @Override
    public BufferedImage process(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage blurredImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 2; x < width - 2; x++) {
            for (int y = 2; y < height - 2; y++) {
                double[] blurredComponents = applyKernel(image, x, y);
                int r = ImageAlgorithm.clamp((int) blurredComponents[0]);
                int g = ImageAlgorithm.clamp((int) blurredComponents[1]);
                int b = ImageAlgorithm.clamp((int) blurredComponents[2]);

                int blurredRgb = ImageAlgorithm.combineRGB(r, g, b);
                blurredImage.setRGB(x, y, blurredRgb);
            }
        }

        return blurredImage;
    }

    private double[] applyKernel(BufferedImage image, int x, int y) {
        double redSum = 0.0;
        double greenSum = 0.0;
        double blueSum = 0.0;

        final int start = -2;
        for (int i = start; i <= 2; i++) {
            for (int j = start; j <= 2; j++) {
                int[] rgb = ImageAlgorithm.getRGBComponents(image.getRGB(x + i, y + j));
                double weight = GAUSSIAN_KERNEL[i + 2][j + 2];

                redSum += rgb[0] * weight;
                greenSum += rgb[1] * weight;
                blueSum += rgb[2] * weight;
            }
        }

        return new double[]{ redSum / KERNEL_SUM, greenSum / KERNEL_SUM, blueSum / KERNEL_SUM };
    }
}