package com.caracrazy;

import com.caracrazy.configuration.ConfigurationData;
import com.caracrazy.configuration.ConfigurationFactory;
import com.caracrazy.idleon.ChopMiniGameInitializer;

public class App {

    public static void main(String[] args) {
        ConfigurationData config = loadConfig(args);
        ChopMiniGameInitializer.initialize(config.getAutoItX(), config.getChopMiniGame());
    }

    private static ConfigurationData loadConfig(String[] args) {
        String configFile = ((args.length > 0) ? args[0] : "config") + ".yaml";
        return ConfigurationFactory.create(configFile);
    }
}
