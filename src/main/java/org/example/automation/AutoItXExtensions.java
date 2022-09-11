package org.example.automation;

import autoitx4java.AutoItX;
import org.example.graphics.Screenshooter;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.example.graphics.ImageExtensions.*;

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

    public static Rectangle getGameArea(AutoItX autoItX, String windowName, BufferedImage reference, Rectangle area) {
        focusWindow(autoItX, windowName);
        BufferedImage screenshot = Screenshooter.screenshot(getWindowRect(autoItX, windowName));
        Point referenceArea = getSubImagePosition(screenshot, reference, getRectangle(screenshot));
        if (referenceArea == null) return null;
        return getEnclosingArea(referenceArea, area);
    }
}
