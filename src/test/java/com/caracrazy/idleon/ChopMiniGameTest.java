package com.caracrazy.idleon;

import com.caracrazy.configuration.ConfigurationData;
import com.caracrazy.graphics.ImageExtensions;
import com.caracrazy.graphics.ImageLoader;
import com.caracrazy.testing.TestImageLoader;
import com.caracrazy.yaml.YamlLoader;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(Enclosed.class)
public class ChopMiniGameTest {

    private static final ConfigurationData config =
            YamlLoader.load(ConfigurationData.class, "config.yaml");

    public static final BufferedImage leaf =
            ImageLoader.loadResource(config.getChopMiniGame().getCursorReference());

    public static class EnclosingAreaTest {

        @Test
        public void shouldFindTheCriticalAreaFromAFullScreenshot() {
            // Given
            BufferedImage screenshot = TestImageLoader.loadResource("inputs/chop/minigame-fullscreen.png");
            BufferedImage reference = ImageLoader.loadResource("chop.bmp");
            BufferedImage expected = TestImageLoader.loadResource("asserts/chop-area-assert.png");
            Rectangle area = new Rectangle(10, -11, 240, 15);

            // When
            Optional<Point> referenceArea = ImageExtensions.getSubImagePosition(screenshot, reference, 8);
            if (!referenceArea.isPresent()) {
                Assert.fail();
                return;
            }
            Rectangle point = ChopMiniGame.getEnclosingArea(referenceArea.get(), area);

            // Then
            BufferedImage shot = screenshot.getSubimage(point.x, point.y, point.width, point.height);
            assertTrue(ImageExtensions.similar(shot, expected, 1));
        }
    }

    @RunWith(Theories.class)
    public static class ChopMiniGameLeafTest {

        @DataPoints
        public static final int[] screenshots =
                IntStream.range(1, 285).toArray();

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
    }

    @RunWith(Theories.class)
    public static class ChopMiniGameNegativeTest {

        @DataPoints
        public static final int[] screenshots =
                IntStream.range(286, 338).toArray();

        @Theory
        public void shouldNotFindLeaf(final int index) {
            // Given
            BufferedImage screenshot = TestImageLoader.loadResource("inputs/chop/minigame (" + index + ").png");
            BufferedImage subImage = screenshot.getSubimage(200, 129, 250, 28);
            Collection<Color> targetColors = config.getChopMiniGame().getTargetColors();

            // When
            Optional<Boolean> result = ChopMiniGame.isGoodToClick(subImage, leaf, targetColors);

            // Then
            assertFalse(result.isPresent());
        }
    }

    @RunWith(Theories.class)
    public static class CloudyChopTest
    {
        @DataPoints
        public static final int[] screenshots =
                IntStream.range(1, 128).toArray();

        @Theory
        public void shouldFindLeafInCloudyEnvironment(final int index) {
            // Given
            BufferedImage screenshot = TestImageLoader.loadResource("inputs/chop/cloudy (" + index + ").png");
            Collection<Color> targetColors = config.getChopMiniGame().getTargetColors();

            // When
            Optional<Boolean> result = ChopMiniGame.isGoodToClick(screenshot, leaf, targetColors);

            // Then
            assertTrue(result.isPresent());
        }
    }
}