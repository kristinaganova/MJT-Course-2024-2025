package bg.sofia.uni.fmi.mjt.imagekit.filesystem;

import bg.sofia.uni.fmi.mjt.imagekit.filesystem.LocalFileSystemImageManager;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

import javax.imageio.ImageIO;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LocalFileSystemImageManagerTest {

    @Test
    void testLoadImage() throws IOException {
        BufferedImage originalImage = new BufferedImage(3, 3, BufferedImage.TYPE_INT_RGB);
        originalImage.setRGB(0, 0, 0xFF0000); // Червено
        originalImage.setRGB(1, 0, 0x00FF00); // Зелено
        originalImage.setRGB(2, 0, 0x0000FF); // Синьо

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(originalImage, "png", outputStream);
        byte[] imageBytes = outputStream.toByteArray();

        File tempFile = File.createTempFile("test_image", ".png");
        tempFile.deleteOnExit();

        try (var fileOutputStream = new java.io.FileOutputStream(tempFile)) {
            fileOutputStream.write(imageBytes);
        }

        LocalFileSystemImageManager manager = new LocalFileSystemImageManager();

        BufferedImage loadedImage = manager.loadImage(tempFile);

        assertEquals(originalImage.getWidth(), loadedImage.getWidth());
        assertEquals(originalImage.getHeight(), loadedImage.getHeight());

        for (int x = 0; x < originalImage.getWidth(); x++) {
            for (int y = 0; y < originalImage.getHeight(); y++) {
                assertEquals(originalImage.getRGB(x, y), loadedImage.getRGB(x, y));
            }
        }
    }

    @Test
    void testLoadImageFromFileWithNull() throws IOException {
        LocalFileSystemImageManager manager = new LocalFileSystemImageManager();
        assertThrows(IllegalArgumentException.class, () -> manager.loadImage(null));
    }

    @Test
    void testLoadImageFileDoesNotExist() throws IOException {
        File mockFile = mock(File.class);
        when(mockFile.exists()).thenReturn(false);

        LocalFileSystemImageManager manager = new LocalFileSystemImageManager();

        assertThrows(IOException.class, () -> {
            manager.loadImage(mockFile);
        });
    }

    @Test
    void testLoadImageNotAFile() throws IOException {
        File mockFile = mock(File.class);
        when(mockFile.exists()).thenReturn(true);
        when(mockFile.isFile()).thenReturn(false);

        LocalFileSystemImageManager manager = new LocalFileSystemImageManager();

        assertThrows(IOException.class, () -> {
            manager.loadImage(mockFile);
        });
    }

    @Test
    void testLoadImagesFromDirectory() throws IOException {
        File mockDir = mock(File.class);
        when(mockDir.exists()).thenReturn(true);
        when(mockDir.isDirectory()).thenReturn(true);

        File mockFile1 = mock(File.class);
        File mockFile2 = mock(File.class);
        when(mockFile1.isFile()).thenReturn(true);
        when(mockFile2.isFile()).thenReturn(true);

        LocalFileSystemImageManager manager = mock(LocalFileSystemImageManager.class);
        List<BufferedImage> mockImages = mock(List.class);
        when(manager.loadImagesFromDirectory(mockDir)).thenReturn(mockImages);

        List<BufferedImage> images = manager.loadImagesFromDirectory(mockDir);

        assertEquals(mockImages.size(), images.size());
    }

    @Test
    void testLoadImagesFromDirectoryInvalidDirectory() {
        File mockDir = mock(File.class);
        when(mockDir.exists()).thenReturn(false);

        LocalFileSystemImageManager manager = new LocalFileSystemImageManager();

        assertThrows(IOException.class, () -> {
            manager.loadImagesFromDirectory(mockDir);
        });
    }

    @Test
    void testLoadImagesFromDirectoryNullDirectory() {
        LocalFileSystemImageManager manager = new LocalFileSystemImageManager();

        assertThrows(IllegalArgumentException.class, () -> {
            manager.loadImagesFromDirectory(null);
        });
    }

    @Test
    void testLoadImagesFromDirectoryNotADirectory() {
        File mockFile = mock(File.class);
        when(mockFile.exists()).thenReturn(true);
        when(mockFile.isDirectory()).thenReturn(false);

        LocalFileSystemImageManager manager = new LocalFileSystemImageManager();

        assertThrows(IOException.class, () -> {
            manager.loadImagesFromDirectory(mockFile);
        });
    }

    @Test
    void testLoadImagesFromDirectoryWithUnsupportedFileFormats() throws IOException {
        File mockDir = mock(File.class);
        when(mockDir.exists()).thenReturn(true);
        when(mockDir.isDirectory()).thenReturn(true);

        File mockFile1 = mock(File.class);
        File mockFile2 = mock(File.class);

        when(mockFile1.getName()).thenReturn("image1.txt");
        when(mockFile2.getName()).thenReturn("image2.gif");

        File[] mockFiles = { mockFile1, mockFile2 };
        when(mockDir.listFiles()).thenReturn(mockFiles);

        LocalFileSystemImageManager manager = mock(LocalFileSystemImageManager.class);
        when(manager.loadImagesFromDirectory(mockDir)).thenCallRealMethod();

        List<BufferedImage> images = manager.loadImagesFromDirectory(mockDir);

        assertEquals(0, images.size());
    }

    @Test
    void testSaveImage() throws IOException {
        BufferedImage originalImage = new BufferedImage(3, 3, BufferedImage.TYPE_INT_RGB);
        originalImage.setRGB(0, 0, 0xFF0000); // Червено
        originalImage.setRGB(1, 0, 0x00FF00); // Зелено
        originalImage.setRGB(2, 0, 0x0000FF); // Синьо

        File mockFile = mock(File.class);
        when(mockFile.exists()).thenReturn(false);
        when(mockFile.createNewFile()).thenReturn(true);

        LocalFileSystemImageManager manager = mock(LocalFileSystemImageManager.class);
        doNothing().when(manager).saveImage(eq(originalImage), eq(mockFile));

        manager.saveImage(originalImage, mockFile);

        verify(manager, times(1)).saveImage(eq(originalImage), eq(mockFile));
    }

    @Test
    void testSaveImageNullImage() {
        LocalFileSystemImageManager manager = new LocalFileSystemImageManager();
        File mockFile = mock(File.class);

        assertThrows(IllegalArgumentException.class, () -> {
            manager.saveImage(null, mockFile);
        });
    }

    @Test
    void testSaveImageNullFile() {
        LocalFileSystemImageManager manager = new LocalFileSystemImageManager();
        BufferedImage mockImage = mock(BufferedImage.class);

        assertThrows(IllegalArgumentException.class, () -> {
            manager.saveImage(mockImage, null);
        });
    }

    @Test
    void testSaveImageFileAlreadyExists() throws IOException {
        BufferedImage mockImage = mock(BufferedImage.class);
        File mockFile = mock(File.class);
        when(mockFile.exists()).thenReturn(true);

        LocalFileSystemImageManager manager = new LocalFileSystemImageManager();

        assertThrows(IOException.class, () -> {
            manager.saveImage(mockImage, mockFile);
        });
    }

    @Test
    void testSaveImageParentDirectoryDoesNotExist() throws IOException {
        BufferedImage mockImage = mock(BufferedImage.class);
        File mockFile = mock(File.class);
        File mockParentDir = mock(File.class);
        when(mockFile.exists()).thenReturn(false);
        when(mockFile.getParentFile()).thenReturn(mockParentDir);
        when(mockParentDir.exists()).thenReturn(false);

        LocalFileSystemImageManager manager = new LocalFileSystemImageManager();

        assertThrows(IOException.class, () -> {
            manager.saveImage(mockImage, mockFile);
        });
    }

    @Test
    void testIsSupportedFormatWithSupportedExtension() {
        LocalFileSystemImageManager manager = new LocalFileSystemImageManager();

        File jpegFile = new File("image.jpeg");
        File pngFile = new File("image.png");
        File bmpFile = new File("image.bmp");

        assertTrue(manager.isSupportedFormat(jpegFile));
        assertTrue(manager.isSupportedFormat(pngFile));
        assertTrue(manager.isSupportedFormat(bmpFile));
    }

    @Test
    void testIsSupportedFormatWithUnsupportedExtension() {
        LocalFileSystemImageManager manager = new LocalFileSystemImageManager();

        File txtFile = new File("image.txt");
        File gifFile = new File("image.gif");
        File pdfFile = new File("image.pdf");

        assertFalse(manager.isSupportedFormat(txtFile));
        assertFalse(manager.isSupportedFormat(gifFile));
        assertFalse(manager.isSupportedFormat(pdfFile));
    }

    @Test
    void testGetFileExtension() {
        LocalFileSystemImageManager manager = new LocalFileSystemImageManager();

        File jpegFile = new File("image.jpeg");
        File pngFile = new File("image.png");
        File bmpFile = new File("image.bmp");
        File noExtensionFile = new File("image");

        assertEquals("jpeg", manager.getFileExtension(jpegFile));
        assertEquals("png", manager.getFileExtension(pngFile));
        assertEquals("bmp", manager.getFileExtension(bmpFile));
        assertEquals("", manager.getFileExtension(noExtensionFile));
    }

    @Test
    void testLoadImagesFromEmptyDirectory() throws IOException {
        File mockDir = mock(File.class);
        when(mockDir.exists()).thenReturn(true);
        when(mockDir.isDirectory()).thenReturn(true);
        when(mockDir.listFiles()).thenReturn(new File[]{});

        LocalFileSystemImageManager manager = new LocalFileSystemImageManager();

        List<BufferedImage> images = manager.loadImagesFromDirectory(mockDir);

        assertEquals(0, images.size());
    }

}
