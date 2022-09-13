package com.caracrazy.automation.janativehook;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import java.util.HashMap;
import java.util.Map;

public class Keyboard implements NativeKeyListener {

    public static final String UNREGISTERING_ERROR =
            "There was a problem unregistering the native hook.";

    public static final String REGISTERING_ERROR =
            "There was a problem registering the native hook.";

    private static final Map<Integer, Boolean> pressedKeys = new HashMap<>();

    private Keyboard() {
        throw new IllegalStateException("Utility Class");
    }

    public static boolean isKeyPressed(int keyCode) {
        return pressedKeys.getOrDefault(keyCode, false);
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

        public static final EventListener INSTANCE = new EventListener();
    }

    public static void use(Runnable runnable) {
        try {
            start();
            runnable.run();
        } finally {
            stop();
        }
    }

    private static void start() {
        registerHook();
        startListening();
    }

    private static void stop() {
        stopListening();
        unregisterHook();
    }

    private static void stopListening() {
        GlobalScreen.removeNativeKeyListener(EventListener.INSTANCE);
    }

    private static void unregisterHook() {
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException e) {
            throw new IllegalStateException(UNREGISTERING_ERROR, e);
        }
    }

    private static void startListening() {
        GlobalScreen.addNativeKeyListener(EventListener.INSTANCE);
    }

    private static void registerHook() {
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            throw new IllegalStateException(REGISTERING_ERROR, e);
        }
    }
}