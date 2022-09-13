package com.caracrazy.idleon;

import autoitx4java.AutoItX;
import com.caracrazy.automation.jnativehook.KeyboardListener;
import com.caracrazy.automation.robot.Screenshooter;
import com.caracrazy.graphics.ImageExtensions;
import com.caracrazy.graphics.ImageLoader;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static com.caracrazy.automation.autoit.AutoItXExtensions.*;
import static com.caracrazy.localization.Messages.messages;

public class ChopMiniGame {

    private ChopMiniGame() {
        throw new IllegalStateException(messages().getErrorUtilityClass());
    }

    public static void start(AutoItX autoItX, ChopMiniGameData config) {
        Rectangle gameArea = findCriticalMinigameArea(autoItX, config.getAppName(), config);
        BufferedImage leaf = ImageLoader.loadResource(config.getCursorReference());
        keepClicking(autoItX, leaf, gameArea, config);
    }

    public static Rectangle findCriticalMinigameArea(AutoItX autoItX, String windowName, ChopMiniGameData config) {
        focusWindow(autoItX, windowName);
        Rectangle windowRect = getWindowRect(autoItX, windowName);
        BufferedImage screenshot = Screenshooter.screenshot(windowRect);
        Rectangle result = findBiggerMinigameArea(screenshot, config);
        return new Rectangle(windowRect.x + result.x, windowRect.y + result.y, result.width, result.height);
    }

    public static Rectangle findBiggerMinigameArea(BufferedImage screenshot, ChopMiniGameData config) {
        BufferedImage reference = ImageLoader.loadResource(config.getFrameReference());
        Rectangle area = new Rectangle(10, -11, 240, 15);
        Point referenceArea = ImageExtensions.getSubImagePosition(screenshot, reference, ImageExtensions.getRectangle(screenshot), 8);
        if (referenceArea == null) throw new IllegalStateException(messages().getErrorFrameNotFound());
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

    public static void keepClicking(AutoItX autoItX, BufferedImage leaf, Rectangle gameArea, ChopMiniGameData config) {
        while (true) {
            BufferedImage screenshot = Screenshooter.screenshot(gameArea);
            Optional<Boolean> isGood = isGoodToClick(screenshot, leaf, config.getTargetColors());
            if(KeyboardListener.isKeyPressed(config.getForceExitKey())) {
                System.out.println(messages().getInfoForceExit());
                return;
            } else if (!isGood.isPresent()){
                System.out.println(messages().getErrorCursorNotFound());
            } else if (Boolean.TRUE.equals(isGood.get())) {
                System.out.println(messages().getInfoClick());
                click(autoItX, gameArea.x, gameArea.y);
            } else {
                autoItX.sleep(1);
            }
        }
    }

    public static Optional<Boolean> isGoodToClick(BufferedImage screenshot, BufferedImage reference, Collection<Color> colors) {
        Rectangle rect = ImageExtensions.getRectangle(screenshot);
        Point leafPoint = ImageExtensions.getSubImagePosition(screenshot, reference, rect, 48);
        if (leafPoint == null) return Optional.empty();

        Collection<Color> foundColors = new ArrayList<>();
        foundColors.add(new Color(screenshot.getRGB(leafPoint.x, screenshot.getHeight() - 1), false));
        foundColors.add(new Color(screenshot.getRGB(leafPoint.x + 1, screenshot.getHeight() - 1), false));
        foundColors.add(new Color(screenshot.getRGB(leafPoint.x - 1, screenshot.getHeight() - 1), false));
        foundColors.add(new Color(screenshot.getRGB(leafPoint.x - 2, screenshot.getHeight() - 1), false));
        foundColors.add(new Color(screenshot.getRGB(leafPoint.x + 2, screenshot.getHeight() - 1), false));
        return Optional.of(colors.containsAll(foundColors));
    }
}
