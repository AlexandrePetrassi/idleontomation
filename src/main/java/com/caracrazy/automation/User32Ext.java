package com.caracrazy.automation;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.win32.W32APIOptions;

public interface User32Ext extends User32 {
    User32Ext INSTANCE = Native.load("user32", User32Ext.class, W32APIOptions.DEFAULT_OPTIONS);

    boolean SetCursorPos(int X, int Y);
    void mouse_event(int dwFlags, int dx, int dy, int cButtons, int dwExtraInfo);
}
