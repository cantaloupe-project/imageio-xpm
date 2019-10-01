package edu.illinois.library.imageio.xpm;

import org.w3c.dom.Node;

import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormatImpl;
import javax.imageio.metadata.IIOMetadataNode;

/**
 * @see <a href="https://docs.oracle.com/javase/7/docs/api/javax/imageio/metadata/doc-files/standard_metadata.html">
 *     Standard (Plug-in Neutral) Metadata Format Specification</a>
 */
final class XPMImageMetadata extends IIOMetadata {

    private static final String DATA_NODE =
            "Data";
    private static final String DATA_PLANAR_CONFIGURATION_NODE =
            "PlanarConfiguration";
    private static final String DATA_SAMPLE_FORMAT_NODE =
            "SampleFormat";
    private static final String DATA_BITS_PER_SAMPLE_NODE =
            "BitsPerSample";
    private static final String DATA_SIGNIFICANT_BITS_PER_SAMPLE_NODE =
            "SignificantBitsPerSample";
    private static final String DIMENSION_NODE =
            "Dimension";
    private static final String DIMENSION_HORIZONTAL_SCREEN_SIZE_NODE =
            "HorizontalScreenSize";
    private static final String DIMENSION_IMAGE_ORIENTATION_NODE =
            "ImageOrientation";
    private static final String DIMENSION_PIXEL_ASPECT_RATIO_NODE =
            "PixelAspectRatio";
    private static final String DIMENSION_VERTICAL_SCREEN_SIZE_NODE =
            "VerticalScreenSize";

    private int width, height, bitsPerSample;

    XPMImageMetadata(int width, int height, int bitsPerSample) {
        super(true, null, null, null, null);
        this.width         = width;
        this.height        = height;
        this.bitsPerSample = bitsPerSample;
    }

    @Override
    public Node getAsTree(String formatName) {
        if (IIOMetadataFormatImpl.standardMetadataFormatName.equals(formatName)) {
            return getStandardTree();
        }
        throw new IllegalArgumentException("Illegal format name: " + formatName);
    }

    @Override
    protected IIOMetadataNode getStandardDataNode() {
        IIOMetadataNode root = new IIOMetadataNode(DATA_NODE);
        IIOMetadataNode child;

        // PlanarConfiguration
        child = new IIOMetadataNode(DATA_PLANAR_CONFIGURATION_NODE);
        child.setAttribute("value", "PixelInterleaved");
        root.appendChild(child);

        // SampleFormat
        child = new IIOMetadataNode(DATA_SAMPLE_FORMAT_NODE);
        child.setAttribute("value", "Index");
        root.appendChild(child);

        // BitsPerSample
        child = new IIOMetadataNode(DATA_BITS_PER_SAMPLE_NODE);
        child.setAttribute("value", "" + bitsPerSample);
        root.appendChild(child);

        // SignificantBitsPerSample
        child = new IIOMetadataNode(DATA_SIGNIFICANT_BITS_PER_SAMPLE_NODE);
        child.setAttribute("value", "" + bitsPerSample);
        root.appendChild(child);

        return root;
    }

    @Override
    protected IIOMetadataNode getStandardDimensionNode() {
        IIOMetadataNode root = new IIOMetadataNode(DIMENSION_NODE);
        IIOMetadataNode child;

        // PixelAspectRatio
        child = new IIOMetadataNode(DIMENSION_PIXEL_ASPECT_RATIO_NODE);
        child.setAttribute("value", "1.0");
        root.appendChild(child);

        // ImageOrientation
        child = new IIOMetadataNode(DIMENSION_IMAGE_ORIENTATION_NODE);
        child.setAttribute("value", "Normal");
        root.appendChild(child);

        // HorizontalScreenSize
        child = new IIOMetadataNode(DIMENSION_HORIZONTAL_SCREEN_SIZE_NODE);
        child.setAttribute("value", "" + width);
        root.appendChild(child);

        // VerticalScreenSize
        child = new IIOMetadataNode(DIMENSION_VERTICAL_SCREEN_SIZE_NODE);
        child.setAttribute("value", "" + height);
        root.appendChild(child);

        return root;
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public void mergeTree(String formatName, Node root) {
        throw new IllegalStateException("This instance is read-only.");
    }

    @Override
    public void reset() {
        throw new IllegalStateException("This instance is read-only.");
    }

}
