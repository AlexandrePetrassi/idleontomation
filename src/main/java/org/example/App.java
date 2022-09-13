package org.example;

import autoitx4java.AutoItX;
import org.example.automation.AutoItXFactory;
import org.example.automation.Keyboard;
import org.example.graphics.ImageLoader;
import org.example.idleon.ChopMiniGame;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.example.idleon.ChopMiniGame.keepClicking;

public class App {

    public static void startChopMiniGame() {
        AutoItX autoItX = AutoItXFactory.create();
        Rectangle gameArea = ChopMiniGame.findCriticalMinigameArea(autoItX, "Legends Of Idleon");
        BufferedImage leaf = ImageLoader.loadResource("leaf.bmp");
        keepClicking(autoItX, leaf, gameArea);
    }

    public static void main(String[] args) {
        Keyboard.use(App::startChopMiniGame);
    }
}
