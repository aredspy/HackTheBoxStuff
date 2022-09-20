package com.facebook.react.bridge;
/* loaded from: classes.dex */
public final class CallbackImpl implements Callback {
    private final int mCallbackId;
    private boolean mInvoked = false;
    private final JSInstance mJSInstance;

    public CallbackImpl(JSInstance jsInstance, int callbackId) {
        this.mJSInstance = jsInstance;
        this.mCallbackId = callbackId;
    }

    @Override // com.facebook.react.bridge.Callback
    public void invoke(Object... args) {
        if (this.mInvoked) {
            throw new RuntimeException("Illegal callback invocation from native module. This callback type only permits a single invocation from native code.");
        }
        this.mJSInstance.invokeCallback(this.mCallbackId, Arguments.fromJavaArgs(args));
        this.mInvoked = true;
    }
}
