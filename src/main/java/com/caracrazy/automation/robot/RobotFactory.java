package com.caracrazy.automation.robot;

import java.awt.*;

import static com.caracrazy.localization.Messages.messages;

public class RobotFactory {

    public static final String ROBOT_INSTANTIATION_ERROR =
            "Problems while instantiate robot";

    private static Robot cachedRobot;

    private RobotFactory() {
        throw new IllegalStateException(messages().getErrorUtilityClass());
    }

    private static Robot createRobot() {
        try {
            return new Robot();
        } catch (AWTException e) {
            throw new IllegalStateException(ROBOT_INSTANTIATION_ERROR, e);
        }
    }

    public static Robot create() {
        if (cachedRobot == null) {
            cachedRobot = createRobot();
        }
        return cachedRobot;
    }
}
