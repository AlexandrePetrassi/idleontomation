package com.caracrazy;

import com.caracrazy.configuration.ConfigurationData;
import com.caracrazy.configuration.ConfigurationFactory;
import com.caracrazy.idleon.ChopMiniGameInitializer;

public class App {

    private static final String DEFAULT_CONFIG_FILE = "config.yaml";

    public static void main(String[] args) {
        ConfigurationData config = loadConfig(args);
        ChopMiniGameInitializer.initialize(config.getAutoItX(), config.getChopMiniGame());
    }

    private static ConfigurationData loadConfig(String[] args) {
        if (args != null && args.length > 0) {
            return ConfigurationFactory.create(args[0] + ".yaml");
        } else {
            return ConfigurationFactory.create(DEFAULT_CONFIG_FILE);
        }
    }
}
