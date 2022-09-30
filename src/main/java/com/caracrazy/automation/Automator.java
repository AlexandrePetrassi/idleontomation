package com.caracrazy.automation;

import java.awt.*;
import java.awt.image.BufferedImage;

public interface Automator {

    BufferedImage screenshot(Rectangle area);

    void sleep(int delay);

    void focusWindow(String windowName);

    Rectangle getWindowRect(String windowName);

    void click(int x, int y);

    void drag(int x1, int y1, int x2, int y2);
}