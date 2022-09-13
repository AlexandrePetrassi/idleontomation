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
            throw new IllegalStateException(messages().getErrorNativeHookUnregister(), e);
        }
    }

    private static void startListening() {
        GlobalScreen.addNativeKeyListener(EventListener.INSTANCE);
    }

    private static void registerHook() {
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            throw new IllegalStateException(messages().getErrorNativeHookRegister(), e);
        }
    }

    private static class EventListener implements NativeKeyListener {

        public static final EventListener INSTANCE = new EventListener();

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