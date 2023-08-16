package com.caracrazy.automation;

import java.awt.*;

public interface Automatizador {
    default Rectangle getRect(String elementID, String windowName) {
        return new Rectangle(
                controlGetPosX(windowName, "", elementID) + winGetPosX(windowName),
                controlGetPosY(windowName, "", elementID) + winGetPosY(windowName),
                controlGetPosWidth(windowName, "", elementID),
                controlGetPosHeight(windowName, "", elementID)
        );
    }

    default Rectangle getRect(String windowName) {
        return new Rectangle(
                winGetPosX(windowName),
                winGetPosY(windowName),
                winGetPosWidth(windowName),
                winGetPosHeight(windowName)
        );
    }

    default boolean activateWindow(String titulo, int timeout) {
        winActivate(titulo);
        return winWaitActive(titulo, "", timeout);
    }

    default void closeAllProcesses(String processName) {
        while (processExists(processName) != 0) {
            processClose(processName);
        }
    }

    String winGetHandle(String titulo);
    void winActivate(String titulo);
    int winGetPosX(String windowName);
    int winGetPosY(String windowName);
    int controlGetPosX(String windowName, String text, String elementID);
    int controlGetPosY(String windowName, String text, String elementID);
    int controlGetPosWidth(String windowName, String text, String elementID);
    int controlGetPosHeight(String windowName, String text, String elementID);
    int processExists(String appName);
    void processClose(String appName);
    boolean mouseMove(int x, int y, int speed);
    void mouseDown(String button);
    void mouseUp(String button);
    void mouseClickDrag(String button, int x, int y, int x2, int y2);
    boolean controlClick(String title, String text, String controlId, String button, int clicks, int x, int y);
    boolean controlClick(String title, String text, String controlId);
    boolean winWaitActive(String title);
    boolean winWaitActive(String title, String text, int timeout);
    boolean controlSend(String title, String text, String control, String keys, boolean isRaw);
    String controlGetText(String title, String text, String controlId);
    int mouseGetPosX();
    int mouseGetPosY();
    int winGetPosWidth(String title);
    int winGetPosHeight(String title);
}
