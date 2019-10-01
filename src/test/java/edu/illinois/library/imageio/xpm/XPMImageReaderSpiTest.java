package edu.illinois.library.imageio.xpm;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class XPMImageReaderSpiTest {

    @BeforeAll
    static void beforeClass() {
        System.setProperty("java.awt.headless", "true");
        ImageIO.scanForPlugins();
    }

    @Test
    void serviceProviderRegistrationByFormatName() {
        Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName("xpm");
        assertTrue(it.hasNext());
        assertTrue(it.next() instanceof XPMImageReader);

        it = ImageIO.getImageReadersByFormatName("XPM");
        assertTrue(it.hasNext());
        assertTrue(it.next() instanceof XPMImageReader);
    }

    @Test
    void serviceProviderRegistrationBySuffix() {
        Iterator<ImageReader> it = ImageIO.getImageReadersBySuffix("xpm");
        assertTrue(it.hasNext());
        assertTrue(it.next() instanceof XPMImageReader);
    }

    @Test
    void serviceProviderRegistrationByMIMEType() {
        Iterator<ImageReader> it = ImageIO.getImageReadersByMIMEType("image/x‑xpixmap");
        assertTrue(it.hasNext());
        assertTrue(it.next() instanceof XPMImageReader);
    }

}
