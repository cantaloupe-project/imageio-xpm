package edu.illinois.library.imageio.util;

import static org.junit.jupiter.api.Assertions.*;

public final class ImageAssert {

    public static void assertEmpty(int pixel) {
        int alpha = (pixel >>> 24) & 0xff;
        assertEquals(0, alpha);
    }

    /**
     * @param pixel         Pixel to test.
     * @param expectedRed   Expected red value of the pixel.
     * @param expectedGreen Expected green value of the pixel.
     * @param expectedBlue  Expected blue value of the pixel.
     */
    public static void assertRGB(int pixel,
                                 int expectedRed,
                                 int expectedGreen,
                                 int expectedBlue) {
        int red   = (pixel >>> 16) & 0xff;
        int green = (pixel >>> 8) & 0xff;
        int blue  = pixel & 0xff;
        assertEquals(expectedRed, red);
        assertEquals(expectedGreen, green);
        assertEquals(expectedBlue, blue);
    }

    private ImageAssert() {}

}
