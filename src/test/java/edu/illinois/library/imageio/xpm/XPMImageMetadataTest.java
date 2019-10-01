package edu.illinois.library.imageio.xpm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class XPMImageMetadataTest {

    private XPMImageMetadata instance;

    @BeforeEach
    void setUp() throws Exception {
        XPMImageReader reader = new XPMImageReader(new XPMImageReaderSpi());
        ImageInputStream is = new FileImageInputStream(
                new File("./src/test/resources/xpm.xpm"));
        reader.setInput(is);

        instance = new XPMImageMetadata(22, 22, 24);
    }

    @Test
    void getAsTreeWithNullFormatName() {
        assertThrows(IllegalArgumentException.class, () ->
                instance.getAsTree(null));
    }

    @Test
    void getAsTreeWithUnknownFormatName() {
        assertThrows(IllegalArgumentException.class, () ->
                instance.getAsTree("whatever"));
    }

    @Test
    void getAsTree() {
        Node root = instance.getAsTree(instance.getMetadataFormatNames()[0]);
        NodeList children = root.getChildNodes();
        for (int c = 0; c < children.getLength(); c++) {
            Node node = children.item(c);
            if ("Data".equals(node.getLocalName())) {
                testDataNode(node);
            } else if ("Dimension".equals(node.getLocalName())) {
                testDimensionNode(node);
            } else {
                fail("Unrecognized root child: " + node.getLocalName());
            }
        }
    }

    private void testDataNode(Node dataNode) {
        NodeList children = dataNode.getChildNodes();
        int length = children.getLength();
        assertEquals(4, length);
        for (int c = 0; c < length; c++) {
            Node node = children.item(c);
            switch (node.getLocalName()) {
                case "PlanarConfiguration":
                    assertEquals("PixelInterleaved",
                            node.getAttributes().getNamedItem("value").getNodeValue());
                    break;
                case "SampleFormat":
                    assertEquals("Index",
                            node.getAttributes().getNamedItem("value").getNodeValue());
                    break;
                case "BitsPerSample":
                    assertEquals("24",
                            node.getAttributes().getNamedItem("value").getNodeValue());
                    break;
                case "SignificantBitsPerSample":
                    assertEquals("24",
                            node.getAttributes().getNamedItem("value").getNodeValue());
                    break;
                default:
                    fail("Unrecognized data node child: " +
                            node.getLocalName());
                    break;
            }
        }
    }

    private void testDimensionNode(Node dataNode) {
        NodeList children = dataNode.getChildNodes();
        int length = children.getLength();
        assertEquals(4, length);
        for (int c = 0; c < length; c++) {
            Node node = children.item(c);
            switch (node.getLocalName()) {
                case "PixelAspectRatio":
                    assertEquals("1.0",
                            node.getAttributes().getNamedItem("value").getNodeValue());
                    break;
                case "ImageOrientation":
                    assertEquals("Normal",
                            node.getAttributes().getNamedItem("value").getNodeValue());
                    break;
                case "HorizontalScreenSize":
                    assertEquals("22",
                            node.getAttributes().getNamedItem("value").getNodeValue());
                    break;
                case "VerticalScreenSize":
                    assertEquals("22",
                            node.getAttributes().getNamedItem("value").getNodeValue());
                    break;
                default:
                    fail("Unrecognized dimension node child: " +
                            node.getLocalName());
                    break;
            }
        }
    }

    @Test
    void getNativeMetadataFormatName() {
        assertEquals("javax_imageio_1.0", instance.getMetadataFormatNames()[0]);
    }

    @Test
    void isReadOnly() {
        assertTrue(instance.isReadOnly());
    }

    @Test
    void isStandardMetadataFormatSupported() {
        assertTrue(instance.isStandardMetadataFormatSupported());
    }

    @Test
    void mergeTree() {
        assertThrows(IllegalStateException.class, () ->
                instance.mergeTree("whatever", null));
    }

    @Test
    void reset() {
        assertThrows(IllegalStateException.class, () -> instance.reset());
    }

}
