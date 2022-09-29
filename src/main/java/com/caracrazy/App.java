package com.caracrazy;

import com.caracrazy.automation.Automator;
import com.caracrazy.automation.autoit.AutoItXFactory;
import com.caracrazy.automation.jnativehook.KeyboardListener;
import com.caracrazy.configuration.ConfigurationData;
import com.caracrazy.configuration.ConfigurationFactory;
import com.caracrazy.idleon.ChopMiniGame;

public class App {

    public static void main(String[] args) {
        String configFile = ((args.length > 0) ? args[0] : "config") + ".yaml";
        ConfigurationData config = ConfigurationFactory.create(configFile);
        Automator autoItX = AutoItXFactory.INSTANCE.create(config.getAutoItX());
        KeyboardListener.use(keyboard -> ChopMiniGame.start(autoItX, keyboard, config.getChopMiniGame()));
    }
}
