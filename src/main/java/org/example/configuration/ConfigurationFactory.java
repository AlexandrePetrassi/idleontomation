package org.example.configuration;

import org.example.yaml.YamlLoader;

public class ConfigurationFactory {

    private static ConfigurationPojo cache;

    private ConfigurationFactory() {
        throw new IllegalStateException("Utility Class");
    }

    public static ConfigurationPojo create(String filename) {
        if (cache == null) {
            cache = YamlLoader.load(ConfigurationPojo.class, filename);
        }
        return cache;
    }
}
