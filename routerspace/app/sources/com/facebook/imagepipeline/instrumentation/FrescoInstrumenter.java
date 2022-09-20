package com.facebook.imagepipeline.instrumentation;
/* loaded from: classes.dex */
public final class FrescoInstrumenter {
    private static volatile Instrumenter sInstance;

    /* loaded from: classes.dex */
    public interface Instrumenter {
        Runnable decorateRunnable(Runnable runnable, String tag);

        boolean isTracing();

        void markFailure(Object token, Throwable th);

        Object onBeforeSubmitWork(String tag);

        Object onBeginWork(Object token, String tag);

        void onEndWork(Object token);
    }

    public static void provide(Instrumenter instrumenter) {
        sInstance = instrumenter;
    }

    public static boolean isTracing() {
        Instrumenter instrumenter = sInstance;
        if (instrumenter == null) {
            return false;
        }
        return instrumenter.isTracing();
    }

    public static Object onBeforeSubmitWork(String tag) {
        Instrumenter instrumenter = sInstance;
        if (instrumenter == null || tag == null) {
            return null;
        }
        return instrumenter.onBeforeSubmitWork(tag);
    }

    public static Object onBeginWork(Object token, String tag) {
        Instrumenter instrumenter = sInstance;
        if (instrumenter == null || token == null) {
            return null;
        }
        return instrumenter.onBeginWork(token, tag);
    }

    public static void onEndWork(Object token) {
        Instrumenter instrumenter = sInstance;
        if (instrumenter == null || token == null) {
            return;
        }
        instrumenter.onEndWork(token);
    }

    public static void markFailure(Object token, Throwable th) {
        Instrumenter instrumenter = sInstance;
        if (instrumenter == null || token == null) {
            return;
        }
        instrumenter.markFailure(token, th);
    }

    public static Runnable decorateRunnable(Runnable runnable, String tag) {
        Instrumenter instrumenter = sInstance;
        if (instrumenter == null || runnable == null) {
            return runnable;
        }
        if (tag == null) {
            tag = "";
        }
        return instrumenter.decorateRunnable(runnable, tag);
    }
}
