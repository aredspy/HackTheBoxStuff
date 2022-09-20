package com.facebook.react.bridge;

import com.facebook.common.logging.FLog;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
/* loaded from: classes.dex */
public class ReactSoftExceptionLogger {
    private static final List<ReactSoftExceptionListener> sListeners = new CopyOnWriteArrayList();

    /* loaded from: classes.dex */
    public interface ReactSoftExceptionListener {
        void logSoftException(final String category, final Throwable cause);
    }

    public static void addListener(ReactSoftExceptionListener listener) {
        List<ReactSoftExceptionListener> list = sListeners;
        if (!list.contains(listener)) {
            list.add(listener);
        }
    }

    public static void removeListener(ReactSoftExceptionListener listener) {
        sListeners.remove(listener);
    }

    public static void clearListeners() {
        sListeners.clear();
    }

    public static void logSoftException(final String category, final Throwable cause) {
        List<ReactSoftExceptionListener> list = sListeners;
        if (list.size() > 0) {
            for (ReactSoftExceptionListener reactSoftExceptionListener : list) {
                reactSoftExceptionListener.logSoftException(category, cause);
            }
            return;
        }
        FLog.e(category, "Unhandled SoftException", cause);
    }

    private static void logNoThrowSoftExceptionWithMessage(final String category, final String message) {
        logSoftException(category, new ReactNoCrashSoftException(message));
    }
}
