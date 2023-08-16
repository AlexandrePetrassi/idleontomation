package com.caracrazy.graphics;

import com.caracrazy.automation.Automator;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Optional;
import java.util.stream.IntStream;

import static com.caracrazy.localization.Messages.messages;

public class NewImageExtensions {

    private NewImageExtensions() {
        throw new IllegalStateException(messages().getErrorUtilityClass());
    }

    public static Optional<Point> getSubImagePosition(Rectangle mainImage, BufferedImage subImage, int threshold, Automator automator) {
        return iterateOverAxisY(mainImage, subImage, threshold, automator);
    }

    private static Optional<Point> iterateOverAxisY(Rectangle mainImage, BufferedImage subImage, int threshold, Automator automator) {
        return IntStream.range(0, (int)mainImage.getHeight() - subImage.getHeight())
                .mapToObj(y -> iterateOverAxisX(mainImage, subImage, threshold, y, automator))
                .parallel()
                .filter(Optional::isPresent)
                .findFirst()
                .orElse(Optional.empty());
    }

    private static Optional<Point> iterateOverAxisX(Rectangle mainImage, BufferedImage subImage, int threshold, int y, Automator automator) {
        return IntStream.range(0, (int)mainImage.getWidth() - subImage.getWidth())
                .mapToObj(x -> getPoint(mainImage, toIntArray(subImage), threshold, y, x, automator))
                .parallel()
                .filter(Optional::isPresent)
                .findFirst()
                .orElse(Optional.empty());
    }

    private static Optional<Point> getPoint(Rectangle area, int[] subImage, int threshold, int y, int x, Automator automator) {
        return fastSimilarity(area, subImage, threshold, automator)
                ? Optional.of(new Point(x, y))
                : Optional.empty();
    }

    private static final int MAGENTA = Color.MAGENTA.getRGB();

    public static boolean fastSimilarity(Rectangle base, int[] other, int threshold, Automator automator) {
        int size = (int)base.getWidth() * (int)base.getHeight();
        long iterations = size / 2 - 1L;
        int initial = size / 3;
        return IntStream
                .iterate(initial, x -> (x + 2) % size)
                .limit(iterations)
                .filter(index -> other[index] != MAGENTA)
                .allMatch(index -> automator.matchPixel(base.x, base.y , other[index], threshold));
    }

    public static int[] toIntArray(BufferedImage image) {
        return toIntArray(((DataBufferByte)image.getRaster().getDataBuffer()).getData());
    }

    private static int[] toIntArray(byte[] bytes) {
        IntBuffer intBuf =
                ByteBuffer.wrap(bytes)
                        .order(ByteOrder.BIG_ENDIAN)
                        .asIntBuffer();
        int[] array = new int[intBuf.remaining()];
        intBuf.get(array);
        return array;
    }
}
