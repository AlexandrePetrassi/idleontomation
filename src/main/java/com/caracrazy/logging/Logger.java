package com.caracrazy.logging;

public interface Logger {
    void info(String text);
    void info(String template, Object... args);
}
