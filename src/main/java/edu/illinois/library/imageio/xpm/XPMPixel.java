package edu.illinois.library.imageio.xpm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>XPM pixel, which may be represented by up to four distinct colors that
 * are adapted to various display types (RGB, grayscale, four-level grayscale,
 * and monochrome).</p>
 *
 * <p>The internal representation of each display type is an 8 bit-per-
 * component ARGB value packed into a 32-bit integer. Components larger than 8
 * bits are rescaled to 8 bits.</p>
 *
 * @author Alex Dolski UIUC
 */
final class XPMPixel {

    private static final Map<String,Integer> COLOR_NAMES = new HashMap<>();
    private static final Set<String> DISPLAY_TYPES =
            new HashSet<>(Arrays.asList("s", "m", "g", "g4", "c"));

    private int monoColor, fourLevelGrayColor, grayColor, rgbColor;
    private boolean isMonoColorSet, isFourLevelGrayColorSet, isGrayColorSet,
            isRGBColorSet;

    /**
     * Caches used by {@link #getEffectiveMonoColor()} etc. to improve
     * efficiency.
     */
    private int effectiveMonoColor, effectiveGrayColor, effectiveRGBColor;
    private boolean isEffectiveMonoColorSet, isEffectiveGrayColorSet,
            isEffectiveRGBColorSet;

    private int rgbComponentSize = 8, grayComponentSize = 8;

    private static synchronized void readColorNames() {
        if (COLOR_NAMES.isEmpty()) {
            COLOR_NAMES.putAll(new X11ColorNameReader().read());
        }
    }

    /**
     * @param line Color line with the leading "chars" string trimmed off.
     */
    static XPMPixel parse(String line) {
        final XPMPixel pixel = new XPMPixel();
        // Formulating a regex that can parse 100% of color lines found in
        // real-world XPMs is a real challenge. So we will parse token-by-token.
        // First remove surrounding quotes and then split on whitespace.
        String[] tokens = line.replaceAll("[\",]", "").trim().split("[ \t]");
        String displayType = "s";
        List<String> colorTokens = new ArrayList<>();
        for (String token : tokens) {
            if (DISPLAY_TYPES.contains(token)) {
                if (!colorTokens.isEmpty()) {
                    finishParsingLine(pixel, displayType, colorTokens);
                    colorTokens.clear();
                }
                displayType = token;
            } else if (!token.isEmpty()) {
                colorTokens.add(token);
            }
        }
        if (!colorTokens.isEmpty()) {
            finishParsingLine(pixel, displayType, colorTokens);
        }
        return pixel;
    }

    private static void finishParsingLine(XPMPixel pixel,
                                          String displayType,
                                          List<String> colorTokens) {
        String color = String.join(" ", colorTokens);
        switch (displayType) {
            case "c":
                pixel.rgbColor         = parseColorValue(color);
                pixel.rgbComponentSize = componentSize(color);
                pixel.isRGBColorSet    = true;
                break;
            case "g":
                pixel.grayColor         = parseColorValue(color);
                pixel.grayComponentSize = componentSize(color);
                pixel.isGrayColorSet    = true;
                break;
            case "g4":
                pixel.fourLevelGrayColor      = parseColorValue(color);
                pixel.isFourLevelGrayColorSet = true;
                break;
            case "m":
                pixel.monoColor      = parseColorValue(color);
                pixel.isMonoColorSet = true;
                break;
            case "s":
                break; // we don't care about symbols
            default:
                throw new IllegalArgumentException(
                        "Invalid display type: " + displayType);
        }
    }

    private static int parseColorValue(String color) {
        readColorNames();
        if (color.equalsIgnoreCase("none")) {
            return 0;
        } else if (color.startsWith("#")) {
            int a = 0xff, r, g, b;
            if (color.length() > 8) { // #rrrrggggbbbb (16-bit)
                r = Integer.parseInt(color.substring(1, 5), 16);
                g = Integer.parseInt(color.substring(5, 9), 16);
                b = Integer.parseInt(color.substring(9, 13), 16);
                double eightBit = Math.pow(2, 8) - 1;
                double sixteenBit = Math.pow(2, 16) - 1;
                r = (int) Math.round((r / sixteenBit) * eightBit);
                g = (int) Math.round((g / sixteenBit) * eightBit);
                b = (int) Math.round((b / sixteenBit) * eightBit);
            } else if (color.length() > 6) { // #rrggbb (8-bit)
                r = Integer.parseInt(color.substring(1, 3), 16);
                g = Integer.parseInt(color.substring(3, 5), 16);
                b = Integer.parseInt(color.substring(5, 7), 16);
            } else { // #rgb (8-bit shorthand)
                String tmp = color.substring(1, 2);
                tmp += tmp;
                r = Integer.parseInt(tmp, 16);
                tmp = color.substring(2, 3);
                tmp += tmp;
                g = Integer.parseInt(tmp, 16);
                tmp = color.substring(3, 4);
                tmp += tmp;
                b = Integer.parseInt(tmp, 16);
            }
            return packARGB(a, r, g, b);
        } else if (COLOR_NAMES.containsKey(color)) {
            return COLOR_NAMES.get(color);
        }
        return 0xff000000;
    }

    private static int componentSize(String colorValue) {
        return (colorValue.startsWith("#") && colorValue.length() > 7) ? 16 : 8;
    }

    private static int convertToGrayscale(int argb) {
        int[] unpacked = unpackARGB(argb);
        unpacked[1] = (int) Math.round(unpacked[1] * 0.299);
        unpacked[2] = (int) Math.round(unpacked[2] * 0.587);
        unpacked[3] = (int) Math.round(unpacked[3] * 0.114);
        int avg = unpacked[1] + unpacked[2] + unpacked[3];
        return packARGB(unpacked[0], avg, avg, avg);
    }

    private static int packARGB(int a, int r, int g, int b) {
        return ((a & 0xff) << 24) | ((r & 0xff) << 16) |
                ((g & 0xff) << 8) | (b & 0xff);
    }

    private static int[] unpackARGB(int argb) {
        return new int[] {
                ((argb >>> 24) & 0xff), ((argb >>> 16) & 0xff),
                ((argb >>> 8) & 0xff), (argb & 0xff) };
    }

    private XPMPixel() {}

    /**
     * @return Effective 8-bit grayscale ARGB color.
     */
    int getEffectiveGrayColor() {
        if (!isEffectiveGrayColorSet) {
            if (isGrayColorSet) {
                effectiveGrayColor = grayColor;
            } else if (isRGBColorSet) {
                effectiveGrayColor = convertToGrayscale(rgbColor);
            } else if (isFourLevelGrayColorSet) {
                effectiveGrayColor = fourLevelGrayColor;
            } else {
                effectiveGrayColor = monoColor;
            }
            isEffectiveGrayColorSet = true;
        }
        return effectiveGrayColor;
    }

    /**
     * @return Effective monochrome ARGB color.
     */
    int getEffectiveMonoColor() {
        if (!isEffectiveMonoColorSet) {
            if (isMonoColorSet) {
                effectiveMonoColor = monoColor;
            } else if (isFourLevelGrayColorSet) {
                int rgb = ((fourLevelGrayColor & 0xff) > 127) ? 0xffffff : 0;
                effectiveMonoColor = fourLevelGrayColor | rgb;
            } else if (isGrayColorSet) {
                int rgb = ((grayColor & 0xff) > 127) ? 0xffffff : 0;
                effectiveMonoColor = grayColor | (rgb & 0xffffff);
            } else {
                int rgb = convertToGrayscale(rgbColor);
                effectiveMonoColor = (rgbColor & 0xff000000) | rgb;
            }
            isEffectiveMonoColorSet = true;
        }
        return effectiveMonoColor;
    }

    /**
     * @return Effective 32-bit ARGB color.
     */
    int getEffectiveARGBColor() {
        if (!isEffectiveRGBColorSet) {
            if (isRGBColorSet) {
                effectiveRGBColor = rgbColor;
            } else if (isGrayColorSet) {
                effectiveRGBColor = grayColor;
            } else if (isFourLevelGrayColorSet) {
                effectiveRGBColor = fourLevelGrayColor;
            } else {
                effectiveRGBColor = monoColor;
            }
            isEffectiveRGBColorSet = true;
        }
        return effectiveRGBColor;
    }

    /**
     * @return Effective 32-bit RGB color.
     */
    int getEffectiveRGBColor() {
        if (!isEffectiveRGBColorSet) {
            if (isRGBColorSet) {
                effectiveRGBColor = rgbColor;
            } else if (isGrayColorSet) {
                effectiveRGBColor = 0xff000000 | grayColor;
            } else if (isFourLevelGrayColorSet) {
                effectiveRGBColor = 0xff000000 | fourLevelGrayColor;
            } else {
                effectiveRGBColor = 0xff000000 | monoColor;
            }
            isEffectiveRGBColorSet = true;
        }
        return effectiveRGBColor;
    }

    int getGrayColor() {
        return grayColor;
    }

    int getGrayComponentSize() {
        return grayComponentSize;
    }

    int getMonoColor() {
        return monoColor;
    }

    int getRGBColor() {
        return rgbColor;
    }

    int getRGBComponentSize() {
        return rgbComponentSize;
    }

}
