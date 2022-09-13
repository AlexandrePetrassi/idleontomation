package com.caracrazy.configuration;

import com.caracrazy.yaml.YamlLoader;

import static com.caracrazy.localization.Messages.messages;

public class ConfigurationFactory {

    private static ConfigurationData cache;

    private ConfigurationFactory() {
        throw new IllegalStateException(messages().getErrorUtilityClass());
    }

    public static ConfigurationData create(String filename) {
        if (cache == null) {
            cache = YamlLoader.load(ConfigurationData.class, filename);
        }
        return cache;
    }
}
