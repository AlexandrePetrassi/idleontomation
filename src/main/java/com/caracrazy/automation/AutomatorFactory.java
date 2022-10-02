package com.caracrazy.automation;

import com.caracrazy.automation.autoit.AutoItXData;
import com.caracrazy.automation.hybrid.HybridAutomatorFactory;

import static com.caracrazy.localization.Messages.messages;

public class AutomatorFactory {

    private AutomatorFactory() {
        throw new IllegalStateException(messages().getErrorUtilityClass());
    }

    public static Automator create(AutoItXData data) {
        return HybridAutomatorFactory.create(data);
    }
}
