package com.caracrazy.automation;

import autoitx4java.AutoItX;

public class AutomatizadorAutoItX implements Automatizador {

    private final AutoItX autoItX;

    public AutomatizadorAutoItX(AutoItX autoItX) {
        this.autoItX = autoItX;
    }


    @Override
    public String winGetHandle(String titulo) {
        return autoItX.winGetHandle(titulo);
    }

    @Override
    public void winActivate(String titulo) {
        autoItX.winActivate(titulo);
    }

    @Override
    public int winGetPosX(String windowName) {
        return autoItX.winGetPosX(windowName);
    }

    @Override
    public int winGetPosY(String windowName) {
        return autoItX.winGetPosY(windowName);
    }

    @Override
    public int controlGetPosX(String windowName, String text, String elementID) {
        return autoItX.controlGetPosX(windowName, text, elementID);
    }

    @Override
    public int controlGetPosY(String windowName, String text, String elementID) {
        return autoItX.controlGetPosY(windowName, text, elementID);
    }

    @Override
    public int controlGetPosWidth(String windowName, String text, String elementID) {
        return autoItX.controlGetPosWidth(windowName, text, elementID);
    }

    @Override
    public int controlGetPosHeight(String windowName, String text, String elementID) {
        return autoItX.controlGetPosHeight(windowName, text, elementID);
    }

    @Override
    public int processExists(String appName) {
        return autoItX.processExists(appName);
    }

    @Override
    public void processClose(String appName) {
        autoItX.processClose(appName);
    }

    @Override
    public boolean mouseMove(int x, int y, int speed) {
        return autoItX.mouseMove(x, y, speed);
    }

    @Override
    public void mouseDown(String button) {
        autoItX.mouseDown(button);
    }

    @Override
    public void mouseUp(String button) {
        autoItX.mouseUp(button);
    }

    @Override
    public void mouseClickDrag(String button, int x, int y, int x2, int y2) {
        autoItX.mouseClickDrag(button, x, y, x2, y2);
    }

    @Override
    public boolean controlClick(String title, String text, String controlId, String button, int clicks, int x, int y) {
        return autoItX.controlClick(title, text, controlId, button, clicks, x, y);
    }

    @Override
    public boolean controlClick(String title, String text, String controlId) {
        return autoItX.controlClick(title, text, controlId);
    }

    @Override
    public boolean winWaitActive(String title) {
        return autoItX.winWaitActive(title);
    }

    @Override
    public boolean winWaitActive(String title, String text, int timeout) {
        return autoItX.winWaitActive(title, text, timeout);
    }

    @Override
    public boolean controlSend(String title, String text, String control, String keys, boolean isRaw) {
        return autoItX.controlSend(title, text, control, keys, isRaw);
    }

    @Override
    public String controlGetText(String title, String text, String controlId) {
        return autoItX.controlGetText(title, text, controlId);
    }

    @Override
    public int mouseGetPosX() {
        return autoItX.mouseGetPosX();
    }

    @Override
    public int mouseGetPosY() {
        return autoItX.mouseGetPosY();
    }

    @Override
    public int winGetPosWidth(String title) {
        return autoItX.winGetPosWidth(title);
    }

    @Override
    public int winGetPosHeight(String title) {
        return autoItX.winGetPosHeight(title);
    }
}
