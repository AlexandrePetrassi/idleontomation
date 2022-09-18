package com.caracrazy.idleon;

import autoitx4java.AutoItX;
import com.caracrazy.automation.jnativehook.Keyboard;
import com.caracrazy.automation.robot.Screenshooter;
import com.caracrazy.graphics.ImageExtensions;
import com.caracrazy.graphics.ImageLoader;
import com.caracrazy.logging.Logger;
import com.caracrazy.logging.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Optional;

import static com.caracrazy.automation.autoit.AutoItXExtensions.*;
import static com.caracrazy.localization.Messages.messages;

public class ChopMiniGame {

    private static final Logger logger = LoggerFactory.create(ChopMiniGame.class.getName());

    private ChopMiniGame() {
        throw new IllegalStateException(messages().getErrorUtilityClass());
    }

    public static void innerStart(AutoItX autoItX, Keyboard keyboard, ChopMiniGameData config) {
        Rectangle gameArea = findCriticalMinigameArea(autoItX, config.getAppName(), config);
        if (gameArea == null) return;
        BufferedImage leaf = ImageLoader.loadResource(config.getCursorReference());
        Point goodRange = calculateGoodRangeWithBorder(config.getTargetColors(), Screenshooter.screenshot(gameArea));
        keepClicking(autoItX, leaf, gameArea, keyboard, goodRange, config, config.getTargetColors());
    }

    public static void start(AutoItX autoItX, Keyboard keyboard, ChopMiniGameData config) {
        while (!keyboard.isKeyPressed(config.getForceExitKey())) {
            innerStart(autoItX, keyboard, config);
        }
        logger.info(messages().getInfoForceExit());
    }

    public static Rectangle findCriticalMinigameArea(AutoItX autoItX, String windowName, ChopMiniGameData config) {
        focusWindow(autoItX, windowName);
        Rectangle windowRect = getWindowRect(autoItX, windowName);
        BufferedImage screenshot = Screenshooter.screenshot(windowRect);
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

    public static void keepClicking(AutoItX autoItX, BufferedImage leaf, Rectangle gameArea, Keyboard keyboard, Point goodRange, ChopMiniGameData config, Collection<Color> colors) {
        while (!keyboard.isKeyPressed(config.getForceExitKey())) {
            tryClick(autoItX, leaf, gameArea, goodRange, colors);
        }
        logger.info(messages().getInfoForceExit());
    }

    public static void tryClick(AutoItX autoItX, BufferedImage leaf, Rectangle gameArea, Point goodRange, Collection<Color> colors) {
        Game game = new Game();
        while (true) {
            BufferedImage screenshot = Screenshooter.screenshot(gameArea);
            if(update(game, gameArea, goodRange, screenshot, leaf, autoItX, colors)) return;
        }
    }

    public static boolean update(Game game, Rectangle gameArea, Point goodRange, BufferedImage screenshot, BufferedImage leaf, AutoItX autoItX, Collection<Color> colors) {
        Optional<Boolean> position = isScreenshotGoodToClick(game, screenshot, leaf, goodRange, colors);
        if (!position.isPresent()) {
            return breakLoop();
        } else if (Boolean.TRUE.equals(position.get())) {
            return clickTheScreen(autoItX, gameArea);
        }
        return false;
    }

    public static boolean breakLoop() {
        logger.info(messages().getErrorCursorNotFound());
        return true;
    }

    public static boolean clickTheScreen(AutoItX autoItX, Rectangle gameArea) {
        logger.info(messages().getInfoClick());
        click(autoItX, gameArea.x, gameArea.y);
        autoItX.sleep(500);
        return true;
    }

    public static boolean isGoodClick(Game game, int point, Point goodRange, BufferedImage screenshot, Collection<Color> colors) {
        return isInGoodRange(game.update(point), goodRange) && isGoodColor(colors, getBarColor(screenshot, point));
    }

    public static Optional<Boolean> isScreenshotGoodToClick(Game game, BufferedImage screenshot, BufferedImage leaf, Point goodRange, Collection<Color> colors) {
        return ImageExtensions
                .getSubImagePosition(screenshot, leaf, 64)
                .map(point -> isGoodClick(game, point.x, goodRange, screenshot, colors));
    }

    public static Color getBarColor(BufferedImage screenshot, int leafPoint) {
        return new Color(screenshot.getRGB(leafPoint, screenshot.getHeight() - 1), false);
    }

    public static boolean isInGoodRange(Game game, Point goodRange) {
        if (game.getPreviousSpeed() == null) return false;
        return between(goodRange, adjustedCurrentPosition(game)) && between(goodRange, adjustedNextPosition(game));
    }

    public static int adjustedCurrentPosition(Game game) {
        return game.getCurrentPosition() - game.getCurrentDirection() * 2;
    }

    public static int adjustedNextPosition(Game game) {
        return game.getNextPosition() + game.getCurrentDirection() * 2;
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
                if (start != null) return new Point(start, i);
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

    public static class Game {

        private Integer previousSpeed;
        public Integer getPreviousSpeed() {
            return previousSpeed;
        }

        private Integer currentSpeed;
        public Integer getCurrentSpeed() {
            return currentSpeed;
        }

        private Integer previousPosition;
        public Integer getPreviousPosition() {
            return previousPosition;
        }

        private Integer currentPosition;
        public Integer getCurrentPosition() {
            return currentPosition;
        }

        private Long currentTime;
        public Long getCurrentTime() {
            return currentTime;
        }

        private Long previousTime;
        public Long getPreviousTime() {
            return previousTime;
        }

        private Long previousDeltaTime;
        public Long getPreviousDeltaTime() {
            return previousDeltaTime;
        }

        private Long currentDeltaTime;
        public Long getCurrentDeltaTime() {
            return currentDeltaTime;
        }

        private Integer previousDeltaPosition;
        public Integer getPreviousDeltaPosition() {
            return previousDeltaPosition;
        }

        private Integer currentDeltaPosition;

        private Integer calculateDeltaPosition() {
            if (previousPosition == null || currentPosition == null) return null;
            return currentPosition - previousPosition;
        }

        private Long calculateDeltaTime() {
            if (previousTime == null || currentTime == null) return null;
            return currentTime - previousTime;
        }

        private Integer calculateCurrentSpeed() {
            if (currentDeltaPosition == null) return null;
            if (currentDeltaTime == null || currentDeltaTime == 0) return null;
            return (int) (currentDeltaPosition / currentDeltaTime);
        }

        private void setCurrentPosition(Integer newPosition) {
            previousPosition = currentPosition;
            previousTime = currentTime;
            previousDeltaTime = currentDeltaTime;
            previousDeltaPosition = currentDeltaPosition;
            previousSpeed = currentSpeed;

            currentPosition = newPosition;
            currentTime = System.nanoTime();
            currentDeltaTime = calculateDeltaTime();
            currentDeltaPosition = calculateDeltaPosition();
            currentSpeed = calculateCurrentSpeed();
        }

        public Game update(Integer newPosition) {
            setCurrentPosition(newPosition);
            return this;
        }

        public Long averageDeltaTime() {
            if (currentDeltaTime == null || previousDeltaTime == null) return null;
            return currentDeltaTime / previousDeltaTime;
        }

        public Integer getNextPosition() {
            return getCurrentPosition() + (int) (getCurrentSpeed() * averageDeltaTime());
        }

        public boolean isSwitchingDirection() {
            return getCurrentDirection() != getPreviousDirection();
        }

        public int getCurrentDirection() {
            return (int) Math.signum(getCurrentSpeed());
        }

        public int getPreviousDirection() {
            return (int)  Math.signum(getPreviousSpeed());
        }

        @Override
        public String toString() {
            return "Game{" +
                    "previousSpeed=" + previousSpeed +
                    ", previousPosition=" + previousPosition +
                    ", currentPosition=" + currentPosition +
                    '}';
        }
    }
}
