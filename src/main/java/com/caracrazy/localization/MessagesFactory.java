package com.caracrazy.localization;

import com.caracrazy.yaml.YamlLoader;

import java.util.HashMap;
import java.util.Map;

import static com.caracrazy.localization.Messages.messages;

public class MessagesFactory {

    private MessagesFactory() {
        throw new IllegalStateException(messages().getErrorUtilityClass());
    }

    private static final String LOCALE_KEY = "user.language";
    private static final String MESSAGES_FILE_PATTERN = "messages-%s.yaml";

    private static final Map<String, Messages> cache = new HashMap<>();

    public static Messages getMessages() {
        return cache.computeIfAbsent(System.getProperty(LOCALE_KEY), MessagesFactory::load);
    }

    private static Messages load(String locale) {
        try {
            String filename = String.format(MESSAGES_FILE_PATTERN, locale);
            return YamlLoader.load(Messages.class, filename);
        } catch (IllegalStateException e) {
            return new Messages();
        }
    }
}
