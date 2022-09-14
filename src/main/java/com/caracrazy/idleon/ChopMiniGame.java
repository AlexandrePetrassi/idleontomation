package com.caracrazy.idleon;

import autoitx4java.AutoItX;
import com.caracrazy.automation.jnativehook.Keyboard;
import com.caracrazy.automation.robot.Screenshooter;
import com.caracrazy.graphics.ImageExtensions;
import com.caracrazy.graphics.ImageLoader;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.caracrazy.automation.autoit.AutoItXExtensions.*;
import static com.caracrazy.localization.Messages.messages;

public class ChopMiniGame {

    private ChopMiniGame() {
        throw new IllegalStateException(messages().getErrorUtilityClass());
    }

    public static void start(AutoItX autoItX, Keyboard keyboard, ChopMiniGameData config) {
        Rectangle gameArea = findCriticalMinigameArea(autoItX, config.getAppName(), config);
        BufferedImage leaf = ImageLoader.loadResource(config.getCursorReference());
        keepClicking(autoItX, leaf, gameArea, keyboard, config);
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
        Point referenceArea = ImageExtensions.getSubImagePosition(screenshot, reference, 8);
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

    public static void keepClicking(AutoItX autoItX, BufferedImage leaf, Rectangle gameArea, Keyboard keyboard, ChopMiniGameData config) {
        while (!keyboard.isKeyPressed(config.getForceExitKey())) {
            BufferedImage screenshot = Screenshooter.screenshot(gameArea);
            Optional<Boolean> isGood = isGoodToClick(screenshot, leaf, config.getTargetColors());
            if (!isGood.isPresent()){
                System.out.println(messages().getErrorCursorNotFound());
            } else if (Boolean.TRUE.equals(isGood.get())) {
                System.out.println(messages().getInfoClick());
                click(autoItX, gameArea.x, gameArea.y);
            }
        }
        System.out.println(messages().getInfoForceExit());
    }

    private static Integer previousPosition = null;
    private static int previousDirection = 0;
    private static Color getBarColor(BufferedImage screenshot, int leafPoint) {
        return new Color(screenshot.getRGB(leafPoint, screenshot.getHeight() - 1), false);
    }

    private static Collection<Color> getBarColors(BufferedImage screenshot, int currentPosition, int previousPosition) {
        int nextPosition = currentPosition - previousPosition;
        int rightWall = screenshot.getWidth() - 8;
        int leftWall = 0;
        Point p = nextPosition > 0
                ? rangeRight(rightWall, nextPosition, currentPosition)
                : rangeLeft(leftWall, nextPosition, currentPosition);
        return Arrays.stream(IntStream.range(p.x, p.y).toArray())
                .mapToObj(x -> getBarColor(screenshot, x))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private static Point rangeRight(int wall, int next, int current) {
        int surplus = (next > wall) ? next - wall : 0;
        int bounce = wall - surplus;
        int start = Math.min(bounce, current);
        int end = (bounce > 0 ? wall : next);
        return new Point(start, end);
    }

    private static Point rangeLeft(int wall, int next, int current) {
        int surplus = (next < wall) ? wall - next : 0;
        int bounce = wall + surplus;
        int start = Math.max(bounce, current);
        int end = (bounce < 0 ? wall : next);
        return new Point(start, end);
    }

    public static Optional<Boolean> isGoodToClick(BufferedImage screenshot, BufferedImage reference, Collection<Color> colors) {
        Point leafPoint = ImageExtensions.getSubImagePosition(screenshot, reference, 64);

        // Not found Leaf
        if (leafPoint == null) return Optional.empty();

        // previousPosition
        if (previousPosition == null) {
            previousPosition = leafPoint.x;
            previousDirection = 0;
            return Optional.of(false);
        }

        // directionChange
        int direction = leafPoint.x - previousDirection > 0 ? 1 : 0;
        if (direction != previousDirection) {
            previousPosition = leafPoint.x;
            previousDirection = direction;
            return Optional.of(false);
        }

        Collection<Color> foundColors = getBarColors(screenshot, leafPoint.x, previousPosition);
        previousPosition = leafPoint.x;
        previousDirection = direction;
        return Optional.of(colors.containsAll(foundColors));
    }
}
