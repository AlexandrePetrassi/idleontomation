package com.caracrazy.idleon;

import com.caracrazy.automation.Automator;
import com.caracrazy.automation.jnativehook.Keyboard;
import com.caracrazy.graphics.ImageExtensions;
import com.caracrazy.graphics.ImageLoader;
import com.caracrazy.logging.Logger;
import com.caracrazy.logging.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Optional;

import static com.caracrazy.localization.Messages.messages;

public class ChopMiniGame {

    private static final Logger logger = LoggerFactory.create(ChopMiniGame.class.getName());

    private ChopMiniGame() {
        throw new IllegalStateException(messages().getErrorUtilityClass());
    }

    public static void playSingleRound(Automator automator, Keyboard keyboard, ChopMiniGameData config) {
        Rectangle gameArea = findCriticalMinigameArea(automator, config.getAppName(), config);
        if (gameArea == null) return;
        BufferedImage leaf = ImageLoader.loadResource(config.getCursorReference());
        Point goodRange = calculateGoodRangeWithBorder(config.getTargetColors(), automator.screenshot(gameArea));
        keepClicking(automator, leaf, gameArea, keyboard, goodRange, config, config.getTargetColors());
    }

    public static void start(Automator automator, Keyboard keyboard, ChopMiniGameData config) {
        while (!keyboard.isKeyPressed(config.getForceExitKey())) {
            playSingleRound(automator, keyboard, config);
        }
        logger.info(messages().getInfoForceExit());
    }

    public static Rectangle findCriticalMinigameArea(Automator automator, String windowName, ChopMiniGameData config) {
        automator.focusWindow(windowName);
        Rectangle windowRect = automator.getWindowRect(windowName);
        BufferedImage screenshot = automator.screenshot(windowRect);
        Rectangle result = findBiggerMinigameArea(screenshot, config);
        if (result == null) return null;
        return new Rectangle(windowRect.x + result.x - 1, windowRect.y + result.y, 230, result.height);
    }

    public static Rectangle findBiggerMinigameArea(BufferedImage screenshot, ChopMiniGameData config) {
        BufferedImage reference = ImageLoader.loadResource(config.getFrameReference());
        Rectangle area = new Rectangle(10, -11, 240, 15);
        Optional<Point> referenceArea = ImageExtensions.getSubImagePosition(screenshot, reference, 8);
        if (referenceArea.isPresent()) return getEnclosingArea(referenceArea.get(), area);
        logger.info(messages().getErrorFrameNotFound());
        return null;
    }

    public static Rectangle getEnclosingArea(Point point, Rectangle area) {
        return new Rectangle(
                point.x + area.x,
                point.y + area.y,
                area.width - area.x,
                area.height - area.y
        );
    }

    public static void keepClicking(Automator automator, BufferedImage leaf, Rectangle gameArea, Keyboard keyboard, Point goodRange, ChopMiniGameData config, Collection<Color> colors) {
        while (!keyboard.isKeyPressed(config.getForceExitKey())) {
            tryClick(automator, leaf, gameArea, goodRange, colors);
        }
        logger.info(messages().getInfoForceExit());
    }

    public static void tryClick(Automator automator, BufferedImage leaf, Rectangle gameArea, Point goodRange, Collection<Color> colors) {
        ChopMiniGameState game = new ChopMiniGameState();
        automator.sleep(500);
        while (true) {
            BufferedImage screenshot = automator.screenshot(gameArea);
            if(update(game, gameArea, goodRange, screenshot, leaf, automator, colors)) return;
        }
    }

    public static boolean update(ChopMiniGameState game, Rectangle gameArea, Point goodRange, BufferedImage screenshot, BufferedImage leaf, Automator automator, Collection<Color> colors) {
        Optional<Boolean> position = isScreenshotGoodToClick(game, screenshot, leaf, goodRange, colors);
        if (!position.isPresent()) {
            return breakLoop();
        } else if (Boolean.TRUE.equals(position.get())) {
            return clickTheScreen(automator, gameArea);
        }
        return false;
    }

    public static boolean breakLoop() {
        logger.info(messages().getErrorCursorNotFound());
        return true;
    }

    public static boolean clickTheScreen(Automator automator, Rectangle gameArea) {
        logger.info(messages().getInfoClick());
        automator.click((int) gameArea.getMaxX(), (int) gameArea.getMaxY());
        return true;
    }

    public static boolean isGoodClick(ChopMiniGameState game, int point, Point goodRange, BufferedImage screenshot, Collection<Color> colors) {
        return isInGoodRange(game.update(point), goodRange) && isGoodColor(colors, getBarColor(screenshot, point));
    }

    public static Optional<Boolean> isScreenshotGoodToClick(ChopMiniGameState game, BufferedImage screenshot, BufferedImage leaf, Point goodRange, Collection<Color> colors) {
        return ImageExtensions
                .getSubImagePosition(screenshot, leaf, 64)
                .map(point -> isGoodClick(game, point.x, goodRange, screenshot, colors));
    }

    public static Color getBarColor(BufferedImage screenshot, int leafPoint) {
        return new Color(screenshot.getRGB(leafPoint, screenshot.getHeight() - 1), false);
    }

    public static boolean isInGoodRange(ChopMiniGameState game, Point goodRange) {
        if (game.getCurrent().getSpeed() == null) return false;
        return between(goodRange, adjustedCurrentPosition(game)) && between(goodRange, adjustedNextPosition(game));
    }

    public static int adjustedCurrentPosition(ChopMiniGameState game) {
        return game.getCurrent().getPosition() - game.getCurrent().getDirection() * 2;
    }

    public static int adjustedNextPosition(ChopMiniGameState game) {
        return game.getNextPosition() + game.getCurrent().getDirection() * 2;
    }

    public static boolean between(Point range, int point) {
        return point > range.x && point < range.y;
    }

    public static Point calculateGoodRangeWithBorder(Collection<Color> colors, BufferedImage screenshot) {
        Point point = calculateGoodRange(colors, screenshot);
        if (point.x <= 1) return new Point(-point.y, point.y);
        if (point.y >= screenshot.getWidth() - 9) return new Point(point.x, point.y + (point.y - point.x));
        return point;
    }

    public static Point calculateGoodRange(Collection<Color> colors, BufferedImage screenshot) {
        Integer start = null;
        for (int i = 1; i < screenshot.getWidth() - 9; i++) {
            Color color = getBarColor(screenshot, i);
            boolean isGood = isGoodColor(colors, color);
            if (isGood) {
                if (start == null) start = i;
            } else {
                if (start != null) return new Point(start, i-1);
            }
        }
        return new Point(0, 0);
    }

    private static boolean isGoodColor(Collection<Color> colors, Color color) {
        return colors.stream().parallel().anyMatch(expected -> isSameColor(expected, color));
    }

    public static boolean isSameColor(Color a, Color b) {
        return a.getRed() == b.getRed() && a.getGreen() == b.getGreen() && a.getBlue() == b.getBlue();
    }

}
