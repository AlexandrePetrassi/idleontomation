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
import static org.example.idleon.ChopMiniGame.findBiggerMinigameArea;
import static org.example.idleon.ChopMiniGame.keepClicking;

public class App {

    public static Rectangle findCriticalMinigameArea(AutoItX autoItX, String windowName) {
        focusWindow(autoItX, windowName);
        Rectangle windowRect = getWindowRect(autoItX, windowName);
        BufferedImage screenshot = Screenshooter.screenshot(windowRect);
        Rectangle result = findBiggerMinigameArea(screenshot);
        return new Rectangle(windowRect.x + result.x, windowRect.y + result.y, result.width, result.height);
    }

    public static void startChopMiniGame() {
        AutoItX autoItX = AutoItXFactory.create();
        Rectangle gameArea = findCriticalMinigameArea(autoItX, "Legends Of Idleon");
        BufferedImage leaf = ImageLoader.loadResource("leaf.bmp");
        keepClicking(autoItX, leaf, gameArea);
    }

    public static void main(String[] args) {
        Keyboard.use(App::startChopMiniGame);
    }
}
