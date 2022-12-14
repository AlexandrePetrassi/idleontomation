package com.caracrazy.idleon;

import com.caracrazy.automation.Automator;
import com.caracrazy.automation.autoit.AutoItXData;
import com.caracrazy.automation.autoit.AutoItXFactory;
import com.caracrazy.automation.jnativehook.KeyboardListener;

import static com.caracrazy.localization.Messages.messages;

public class ChopMiniGameInitializer {

    private ChopMiniGameInitializer() {
        throw new IllegalStateException(messages().getErrorUtilityClass());
    }

    public static void initialize(AutoItXData autoItXData, ChopMiniGameData chopMiniGameData) {
        Automator autoItX = AutoItXFactory.INSTANCE.create(autoItXData);
        KeyboardListener.use(keyboard -> ChopMiniGame.start(autoItX, keyboard, chopMiniGameData));
    }
}
