package edu.illinois.library.imageio.xpm;

import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

public class XPMImageReaderSpi extends ImageReaderSpi {

    private static final String DESCRIPTION        = "XPM Image Reader";
    private static final String VENDOR_NAME        = "University of Illinois at Urbana-Champaign Library";
    // N.B.: this needs to be kept in sync with <version> in pom.xml
    private static final String VERSION            = "1.0.1";
    private static final String READER_CLASS_NAME  =
            XPMImageReader.class.getName();
    private static final String[] NAMES            = { "xpm" };
    private static final String[] SUFFIXES         = { "xpm" };
    private static final String[] MIME_TYPES       = { "image/x‑xpixmap" };
    private static final String[] WRITER_SPI_NAMES = {};

    private static final boolean SUPPORTS_STANDARD_STREAM_METADATA_FORMAT  = false;
    private static final String NATIVE_STREAM_METADATA_FORMAT_NAME         = null;
    private static final String NATIVE_STREAM_METADATA_FORMAT_CLASS_NAME   = null;
    private static final String[] EXTRA_STREAM_METADATA_FORMAT_NAMES       = null;
    private static final String[] EXTRA_STREAM_METADATA_FORMAT_CLASS_NAMES = null;
    private static final boolean SUPPORTS_STANDARD_IMAGE_METADATA_FORMAT   = true;
    private static final String NATIVE_IMAGE_METADATA_FORMAT_NAME          = null;
    private static final String NATIVE_IMAGE_METADATA_FORMAT_CLASS_NAME    = null;
    private static final String[] EXTRA_IMAGE_METADATA_FORMAT_NAMES        = null;
    private static final String[] EXTRA_IMAGE_METADATA_FORMAT_CLASS_NAMES  = null;

    private static final byte[] XPM_SIGNATURE = "/* XPM */".getBytes();

    public XPMImageReaderSpi() {
        super(VENDOR_NAME,
                VERSION,
                NAMES,
                SUFFIXES,
                MIME_TYPES,
                READER_CLASS_NAME,
                new Class[] { ImageInputStream.class },
                WRITER_SPI_NAMES,
                SUPPORTS_STANDARD_STREAM_METADATA_FORMAT,
                NATIVE_STREAM_METADATA_FORMAT_NAME,
                NATIVE_STREAM_METADATA_FORMAT_CLASS_NAME,
                EXTRA_STREAM_METADATA_FORMAT_NAMES,
                EXTRA_STREAM_METADATA_FORMAT_CLASS_NAMES,
                SUPPORTS_STANDARD_IMAGE_METADATA_FORMAT,
                NATIVE_IMAGE_METADATA_FORMAT_NAME,
                NATIVE_IMAGE_METADATA_FORMAT_CLASS_NAME,
                EXTRA_IMAGE_METADATA_FORMAT_NAMES,
                EXTRA_IMAGE_METADATA_FORMAT_CLASS_NAMES);
    }

    @Override
    public boolean canDecodeInput(Object source) {
        if (source instanceof ImageInputStream) {
            ImageInputStream inputStream = (ImageInputStream) source;
            byte[] bytes = new byte[XPM_SIGNATURE.length];
            try {
                inputStream.mark();
                inputStream.readFully(bytes);
                inputStream.reset();
            } catch (IOException ignore) {
                // The official example swallows this. See:
                // https://docs.oracle.com/javase/8/docs/technotes/guides/imageio/spec/extending.fm3.html
                return false;
            }
            return Arrays.equals(XPM_SIGNATURE, bytes);
        }
        return false;
    }

    @Override
    public ImageReader createReaderInstance(Object extension) {
        return new XPMImageReader(this);
    }

    @Override
    public String getDescription(Locale locale) {
        return DESCRIPTION;
    }

}
