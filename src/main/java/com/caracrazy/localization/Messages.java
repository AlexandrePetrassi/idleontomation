package com.caracrazy.localization;

public class Messages {

    public static Messages messages() {
        return MessagesFactory.getMessages();
    }

    private String errorUtilityClass = "em0";
    private String errorNativeHookUnregister = "em1";
    private String errorNativeHookRegister = "em2";
    private String errorRobotInstantiation = "em3";
    private String errorFrameNotFound = "em4";
    private String errorCursorNotFound = "em5";
    private String errorImageNotSameSize = "em6";
    private String errorImageRead = "em7";
    private String errorImageLoad = "em8";
    private String infoForceExit = "im1";
    private String infoClick = "im2";

    public String getErrorUtilityClass() {
        return errorUtilityClass;
    }

    public String getErrorNativeHookUnregister() {
        return errorNativeHookUnregister;
    }

    public String getErrorNativeHookRegister() {
        return errorNativeHookRegister;
    }

    public String getErrorRobotInstantiation() {
        return errorRobotInstantiation;
    }

    public String getErrorFrameNotFound() {
        return errorFrameNotFound;
    }

    public String getErrorCursorNotFound() {
        return errorCursorNotFound;
    }

    public String getInfoForceExit() {
        return infoForceExit;
    }

    public String getInfoClick() {
        return infoClick;
    }

    public String getErrorImageNotSameSize() {
        return errorImageNotSameSize;
    }

    public String getErrorImageRead() {
        return errorImageRead;
    }

    public String getErrorImageLoad() {
        return errorImageLoad;
    }
}