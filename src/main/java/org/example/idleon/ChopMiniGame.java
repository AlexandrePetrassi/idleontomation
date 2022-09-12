package org.example.idleon;

import autoitx4java.AutoItX;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import org.example.automation.Keyboard;
import org.example.graphics.ImageExtensions;
import org.example.graphics.Screenshooter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Optional;

import static org.example.automation.AutoItXExtensions.click;
import static org.example.graphics.ImageExtensions.getEnclosingArea;
import static org.example.graphics.ImageExtensions.getSubImagePosition;

public class ChopMiniGame {

    private static final Color[] COLORS = new Color[]{
            new Color(122, 206, 39),
            new Color(38, 138, 20),
            new Color(252, 255, 122),
            new Color(232, 168, 41)
    };

    private ChopMiniGame() {
        throw new IllegalStateException("Utility Class");
    }

    private static int allowedFailures = 32;

    public static void keepClicking(AutoItX autoItX, BufferedImage leaf, Rectangle gameArea, Rectangle offset) {
        while (true) {
            BufferedImage screenshot = Screenshooter.screenshot(gameArea);
            Optional<Boolean> isGood = isGoodToClick(screenshot, leaf, offset, COLORS);
            if(Keyboard.isKeyPressed(NativeKeyEvent.VC_ESCAPE)) {
                System.out.println("Interrupted by ESC Key");
                return;
            } else if (!isGood.isPresent()){
                --allowedFailures;
                if (allowedFailures <= 0) {
                    System.out.println("Interrupted for not finding the reference image");
                    return;
                }
            } else if (Boolean.TRUE.equals(isGood.get())) {
                click(autoItX, gameArea.x, gameArea.y);
                allowedFailures = 32;
            } else {
                autoItX.sleep(1);
                allowedFailures = 32;
            }
        }
    }

    public static Optional<Boolean> isGoodToClick(BufferedImage screenshot, BufferedImage reference, Rectangle offset, Color[] colors) {
        Rectangle rect = ImageExtensions.getRectangle(screenshot);
        Point leafPoint = getSubImagePosition(screenshot, reference, rect);
        if (leafPoint == null) {
            System.out.println("Reference image not found");
            return Optional.empty();
        }
        Rectangle rectangle = getEnclosingArea(leafPoint, offset);
        Color color = new Color(screenshot.getRGB(rectangle.x, (int)rectangle.getMaxY()), false);
        return Optional.of(Arrays.asList(colors).contains(color));
    }
}
