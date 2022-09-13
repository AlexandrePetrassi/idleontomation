package com.caracrazy.configuration;

import com.caracrazy.yaml.YamlLoader;

public class ConfigurationFactory {

    private static ConfigurationData cache;

    private ConfigurationFactory() {
        throw new IllegalStateException("Utility Class");
    }

    public static ConfigurationData create(String filename) {
        if (cache == null) {
            cache = YamlLoader.load(ConfigurationData.class, filename);
        }
        return cache;
    }
}
