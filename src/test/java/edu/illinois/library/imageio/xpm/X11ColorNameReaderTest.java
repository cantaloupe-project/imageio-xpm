package edu.illinois.library.imageio.xpm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class X11ColorNameReaderTest {

    private X11ColorNameReader instance;

    @BeforeEach
    void setUp() {
        instance = new X11ColorNameReader();
    }

    @Test
    void read() {
        Map<String,Integer> actual = instance.read();
        assertTrue(actual.size() > 850);
        assertEquals(0xffa2b5cd, actual.get("LightSteelBlue3"));
    }

}
