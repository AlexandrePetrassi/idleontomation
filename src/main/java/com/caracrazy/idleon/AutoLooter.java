package com.caracrazy.idleon;

import com.caracrazy.automation.Automator;
import com.caracrazy.automation.autoit.AutoItXData;
import com.caracrazy.automation.hybrid.HybridAutomatorFactory;
import com.caracrazy.input.Keyboard;
import com.caracrazy.input.KeyboardListener;

import java.awt.*;

public enum AutoLooter {

    INSTANCE();

    public void start(Keyboard keyboard, Automator automator) {
        automator.focusWindow("Legends Of Idleon");
        Rectangle rect = automator.getWindowRect("Legends Of Idleon");
        Rectangle adjustedRect = adjustRect(rect, 32, 64, -32, -128);
        int increment = 24;
        while (!keyboard.isKeyPressed(1)) {
            dragOverArea(keyboard, automator, adjustedRect, increment);
        }
    }

    public Rectangle adjustRect(Rectangle rect, int x, int y, int w, int h) {
        return new Rectangle(
                rect.x + x,
                rect.y + y,
                rect.width + w,
                rect.height + h
        );
    }

    private void dragOverArea(Keyboard keyboard, Automator automator, Rectangle area, int increment) {
        automator.drag((int)area.getCenterX(), (int)area.getCenterY(), (int)area.getCenterX(), (int)area.getCenterY());
        for (int i = area.y; (i < (int) area.getMaxY()) && !keyboard.isKeyPressed(1); i += increment) {
            automator.dragHold(area.x, i, (int)area.getMaxX(), i);
            automator.dragHold((int)area.getMaxX(), i, area.x, i);
        }
    }

    public void start(AutoItXData config) {
        Automator autoItX = HybridAutomatorFactory.create(config);
        KeyboardListener.use(keyboard -> start(keyboard, autoItX));
    }
}
