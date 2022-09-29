package com.caracrazy.automation.autoit;

import autoitx4java.AutoItX;
import com.caracrazy.automation.Automator;

import java.awt.*;
import java.awt.image.BufferedImage;

public class AutoItXAutomator implements Automator {

    private final AutoItX autoItX;
    private final Robot robot;

    public AutoItXAutomator(AutoItX autoItX, Robot robot) {
        this.autoItX = autoItX;
        this.robot = robot;
    }

    @Override
    public BufferedImage screenshot(Rectangle area) {
        return robot.createScreenCapture(area);
    }

    @Override
    public void sleep(int delay) {
        autoItX.sleep(delay);
    }

    @Override
    public  void focusWindow(String windowName) {
       autoItX.winActivate(windowName);
       autoItX.winWaitActive(windowName, "", 1);
    }

    @Override
    public  Rectangle getWindowRect(String windowName) {
        return new Rectangle(
                autoItX.winGetPosX(windowName),
                autoItX.winGetPosY(windowName),
                autoItX.winGetPosWidth(windowName),
                autoItX.winGetPosHeight(windowName)
        );
    }

    @Override
    public  void click(int x, int y) {
        autoItX.mouseMove(x, y, 0);
        autoItX.mouseDown("left");
        autoItX.mouseUp("left");
    }
}
