package edu.illinois.library.imageio.xpm;

import javax.imageio.ImageReadParam;

@SuppressWarnings("WeakerAccess")
public class XPMImageReadParam extends ImageReadParam {

    private DisplayType displayType = DisplayType.COLOR;

    public DisplayType getDisplayType() {
        return displayType;
    }

    /**
     * <p>Selects a display type for decoding. Colors for that display type
     * are used if possible, falling back to other display types according to
     * the following heuristic:</p>
     *
     * <ol>
     *     <li>{@link DisplayType#COLOR}
     *         <ol>
     *             <li>Color</li>
     *             <li>Grayscale</li>
     *             <li>Four-level grayscale</li>
     *             <li>Monochrome</li>
     *         </ol>
     *     </li>
     *     <li>{@link DisplayType#GRAYSCALE} and {@link
     *     DisplayType#FOUR_LEVEL_GRAYSCALE}
     *         <ol>
     *             <li>Grayscale</li>
     *             <li>Color (converted to grayscale)</li>
     *             <li>Four-level grayscale</li>
     *             <li>Monochrome</li>
     *         </ol>
     *     </li>
     *     <li>{@link DisplayType#MONOCHROME}
     *         <ol>
     *             <li>Monochrome</li>
     *             <li>Four-level grayscale</li>
     *             <li>Grayscale</li>
     *             <li>Color (converted to grayscale)</li>
     *         </ol>
     *     </li>
     * </ol>
     *
     * <p>The default display type (when this method is not used) is {@link
     * DisplayType#COLOR}.</p>
     */
    public void setDisplayType(DisplayType displayType) {
        if (displayType == null) {
            throw new NullPointerException("displayType cannot be null");
        }
        this.displayType = displayType;
    }

}
