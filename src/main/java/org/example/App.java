package org.example;

import autoitx4java.AutoItX;
import org.example.automation.AutoItXFactory;
import org.example.automation.Keyboard;
import org.example.configuration.ConfigurationFactory;
import org.example.data.ConfigurationPojo;
import org.example.idleon.ChopMiniGame;

public class App {

    public static void main(String[] args) {
        String configFile = ((args.length > 0) ? args[0] : "config") + ".yaml";
        ConfigurationPojo config = ConfigurationFactory.create(configFile);
        AutoItX autoItX = AutoItXFactory.create(config.getAutoItX());
        Keyboard.use(() -> ChopMiniGame.start(autoItX));
    }
}
