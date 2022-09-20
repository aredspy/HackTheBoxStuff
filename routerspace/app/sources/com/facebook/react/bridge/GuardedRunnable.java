package com.facebook.react.bridge;
/* loaded from: classes.dex */
public abstract class GuardedRunnable implements Runnable {
    private final NativeModuleCallExceptionHandler mExceptionHandler;

    public abstract void runGuarded();

    @Deprecated
    public GuardedRunnable(ReactContext reactContext) {
        this(reactContext.getExceptionHandler());
    }

    public GuardedRunnable(NativeModuleCallExceptionHandler exceptionHandler) {
        this.mExceptionHandler = exceptionHandler;
    }

    @Override // java.lang.Runnable
    public final void run() {
        try {
            runGuarded();
        } catch (RuntimeException e) {
            this.mExceptionHandler.handleException(e);
        }
    }
}
