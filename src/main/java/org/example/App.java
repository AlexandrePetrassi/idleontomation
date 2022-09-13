package org.example;

import autoitx4java.AutoItX;
import org.example.automation.AutoItXFactory;
import org.example.automation.Keyboard;
import org.example.graphics.ImageLoader;
import org.example.graphics.Screenshooter;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.example.automation.AutoItXExtensions.focusWindow;
import static org.example.automation.AutoItXExtensions.getWindowRect;
import static org.example.graphics.ImageExtensions.*;
import static org.example.idleon.ChopMiniGame.keepClicking;

public class App {

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
        Point referenceArea = getSubImagePosition(screenshot, reference, getRectangle(screenshot), 8);
        if (referenceArea == null) throw new IllegalStateException("Chop mini game not found");
        return getEnclosingArea(referenceArea, area);
    }

    public static void startChopMiniGame(AutoItX autoItX, Rectangle gameArea) {
        BufferedImage leaf = ImageLoader.load("/leaf.bmp");
        keepClicking(autoItX, leaf, gameArea);
    }

    public static void automate() {
        AutoItX autoItX = AutoItXFactory.create();
        Rectangle gameArea = findCriticalMinigameArea(autoItX, "Legends Of Idleon");
        startChopMiniGame(autoItX, gameArea);
    }

    public static void main(String[] args) {
        Keyboard.use(App::automate);
    }
}
