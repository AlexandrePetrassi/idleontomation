package org.example.idleon;

import autoitx4java.AutoItX;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import org.example.automation.Keyboard;
import org.example.graphics.ImageExtensions;
import org.example.graphics.ImageLoader;
import org.example.graphics.Screenshooter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import static org.example.automation.AutoItXExtensions.click;
import static org.example.graphics.ImageExtensions.*;

public class ChopMiniGame {

    public static final Color[] COLORS = new Color[]{
            new Color(122, 206, 39),
            new Color(38, 138, 20),
            new Color(252, 255, 122),
            new Color(232, 168, 41)
    };

    private ChopMiniGame() {
        throw new IllegalStateException("Utility Class");
    }

    public static Rectangle findBiggerMinigameArea(BufferedImage screenshot) {
        BufferedImage reference = ImageLoader.loadResource("chop.bmp");
        Rectangle area = new Rectangle(10, -11, 240, 15);
        Point referenceArea = getSubImagePosition(screenshot, reference, getRectangle(screenshot), 8);
        if (referenceArea == null) throw new IllegalStateException("Chop mini game not found");
        return getEnclosingArea(referenceArea, area);
    }

    public static void keepClicking(AutoItX autoItX, BufferedImage leaf, Rectangle gameArea) {
        while (true) {
            BufferedImage screenshot = Screenshooter.screenshot(gameArea);
            Optional<Boolean> isGood = isGoodToClick(screenshot, leaf, COLORS);
            if(Keyboard.isKeyPressed(NativeKeyEvent.VC_ESCAPE)) {
                System.out.println("Interrupted by ESC Key");
                return;
            } else if (!isGood.isPresent()){
                System.out.println("Reference image not found");
            } else if (Boolean.TRUE.equals(isGood.get())) {
                System.out.println("click");
                click(autoItX, gameArea.x, gameArea.y);
            } else {
                autoItX.sleep(1);
            }
        }
    }

    public static Optional<Boolean> isGoodToClick(BufferedImage screenshot, BufferedImage reference, Color[] colors) {
        Rectangle rect = ImageExtensions.getRectangle(screenshot);
        Point leafPoint = getSubImagePosition(screenshot, reference, rect, 48);
        if (leafPoint == null) {
            System.out.println("Reference image not found");
            return Optional.empty();
        }
        Collection<Color> foundColors = new ArrayList<>();
        foundColors.add(new Color(screenshot.getRGB(leafPoint.x, screenshot.getHeight() - 1), false));
        foundColors.add(new Color(screenshot.getRGB(leafPoint.x + 1, screenshot.getHeight() - 1), false));
        foundColors.add(new Color(screenshot.getRGB(leafPoint.x - 1, screenshot.getHeight() - 1), false));
        foundColors.add(new Color(screenshot.getRGB(leafPoint.x - 2, screenshot.getHeight() - 1), false));
        foundColors.add(new Color(screenshot.getRGB(leafPoint.x + 2, screenshot.getHeight() - 1), false));
        return Optional.of(Arrays.asList(colors).containsAll(foundColors));
    }
}
