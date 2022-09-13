package com.caracrazy.yaml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.net.URL;

import static com.caracrazy.localization.Messages.messages;

public class YamlLoader {

    public static final String TEMPLATE_ERROR = "%s: %s";

    private YamlLoader() {
        throw new IllegalStateException(messages().getErrorUtilityClass());
    }

    private static final ObjectMapper objectMapper = getMapper();

    private static ObjectMapper getMapper() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        return mapper;
    }

    private static URL getUrl(String filename) {
        URL url = YamlLoader.class.getClassLoader().getResource(filename);
        if (url == null) {
            String message = String.format(TEMPLATE_ERROR, messages().getErrorImageLoad(), filename);
            throw new IllegalStateException(message);
        }
        return url;
    }

    public static <T> T load(Class<T> clazz, String filename) {
        try {
            return objectMapper.readValue(getUrl(filename), clazz);
        } catch (IOException e) {
            String message = String.format(TEMPLATE_ERROR, messages().getErrorImageRead(), filename);
            throw new IllegalStateException(message, e);
        }
    }
}
