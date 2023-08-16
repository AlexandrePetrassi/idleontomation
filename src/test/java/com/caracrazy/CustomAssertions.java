package com.caracrazy;

import org.junit.AssumptionViolatedException;

import static com.caracrazy.localization.Messages.messages;

public class CustomAssertions {

    private CustomAssertions() {
        throw new IllegalStateException(messages().getErrorUtilityClass());
    }

    public static void assertAll(Runnable... assertions) {
        Level level = Level.SUCCESS;
        StringBuilder sb = new StringBuilder();
        for (Runnable it : assertions) {
            try {
                it.run();
            } catch (Throwable throwable) {
                level = level.update(throwable);
                sb
                        .append("\n    ")
                        .append("(")
                        .append(throwable.getClass().getSimpleName())
                        .append("): ")
                        .append(throwable.getLocalizedMessage());
            }
        }
        level.throwBasedOnLevel(sb.toString());
    }

    enum Level {
        SUCCESS, SKIP, ASSERTION_ERROR, EXECUTION_ERROR;

        Level update(Throwable throwable) {
            switch (this) {
                case SUCCESS:
                case SKIP:
                    if (throwable instanceof AssumptionViolatedException) return Level.SKIP;
                    if (throwable instanceof AssertionError) return Level.ASSERTION_ERROR;
                    return Level.EXECUTION_ERROR;
                case ASSERTION_ERROR:
                    if (throwable instanceof AssumptionViolatedException) return Level.ASSERTION_ERROR;
                    if (throwable instanceof AssertionError) return Level.ASSERTION_ERROR;
                    return Level.EXECUTION_ERROR;
                case EXECUTION_ERROR:
                    return Level.EXECUTION_ERROR;
            }
            throw new IllegalStateException("Error during soft assertions");
        }

        void throwBasedOnLevel(String message) {
            switch (this) {
                case SUCCESS: return;
                case SKIP: throw new AssumptionViolatedException("Skipping test because assumptions have failed:\n" + message);
                case ASSERTION_ERROR: throw new AssertionError("Some assertions have failed:\n" + message);
                case EXECUTION_ERROR: throw new IllegalStateException("Errors occurred during test execution:\n" + message);
            }
        }
    }
}
