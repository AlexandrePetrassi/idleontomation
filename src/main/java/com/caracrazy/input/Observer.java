package com.caracrazy.input;

public interface Observer<T extends Enum<T>> {

    void invoke(T eventType);

    void addEventListener(T event, Runnable callback);

    void removeEventListener(T event, Runnable callback);
}
