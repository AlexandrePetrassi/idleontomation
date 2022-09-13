package com.caracrazy.graphics;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import static com.caracrazy.localization.Messages.messages;

public class ImageLoader {

    public static final String TEMPLATE_ERROR = "%s: %s";

    private ImageLoader() {
        throw new IllegalStateException(messages().getErrorUtilityClass());
    }

    private static URL getUrl(String filename) {
        URL url = ImageLoader.class.getResource(filename);
        if (url == null) {
            String message = String.format(TEMPLATE_ERROR, messages().getErrorImageLoad(), filename);
            throw new IllegalStateException(message);
        }
        return url;
    }

    public static BufferedImage load(String filename) {
        try {
            return ImageIO.read(getUrl(filename));
        } catch (IOException e) {
            String message = String.format(TEMPLATE_ERROR, messages().getErrorImageRead(), filename);
            throw new IllegalStateException(message, e);
        }
    }

    public static BufferedImage loadResource(String filename) {
        if(filename.startsWith("/")) return load(filename);
        return load("/" + filename);
    }
}
