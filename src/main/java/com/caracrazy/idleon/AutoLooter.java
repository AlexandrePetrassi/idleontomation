package com.caracrazy.idleon;

import com.caracrazy.automation.Automator;
import com.caracrazy.automation.autoit.AutoItXData;
import com.caracrazy.automation.autoit.AutoItXFactory;
import com.caracrazy.automation.jnativehook.Keyboard;
import com.caracrazy.automation.jnativehook.KeyboardListener;

import java.awt.*;

public enum AutoLooter {
    INSTANCE();

    public void start(Keyboard keyboard, Automator automator) {
        automator.focusWindow("Legends Of Idleon");
        Rectangle rect = automator.getWindowRect("Legends Of Idleon");
        int x1 = rect.x + 32;
        int x2 = rect.x + rect.width - 32;
        int y = rect.y + 64;
        int maxY = rect.y + rect.height - 128;
        int increment = 24;
        while (!keyboard.isKeyPressed(1)) {
            automator.drag(x1, y, x2, y);
            int oldY = y;
            y += increment;
            if (++y>= maxY) y = rect.y + 64;
            automator.drag(x2, oldY, x1, y);
        }
    }


    public void start(AutoItXData config) {
        Automator autoItX = AutoItXFactory.INSTANCE.create(config);
        KeyboardListener.use(keyboard -> start(keyboard, autoItX));
    }
}
