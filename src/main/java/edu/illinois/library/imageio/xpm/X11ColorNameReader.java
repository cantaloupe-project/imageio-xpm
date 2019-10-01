package edu.illinois.library.imageio.xpm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class X11ColorNameReader {

    private static final Logger LOGGER =
            Logger.getLogger(X11ColorNameReader.class.getName());

    private static final Pattern LINE_PATTERN =
            Pattern.compile("(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(.+)");

    /**
     * Reads the full list of X11 color names from {@literal rgb.txt}.
     *
     * @return Map of color names to ARGB integers.
     */
    Map<String,Integer> read() {
        final Map<String,Integer> colors = new HashMap<>();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("rgb.txt");
             InputStreamReader isReader = new InputStreamReader(is);
             BufferedReader reader = new BufferedReader(isReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = LINE_PATTERN.matcher(line);
                if (matcher.find()) {
                    String name = matcher.group(4);
                    int color = 0xff000000 |
                            ((Integer.parseInt(matcher.group(1)) & 0xff) << 16) |
                            ((Integer.parseInt(matcher.group(2)) & 0xff) << 8) |
                            (Integer.parseInt(matcher.group(3)) & 0xff);
                    colors.put(name, color);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "read(): " + e.getMessage(), e);
        }
        return colors;
    }

}
