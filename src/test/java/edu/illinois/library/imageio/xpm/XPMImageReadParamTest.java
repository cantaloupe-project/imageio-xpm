package edu.illinois.library.imageio.xpm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class XPMImageReadParamTest {

    private XPMImageReadParam instance;

    @BeforeEach
    void setUp() {
        instance = new XPMImageReadParam();
    }

    @Test
    void setDisplayTypeWithNullArgument() {
        assertThrows(NullPointerException.class, () ->
                instance.setDisplayType(null));
    }

}