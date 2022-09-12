package org.example;

import autoitx4java.AutoItX;
import org.example.automation.AutoItXFactory;
import org.example.automation.Keyboard;
import org.example.graphics.ImageLoader;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.example.automation.AutoItXExtensions.getGameArea;
import static org.example.idleon.ChopMiniGame.keepClicking;

public class App {

    public static Rectangle gameArea(AutoItX autoItX) {
        String windowName = "Legends Of Idleon";
        BufferedImage chop = ImageLoader.load("/chop.bmp");
        Rectangle area = new Rectangle(-235, -20, 0, 35);
        return getGameArea(autoItX, windowName, chop, area);
    }

    public static void startChopMiniGame(AutoItX autoItX, Rectangle gameArea) {
        BufferedImage leaf = ImageLoader.load("/leaf.bmp");
        Rectangle offset = new Rectangle(0, 10, 1, 35);
        keepClicking(autoItX, leaf, gameArea, offset);
    }

    public static void automate() {
        AutoItX autoItX = AutoItXFactory.create();
        Rectangle gameArea = gameArea(autoItX);
        if (gameArea == null) {
            System.out.println("Chop mini game not found");
            return;
        }
        startChopMiniGame(autoItX, gameArea);
    }

    public static void main(String[] args) {
        Keyboard.start();
        try {
            automate();
        } finally {
            Keyboard.stop();
        }
    }
}
