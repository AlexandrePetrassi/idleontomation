package com.caracrazy.logging;

import static com.caracrazy.localization.Messages.messages;

public final class LoggerFactory {

    private LoggerFactory() {
        throw new IllegalStateException(messages().getErrorUtilityClass());
    }

    public static Logger create() {
        return new ConcreteLogger();
    }
}
