package com.caracrazy.graphics;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.stream.IntStream;

import static com.caracrazy.graphics.ColorExtensions.averageColor;
import static com.caracrazy.graphics.ColorExtensions.matchPixel;
import static com.caracrazy.localization.Messages.messages;

public class ImageExtensions {

    private static final String TEMPLATE_SIZE_ERROR = "(%d, %d) vs (%d, %d)";

    private ImageExtensions() {
        throw new IllegalStateException(messages().getErrorUtilityClass());
    }

    public static Point getSubImagePosition(BufferedImage mainImage, BufferedImage subImage, Rectangle searchArea, int threshold) {
        int maxX = Math.min((int)searchArea.getMaxX(), mainImage.getWidth() - subImage.getWidth());
        int maxY = Math.min((int)searchArea.getMaxY(), mainImage.getHeight() - subImage.getHeight());
        return getPoint1(mainImage, subImage, (int) searchArea.getMinX(), (int) searchArea.getMinY(), threshold, maxX, maxY);
    }

    private static Point getPoint1(BufferedImage mainImage, BufferedImage subImage, int minX, int minY, int threshold, int maxX, int maxY) {
        return IntStream.range(minY, maxY)
                .mapToObj(y -> getPoint(mainImage, subImage, minX, threshold, maxX, y))
                .parallel()
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    private static Point getPoint(BufferedImage mainImage, BufferedImage subImage, int minX, int threshold, int maxX, int y) {
        return IntStream.range(minX, maxX)
                .mapToObj(x -> getPoint(mainImage, subImage, threshold, y, x))
                .parallel()
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    private static Point getPoint(BufferedImage mainImage, BufferedImage subImage, int threshold, int y, int x) {
        return fastSimilarity(mainImage.getSubimage(x, y, subImage.getWidth(), subImage.getHeight()), subImage, threshold)
                ? new Point(x, y)
                : null;
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

    public static Color getPixel(BufferedImage image, int index) {
        return new Color(image.getRGB(index % image.getWidth(), index / image.getWidth()), false);
    }

    public static void requireSameSize(BufferedImage base, BufferedImage other) {
        if (base.getWidth() != other.getWidth() || base.getHeight() != other.getHeight()) {
            throw new IllegalArgumentException(messages().getErrorImageNotSameSize() + String.format(
                    TEMPLATE_SIZE_ERROR,
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
}
