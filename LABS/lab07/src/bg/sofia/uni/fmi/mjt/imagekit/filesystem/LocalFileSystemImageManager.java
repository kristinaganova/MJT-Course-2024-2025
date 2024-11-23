package bg.sofia.uni.fmi.mjt.imagekit.filesystem;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LocalFileSystemImageManager implements FileSystemImageManager {

    @Override
    public BufferedImage loadImage(File imageFile) throws IOException {
        if (imageFile == null) {
            throw new IllegalArgumentException("Image file cannot be null");
        }

        if (!imageFile.exists() || !imageFile.isFile()) {
            throw new IOException("Image file " + imageFile + " does not exist or is not a file");
        }

        return ImageIO.read(imageFile);
    }

    @Override
    public List<BufferedImage> loadImagesFromDirectory(File imagesDirectory) throws IOException {
        if (imagesDirectory == null) {
            throw new IllegalArgumentException("Directory cannot be null.");
        }
        if (!imagesDirectory.exists() || !imagesDirectory.isDirectory()) {
            throw new IOException("Invalid directory: " + imagesDirectory.getPath());
        }

        List<BufferedImage> images = new ArrayList<>();
        for (File file : imagesDirectory.listFiles()) {
            if (file.isFile() && isSupportedFormat(file)) {
                images.add(loadImage(file));
            }
        }
        return images;
    }

    @Override
    public void saveImage(BufferedImage image, File imageFile) throws IOException {

        if (image == null || imageFile == null) {
            throw new IllegalArgumentException("Image or file cannot be null.");
        }

        if (imageFile.exists()) {
            throw new IOException("File already exists: " + imageFile.getPath());
        }

        File parentDir = imageFile.getParentFile();

        if (!parentDir.exists()) {
            throw new IOException("Parent directory does not exist: " + parentDir.getPath());
        }

        String format = getFileExtension(imageFile);
        ImageIO.write(image, format, imageFile);
    }

    public boolean isSupportedFormat(File file) {
        String ext = getFileExtension(file);
        return ext.equalsIgnoreCase("jpeg") || ext.equalsIgnoreCase("png")
                                    || ext.equalsIgnoreCase("bmp");
    }

    public String getFileExtension(File file) {
        String name = file.getName();
        int dotIndex = name.lastIndexOf('.');
        return (dotIndex == -1) ? "" : name.substring(dotIndex + 1).toLowerCase();
    }
}
