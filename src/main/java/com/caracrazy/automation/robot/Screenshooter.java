package com.caracrazy.automation.robot;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Screenshooter {

    private Screenshooter() {
        throw new IllegalStateException("Utility Class");
    }

    public static BufferedImage screenshot(Rectangle screenshotArea) {
        return RobotFactory.create().createScreenCapture(screenshotArea);
    }


}
