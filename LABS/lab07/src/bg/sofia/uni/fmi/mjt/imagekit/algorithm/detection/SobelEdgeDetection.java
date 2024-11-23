package bg.sofia.uni.fmi.mjt.imagekit.algorithm.detection;

import java.awt.image.BufferedImage;

import bg.sofia.uni.fmi.mjt.imagekit.algorithm.ImageAlgorithm;

public class SobelEdgeDetection implements EdgeDetectionAlgorithm {

    private final ImageAlgorithm grayscaleAlgorithm;

    private static final int[][] SOBEL_X = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
    private static final int[][] SOBEL_Y = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};

    public SobelEdgeDetection(ImageAlgorithm grayscaleAlgorithm) {
        this.grayscaleAlgorithm = grayscaleAlgorithm;
    }

    @Override
    public BufferedImage process(BufferedImage image) {
        BufferedImage newImage = grayscaleAlgorithm.process(image);
        int width = newImage.getWidth();
        int height = newImage.getHeight();
        BufferedImage edgeDetectionImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                int gx = 0;
                int gy = 0;

                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        int rgb = newImage.getRGB(x + i, y + j) & MAX_PIXEL_VALUE;
                        gx += rgb * SOBEL_X[i + 1][j + 1];
                        gy += rgb * SOBEL_Y[i + 1][j + 1];
                    }
                }
                int edgeValue = (int) Math.min(MAX_PIXEL_VALUE, Math.sqrt(gx * gx + gy * gy));
                int egdeRGB = (edgeValue << BLOCK_SIZE) | (edgeValue << (BLOCK_SIZE / 2)) | edgeValue;
                edgeDetectionImage.setRGB(x, y, egdeRGB);
            }
        }
        return edgeDetectionImage;
    }
}
