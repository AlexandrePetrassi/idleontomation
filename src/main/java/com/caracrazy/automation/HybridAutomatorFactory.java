package com.caracrazy.automation;

import autoitx4java.AutoItX;
import com.caracrazy.automation.autoit.AutoItXData;
import com.caracrazy.automation.autoit.AutoItXFactory;
import com.caracrazy.automation.autoit.HybridAutomator;
import com.caracrazy.automation.robot.RobotFactory;

import java.awt.*;

import static com.caracrazy.localization.Messages.messages;

public class HybridAutomatorFactory {

    private HybridAutomatorFactory() {
        throw new IllegalStateException(messages().getErrorUtilityClass());
    }

    public static Automator create(AutoItXData config) {
        Robot robot = RobotFactory.create();
        AutoItX autoItX = AutoItXFactory.INSTANCE.create(config);
        return new HybridAutomator(autoItX, robot);
    }
}
