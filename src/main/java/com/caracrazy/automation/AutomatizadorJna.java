package com.caracrazy.automation;

import com.sun.jna.platform.win32.*;

import java.util.Arrays;

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
        User32 user32 = User32.INSTANCE;
        WinDef.HWND hwnd = AutomationUtils.findWindowHandlerByRegexpTitle(titulo); //user32.FindWindow(null, titulo);

        if (hwnd != null) {
            user32.SetForegroundWindow(hwnd);
            log("Activated window '" + titulo + '.');
        } else {
            log("failed to activate window '" + titulo + "'.");
        }
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

        if (parentHwnd != null) {
            WinDef.HWND controlHwnd = user32.FindWindowEx(parentHwnd, null, elementID, text);

            if (controlHwnd != null) {
                WinDef.RECT rect = new WinDef.RECT();
                user32.GetWindowRect(controlHwnd, rect);

                return rect.left; // Left coordinate
            }
        }

        return -1; // Window or control not found
    }

    @Override
    public int controlGetPosY(String windowName, String text, String elementID) {
        User32 user32 = User32.INSTANCE;
        WinDef.HWND parentHwnd = AutomationUtils.findWindowHandlerByRegexpTitle(windowName);

        if (parentHwnd != null) {
            WinDef.HWND controlHwnd = user32.FindWindowEx(parentHwnd, null, elementID, text);

            if (controlHwnd != null) {
                WinDef.RECT rect = new WinDef.RECT();
                user32.GetWindowRect(controlHwnd, rect);

                return rect.top; // Left coordinate
            }
        }

        return -1; // Window or control not found
    }

    @Override
    public int controlGetPosWidth(String windowName, String text, String elementID) {
        User32 user32 = User32.INSTANCE;
        WinDef.HWND parentHwnd = AutomationUtils.findWindowHandlerByRegexpTitle(windowName);

        if (parentHwnd != null) {
            WinDef.HWND controlHwnd = user32.FindWindowEx(parentHwnd, null, elementID, text);

            if (controlHwnd != null) {
                WinDef.RECT rect = new WinDef.RECT();
                user32.GetWindowRect(controlHwnd, rect);

                return rect.right - rect.left; // Left coordinate
            }
        }

        return -1; // Window or control not found
    }

    @Override
    public int controlGetPosHeight(String windowName, String text, String elementID) {
        User32 user32 = User32.INSTANCE;
        WinDef.HWND parentHwnd = AutomationUtils.findWindowHandlerByRegexpTitle(windowName);

        if (parentHwnd != null) {
            WinDef.HWND controlHwnd = user32.FindWindowEx(parentHwnd, null, elementID, text);

            if (controlHwnd != null) {
                WinDef.RECT rect = new WinDef.RECT();
                user32.GetWindowRect(controlHwnd, rect);

                return rect.bottom - rect.top; // Left coordinate
            }
        }

        return -1; // Window or control not found
    }

    @Override
    public int processExists(String appName) {
        Kernel32 kernel32 = Kernel32.INSTANCE;
        Tlhelp32.PROCESSENTRY32 pe32 = new Tlhelp32.PROCESSENTRY32();

        WinNT.HANDLE snapshot = kernel32.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPPROCESS, new WinDef.DWORD(0));

        if (kernel32.Process32First(snapshot, pe32)) {
            do {
                String currentProcessName = Arrays.toString(pe32.szExeFile).trim();
                if (currentProcessName.equalsIgnoreCase(appName)) {
                    kernel32.CloseHandle(snapshot);
                    int result = pe32.th32ProcessID.intValue();
                    log("Pid is: " + result);
                    return result;
                }
            } while (kernel32.Process32Next(snapshot, pe32));
        }

        kernel32.CloseHandle(snapshot);
        log("Pid is: 0");
        return 0; // Process not found
    }

    @Override
    public void processClose(String appName) {
        int processId = processExists(appName);

        if (processId != -1) {
            Kernel32 kernel32 = Kernel32.INSTANCE;
            WinNT.HANDLE processHandle = kernel32.OpenProcess(1, false, processId);

            if (processHandle != null) {
                kernel32.TerminateProcess(processHandle, 0);
                kernel32.CloseHandle(processHandle);
            }
        }

        log("Process '" + appName + "' not found or unable to close");
    }

    @Override
    public boolean mouseMove(int x, int y, int speed) {
        User32Ext user32 = User32Ext.INSTANCE;

        if (speed <= 0) {
            speed = 1;
        }

        int steps = Math.abs(speed);

        int startX = user32.GetSystemMetrics(User32Ext.SM_CXSCREEN);
        int startY = user32.GetSystemMetrics(User32Ext.SM_CYSCREEN);

        double deltaX = (double) (x - startX) / steps;
        double deltaY = (double) (y - startY) / steps;

        for (int i = 0; i < steps; i++) {
            int newX = (int) (startX + deltaX * (i + 1));
            int newY = (int) (startY + deltaY * (i + 1));

            user32.SetCursorPos(newX, newY);

            try {
                Thread.sleep(15); // Adjust sleep time if needed
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
        mouseMove(x2, y2, 0);
        mouseUp(button);
    }

    @Override
    public boolean controlClick(String title, String text, String controlId, String button, int clicks, int x, int y) {
        User32Ext user32 = User32Ext.INSTANCE;

        int WM_LBUTTONDOWN = 0x0201;
        int WM_LBUTTONUP = 0x0202;

        WinDef.HWND hWnd = user32.FindWindow(null, title);

        if (hWnd != null) {
            WinDef.HWND controlHWnd = user32.FindWindowEx(hWnd, null, null, controlId);

            if (controlHWnd != null) {
                user32.SetForegroundWindow(controlHWnd);

                int wParam = 0;
                int lParam = (y << 16) | x;
                if ("right".equalsIgnoreCase(button)) {
                    wParam |= 0x0008;
                }
                for (int i = 0; i < clicks; i++) {
                    user32.SendMessage(controlHWnd, WM_LBUTTONDOWN, new WinDef.WPARAM(wParam), new WinDef.LPARAM(lParam));
                    user32.SendMessage(controlHWnd, WM_LBUTTONUP, new WinDef.WPARAM(wParam), new WinDef.LPARAM(lParam));
                }

                return true;
            }
        }

        return false; // Window or control not found
    }

    @Override
    public boolean controlClick(String title, String text, String controlId) {
        User32Ext user32 = User32Ext.INSTANCE;

        int BM_CLICK = 0xF5;

        WinDef.HWND hWnd = user32.FindWindow(null, title);

        if (hWnd != null) {
            WinDef.HWND controlHWnd = user32.FindWindowEx(hWnd, null, null, controlId);

            if (controlHWnd != null) {
                user32.SendMessage(controlHWnd, BM_CLICK, new WinDef.WPARAM(0), new WinDef.LPARAM(0));
                return true;
            }
        }

        return false; // Window or control not found
    }

    @Override
    public boolean winWaitActive(String title) {
        User32Ext user32 = User32Ext.INSTANCE;

        WinDef.HWND hWnd = user32.FindWindow(null, title);

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

    @Override
    public boolean winWaitActive(String title, String text, int timeout) {
        User32 user32 = User32.INSTANCE;

        WinDef.HWND hWnd = user32.FindWindow(null, title);

        if (hWnd == null) {
            log("dont : " + title);
            return false; // Window not found
        }

        log("PRINT");

        user32.SetForegroundWindow(hWnd);

        long endTime = System.currentTimeMillis() + timeout;

        while (!user32.GetForegroundWindow().equals(hWnd)) {
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

        int WM_CHAR = 0x0102;

        WinDef.HWND hWnd = user32.FindWindow(null, title);

        if (hWnd != null) {
            WinDef.HWND controlHWnd = user32.FindWindowEx(hWnd, null, null, control);

            if (controlHWnd != null)
                user32.SetForegroundWindow(controlHWnd);

            byte[] keysBytes = keys.getBytes();
            for (byte keyByte : keysBytes) {
                user32.SendMessage(controlHWnd, WM_CHAR, new WinDef.WPARAM(keyByte), new WinDef.LPARAM(0));
            }

            return true;
        }

        return false; // Window or control not found
    }

    @Override
    public String controlGetText(String title, String text, String controlId) {
        User32Ext user32 = User32Ext.INSTANCE;

        WinDef.HWND hWnd = user32.FindWindow(null, title);

        if (hWnd != null) {
            WinDef.HWND controlHWnd = user32.FindWindowEx(hWnd, null, null, controlId);

            if (controlHWnd != null) {
                int maxLength = 1024; // Adjust the maximum length as needed
                char[] buffer = new char[maxLength];

                int length = user32.GetWindowText(controlHWnd, buffer, maxLength);

                if (length > 0) {
                    return new String(buffer, 0, length);
                }
            }
        }

        return ""; // Window or control not found
    }

    @Override
    public int mouseGetPosX() {
        User32Ext user32 = User32Ext.INSTANCE;

        return user32.GetSystemMetrics(User32.SM_CXSCREEN);
    }

    @Override
    public int mouseGetPosY() {
        User32Ext user32 = User32Ext.INSTANCE;

        return user32.GetSystemMetrics(User32.SM_CYSCREEN);
    }

    @Override
    public int winGetPosWidth(String title) {
        User32Ext user32 = User32Ext.INSTANCE;

        WinDef.HWND hWnd = user32.FindWindow(null, title);

        if (hWnd != null) {
            WinDef.RECT rect = new WinDef.RECT();
            user32.GetWindowRect(hWnd, rect);

            return rect.right - rect.left;
        }

        return 0; // Window not found
    }

    @Override
    public int winGetPosHeight(String title) {
        User32Ext user32 = User32Ext.INSTANCE;

        WinDef.HWND hWnd = user32.FindWindow(null, title);

        if (hWnd != null) {
            WinDef.RECT rect = new WinDef.RECT();
            user32.GetWindowRect(hWnd, rect);

            return rect.bottom - rect.top;
        }

        return 0; // Window not found
    }
}
