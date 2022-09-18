package com.caracrazy.logging;

import static com.caracrazy.localization.Messages.messages;

public final class LoggerFactory {

    private LoggerFactory() {
        throw new IllegalStateException(messages().getErrorUtilityClass());
    }

    public static Logger create() {
        return new ConcreteLogger();
    }

    public static Logger create(String className) {
        return new ConcreteLogger(className);
    }

    public static Logger create(String className, String bundle) {
        return new ConcreteLogger(className, bundle);
    }

    public static Logger createAnonymous(String bundle) {
        return new ConcreteLogger("", bundle);
    }
}
