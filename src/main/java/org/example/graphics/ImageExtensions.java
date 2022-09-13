package org.example.graphics;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

import static org.example.graphics.ColorExtensions.matchPixel;

public class ImageExtensions {

    private static final String ERROR_NOT_SAME_SIZE =
            "Images are not same size: base(%d, %d) vs other(%d, %d)";

    private ImageExtensions() {
        throw new IllegalStateException("Utility class");
    }

    public static Point getSubImagePosition(BufferedImage mainImage, BufferedImage subImage, Rectangle searchArea, int threshold) {
        int maxX = Math.min((int)searchArea.getMaxX(), mainImage.getWidth() - subImage.getWidth());
        int maxY = Math.min((int)searchArea.getMaxY(), mainImage.getHeight() - subImage.getHeight());
        for (int y = (int) searchArea.getMinY(); y < maxY; y++) {
            for (int x = (int) searchArea.getMinX(); x < maxX; x++) {
                BufferedImage sub = mainImage.getSubimage(x, y, subImage.getWidth(), subImage.getHeight());
                if (fastSimilarity(sub, subImage, threshold)) return new Point(x, y);
            }
        }
        return null;
    }

    public static boolean similar(BufferedImage base, BufferedImage other, int limit) {
        requireSameSize(base, other);
        int size = base.getWidth() * base.getHeight();
        long iterations = size - 1L;
        return IntStream
                .iterate(0, x -> x)
                .limit(iterations)
                .filter(index -> !getPixel(other, index).equals(Color.MAGENTA))
                .allMatch(index -> matchPixel(getPixel(base, index), getPixel(other, index), limit));
    }

    public static boolean fastSimilarity(BufferedImage base, BufferedImage other, int threshold) {
        requireSameSize(base, other);
        int size = base.getWidth() * base.getHeight();
        long iterations = size / 2 - 1L;
        int initial = size / 3;
        return IntStream
                .iterate(initial, x -> (x + 2) % size)
                .limit(iterations)
                .filter(index -> !getPixel(other, index).equals(Color.MAGENTA))
                .allMatch(index -> smartMatch(base, other, index, threshold));
    }

    public static boolean smartMatch(BufferedImage base, BufferedImage other, int index, int threshold) {
        Color colorA = getPixel(base, index);
        Color colorB = getPixel(other, index);
        if (matchPixel(colorA, colorB, threshold)) return true;
        if (matchNextAverage(base, other, index, colorA, colorB, threshold)) return true;
        return matchPreviousAverage(base, other, index, colorA, colorB, threshold);
    }

    public static boolean matchNextAverage(BufferedImage base, BufferedImage other, int index, Color colorA, Color colorB, int threshold) {
        if (other.getWidth() - 1 == index % other.getWidth()) return false;
        Color colorC = getPixel(other, index + 1);
        if (colorC.equals(Color.MAGENTA)) {
            Color colorD = getPixel(base, index + 1);
            return matchPixel(colorA, averageColor(colorB, colorD), threshold);
        } else {
            return matchPixel(colorA, averageColor(colorB, colorC), threshold);
        }
    }


    public static boolean matchPreviousAverage(BufferedImage base, BufferedImage other, int index, Color colorA, Color colorB, int threshold) {
        if (0 == index % other.getWidth()) return false;
        Color colorC = getPixel(other, index - 1);
        if (colorC.equals(Color.MAGENTA)) {
            Color colorD = getPixel(base, index - 1);
            return matchPixel(colorA, averageColor(colorB, colorD), threshold);
        } else {
            return matchPixel(colorA, averageColor(colorB, colorC), threshold);
        }
    }

    public static Color averageColor(Color colorA, Color colorB) {
        int r = (colorA.getRed() + colorB.getRed()) / 2;
        int g = (colorA.getGreen() + colorB.getGreen()) / 2;
        int b = (colorA.getBlue() + colorB.getBlue()) / 2;
        return new Color(r, g, b);
    }

    public static Color getPixel(BufferedImage image, int index) {
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

    public static Rectangle getRectangle(BufferedImage bufferedImage) {
        return new Rectangle(
                0,
                0,
                bufferedImage.getWidth(),
                bufferedImage.getHeight()
        );
    }

    public static Rectangle getEnclosingArea(Point point, Rectangle area) {
        return new Rectangle(
                point.x + area.x,
                point.y + area.y,
                area.width - area.x,
                area.height - area.y
        );
    }
}
