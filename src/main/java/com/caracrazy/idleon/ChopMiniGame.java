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

    private static final Logger logger = LoggerFactory.create();

    private ChopMiniGame() {
        throw new IllegalStateException(messages().getErrorUtilityClass());
    }

    public static void start(AutoItX autoItX, Keyboard keyboard, ChopMiniGameData config) {
        Rectangle gameArea = findCriticalMinigameArea(autoItX, config.getAppName(), config);
        BufferedImage leaf = ImageLoader.loadResource(config.getCursorReference());
        Point goodRange = calculateGoodRange(config.getTargetColors(), Screenshooter.screenshot(gameArea));
        keepClicking(autoItX, leaf, gameArea, keyboard, goodRange, config);
    }

    public static Rectangle findCriticalMinigameArea(AutoItX autoItX, String windowName, ChopMiniGameData config) {
        focusWindow(autoItX, windowName);
        Rectangle windowRect = getWindowRect(autoItX, windowName);
        BufferedImage screenshot = Screenshooter.screenshot(windowRect);
        Rectangle result = findBiggerMinigameArea(screenshot, config);
        return new Rectangle(windowRect.x + result.x + 10, windowRect.y + result.y, result.width - 63, result.height);
    }

    public static Rectangle findBiggerMinigameArea(BufferedImage screenshot, ChopMiniGameData config) {
        BufferedImage reference = ImageLoader.loadResource(config.getFrameReference());
        Rectangle area = new Rectangle(10, -11, 240, 15);
        Optional<Point> referenceArea = ImageExtensions.getSubImagePosition(screenshot, reference, 8);
        if (referenceArea.isPresent()) return getEnclosingArea(referenceArea.get(), area);
        throw new IllegalStateException(messages().getErrorFrameNotFound());
    }

    public static Rectangle getEnclosingArea(Point point, Rectangle area) {
        return new Rectangle(
                point.x + area.x,
                point.y + area.y,
                area.width - area.x,
                area.height - area.y
        );
    }

    public static void keepClicking(AutoItX autoItX, BufferedImage leaf, Rectangle gameArea, Keyboard keyboard, Point goodRange, ChopMiniGameData config) {
        while (!keyboard.isKeyPressed(config.getForceExitKey())) {
            tryClick(autoItX, leaf, gameArea, goodRange);
        }
        logger.info(messages().getInfoForceExit());
    }

    public static void tryClick(AutoItX autoItX, BufferedImage leaf, Rectangle gameArea, Point goodRange) {
        Game game = new Game();
        while (true) {
            BufferedImage screenshot = Screenshooter.screenshot(gameArea);
            Optional<Boolean> position = isScreenshotGoodToClick(game, screenshot, leaf, goodRange);
            if (!position.isPresent()) {
                logger.info(messages().getErrorCursorNotFound());
                return;
            } else if (Boolean.TRUE.equals(position.get())){
                logger.info(messages().getInfoClick());
                click(autoItX, gameArea.x, gameArea.y);
                return;
            }
        }
    }

    public static Optional<Boolean> isScreenshotGoodToClick(Game game, BufferedImage screenshot, BufferedImage leaf, Point goodRange) {
        return ImageExtensions
                .getSubImagePosition(screenshot, leaf, 64)
                .flatMap(position -> Optional.of(isInGoodRange(game.update(position.x), goodRange)));
    }

    public static Color getBarColor(BufferedImage screenshot, int leafPoint) {
        return new Color(screenshot.getRGB(leafPoint, screenshot.getHeight() - 1), false);
    }

    public static boolean isInGoodRange(Game game, Point goodRange) {
        if (game.getPreviousSpeed() == null) return false;
        return between(goodRange, adjustedCurrentPosition(game)) && between(goodRange, adjustedNextPosition(game));
    }

    public static int adjustedCurrentPosition(Game game) {
        return game.getCurrentPosition() - game.getCurrentDirection();
    }

    public static int adjustedNextPosition(Game game) {
        return game.getNextPosition() - game.getCurrentDirection();
    }

    public static boolean between(Point range, int point) {
        return point > range.x && point < range.y;
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

        private Integer previousPosition;
        public Integer getPreviousPosition() {
            return previousPosition;
        }

        private Integer currentPosition;
        public Integer getCurrentPosition() {
            return currentPosition;
        }

        private void setCurrentPosition(Integer newPosition) {
            previousSpeed = previousPosition == null ? null : currentPosition - previousPosition;
            previousPosition = currentPosition;
            currentPosition = newPosition;
        }

        public Game update(Integer newPosition) {
            setCurrentPosition(newPosition);
            return this;
        }

        public Integer getCurrentSpeed() {
            return getCurrentPosition() - getPreviousPosition();
        }

        public Integer getNextPosition() {
            return getCurrentPosition() + getCurrentSpeed();
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
