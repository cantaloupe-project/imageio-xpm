package edu.illinois.library.imageio.xpm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class XPMPixelTest {

    @Test
    void parseWithX11Colors1() {
        XPMPixel actual = XPMPixel.parse("\tc sea green\",");
        assertEquals(0xff2e8b57, actual.getRGBColor());
        assertEquals(0, actual.getGrayColor());
        assertEquals(0, actual.getMonoColor());
    }

    @Test
    void parseWithX11Colors2() {
        XPMPixel actual = XPMPixel.parse("c red       m white  s light_color ");
        assertEquals(0xffff0000, actual.getRGBColor());
        assertEquals(0, actual.getGrayColor());
        assertEquals(0xffffffff, actual.getMonoColor());
    }

    @Test
    void parseWithX11Colors3() {
        XPMPixel actual = XPMPixel.parse("              m black  s dark_color ");
        assertEquals(0, actual.getRGBColor());
        assertEquals(0, actual.getGrayColor());
        assertEquals(0xff000000, actual.getMonoColor());
    }

    @Test
    void parseWithTransparentColor() {
        XPMPixel actual = XPMPixel.parse("  c none               s mask ");
        assertEquals(0, actual.getRGBColor());
        assertEquals(0, actual.getGrayColor());
        assertEquals(0, actual.getMonoColor());
    }

    @Test
    void parseWith3CharacterHexadecimalColors() {
        XPMPixel actual = XPMPixel.parse("c #fea");
        assertEquals(0xffffeeaa, actual.getRGBColor());
        assertEquals(0, actual.getGrayColor());
        assertEquals(0, actual.getMonoColor());
    }

    @Test
    void parseWith6CharacterHexadecimalColors() {
        XPMPixel actual = XPMPixel.parse("c #ff0000       g #808080  s light_color ");
        assertEquals(0xffff0000, actual.getRGBColor());
        assertEquals(0xff808080, actual.getGrayColor());
        assertEquals(0, actual.getMonoColor());
    }

    @Test
    void parseWith12CharacterHexadecimalColors() {
        XPMPixel actual = XPMPixel.parse("c #ffc8a030ba34");
        assertEquals(0xffffa0b9, actual.getRGBColor());
        assertEquals(0, actual.getMonoColor());
    }

    @Test
    void getEffectiveGrayColorWithGrayColorSet() {
        XPMPixel pixel = XPMPixel.parse("g black");
        assertEquals(0xff000000, pixel.getEffectiveGrayColor());
    }

    @Test
    void getEffectiveGrayColorWithRGBColorSet() {
        XPMPixel pixel = XPMPixel.parse("c red");
        assertEquals(0xff4c4c4c, pixel.getEffectiveGrayColor());

        pixel = XPMPixel.parse("c none");
        assertEquals(0, pixel.getEffectiveGrayColor());
    }

    @Test
    void getEffectiveGrayColorWithFourLevelGrayColorSet() {
        XPMPixel pixel = XPMPixel.parse("g4 #202020");
        assertEquals(0xff202020, pixel.getEffectiveGrayColor());
    }

    @Test
    void getEffectiveGrayColorWithMonoColorSet() {
        XPMPixel pixel = XPMPixel.parse("g black");
        assertEquals(0xff000000, pixel.getEffectiveGrayColor());
    }

    @Test
    void getEffectiveMonoColorWithRGBColorSet() {
        XPMPixel pixel = XPMPixel.parse("c red");
        assertEquals(0xff4c4c4c, pixel.getEffectiveGrayColor());

        pixel = XPMPixel.parse("c none");
        assertEquals(0, pixel.getEffectiveGrayColor());
    }

    @Test
    void getEffectiveMonoColorWithGrayColorSet() {
        XPMPixel pixel = XPMPixel.parse("g black");
        assertEquals(0xff000000, pixel.getEffectiveMonoColor());
    }

    @Test
    void getEffectiveMonoColorWithFourLevelGrayColorSet() {
        XPMPixel pixel = XPMPixel.parse("g4 black");
        assertEquals(0xff000000, pixel.getEffectiveMonoColor());
    }

    @Test
    void getEffectiveMonoColorWithMonoColorSet() {
        XPMPixel pixel = XPMPixel.parse("g black");
        assertEquals(0xff000000, pixel.getEffectiveMonoColor());
    }

    @Test
    void getEffectiveARGBColorWithRGBColorSet() {
        XPMPixel pixel = XPMPixel.parse("c red");
        assertEquals(0xffff0000, pixel.getEffectiveARGBColor());

        pixel = XPMPixel.parse("c none");
        assertEquals(0x00000000, pixel.getEffectiveARGBColor());
    }

    @Test
    void getEffectiveARGBColorWithGrayColorSet() {
        XPMPixel pixel = XPMPixel.parse("g black");
        assertEquals(0xff000000, pixel.getEffectiveARGBColor());
    }

    @Test
    void getEffectiveARGBColorWithFourLevelGrayColorSet() {
        XPMPixel pixel = XPMPixel.parse("g4 black");
        assertEquals(0xff000000, pixel.getEffectiveARGBColor());
    }

    @Test
    void getEffectiveARGBColorWithMonoColorSet() {
        XPMPixel pixel = XPMPixel.parse("g black");
        assertEquals(0xff000000, pixel.getEffectiveARGBColor());
    }

    @Test
    void getEffectiveRGBColorWithRGBColorSet() {
        XPMPixel pixel = XPMPixel.parse("c red");
        assertEquals(0xffff0000, pixel.getEffectiveRGBColor());

        pixel = XPMPixel.parse("c none");
        assertEquals(0, pixel.getEffectiveRGBColor());
    }

    @Test
    void getEffectiveRGBColorWithGrayColorSet() {
        XPMPixel pixel = XPMPixel.parse("g black");
        assertEquals(0xff000000, pixel.getEffectiveRGBColor());
    }

    @Test
    void getEffectiveRGBColorWithFourLevelGrayColorSet() {
        XPMPixel pixel = XPMPixel.parse("g4 black");
        assertEquals(0xff000000, pixel.getEffectiveRGBColor());
    }

    @Test
    void getEffectiveRGBColorWithMonoColorSet() {
        XPMPixel pixel = XPMPixel.parse("g black");
        assertEquals(0xff000000, pixel.getEffectiveRGBColor());
    }

}