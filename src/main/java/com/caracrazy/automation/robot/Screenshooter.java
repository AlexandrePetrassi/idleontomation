package com.caracrazy.automation.robot;

import java.awt.*;
import java.awt.image.BufferedImage;

import static com.caracrazy.localization.Messages.messages;

public class Screenshooter {

    private Screenshooter() {
        throw new IllegalStateException(messages().getErrorUtilityClass());
    }

    public static BufferedImage screenshot(Rectangle screenshotArea) {
        return RobotFactory.create().createScreenCapture(screenshotArea);
    }
}
