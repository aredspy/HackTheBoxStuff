package com.facebook.react.bridge;
/* loaded from: classes.dex */
public class SoftAssertions {
    public static void assertUnreachable(String message) {
        ReactSoftExceptionLogger.logSoftException("SoftAssertions", new AssertionException(message));
    }

    public static void assertCondition(boolean condition, String message) {
        if (!condition) {
            ReactSoftExceptionLogger.logSoftException("SoftAssertions", new AssertionException(message));
        }
    }

    public static <T> T assertNotNull(T instance) {
        if (instance == null) {
            ReactSoftExceptionLogger.logSoftException("SoftAssertions", new AssertionException("Expected object to not be null!"));
        }
        return instance;
    }
}
