package com.caracrazy.idleon;

import com.caracrazy.automation.Automator;
import com.caracrazy.automation.autoit.AutoItXData;
import com.caracrazy.automation.hybrid.HybridAutomatorFactory;
import com.caracrazy.input.KeyboardListener;

import static com.caracrazy.localization.Messages.messages;

public class ChopMiniGameInitializer {

    private ChopMiniGameInitializer() {
        throw new IllegalStateException(messages().getErrorUtilityClass());
    }

    public static void initialize(AutoItXData autoItXData, ChopMiniGameData chopMiniGameData) {
        Automator autoItX = HybridAutomatorFactory.create(autoItXData);
        KeyboardListener.use(keyboard -> ChopMiniGame.start(autoItX, keyboard, chopMiniGameData));
    }
}
