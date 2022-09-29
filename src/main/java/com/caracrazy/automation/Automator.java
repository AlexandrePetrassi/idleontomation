package com.caracrazy.automation;

import java.awt.*;
import java.awt.image.BufferedImage;

public interface Automator {

    BufferedImage screenshot(Rectangle area);

    void sleep(int delay);

    void focusWindow(String windowName);

    Rectangle getWindowRect(String windowName);

    void click(int x, int y);
}