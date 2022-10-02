package com.caracrazy;

import com.caracrazy.configuration.ConfigurationData;
import com.caracrazy.configuration.ConfigurationFactory;
import com.caracrazy.idleon.AutoLooter;
import com.caracrazy.idleon.ChopMiniGameInitializer;

public class App {

    private static final String DEFAULT_CONFIG_FILE = "config.yaml";

    public static void main(String[] args) {
        ConfigurationData config = loadConfig(args);
        String strategyName = getStrategyName(args);
        chooseStrategy(config, strategyName);
    }

    private static ConfigurationData loadConfig(String[] args) {
        if (args != null && args.length > 0) {
            return ConfigurationFactory.create(args[0] + ".yaml");
        } else {
            return ConfigurationFactory.create(DEFAULT_CONFIG_FILE);
        }
    }

    private static String getStrategyName(String[] args) {
        if (args != null && args.length > 1) {
            return args[1];
        } else {
            return "";
        }
    }

    private static void chooseStrategy(ConfigurationData config, String strategyName) {
        if (strategyName.equalsIgnoreCase("Chop")) {
            ChopMiniGameInitializer.initialize(config.getAutomator(), config.getChopMiniGame());
        } else if (strategyName.equalsIgnoreCase("Loot")) {
            AutoLooter.INSTANCE.start(config.getAutomator());
        }
    }
}
