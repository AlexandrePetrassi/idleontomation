package com.caracrazy.idleon;

import java.util.List;

public class ChopMiniGamePojo {

    private String appName;
    private String frameReference;
    private String cursorReference;
    private int forceExitKey;
    private Messages messages;
    private List<String> colors;

    public String getAppName() {
        return appName;
    }

    public String getFrameReference() {
        return frameReference;
    }

    public String getCursorReference() {
        return cursorReference;
    }

    public int getForceExitKey() {
        return forceExitKey;
    }

    public Messages getMessages() {
        return messages;
    }

    public List<String> getColors() {
        return colors;
    }

    public static class Messages {

        private String frameNotFound;
        private String cursorNotFound;
        private String forceExit;
        private String onClick;

        public String getFrameNotFound() {
            return frameNotFound;
        }

        public String getCursorNotFound() {
            return cursorNotFound;
        }

        public String getForceExit() {
            return forceExit;
        }

        public String getOnClick() {
            return onClick;
        }
    }
}
