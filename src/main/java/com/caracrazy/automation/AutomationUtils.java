package com.caracrazy.automation;

import com.sun.jna.Pointer;
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

    public static WindowInfo findWindow(String regex) {
        return listWindows().stream()
                .filter(it -> it.getTitle().matches(regex))
                .findFirst()
                .orElse(null);
    }

    public static WinDef.HWND findWindowHandlerByRegexpTitle(String regexpTitle) {
        WindowInfo window = findWindow(regexpTitle(regexpTitle));
        return window != null ? window.handler : null;
    }

    public static class WindowInfo {
        private final WinDef.HWND handler;
        private final String title;

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
        System.out.println(findWindow("(?i).*Java.*"));
    }
}
