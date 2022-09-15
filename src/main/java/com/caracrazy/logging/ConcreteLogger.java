package com.caracrazy.logging;

class ConcreteLogger implements Logger {

    @Override
    public void info(String text) {
        System.out.println(text);
    }

    @Override
    public void info(String template, Object... args) {
        info(String.format(template, args));
    }
}
