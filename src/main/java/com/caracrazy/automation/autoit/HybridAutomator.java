package com.caracrazy.automation.autoit;

import autoitx4java.AutoItX;
import com.caracrazy.automation.Automator;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

public class HybridAutomator implements Automator {

    private final AutoItX autoItX;
    private final Robot robot;

    public HybridAutomator(AutoItX autoItX, Robot robot) {
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

    @Override
    public void drag(int x1, int y1, int x2, int y2) {
        dragHold(x1, y1, x2, y2);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    @Override
    public void dragHold(int x1, int y1, int x2, int y2) {
        float direction = Math.signum(x2 - (float)x1);
        int iterations = Math.abs(x2 - x1);

        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseMove(x1, y1);

        IntStream.range(0, iterations)
                .map(i -> (int)(i * direction))
                .parallel()
                .forEach(i -> robot.mouseMove(x1 + i, y2));
    }
}
