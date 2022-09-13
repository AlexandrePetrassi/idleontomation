package com.caracrazy.automation.jnativehook;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import java.util.HashMap;
import java.util.Map;

import static com.caracrazy.localization.Messages.messages;

public class Keyboard implements NativeKeyListener {

    private static final Map<Integer, Boolean> pressedKeys = new HashMap<>();

    private Keyboard() {
        throw new IllegalStateException(messages().getErrorUtilityClass());
    }

    public static boolean isKeyPressed(int keyCode) {
        return pressedKeys.getOrDefault(keyCode, false);
    }

    public static void use(Runnable runnable) {
        pressedKeys.clear();
        NativeKeyListener listener = new EventListener();
        try {
            start(listener);
            runnable.run();
        } finally {
            stop(listener);
        }
    }

    private static void start(NativeKeyListener listener) {
        registerHook();
        startListening(listener);
    }

    private static void stop(NativeKeyListener listener) {
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

    private static class EventListener implements NativeKeyListener {

        @Override
        public void nativeKeyPressed(NativeKeyEvent e) {
            pressedKeys.put(e.getKeyCode(), true);
        }

        @Override
        public void nativeKeyReleased(NativeKeyEvent e) {
            pressedKeys.put(e.getKeyCode(), false);
        }

        @Override
        public void nativeKeyTyped(NativeKeyEvent e) {
            // Nothing
        }
    }
}