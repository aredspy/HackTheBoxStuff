package com.facebook.react.fabric;

import com.facebook.react.bridge.NativeModuleCallExceptionHandler;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.modules.core.ChoreographerCompat;
/* loaded from: classes.dex */
public abstract class GuardedFrameCallback extends ChoreographerCompat.FrameCallback {
    private final NativeModuleCallExceptionHandler mExceptionHandler;

    protected abstract void doFrameGuarded(long frameTimeNanos);

    @Deprecated
    public GuardedFrameCallback(ReactContext reactContext) {
        this(reactContext.getExceptionHandler());
    }

    protected GuardedFrameCallback(NativeModuleCallExceptionHandler exceptionHandler) {
        this.mExceptionHandler = exceptionHandler;
    }

    @Override // com.facebook.react.modules.core.ChoreographerCompat.FrameCallback
    public final void doFrame(long frameTimeNanos) {
        try {
            doFrameGuarded(frameTimeNanos);
        } catch (RuntimeException e) {
            this.mExceptionHandler.handleException(e);
        }
    }
}
