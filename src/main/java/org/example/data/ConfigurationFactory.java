package org.example.data;

public class ConfigurationFactory {

    private static final String DEFAULT_CONFIG_NAME = "config.yaml";

    private static ConfigurationPojo cache;

    private ConfigurationFactory() {
        throw new IllegalStateException("Utility Class");
    }

    public static ConfigurationPojo create() {
        if (cache == null) {
            cache = YamlLoader.load(ConfigurationPojo.class, DEFAULT_CONFIG_NAME);
        }
        return cache;
    }
}
