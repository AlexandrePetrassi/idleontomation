package com.caracrazy.input;

public interface Keyboard {

    boolean isKeyPressed(int keyCode);

    void addKeyEventListener(int keyCode, KeyboardEvent event, Runnable callback);

    void removeKeyEventListener(int keyCode, KeyboardEvent event, Runnable callback);
}
