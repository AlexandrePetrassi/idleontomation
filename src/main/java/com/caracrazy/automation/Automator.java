package com.caracrazy.automation;

import com.caracrazy.automation.autoit.AutoItXData;
import com.caracrazy.automation.hybrid.HybridAutomatorFactory;

import java.awt.*;
import java.awt.image.BufferedImage;

public interface Automator {

    static Automator create(AutoItXData data) {
        return HybridAutomatorFactory.create(data);
    }

    BufferedImage screenshot(Rectangle area);

    void sleep(int delay);

    void focusWindow(String windowName);

    Rectangle getWindowRect(String windowName);

    void click(int x, int y);

    void drag(int x1, int y1, int x2, int y2);

    void dragHold(int x1, int y1, int x2, int y2);
}
