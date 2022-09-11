package org.example;

import autoitx4java.AutoItX;

import java.awt.*;

public class App 
{

    public static void focusIdleOnWindow(AutoItX autoItX, String windowName) {
        autoItX.winActivate(windowName);
        autoItX.winWaitActive(windowName, "", 1);
    }

    public static Rectangle getIdleOnScreenRect(AutoItX autoItX, String windowName) {
        return new Rectangle(
                autoItX.winGetPosX(windowName),
                autoItX.winGetPosY(windowName),
                autoItX.winGetPosWidth(windowName),
                autoItX.winGetPosHeight(windowName)
        );
    }

    public static void main(String[] args) {
        AutoItX autoItX = AutoItXFactory.create();
        Paint.drawCircleUsingPaint(autoItX);
        focusIdleOnWindow(autoItX, "Legends Of Idleon");
    }
}
