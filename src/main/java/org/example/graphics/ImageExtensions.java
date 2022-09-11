package org.example.graphics;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

public class ImageExtensions {

    private static final String ERROR_NOT_SAME_SIZE =
            "Images are not same size: base(%d, %d) vs other(%d, %d)";

    private ImageExtensions() {
        throw new IllegalStateException("Utility class");
    }

    public static Point getSubImagePosition(BufferedImage mainImage, BufferedImage subImage, Rectangle searchArea) {
        for (int y = (int) searchArea.getMinY(); y < searchArea.getMaxY(); y++) {
            for (int x = (int) searchArea.getMinX(); x < searchArea.getMaxX(); x++) {
                BufferedImage sub = mainImage.getSubimage(x, y, subImage.getWidth(), subImage.getHeight());
                if (fastSimilarity(sub, subImage)) return new Point(x, y);
            }
        }
        return null;
    }

    public static boolean fastSimilarity(BufferedImage base, BufferedImage other) {
        requireSameSize(base, other);
        int size = base.getWidth() * base.getHeight();
        long iterations = size / 2 - 1L;
        int initial = size / 3;
        return IntStream
                .iterate(initial, x -> (x + 2) % size)
                .limit(iterations)
                .filter(index -> !getPixel(other, index).equals(Color.MAGENTA))
                .allMatch(index -> getPixel(base, index).equals(getPixel(other, index)));
    }

    private static Color getPixel(BufferedImage image, int index) {
        return new Color(image.getRGB(index % image.getWidth(), index / image.getWidth()), false);
    }

    public static void requireSameSize(BufferedImage base, BufferedImage other) {
        if (base.getWidth() != other.getWidth() || base.getHeight() != other.getHeight()) {
            throw new IllegalArgumentException(String.format(
                    ERROR_NOT_SAME_SIZE,
                    base.getWidth(),
                    base.getHeight(),
                    other.getWidth(),
                    other.getHeight()
            ));
        }
    }

    public static Rectangle firstQuadrant(BufferedImage bufferedImage) {
        return new Rectangle(
                0,
                0,
                bufferedImage.getWidth() / 2,
                bufferedImage.getHeight() / 2
        );
    }
}
