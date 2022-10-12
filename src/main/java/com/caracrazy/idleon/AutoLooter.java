package com.caracrazy.idleon;

import com.caracrazy.automation.Automator;
import com.caracrazy.automation.AutomatorData;
import com.caracrazy.automation.AutomatorFactory;
import com.caracrazy.input.Keyboard;
import com.caracrazy.input.KeyboardEvent;
import com.caracrazy.input.ManualKeyboardListener;
import com.caracrazy.logging.Logger;
import com.caracrazy.logging.LoggerFactory;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public enum AutoLooter {

    INSTANCE();

    private static final Logger logger = LoggerFactory.create(AutoLooter.class.getName());

    public void start(ManualKeyboardListener.EventListener keyboard, Automator automator) {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() { @Override public void run() { old(keyboard, automator); } };
        keyboard.addKeyEventListener(0 , KeyboardEvent.RELEASE, timer::cancel);
        keyboard.addKeyEventListener(0 , KeyboardEvent.RELEASE, () -> ManualKeyboardListener.stop(keyboard));
        keyboard.addKeyEventListener(0 , KeyboardEvent.RELEASE, () -> Thread.currentThread().interrupt());
        timer.schedule(task, 0, 1);
    }
    private void old(Keyboard keyboard, Automator automator) {
        automator.focusWindow("Legends Of Idleon");
        Rectangle rect = automator.getWindowRect("Legends Of Idleon");
        Rectangle adjustedRect = adjustRect(rect, 32, 64, -32, -128);
        int increment = 24;
        dragOverArea(keyboard, automator, adjustedRect, increment);
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

    public void start(AutomatorData config) {
        Automator autoItX = AutomatorFactory.create(config);
        ManualKeyboardListener.EventListener keyboard = ManualKeyboardListener.create();
        ManualKeyboardListener.start(keyboard);
        start(keyboard, autoItX);
    }
}
