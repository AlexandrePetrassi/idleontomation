package com.caracrazy.automation;

import autoitx4java.AutoItX;

import java.awt.*;

public class AutoItXExtensions {

    private AutoItXExtensions() {
        throw new IllegalStateException("Utility Class");
    }

    public static void focusWindow(AutoItX autoItX, String windowName) {
        autoItX.winActivate(windowName);
        autoItX.winWaitActive(windowName, "", 1);
    }

    public static Rectangle getWindowRect(AutoItX autoItX, String windowName) {
        return new Rectangle(
                autoItX.winGetPosX(windowName),
                autoItX.winGetPosY(windowName),
                autoItX.winGetPosWidth(windowName),
                autoItX.winGetPosHeight(windowName)
        );
    }

    public static void click(AutoItX autoItX, int x, int y) {
        autoItX.mouseMove(x, y, 0);
        autoItX.mouseDown("left");
        autoItX.mouseUp("left");
        autoItX.sleep(50);
    }
}
