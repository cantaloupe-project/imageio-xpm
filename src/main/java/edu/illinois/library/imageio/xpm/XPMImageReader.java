package edu.illinois.library.imageio.xpm;

import javax.imageio.IIOException;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Implementation supporting the XPM format version 3.</p>
 *
 * @see <a href="https://www.x.org/docs/XPM/xpm.pdf">XPM Manual</a>
 * @author Alex Dolski UIUC
 */
public final class XPMImageReader extends ImageReader {

    private static final Logger LOGGER =
            Logger.getLogger(XPMImageReader.class.getName());

    private static final Pattern COLOR_PATTERN =
            Pattern.compile("(m|s|g|g4|c)\\s+");
    private static final Pattern VALUES_PATTERN =
            Pattern.compile("\"\\s*(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+).*");

    private BufferedReader reader;

    private int width, height, numColors, numCharsPerPixel;
    private final Map<String, XPMPixel> colorMap = new HashMap<>();
    private boolean isWithinComment;

    XPMImageReader(XPMImageReaderSpi spi) {
        super(spi);
    }

    private void createReader() {
        if (reader == null) {
            reader = new BufferedReader(
                    new InputStreamReader(
                            new ImageInputStreamWrapper((ImageInputStream) input)));
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        colorMap.clear();
        width = height = numColors = numCharsPerPixel = 0;
        isWithinComment = false;
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "dispose(): " + e.getMessage(), e);
            } finally {
                reader = null;
            }
        }
    }

    @Override
    public int getHeight(int imageIndex) throws IOException {
        readValues();
        return height;
    }

    @Override
    public IIOMetadata getImageMetadata(int imageIndex) throws IOException {
        readValues();
        // Bits per sample here is interpreted as "maximum available bits per
        // sample in any of the display types."
        int bps = 1;
        for (XPMPixel pixel : colorMap.values()) {
            bps = Math.max(bps, pixel.getRGBComponentSize());
            bps = Math.max(bps, pixel.getGrayComponentSize());
        }
        return new XPMImageMetadata(width, height, bps);
    }

    @Override
    public Iterator<ImageTypeSpecifier> getImageTypes(int imageIndex) {
        return Arrays.asList(
                ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_ARGB),
                ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB),
                ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_BYTE_GRAY),
                ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_BYTE_BINARY))
                .iterator();
    }

    @Override
    public int getNumImages(boolean allowSearch) {
        return 1;
    }

    @Override
    public int getNumThumbnails(int imageIndex) {
        return 0;
    }

    /**
     * @return {@code null}, as there is no metadata associated with image
     *         streams/sequences for this format.
     */
    @Override
    public IIOMetadata getStreamMetadata() {
        return null;
    }

    @Override
    public int getWidth(int imageIndex) throws IOException {
        readValues();
        return width;
    }

    /**
     * Reads the "values" section containing the image dimensions, number of
     * colors, etc.
     */
    private void readValues() throws IOException {
        if (width != 0 || height != 0) {
            return; // values have already been read
        }
        createReader();

        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (!line.isEmpty() && !line.startsWith("//") &&
                    !line.startsWith("/*")) {
                Matcher matcher = VALUES_PATTERN.matcher(line);
                if (matcher.find()) {
                    parseValues(line);
                    break;
                }
            }
        }
    }

    /**
     * Parses the values line. This line contains four or six integers in base
     * 10 that correspond to: the width and height, the number of colors, the
     * number of characters per pixel, and possibly some other irrelevant
     * stuff.
     */
    private void parseValues(String line) throws IIOException {
        Matcher matcher = VALUES_PATTERN.matcher(line);
        if (matcher.find()) {
            width            = Integer.parseInt(matcher.group(1));
            height           = Integer.parseInt(matcher.group(2));
            numColors        = Integer.parseInt(matcher.group(3));
            numCharsPerPixel = Integer.parseInt(matcher.group(4));
        } else {
            throw new IIOException("Invalid values line: " + line);
        }
    }

    @Override
    public BufferedImage read(int imageIndex,
                              ImageReadParam readParam) throws IOException {
        readValues();
        readColorMap();

        final Dimension srcDims = new Dimension(
                getWidth(imageIndex),
                getHeight(imageIndex));
        Rectangle roi    = new Rectangle(0, 0, srcDims.width, srcDims.height);
        Point destOffset = new Point();
        int subsampX     = 1;
        int subsampY     = 1;

        if (readParam != null) {
            Point tmpOffset = readParam.getDestinationOffset();
            if (tmpOffset != null) {
                destOffset = tmpOffset;
            }
            Rectangle tmpROI = readParam.getSourceRegion();
            if (tmpROI != null) {
                roi = tmpROI;
            }
            subsampX = readParam.getSourceXSubsampling();
            subsampY = readParam.getSourceYSubsampling();
        }
        final BufferedImage bufImage = getDestination(
                readParam,
                getImageTypes(imageIndex),
                getWidth(imageIndex),
                getHeight(imageIndex));
        for (int srcY = 0; srcY < srcDims.height; srcY += subsampY) {
            String line = "";
            for (int sy = subsampY; sy > 0 && line != null; sy--) {
                line = reader.readLine();
            }
            if (line == null) {
                break; // EOF
            } else if (line.startsWith("/*") || line.startsWith("//")) {
                continue;
            }
            int quotePos = line.indexOf("\"");
            if (quotePos == -1) {
                break; // end of image data
            }
            line = line.substring(quotePos + 1);
            for (int srcX = 0; srcX < srcDims.width; srcX += subsampX) {
                final int destX = destOffset.x + (srcX - roi.x) / subsampX;
                final int destY = destOffset.y + (srcY - roi.y) / subsampY;
                if (srcX >= roi.x  &&
                        srcY >= roi.y &&
                        srcX < roi.x + roi.width &&
                        srcY < roi.y + roi.height &&
                        destX < bufImage.getWidth() &&
                        destY < bufImage.getHeight()) {
                    String pixelID = line.substring(
                            srcX * numCharsPerPixel,
                            srcX * numCharsPerPixel + numCharsPerPixel);
                    XPMPixel pixel = colorMap.get(pixelID);
                    writePixel(pixel, bufImage, destX, destY, readParam);
                }
            }
        }
        return bufImage;
    }

    private void writePixel(XPMPixel pixel,
                            BufferedImage bufImage,
                            int destX,
                            int destY, ImageReadParam readParam) {
        if (pixel == null) {
            return;
        }
        int color;
        if (readParam instanceof XPMImageReadParam) {
            XPMImageReadParam xpmReadParam = (XPMImageReadParam) readParam;
            switch (xpmReadParam.getDisplayType()) {
                case GRAYSCALE:
                case FOUR_LEVEL_GRAYSCALE:
                    color = pixel.getEffectiveGrayColor();
                    break;
                case MONOCHROME:
                    color = pixel.getEffectiveMonoColor();
                    break;
                default:
                    color = pixel.getEffectiveRGBColor();
                    break;
            }
        } else {
            color = pixel.getEffectiveRGBColor();
        }
        bufImage.setRGB(destX, destY, color);
    }

    private void readColorMap() throws IOException {
        if (!colorMap.isEmpty()) {
            return;
        }
        String line;
        while (colorMap.size() < numColors &&
                (line = reader.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("/*") && !line.contains("*/")) {
                isWithinComment = true;
            } else if (line.contains("*/")) {
                isWithinComment = false;
            } else if (!isWithinComment) {
                Matcher matcher = COLOR_PATTERN.matcher(line);
                if (matcher.find()) {
                    String id = line.substring(1, 1 + numCharsPerPixel);
                    line      = line.substring(1 + numCharsPerPixel);
                    colorMap.put(id, XPMPixel.parse(line));
                }
            }
        }
    }

}
