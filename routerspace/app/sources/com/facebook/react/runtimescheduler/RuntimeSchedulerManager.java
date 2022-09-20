package com.facebook.react.runtimescheduler;

import com.facebook.jni.HybridData;
import com.facebook.react.bridge.RuntimeExecutor;
import com.facebook.soloader.SoLoader;
/* loaded from: classes.dex */
public class RuntimeSchedulerManager {
    private final HybridData mHybridData;

    private native HybridData initHybrid(RuntimeExecutor runtimeExecutor);

    private native void installJSIBindings();

    static {
        staticInit();
    }

    public RuntimeSchedulerManager(RuntimeExecutor runtimeExecutor) {
        this.mHybridData = initHybrid(runtimeExecutor);
        installJSIBindings();
    }

    private static void staticInit() {
        SoLoader.loadLibrary("runtimeschedulerjni");
    }
}
