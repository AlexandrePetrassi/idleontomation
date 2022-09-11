package org.example.graphics;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class ImageLoader {

    private static final String FAIL_TO_READ_IMAGE =
            "Failed to read image from file \"%s\"";

    private static final String FILE_NOT_FOUND =
            "File \"%s\" not found";

    private ImageLoader() {
        throw new IllegalStateException("Utility Class");
    }

    private static URL getUrl(String filename) {
        URL url = ImageLoader.class.getResource(filename);
        if (url == null) throw new IllegalStateException(String.format(FILE_NOT_FOUND, filename));
        return url;
    }

    public static BufferedImage load(String filename) {
        try {
            return ImageIO.read(getUrl(filename));
        } catch (IOException e) {
            throw new IllegalStateException(String.format(FAIL_TO_READ_IMAGE, filename), e);
        }
    }
}
