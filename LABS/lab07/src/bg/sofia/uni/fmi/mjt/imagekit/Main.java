package bg.sofia.uni.fmi.mjt.imagekit;

import bg.sofia.uni.fmi.mjt.imagekit.algorithm.ImageAlgorithm;
import bg.sofia.uni.fmi.mjt.imagekit.algorithm.blur.GaussianBlur;
import bg.sofia.uni.fmi.mjt.imagekit.algorithm.detection.SobelEdgeDetection;
import bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale.LuminosityGrayscale;
import bg.sofia.uni.fmi.mjt.imagekit.filesystem.FileSystemImageManager;
import bg.sofia.uni.fmi.mjt.imagekit.filesystem.LocalFileSystemImageManager;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Main {
    public static void main(String[] args) throws IOException {

        System.out.println("Working Directory: " + System.getProperty("user.dir"));

        FileSystemImageManager fsImageManager = new LocalFileSystemImageManager();

        File file = new File("resources/kitten.png");
        if (!file.exists()) {
            throw new IOException("File not found: " + file.getAbsolutePath());
        }
        BufferedImage image = fsImageManager.loadImage(file);

        ImageAlgorithm blur = new GaussianBlur();
        BufferedImage bluredImage = blur.process(image);

        File outputDir = new File("output");
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw new IOException("Failed to create output directory");
        }

        fsImageManager.saveImage(bluredImage, new File("output/blurred.png"));
    }
}
