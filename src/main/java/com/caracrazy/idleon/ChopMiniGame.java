package com.caracrazy.idleon;

import autoitx4java.AutoItX;
import com.caracrazy.automation.Keyboard;
import com.caracrazy.graphics.ImageExtensions;
import com.caracrazy.graphics.ImageLoader;
import com.caracrazy.graphics.Screenshooter;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import static com.caracrazy.automation.AutoItXExtensions.*;

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

    public static void start(AutoItX autoItX) {
        Rectangle gameArea = ChopMiniGame.findCriticalMinigameArea(autoItX, "Legends Of Idleon");
        BufferedImage leaf = ImageLoader.loadResource("leaf.bmp");
        keepClicking(autoItX, leaf, gameArea);
    }

    public static Rectangle findCriticalMinigameArea(AutoItX autoItX, String windowName) {
        focusWindow(autoItX, windowName);
        Rectangle windowRect = getWindowRect(autoItX, windowName);
        BufferedImage screenshot = Screenshooter.screenshot(windowRect);
        Rectangle result = findBiggerMinigameArea(screenshot);
        return new Rectangle(windowRect.x + result.x, windowRect.y + result.y, result.width, result.height);
    }

    public static Rectangle findBiggerMinigameArea(BufferedImage screenshot) {
        BufferedImage reference = ImageLoader.loadResource("chop.bmp");
        Rectangle area = new Rectangle(10, -11, 240, 15);
        Point referenceArea = ImageExtensions.getSubImagePosition(screenshot, reference, ImageExtensions.getRectangle(screenshot), 8);
        if (referenceArea == null) throw new IllegalStateException("Chop mini game not found");
        return getEnclosingArea(referenceArea, area);
    }

    public static Rectangle getEnclosingArea(Point point, Rectangle area) {
        return new Rectangle(
                point.x + area.x,
                point.y + area.y,
                area.width - area.x,
                area.height - area.y
        );
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
        Point leafPoint = ImageExtensions.getSubImagePosition(screenshot, reference, rect, 48);
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
