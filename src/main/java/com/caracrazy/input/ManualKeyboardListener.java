package com.caracrazy.input;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import java.util.HashMap;
import java.util.Map;

import static com.caracrazy.localization.Messages.messages;

public class ManualKeyboardListener {

    private ManualKeyboardListener() {
        throw new IllegalStateException(messages().getErrorUtilityClass());
    }

    public static EventListener create() {
        return new EventListener();
    }

    public static void start(EventListener listener) {
        registerHook();
        startListening(listener);
    }

    public static void stop(EventListener listener) {
        stopListening(listener);
        unregisterHook();
    }

    private static void stopListening(NativeKeyListener listener) {
        GlobalScreen.removeNativeKeyListener(listener);
    }

    private static void unregisterHook() {
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException e) {
            throw new IllegalStateException(messages().getErrorNativeHookUnregister(), e);
        }
    }

    private static void startListening(NativeKeyListener listener) {
        GlobalScreen.addNativeKeyListener(listener);
    }

    private static void registerHook() {
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            throw new IllegalStateException(messages().getErrorNativeHookRegister(), e);
        }
    }

    public static class EventListener implements NativeKeyListener, Keyboard {

        private final Map<Integer, Boolean> pressedKeys = new HashMap<>();

        private final ObserverMap observers = new ObserverMap();

        @Override
        public void nativeKeyPressed(NativeKeyEvent e) {
            boolean oldValue = pressedKeys.getOrDefault(e.getKeyCode(), false);
            pressedKeys.put(e.getKeyCode(), true);
            observers.invoke(e.getKeyCode(), oldValue ? KeyboardEvent.HOLD : KeyboardEvent.DOWN);
        }

        @Override
        public void nativeKeyReleased(NativeKeyEvent e) {
            pressedKeys.put(e.getKeyCode(), false);
            observers.invoke(e.getKeyCode(), KeyboardEvent.RELEASE);
        }

        @Override
        public void nativeKeyTyped(NativeKeyEvent e) {
            // Nothing
        }

        @Override
        public boolean isKeyPressed(int keyCode) {
            return pressedKeys.getOrDefault(keyCode, false);
        }

        @Override
        public void addKeyEventListener(int keyCode, KeyboardEvent event, Runnable callback) {
            observers.addEventListener(keyCode, event, callback);
        }

        @Override
        public void removeKeyEventListener(int keyCode, KeyboardEvent event, Runnable callback) {
            observers.removeEventListener(keyCode, event, callback);
        }
    }
}
