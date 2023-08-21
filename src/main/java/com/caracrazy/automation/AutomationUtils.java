package com.caracrazy.automation;

import com.sun.jna.Pointer;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.caracrazy.localization.Messages.messages;

public class AutomationUtils {
    private AutomationUtils() {
        throw new IllegalStateException(messages().getErrorUtilityClass());
    }

    public static List<WindowInfo> listWindows() {
        User32 user32 = User32.INSTANCE;

        List<WindowInfo> windowList = new ArrayList<>();

        WinUser.WNDENUMPROC proc = (WinDef.HWND hWnd, Pointer data) -> {
            char[] title = new char[512];
            user32.GetWindowText(hWnd, title, title.length);
            String windowTitle = WindowInfo.trimTitle(title);

            if (user32.IsWindowVisible(hWnd) && !windowTitle.isEmpty()) {
                windowList.add(new WindowInfo(hWnd, windowTitle));
            }
            return true;
        };

        user32.EnumWindows(proc, null);

        return windowList;
    }

    private static final Pattern REGEXPTITLE = Pattern.compile("\\[REGEXPTITLE:(.*)]");

    public static String regexpTitle(String autoItPattern) {
        Matcher matcher = REGEXPTITLE.matcher(autoItPattern);
        return matcher.matches() ? matcher.group(1) : autoItPattern;
    }

    public static WindowInfo findWindowByTitle(String regex) {
        return listWindows().stream()
                .filter(it -> it.getTitle().matches(regex))
                .findFirst()
                .orElse(null);
    }

    public static WindowInfo findWindowByExecutable(String name) {
        return listWindows().stream()
                .filter(it -> it.getExecutable().contains(name))
                .findFirst()
                .orElse(null);
    }

    public static WinDef.HWND findWindowHandlerByRegexpTitle(String regexpTitle) {
        WindowInfo window = findWindowByTitle(regexpTitle(regexpTitle));
        return window != null ? window.handler : null;
    }

    public static WinDef.HWND findChildWindowByClassNameAndText(WinDef.HWND parentHWnd, String text, String className) {
        User32 user32 = User32.INSTANCE;

        WinDef.HWND childHWnd = user32.FindWindowEx(parentHWnd, null, null, null);
        while (childHWnd != null) {
            char[] classNameBuffer = new char[256];
            user32.GetClassName(childHWnd, classNameBuffer, 256);
            String currentClassName = new String(classNameBuffer).trim();

            char[] textBuffer = new char[256];
            user32.GetWindowText(childHWnd, textBuffer, 256);
            String currentText = new String(textBuffer).trim();

            boolean classMatches = className == null || className.trim().isEmpty() || currentClassName.equals(className);
            boolean textMatches = text == null || text.trim().isEmpty() || currentText.equals(text);
            if (classMatches) {
                System.out.println("Class matched for " + className);
            } else {
                System.out.println("Class didn't matched for " + className + " with " + currentClassName);
            }

            if (textMatches) {
                System.out.println("Text matched for " + text);
            } else {
                System.out.println("Text didn't matched for " + text + " with " + currentText);
            }

            if (classMatches && textMatches) {
                System.out.println("Found HWND : " + childHWnd);
                return childHWnd;
            }
            System.out.println("(HWND: " + childHWnd + ", CLASS: " + currentClassName + ", TEXT: " + currentText + ")");
            childHWnd = user32.FindWindowEx(parentHWnd, childHWnd, null, null);
        }

        return null; // Child window not found
    }

    public static WinDef.HWND findControl(String title, String text, String className) {
        User32 user32 = User32.INSTANCE;
        WinDef.HWND parentHwnd = AutomationUtils.findWindowHandlerByRegexpTitle(title);

        if (parentHwnd == null) {
            System.out.println("PARENT HWND NOT FOUND FOR TITLE: " + title);
            return null;
        }
        WinDef.HWND controlHwnd = AutomationUtils.findChildWindowByClassNameAndText(parentHwnd, text, className); //user32.GetWindow(parentHwnd, new WinDef.DWORD(GW_CHILD));

        if (controlHwnd == null) {
            System.out.println("CONTROL HWND NOT FOUND FOR : (HWND: " + controlHwnd + ", CLASS: " + className + ", TEXT: " + text + ")");
            return null;
        }
        return controlHwnd;
    }

    public static class WindowInfo {
        private final WinDef.HWND handler;
        private final String title;

        private String executable;

        private Integer pid;

        public WindowInfo(WinDef.HWND handler, String title) {
            this.handler = handler;
            this.title = title;
        }

        public WinDef.HWND getHandler() {
            return this.handler;
        }

        public String getTitle() {
            return title;
        }

        public static String trimTitle(char[] title) {
            int filterCounter = 0;
            for (int i = 0; i < title.length; i++) {
                if (title[i] == 0) {
                    filterCounter += 1;
                    title[i] = i + filterCounter < title.length ? title[i + filterCounter] : ' ';
                }
            }
            char[] filteredTitle = Arrays.copyOfRange(title, 0, title.length - filterCounter);
            String titleAsString = new String(filteredTitle);
            return titleAsString.trim();
        }

        public String getExecutable() {
            if (executable == null)
                executable = WindowUtils.getProcessFilePath(getHandler());
            return executable;
        }

        public int getPid() {
            if (pid == null)
                pid = User32.INSTANCE.GetWindowThreadProcessId(getHandler(),null);
            return pid;
        }

        @Override
        public String toString() {
            return "\nWindowInfo{" +
                    "handler=" + handler +
                    ", title='" + title + '\'' +
                    '}';
        }
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(listWindows().toArray()));
        System.out.println("----------");
        System.out.println(findWindowByTitle("(?i).*Java.*"));
    }
}
