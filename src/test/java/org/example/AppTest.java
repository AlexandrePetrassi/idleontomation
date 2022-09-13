package org.example;

import org.example.graphics.ImageLoader;
import org.example.idleon.ChopMiniGame;
import org.example.testing.TestImageLoader;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static org.example.graphics.ImageExtensions.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


@RunWith(Theories.class)
public class AppTest
{
    @DataPoints
    public static final int[] screenshots =
            IntStream.range(1, 285).toArray();

    @DataPoints
    public static final long[] screenshots2 =
            LongStream.range(286, 338).toArray();

    public static final BufferedImage leaf =
            ImageLoader.loadResource("leaf.bmp");

    @Theory
    public void shouldFindLeaf(final int index)
    {
        // Given
        BufferedImage screenshot = TestImageLoader.load("/inputs/chop/minigame (" + index + ").png");
        BufferedImage subImage = screenshot.getSubimage(200, 129, 250, 28);

        // When
        Optional<Boolean> result = ChopMiniGame.isGoodToClick(subImage, leaf, ChopMiniGame.COLORS);

        // Then
        assertTrue(result.isPresent());
    }

    @Theory
    public void shouldNotFindLeaf(final long index)
    {
        // Given
        BufferedImage screenshot = TestImageLoader.load("/inputs/chop/minigame (" + index + ").png");
        BufferedImage subImage = screenshot.getSubimage(200, 129, 250, 28);

        // When
        Optional<Boolean> result = ChopMiniGame.isGoodToClick(subImage, leaf, ChopMiniGame.COLORS);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    public void test() {
        // Dado
        BufferedImage screenshot = TestImageLoader.load("/inputs/chop/minigame-fullscreen.png");
        BufferedImage reference = ImageLoader.load("/chop.bmp");
        BufferedImage expected = TestImageLoader.load("/asserts/chop-area-assert.png");
        Rectangle area = new Rectangle(10, -11, 240, 15);

        // Quando
        Point referenceArea = getSubImagePosition(screenshot, reference, getRectangle(screenshot), 8);
        if (referenceArea == null) Assert.fail();

        // Entao
        Rectangle point = getEnclosingArea(referenceArea, area);
        BufferedImage shot = screenshot.getSubimage(point.x, point.y, point.width, point.height);
        assertTrue(similar(shot, expected, 1));
    }
}
