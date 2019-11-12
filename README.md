Java Image I/O reader plugin for the XPM image format (version 3).

Requires Java 8 or later.

# Overview

XPM is an uncompressed palette color format that expands on the older
monochrome XBM. The encoding is ASCII text formatted as a C string array.

XPM is often used for encoding user interface icons. The color palette supports
up to four different display types per pixel--monochrome, four-level grayscale,
grayscale, and RGB color--which enables a single image to be optimized for each
display type. (This reader renders the RGB version by default, but it can
render other versions via the use of a custom `XPMImageReadParam` instance (see
examples).

# Installation

This plugin is available in Maven Central. Add the following to the
`<dependencies>` section of your `pom.xml`:

```xml
<dependency>
    <groupId>edu.illinois.library</groupId>
    <artifactId>imageio-xpm</artifactId>
    <version>[the version you want]</version>
</dependency>
```

# Usage

## Simple

```java
File file = new File("test.xpm");
BufferedImage image = ImageIO.read(file);
```

## Advanced

```java
Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName("XPM");
if (it.hasNext()) {
    ImageReader reader = it.next();
    try {
        ImageInputStream is = new FileImageInputStream(new File("test.xpm"));
        reader.setInput(is);
        BufferedImage image = reader.read(0);
    } finally {
        reader.dispose();
    }
}
```

## More Advanced

```java
import edu.illinois.library.imageio.xpm.XPMImageReadParam;

// ...

Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName("XPM");
if (it.hasNext()) {
    ImageReader reader = it.next();
    try {
        ImageInputStream is = new FileImageInputStream(new File("test.xpm"));
        reader.setInput(is);
        XPMImageReadParam readParam = new XPMImageReadParam();
        readParam.setDisplayType(DisplayType.GRAYSCALE);
        BufferedImage image = reader.read(0, readParam);
    } finally {
        reader.dispose();
    }
}
```

# Test

The basic tests can be run as usual using `mvn test`. There is also an
assortment of test icons included that can be processed and saved to an
output directory of your choosing for visual inspection. Enable this via:

```
$ mvn clean test -DargLine="-DiconOutputPath=/home/myself/icons"
```

# Build

```
$ mvn clean package
```

# Notes

1. Logging (which is minimal) uses `java.util.logging`.
2. Only reading support is available--no writing.
3. 16-bit samples are rescaled to 8 bits.
4. The IIOParamController (for progress updates etc.). is not supported.
