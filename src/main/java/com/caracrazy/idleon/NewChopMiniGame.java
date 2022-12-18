package com.caracrazy.idleon;

import com.caracrazy.automation.Automator;
import com.caracrazy.graphics.ColorExtensions;
import com.caracrazy.graphics.ImageExtensions;
import com.caracrazy.graphics.ImageLoader;
import com.caracrazy.graphics.NewImageExtensions;
import com.caracrazy.input.Keyboard;
import com.caracrazy.logging.Logger;
import com.caracrazy.logging.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Optional;

import static com.caracrazy.localization.Messages.messages;

public class NewChopMiniGame {

    private static final Logger logger = LoggerFactory.create(NewChopMiniGame.class.getName());

    private NewChopMiniGame() {
        throw new IllegalStateException(messages().getErrorUtilityClass());
    }

    public static void playSingleRound(Automator automator, Keyboard keyboard, ChopMiniGameData config) {
        Rectangle gameArea = findCriticalMinigameArea(automator, config.getAppName(), config);
        if (gameArea == null) return;
        BufferedImage leaf = ImageLoader.loadResource(config.getCursorReference());
        int[] colors = ColorExtensions.toIntArray(config.getTargetColors());
        Point goodRange = calculateGoodRangeWithBorder(colors, gameArea.width, gameArea.height, automator);
        keepClicking(automator, leaf, gameArea, keyboard, goodRange, config, colors);
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

    public static void keepClicking(Automator automator, BufferedImage leaf, Rectangle gameArea, Keyboard keyboard, Point goodRange, ChopMiniGameData config, int[] colors) {
        while (!keyboard.isKeyPressed(config.getForceExitKey())) {
            tryClick(automator, leaf, gameArea, goodRange, colors);
        }
        logger.info(messages().getInfoForceExit());
    }

    public static void tryClick(Automator automator, BufferedImage leaf, Rectangle gameArea, Point goodRange, int[] colors) {
        ChopMiniGameState game = new ChopMiniGameState();
        automator.sleep(500);
        while (true) {
            if(update(game, gameArea, goodRange, leaf, automator, colors)) return;
        }
    }

    public static boolean update(ChopMiniGameState game, Rectangle gameArea, Point goodRange,BufferedImage leaf, Automator automator, int[] colors) {
        Optional<Boolean> position = isScreenshotGoodToClick(game, gameArea, leaf, goodRange, colors, automator);
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

    public static boolean isGoodClick(ChopMiniGameState game, int point, Point goodRange, Rectangle screenshot, Automator automator, int[] colors) {
        return isInGoodRange(game.update(point), goodRange) && isBarColorGood(screenshot.height, point, automator, colors);
    }

    public static Optional<Boolean> isScreenshotGoodToClick(ChopMiniGameState game, Rectangle screenshot, BufferedImage leaf, Point goodRange, int[] colors, Automator automator) {
        return NewImageExtensions
                .getSubImagePosition(screenshot, leaf, 64, automator)
                .map(point -> isGoodClick(game, point.x, goodRange, screenshot, automator, colors));
    }

    public static boolean isBarColorGood(int height, int leafPoint, Automator automator, int[] colors) {
        for(int color : colors) {
            if (automator.matchPixel(leafPoint, height -1 , color, 0)) {
                return true;
            }
        }
        return false;
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

    public static Point calculateGoodRangeWithBorder(int[] colors, int w, int h, Automator automator) {
        Point point = calculateGoodRange(colors, w, h, automator);
        if (point.x <= 1) return new Point(-point.y, point.y);
        if (point.y >= w - 9) return new Point(point.x, point.y + (point.y - point.x));
        return point;
    }

    public static Point calculateGoodRange(int[] colors, int w, int h, Automator automator) {
        Integer start = null;
        for (int i = 1; i < w - 9; i++) {
            if (isBarColorGood(h, i, automator, colors)) {
                if (start == null) start = i;
            } else {
                if (start != null) return new Point(start, i - 1);
            }
        }
        return new Point(0, 0);
    }

}
