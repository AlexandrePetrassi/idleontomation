package com.caracrazy;

import com.caracrazy.configuration.ConfigurationData;
import com.caracrazy.graphics.ImageExtensions;
import com.caracrazy.graphics.ImageLoader;
import com.caracrazy.idleon.ChopMiniGame;
import com.caracrazy.testing.TestImageLoader;
import com.caracrazy.yaml.YamlLoader;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

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

    private static final ConfigurationData config =
            YamlLoader.load(ConfigurationData.class, "config.yaml");

    public static final BufferedImage leaf =
            ImageLoader.loadResource(config.getChopMiniGame().getCursorReference());

    @Theory
    public void shouldFindLeaf(final int index) {
        // Given
        BufferedImage screenshot = TestImageLoader.loadResource("inputs/chop/minigame (" + index + ").png");
        BufferedImage subImage = screenshot.getSubimage(200, 129, 250, 28);
        Collection<Color> targetColors = config.getChopMiniGame().getTargetColors();

        // When
        Optional<Boolean> result = ChopMiniGame.isGoodToClick(subImage, leaf, targetColors);

        // Then
        assertTrue(result.isPresent());
    }

    @Theory
    public void shouldNotFindLeaf(final long index) {
        // Given
        BufferedImage screenshot = TestImageLoader.loadResource("inputs/chop/minigame (" + index + ").png");
        BufferedImage subImage = screenshot.getSubimage(200, 129, 250, 28);
        Collection<Color> targetColors = config.getChopMiniGame().getTargetColors();

        // When
        Optional<Boolean> result = ChopMiniGame.isGoodToClick(subImage, leaf, targetColors);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    public void shouldFindTheCriticalAreaFromAFullScreenshot() {
        // Given
        BufferedImage screenshot = TestImageLoader.loadResource("inputs/chop/minigame-fullscreen.png");
        BufferedImage reference = ImageLoader.loadResource("chop.bmp");
        BufferedImage expected = TestImageLoader.loadResource("asserts/chop-area-assert.png");
        Rectangle area = new Rectangle(10, -11, 240, 15);

        // When
        Point referenceArea = ImageExtensions.getSubImagePosition(screenshot, reference, 8);
        if (referenceArea == null) Assert.fail();
        Rectangle point = ChopMiniGame.getEnclosingArea(referenceArea, area);

        // Then
        BufferedImage shot = screenshot.getSubimage(point.x, point.y, point.width, point.height);
        assertTrue(ImageExtensions.similar(shot, expected, 1));
    }
}
