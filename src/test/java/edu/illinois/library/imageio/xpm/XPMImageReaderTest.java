package edu.illinois.library.imageio.xpm;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;
import static edu.illinois.library.imageio.util.ImageAssert.*;

class XPMImageReaderTest {

    private static final String ICON_OUTPUT_PATH_VM_ARG = "iconOutputPath";

    @BeforeAll
    static void beforeClass() {
        System.setProperty("java.awt.headless", "true");
    }

    private XPMImageReader newReaderForImage(String filename) {
        try {
            XPMImageReader reader = new XPMImageReader(new XPMImageReaderSpi());
            ImageInputStream is = new FileImageInputStream(
                    new File("./src/test/resources/" + filename));
            reader.setInput(is);
            return reader;
        } catch (IOException e) {
            fail(e);
            return null;
        }
    }

    @Test
    void getHeight() throws Exception {
        XPMImageReader reader = newReaderForImage("xpm.xpm");
        try {
            assertEquals(22, reader.getHeight(0));
        } finally {
            reader.dispose();
        }
    }

    @Test
    void getImageMetadata() throws Exception {
        XPMImageReader reader = newReaderForImage("xpm.xpm");
        try {
            // This is tested more thoroughly in XPMImageMetadataTest.
            assertNotNull(reader.getImageMetadata(0));
        } finally {
            reader.dispose();
        }
    }

    @Test
    void getImageTypes() {
        XPMImageReader reader = newReaderForImage("xpm.xpm");
        try {
            Iterator<ImageTypeSpecifier> it = reader.getImageTypes(0);
            assertEquals(ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_ARGB),
                    it.next());
            assertEquals(ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB),
                    it.next());
            assertEquals(ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_BYTE_GRAY),
                    it.next());
            assertEquals(ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_BYTE_BINARY),
                    it.next());
            assertFalse(it.hasNext());
        } finally {
            reader.dispose();
        }
    }

    @Test
    void getNumImages() {
        XPMImageReader reader = newReaderForImage("xpm.xpm");
        try {
            assertEquals(1, reader.getNumImages(true));
        } finally {
            reader.dispose();
        }
    }

    @Test
    void getNumImagesWithFalseArgument() {
        XPMImageReader reader = newReaderForImage("xpm.xpm");
        try {
            assertEquals(1, reader.getNumImages(false));
        } finally {
            reader.dispose();
        }
    }

    @Test
    void getNumThumbnails() {
        XPMImageReader reader = newReaderForImage("xpm.xpm");
        try {
            assertEquals(0, reader.getNumThumbnails(0));
        } finally {
            reader.dispose();
        }
    }

    @Test
    void getStreamMetadata() {
        XPMImageReader reader = newReaderForImage("xpm.xpm");
        try {
            assertNull(reader.getStreamMetadata());
        } finally {
            reader.dispose();
        }
    }

    @Test
    void getWidth() throws Exception {
        XPMImageReader reader = newReaderForImage("xpm.xpm");
        try {
            assertEquals(22, reader.getWidth(0));
        } finally {
            reader.dispose();
        }
    }

    @Test
    void read() throws Exception {
        XPMImageReader reader = newReaderForImage("xpm.xpm");
        try {
            BufferedImage image = reader.read(0);
            assertEquals(BufferedImage.TYPE_INT_ARGB, image.getType());
            assertEquals(22, image.getWidth());
            assertEquals(22, image.getHeight());
            assertEmpty(image.getRGB(5, 5));
            assertRGB(image.getRGB(3, 15), 255, 0, 0);
            assertRGB(image.getRGB(13, 3), 0, 0, 0);
        } finally {
            reader.dispose();
        }
    }

    @Test
    void readWith16BitsPerSample() throws Exception {
        XPMImageReader reader = newReaderForImage("16bit.xpm");
        try {
            BufferedImage image = reader.read(0);
            assertEquals(48, image.getWidth());
            assertEquals(48, image.getHeight());
            assertEmpty(image.getRGB(1, 1));
            assertRGB(image.getRGB(10, 10), 223, 146, 57);
            assertRGB(image.getRGB(20, 20), 49, 49, 40);
        } finally {
            reader.dispose();
        }
    }

    @Test
    void readWith1CharacterPerPixel() throws Exception {
        XPMImageReader reader = newReaderForImage("1_char_per_pixel.xpm");
        try {
            BufferedImage image = reader.read(0);
            assertEquals(22, image.getWidth());
            assertEquals(22, image.getHeight());
            assertEmpty(image.getRGB(5, 5));
            assertRGB(image.getRGB(3, 15), 255, 0, 0);
            assertRGB(image.getRGB(13, 3), 0, 0, 0);
        } finally {
            reader.dispose();
        }
    }

    @Test
    void readWith2CharactersPerPixel() throws Exception {
        XPMImageReader reader = newReaderForImage("2_chars_per_pixel.xpm");
        try {
            BufferedImage image = reader.read(0);
            assertEquals(22, image.getWidth());
            assertEquals(22, image.getHeight());
            assertEmpty(image.getRGB(5, 5));
            assertRGB(image.getRGB(3, 15), 255, 0, 0);
            assertRGB(image.getRGB(13, 3), 0, 0, 0);
        } finally {
            reader.dispose();
        }
    }

    @Test
    void readWithDestinationOffset() throws Exception {
        XPMImageReader reader = newReaderForImage("xpm.xpm");
        try {
            ImageReadParam param = reader.getDefaultReadParam();
            param.setDestinationOffset(new Point(10, 10));
            BufferedImage image = reader.read(0, param);
            assertEquals(32, image.getWidth());
            assertEquals(32, image.getHeight());
            assertRGB(image.getRGB(5, 5), 0, 0, 0);
            assertRGB(image.getRGB(18, 16), 0, 255, 0);
            assertRGB(image.getRGB(18, 20), 255, 0, 0);
        } finally {
            reader.dispose();
        }
    }

    @Test
    void readWithHexColors() throws Exception {
        XPMImageReader reader = newReaderForImage("hex_colors.xpm");
        try {
            BufferedImage image = reader.read(0);
            assertEquals(32, image.getWidth());
            assertEquals(28, image.getHeight());
            assertRGB(image.getRGB(3, 3), 70, 136, 85);
            assertRGB(image.getRGB(25, 25), 153, 133, 107);
        } finally {
            reader.dispose();
        }
    }

    /**
     * Hotspots are irrelevant to the reader; this just tests whether it can
     * parse a file that contains them.
     */
    @Test
    void readWithHotspot() throws Exception {
        XPMImageReader reader = newReaderForImage("hotspot.xpm");
        try {
            BufferedImage image = reader.read(0);
            assertEquals(22, image.getWidth());
            assertEquals(22, image.getHeight());
        } finally {
            reader.dispose();
        }
    }

    @Test
    void readWithOddDimensions() throws Exception {
        XPMImageReader reader = newReaderForImage("odd_dimensions.xpm");
        try {
            BufferedImage image = reader.read(0);
            assertEquals(21, image.getWidth());
            assertEquals(18, image.getHeight());
        } finally {
            reader.dispose();
        }
    }

    @Test
    void readWithSourceRegion() throws Exception {
        XPMImageReader reader = newReaderForImage("xpm.xpm");
        try {
            ImageReadParam param = reader.getDefaultReadParam();
            param.setSourceRegion(new Rectangle(11, 11, 11, 11));
            BufferedImage image = reader.read(0, param);
            assertEquals(11, image.getWidth());
            assertEquals(11, image.getHeight());
            assertRGB(image.getRGB(0, 0), 0, 0, 0);
            assertRGB(image.getRGB(1, 0), 0, 0, 0);
            assertRGB(image.getRGB(5, 4), 0, 255, 0);
            assertRGB(image.getRGB(10, 10), 255, 0, 0);
            assertRGB(image.getRGB(9, 10), 0, 0, 0);
        } finally {
            reader.dispose();
        }
    }

    @Test
    void readWithSubsampling() throws Exception {
        XPMImageReader reader = newReaderForImage("xpm.xpm");
        try {
            ImageReadParam param = reader.getDefaultReadParam();
            param.setSourceSubsampling(2, 2, 0, 0);
            BufferedImage image = reader.read(0, param);
            assertEquals(11, image.getWidth());
            assertEquals(11, image.getHeight());
            assertEmpty(image.getRGB(0, 0));
            assertRGB(image.getRGB(0, 8), 0, 0, 0);
            assertRGB(image.getRGB(8, 0), 255, 255, 0);
        } finally {
            reader.dispose();
        }
    }

    @Test
    void readWithTypeSpecifierOfIntegerRGB() throws Exception {
        XPMImageReader reader = newReaderForImage("xpm.xpm");
        try {
            ImageReadParam param = reader.getDefaultReadParam();
            param.setDestinationType(ImageTypeSpecifier.createFromBufferedImageType(
                    BufferedImage.TYPE_INT_RGB));
            BufferedImage image = reader.read(0, param);
            assertEquals(BufferedImage.TYPE_INT_RGB, image.getType());
            assertEquals(22, image.getWidth());
            assertEquals(22, image.getHeight());
            assertRGB(image.getRGB(5, 5), 0, 0, 0);
            assertRGB(image.getRGB(3, 15), 255, 0, 0);
            assertRGB(image.getRGB(13, 3), 0, 0, 0);
        } finally {
            reader.dispose();
        }
    }

    @Test
    void readWithTypeSpecifierOfIntegerARGB() throws Exception {
        XPMImageReader reader = newReaderForImage("xpm.xpm");
        try {
            ImageReadParam param = reader.getDefaultReadParam();
            param.setDestinationType(ImageTypeSpecifier.createFromBufferedImageType(
                    BufferedImage.TYPE_INT_ARGB));
            BufferedImage image = reader.read(0, param);
            assertEquals(BufferedImage.TYPE_INT_ARGB, image.getType());
            assertEquals(22, image.getWidth());
            assertEquals(22, image.getHeight());
            assertEmpty(image.getRGB(5, 5));
            assertRGB(image.getRGB(3, 15), 255, 0, 0);
            assertRGB(image.getRGB(13, 3), 0, 0, 0);
        } finally {
            reader.dispose();
        }
    }

    @Test
    void readWithTypeSpecifierOfByteGray() throws Exception {
        XPMImageReader reader = newReaderForImage("xpm.xpm");
        try {
            ImageReadParam param = reader.getDefaultReadParam();
            param.setDestinationType(ImageTypeSpecifier.createFromBufferedImageType(
                    BufferedImage.TYPE_BYTE_GRAY));
            BufferedImage image = reader.read(0, param);
            assertEquals(BufferedImage.TYPE_BYTE_GRAY, image.getType());
            assertEquals(22, image.getWidth());
            assertEquals(22, image.getHeight());
            assertRGB(image.getRGB(5, 5), 0, 0, 0);
            assertRGB(image.getRGB(3, 15), 127, 127, 127);
            assertRGB(image.getRGB(13, 3), 0, 0, 0);
        } finally {
            reader.dispose();
        }
    }

    @Test
    void readWithTypeSpecifierOfByteBinary() throws Exception {
        XPMImageReader reader = newReaderForImage("xpm.xpm");
        try {
            ImageReadParam param = reader.getDefaultReadParam();
            param.setDestinationType(ImageTypeSpecifier.createFromBufferedImageType(
                    BufferedImage.TYPE_BYTE_BINARY));
            BufferedImage image = reader.read(0, param);
            assertEquals(BufferedImage.TYPE_BYTE_BINARY, image.getType());
            assertEquals(22, image.getWidth());
            assertEquals(22, image.getHeight());
            assertRGB(image.getRGB(5, 5), 0, 0, 0);
            assertRGB(image.getRGB(3, 15), 0, 0, 0);
            assertRGB(image.getRGB(13, 3), 0, 0, 0);
        } finally {
            reader.dispose();
        }
    }

    @Test
    void readWithDisplayTypeOfColor() throws Exception {
        XPMImageReader reader = newReaderForImage("xpm.xpm");
        try {
            XPMImageReadParam param = new XPMImageReadParam();
            param.setDisplayType(DisplayType.COLOR);
            BufferedImage image = reader.read(0, param);
            assertEquals(BufferedImage.TYPE_INT_ARGB, image.getType());
            assertEquals(22, image.getWidth());
            assertEquals(22, image.getHeight());
            assertEmpty(image.getRGB(5, 5));
            assertRGB(image.getRGB(3, 15), 255, 0, 0);
            assertRGB(image.getRGB(13, 3), 0, 0, 0);
        } finally {
            reader.dispose();
        }
    }

    @Test
    void readWithDisplayTypeOfGrayscale() throws Exception {
        XPMImageReader reader = newReaderForImage("xpm.xpm");
        try {
            XPMImageReadParam param = new XPMImageReadParam();
            param.setDisplayType(DisplayType.GRAYSCALE);
            BufferedImage image = reader.read(0, param);
            assertEquals(BufferedImage.TYPE_INT_ARGB, image.getType());
            assertEquals(22, image.getWidth());
            assertEquals(22, image.getHeight());
            assertEmpty(image.getRGB(5, 5));
            assertRGB(image.getRGB(3, 15), 76, 76, 76);
            assertRGB(image.getRGB(13, 3), 0, 0, 0);
        } finally {
            reader.dispose();
        }
    }

    @Test
    void readWithDisplayTypeOfFourLevelGrayscale() throws Exception {
        XPMImageReader reader = newReaderForImage("xpm.xpm");
        try {
            XPMImageReadParam param = new XPMImageReadParam();
            param.setDisplayType(DisplayType.FOUR_LEVEL_GRAYSCALE);
            BufferedImage image = reader.read(0, param);
            assertEquals(BufferedImage.TYPE_INT_ARGB, image.getType());
            assertEquals(22, image.getWidth());
            assertEquals(22, image.getHeight());
            assertEmpty(image.getRGB(5, 5));
            assertRGB(image.getRGB(3, 15), 76, 76, 76);
            assertRGB(image.getRGB(13, 3), 0, 0, 0);
        } finally {
            reader.dispose();
        }
    }

    @Test
    void readWithDisplayTypeOfMonochrome() throws Exception {
        XPMImageReader reader = newReaderForImage("xpm.xpm");
        try {
            XPMImageReadParam param = new XPMImageReadParam();
            param.setDisplayType(DisplayType.MONOCHROME);
            BufferedImage image = reader.read(0, param);
            assertEquals(BufferedImage.TYPE_INT_ARGB, image.getType());
            assertEquals(22, image.getWidth());
            assertEquals(22, image.getHeight());
            assertEmpty(image.getRGB(5, 5));
            assertRGB(image.getRGB(3, 15), 255, 255, 255);
            assertRGB(image.getRGB(13, 3), 0, 0, 0);
        } finally {
            reader.dispose();
        }
    }

    /**
     * Tests reading an image written by XV.
     */
    @Test
    void readWithXVImage() throws Exception {
        XPMImageReader reader = newReaderForImage("xv.xpm");
        try {
            BufferedImage image = reader.read(0);
            assertEquals(BufferedImage.TYPE_INT_ARGB, image.getType());
            assertEquals(48, image.getWidth());
            assertEquals(48, image.getHeight());
            assertRGB(image.getRGB(2, 2), 5, 3, 7);
            assertRGB(image.getRGB(46, 6), 5, 50, 207);
        } finally {
            reader.dispose();
        }
    }

    /**
     * Ignored unless {@link #ICON_OUTPUT_PATH_VM_ARG} is set.
     */
    @Test
    void readIcons() throws IOException {
        String pathStr = System.getProperty(ICON_OUTPUT_PATH_VM_ARG);
        assumeTrue(pathStr != null);

        final Path start = Paths.get("./src/test/resources/icons");
        final Path dest  = Paths.get(pathStr);

        if (!Files.exists(dest)) {
            Files.createDirectories(dest);
        }
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path path,
                                             BasicFileAttributes attrs) throws IOException {
                if (!path.toString().endsWith(".xpm")) {
                    return FileVisitResult.CONTINUE;
                }
                XPMImageReader reader = null;
                try {
                    reader = new XPMImageReader(new XPMImageReaderSpi());
                    ImageInputStream is = new FileImageInputStream(path.toFile());
                    reader.setInput(is);
                    BufferedImage image = reader.read(0);
                    ImageIO.write(image, "PNG", dest.resolve(path.getFileName() + ".png").toFile());
                } catch (Exception e) {
                    System.err.println("Fixture: " + path);
                    throw e;
                } finally {
                    if (reader != null) {
                        reader.dispose();
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

}
