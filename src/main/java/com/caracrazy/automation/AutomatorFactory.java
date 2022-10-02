package com.caracrazy.automation;

import com.caracrazy.automation.hybrid.HybridAutomatorFactory;

import static com.caracrazy.localization.Messages.messages;

public class AutomatorFactory {

    private AutomatorFactory() {
        throw new IllegalStateException(messages().getErrorUtilityClass());
    }

    public static Automator create(AutomatorData data) {
        if (data.getAutoItX() != null) {
            return HybridAutomatorFactory.create(data.getAutoItX());
        } else {
            throw new IllegalArgumentException(messages().getErrorNoSuitableImplementationFound());
        }
    }
}
