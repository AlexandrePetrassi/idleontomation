package com.caracrazy.input;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class EventObserver<T extends Enum<T>> implements Observer<T> {

    private final Collection<Runnable> empty = new ArrayList<>();

    private final Map<T, Collection<Runnable>> callbacks = new HashMap<>();

    @Override
    public void invoke(T eventType) {
        callbacks.getOrDefault(eventType, empty).forEach(Runnable::run);
    }

    @Override
    public void addEventListener(T event, Runnable callback) {
        callbacks.computeIfAbsent(event, it -> new ArrayList<>()).add(callback);
    }

    @Override
    public void removeEventListener(T event, Runnable callback) {
        callbacks.computeIfAbsent(event, it -> new ArrayList<>()).remove(callback);
    }
}
