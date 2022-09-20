package com.facebook.react.bridge;

import com.facebook.jni.HybridData;
/* loaded from: classes.dex */
public class CxxCallbackImpl implements Callback {
    private final HybridData mHybridData;

    private native void nativeInvoke(NativeArray arguments);

    private CxxCallbackImpl(HybridData hybridData) {
        this.mHybridData = hybridData;
    }

    @Override // com.facebook.react.bridge.Callback
    public void invoke(Object... args) {
        nativeInvoke(Arguments.fromJavaArgs(args));
    }
}
