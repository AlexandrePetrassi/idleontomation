package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.net.URL;

public class YamlLoader {

    private static final String FAIL_TO_PARSE_YAML =
            "Failed to parse YAML file \"%s\"";

    private static final String FILE_NOT_FOUND =
            "File \"%s\" not found";

    private YamlLoader() {
        throw new IllegalStateException("Utility class");
    }

    private static final ObjectMapper objectMapper = getMapper();

    private static ObjectMapper getMapper() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        return mapper;
    }

    private static URL getUrl(String filename) {
        URL url = YamlLoader.class.getClassLoader().getResource(filename);
        if (url == null) throw new IllegalStateException(String.format(FILE_NOT_FOUND, filename));
        return url;
    }

    public static <T> T load(Class<T> clazz, String filename) {
        try {
            return objectMapper.readValue(getUrl(filename), clazz);
        } catch (IOException e) {
            throw new IllegalStateException(String.format(FAIL_TO_PARSE_YAML, filename), e);
        }
    }
}
