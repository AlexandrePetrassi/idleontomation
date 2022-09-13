package org.example;

import org.example.automation.Keyboard;
import org.example.idleon.ChopMiniGame;

public class App {

    public static void main(String[] args) {
        Keyboard.use(ChopMiniGame::start);
    }
}
