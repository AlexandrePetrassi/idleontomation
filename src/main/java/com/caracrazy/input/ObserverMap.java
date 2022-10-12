package com.caracrazy.input;

import java.util.HashMap;
import java.util.Map;

public class ObserverMap {

    private final Map<Integer, Observer<KeyboardEvent>> delegate = new HashMap<>();

    public void invoke(int key, KeyboardEvent event) {
        Observer<KeyboardEvent> observer = delegate.get(key);
        if (observer != null) observer.invoke(event);
    }

    public void addEventListener(int key, KeyboardEvent event, Runnable callback) {
        delegate.computeIfAbsent(key, it -> new EventObserver<>()).addEventListener(event, callback);
    }

    public void removeEventListener(int key, KeyboardEvent event, Runnable callback) {
        delegate.computeIfAbsent(key, it -> new EventObserver<>()).removeEventListener(event, callback);
    }
}
