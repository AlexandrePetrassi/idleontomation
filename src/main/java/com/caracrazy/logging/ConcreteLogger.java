package com.caracrazy.logging;

final class ConcreteLogger implements Logger {

    private final java.util.logging.Logger delegate;

    private static java.util.logging.Logger getLogger(String className, String bundle) {
        if(className == null || className.equals("")) {
            if(bundle == null || bundle.equals("")) {
                return java.util.logging.Logger.getAnonymousLogger();
            } else {
                return java.util.logging.Logger.getAnonymousLogger(bundle);
            }
        } else {
            if(bundle == null || bundle.equals("")) {
                return java.util.logging.Logger.getLogger(className);
            } else {
                return java.util.logging.Logger.getLogger(className, bundle);
            }
        }
    }

    public ConcreteLogger(String className, String additionalInfo) {
        this.delegate = getLogger(className, additionalInfo);
    }

    public ConcreteLogger(String className) {
        this(className, "");
    }

    public ConcreteLogger() {
        this("", "");
    }

    @Override
    public void info(String text) {
        delegate.info(text);
    }

    @Override
    public void info(String template, Object... args) {
        info(String.format(template, args));
    }
}
