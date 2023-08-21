package com.caracrazy.automation;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;

import java.awt.*;

public class AutomatizadorJna implements Automatizador {

    public static class NotImplementedException extends RuntimeException {
        public NotImplementedException() {
            super("Not Implemented");
        }
    }

    public static void log(String message) {
        System.out.println(message);
    }

    @Override
    public String winGetHandle(String titulo) {
        WinDef.HWND hwnd = AutomationUtils.findWindowHandlerByRegexpTitle(titulo);

        if (hwnd != null) {
            return hwnd.toString();
        } else {
            return null;
        }
    }

    @Override
    public void winActivate(String titulo) {
        WinDef.HWND hWnd = AutomationUtils.findWindowHandlerByRegexpTitle(titulo);
        if (hWnd == null) return;
        User32 user32 = User32.INSTANCE;
        user32.ShowWindow(hWnd, WinUser.SW_SHOW);
        user32.SetForegroundWindow(hWnd);
    }

    @Override
    public int winGetPosX(String windowName) {
        User32 user32 = User32.INSTANCE;
        WinDef.HWND hwnd = AutomationUtils.findWindowHandlerByRegexpTitle(windowName);

        if (hwnd != null) {
            WinDef.RECT rect = new WinDef.RECT();
            user32.GetWindowRect(hwnd, rect);

            return rect.left; // Left coordinate
        } else {
            return -1; // Window not found
        }
    }

    @Override
    public int winGetPosY(String windowName) {
        User32 user32 = User32.INSTANCE;
        WinDef.HWND hwnd = AutomationUtils.findWindowHandlerByRegexpTitle(windowName);

        if (hwnd != null) {
            WinDef.RECT rect = new WinDef.RECT();
            user32.GetWindowRect(hwnd, rect);

            return rect.top; // Left coordinate
        } else {
            return -1; // Window not found
        }
    }

    @Override
    public int controlGetPosX(String windowName, String text, String elementID) {
        User32 user32 = User32.INSTANCE;
        WinDef.HWND parentHwnd = AutomationUtils.findWindowHandlerByRegexpTitle(windowName);

        if (parentHwnd == null) {
            log("PARENT HWND NOT FOUND");
            return -1; // Window or control not found
        }
        WinDef.HWND controlHwnd = AutomationUtils.findChildWindowByClassNameAndText(parentHwnd, text, elementID); //user32.GetWindow(parentHwnd, new WinDef.DWORD(GW_CHILD));
        if (controlHwnd == null) {
            log("CONTROL HWND NOT FOUND");
            return -1; // Window or control not found
        }

        WinDef.RECT rect = new WinDef.RECT();
        user32.GetWindowRect(parentHwnd, rect);
        int anchor = rect.left;
        user32.GetWindowRect(controlHwnd, rect);

        return rect.left - anchor - 8; // Left coordinate

    }

    @Override
    public int controlGetPosY(String windowName, String text, String elementID) {
        User32 user32 = User32.INSTANCE;
        WinDef.HWND parentHwnd = AutomationUtils.findWindowHandlerByRegexpTitle(windowName);

        if (parentHwnd == null) {
            log("PARENT HWND NOT FOUND");
            return -1; // Window or control not found
        }
        WinDef.HWND controlHwnd = AutomationUtils.findChildWindowByClassNameAndText(parentHwnd, text, elementID); //user32.GetWindow(parentHwnd, new WinDef.DWORD(GW_CHILD));

        if (controlHwnd == null) {
            log("CONTROL HWND NOT FOUND");
            return -1; // Window or control not found
        }
        WinDef.RECT rect = new WinDef.RECT();
        user32.GetWindowRect(parentHwnd, rect);
        int anchor = rect.top;
        user32.GetWindowRect(controlHwnd, rect);

        return rect.top - anchor - 51; // Left coordinate

    }

    @Override
    public int controlGetPosWidth(String windowName, String text, String elementID) {
        User32 user32 = User32.INSTANCE;

        WinDef.HWND controlHwnd =  AutomationUtils.findControl(windowName, text, elementID);

        WinDef.RECT rect = new WinDef.RECT();
        user32.GetWindowRect(controlHwnd, rect);

        return rect.right - rect.left;
    }

    @Override
    public int controlGetPosHeight(String windowName, String text, String elementID) {
        User32 user32 = User32.INSTANCE;

        WinDef.HWND controlHwnd =  AutomationUtils.findControl(windowName, text, elementID);

        WinDef.RECT rect = new WinDef.RECT();
        user32.GetWindowRect(controlHwnd, rect);

        return rect.bottom - rect.top;
    }

    @Override
    public int processExists(String appName) {
        AutomationUtils.WindowInfo windowInfo = AutomationUtils.findWindowByExecutable(appName);
        return windowInfo == null ? 0 : windowInfo.getPid();
    }

    @Override
    public void processClose(String appName) {
        CmdController.INSTANCE.fecharInstancia(appName, "");
    }

    @Override
    public boolean mouseMove(int x, int y, int speed) {
        User32Ext user32 = User32Ext.INSTANCE;

        if (speed == 0) {
            user32.SetCursorPos(x, y);
            return true;
        }
        if (speed < 0) {
            speed = 1;
        }

        long sleepTime = 15L;

        int steps = Math.abs(speed);

        Point start = MouseInfo.getPointerInfo().getLocation();

        double deltaX = (double) (x - start.x) / steps;
        double deltaY = (double) (y - start.y) / steps;

        for (int i = 0; i < steps; i++) {
            int newX = (int) (start.x + deltaX * (i + 1));
            int newY = (int) (start.y + deltaY * (i + 1));

            user32.SetCursorPos(newX, newY);

            try {
                Thread.sleep(sleepTime); // Adjust sleep time if needed
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Set the final cursor position to the target coordinates
        user32.SetCursorPos(x, y);
        return true;
    }

    @Override
    public void mouseDown(String button) {
        User32Ext user32 = User32Ext.INSTANCE;

        int MOUSEEVENTF_LEFTDOWN = 0x0002;
        int MOUSEEVENTF_RIGHTDOWN = 0x0008;
        int MOUSEEVENTF_MIDDLEDOWN = 0x0020;

        int dwFlags = 0;

        switch (button.toLowerCase()) {
            case "left":
                dwFlags = MOUSEEVENTF_LEFTDOWN;
                break;
            case "right":
                dwFlags = MOUSEEVENTF_RIGHTDOWN;
                break;
            case "middle":
                dwFlags = MOUSEEVENTF_MIDDLEDOWN;
                break;
            default:
                throw new IllegalArgumentException("Invalid mouse button specified: " + button);
        }

        user32.mouse_event(dwFlags, 0, 0, 0, 0);
    }

    @Override
    public void mouseUp(String button) {
        User32Ext user32 = User32Ext.INSTANCE;

        int MOUSEEVENTF_LEFTUP = 0x0004;
        int MOUSEEVENTF_RIGHTUP = 0x0010;
        int MOUSEEVENTF_MIDDLEUP = 0x0040;

        int dwFlags = 0;

        switch (button.toLowerCase()) {
            case "left":
                dwFlags = MOUSEEVENTF_LEFTUP;
                break;
            case "right":
                dwFlags = MOUSEEVENTF_RIGHTUP;
                break;
            case "middle":
                dwFlags = MOUSEEVENTF_MIDDLEUP;
                break;
            default:
                throw new IllegalArgumentException("Invalid mouse button specified: " + button);
        }

        user32.mouse_event(dwFlags, 0, 0, 0, 0);
    }

    @Override
    public void mouseClickDrag(String button, int x, int y, int x2, int y2) {
        mouseMove(x, y, 0);
        mouseDown(button);
        mouseMove(x2, y2, 10);
        mouseUp(button);
    }

    @Override
    public boolean controlClick(String title, String text, String controlId, String button, int clicks, int x, int y) {
        User32Ext user32 = User32Ext.INSTANCE;

        int WM_LBUTTONDOWN = 0x0201;
        int WM_LBUTTONUP = 0x0202;

        WinDef.HWND controlHWnd = AutomationUtils.findControl(title, text, controlId);

        if (controlHWnd == null) {
            return false; // Window or control not found
        }
        user32.SetForegroundWindow(controlHWnd);

        int wParam = 0;
        int lParam = (y << 16) | x;
        if ("right".equalsIgnoreCase(button)) wParam |=  0x0008;
        for (int i = 0; i < clicks; i++) {
            user32.SendMessage(controlHWnd, WM_LBUTTONDOWN, new WinDef.WPARAM(wParam), new WinDef.LPARAM(lParam));
            user32.SendMessage(controlHWnd, WM_LBUTTONUP, new WinDef.WPARAM(wParam), new WinDef.LPARAM(lParam));
        }

        return true;

    }

    @Override
    public boolean controlClick(String title, String text, String controlId) {
        User32Ext user32 = User32Ext.INSTANCE;
        int BM_CLICK = 0xF5;

        WinDef.HWND controlHWnd = AutomationUtils.findControl(title, text,controlId);
        if (controlHWnd == null) return false;
        user32.SendMessage(controlHWnd, BM_CLICK, new WinDef.WPARAM(0), new WinDef.LPARAM(0));
        return true;

    }

    @Override
    public boolean winWaitActive(String title) {
        User32Ext user32 = User32Ext.INSTANCE;

        WinDef.HWND hWnd = AutomationUtils.findWindowHandlerByRegexpTitle(title);

        if (hWnd != null) {
            user32.SetForegroundWindow(hWnd);

            long timeout = System.currentTimeMillis() + 5000; // Adjust the timeout value (in milliseconds) as needed

            while (!user32.GetForegroundWindow().equals(hWnd)) {
                if (System.currentTimeMillis() > timeout) {
                    return false; // Timeout reached
                }

                try {
                    Thread.sleep(100); // Adjust sleep time if needed
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return true; // Window became active
        }

        return false; // Window not found
    }

    public static boolean checkForeground(WinDef.HWND hwnd) {
        WinDef.HWND foreground = User32.INSTANCE.GetForegroundWindow();
        return foreground != null && foreground.equals(hwnd);
    }

    @Override
    public boolean winWaitActive(String title, String text, int timeout) {
        WinDef.HWND hWnd = AutomationUtils.findWindowHandlerByRegexpTitle(title);

        if (hWnd == null) {
            return false; // Window not found
        }

        long endTime = System.currentTimeMillis() + timeout;

        while (!checkForeground(hWnd)) {
            if (System.currentTimeMillis() > endTime) {
                return false; // Timeout reached
            }

            try {
                Thread.sleep(100); // Adjust sleep time if needed
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return true; // Window became active

    }

    @Override
    public boolean controlSend(String title, String text, String control, String keys, boolean isRaw) {
        User32Ext user32 = User32Ext.INSTANCE;

        WinDef.HWND controlHWnd = AutomationUtils.findControl(title, text, control);

        if (controlHWnd == null) return false; // Window or control not found

        user32.SetForegroundWindow(controlHWnd);

        char[] keysBuffer = keys.toCharArray();
        for (char key : keysBuffer) {
            user32.SendMessage(controlHWnd, WinUser.WM_CHAR, new WinDef.WPARAM(key), new WinDef.LPARAM(1));
        }

        return true;
    }

    @Override
    public String controlGetText(String title, String text, String controlId) {
        User32Ext user32 = User32Ext.INSTANCE;
        int WM_GETTEXT = 0x000D;
        int WM_GETTEXTLENGTH = 0x000E;

        WinDef.HWND controlHWnd = AutomationUtils.findControl(title, text, controlId);

        if (controlHWnd == null) return ""; // Window or control not found

        int textLength = user32.SendMessage(controlHWnd, WM_GETTEXTLENGTH, new WinDef.WPARAM(0), new WinDef.LPARAM(0)).intValue();

        if (textLength <= 0) return "";

        char[] textBuffer = new char[textLength];
        user32.SendMessage(controlHWnd, WM_GETTEXT, new WinDef.WPARAM(textLength + 1), textBuffer);

        return new String(textBuffer);
    }

    @Override
    public int mouseGetPosX() {
        return MouseInfo.getPointerInfo().getLocation().x;
    }

    @Override
    public int mouseGetPosY() {
        return MouseInfo.getPointerInfo().getLocation().y;
    }

    @Override
    public int winGetPosWidth(String title) {
        User32Ext user32 = User32Ext.INSTANCE;

        WinDef.HWND hWnd = AutomationUtils.findWindowHandlerByRegexpTitle(title); // user32.FindWindow(null, title);

        if (hWnd == null) return 0; // Window not found

        WinDef.RECT rect = new WinDef.RECT();
        user32.GetWindowRect(hWnd, rect);

        return rect.right - rect.left;

    }

    @Override
    public int winGetPosHeight(String title) {
        User32Ext user32 = User32Ext.INSTANCE;

        WinDef.HWND hWnd = AutomationUtils.findWindowHandlerByRegexpTitle(title); // user32.FindWindow(null, title);

        if (hWnd == null) return 0; // Window not found

        WinDef.RECT rect = new WinDef.RECT();
        user32.GetWindowRect(hWnd, rect);

        return rect.bottom - rect.top;

    }
}
