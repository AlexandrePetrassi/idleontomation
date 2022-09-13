package com.caracrazy;

import autoitx4java.AutoItX;
import com.caracrazy.automation.autoit.AutoItXFactory;
import com.caracrazy.automation.jnativehook.Keyboard;
import com.caracrazy.configuration.ConfigurationData;
import com.caracrazy.configuration.ConfigurationFactory;
import com.caracrazy.idleon.ChopMiniGame;

public class App {

    public static void main(String[] args) {
        String configFile = ((args.length > 0) ? args[0] : "config") + ".yaml";
        ConfigurationData config = ConfigurationFactory.create(configFile);
        AutoItX autoItX = AutoItXFactory.create(config.getAutoItX());
        Keyboard.use(() -> ChopMiniGame.start(autoItX, config.getChopMiniGame()));
    }
}
