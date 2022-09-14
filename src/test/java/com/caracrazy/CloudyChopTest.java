package com.caracrazy;

import com.caracrazy.configuration.ConfigurationData;
import com.caracrazy.graphics.ImageLoader;
import com.caracrazy.idleon.ChopMiniGame;
import com.caracrazy.testing.TestImageLoader;
import com.caracrazy.yaml.YamlLoader;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.Assert.assertTrue;

@RunWith(Theories.class)
public class CloudyChopTest
{
    @DataPoints
    public static final int[] screenshots =
            IntStream.range(1, 128).toArray();

    private static final ConfigurationData config =
            YamlLoader.load(ConfigurationData.class, "config.yaml");

    public static final BufferedImage leaf =
            ImageLoader.loadResource(config.getChopMiniGame().getCursorReference());

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
