package com.facebook.react.uimanager;

import com.facebook.jni.HybridData;
import com.facebook.react.bridge.RuntimeExecutor;
import com.facebook.soloader.SoLoader;
/* loaded from: classes.dex */
public class ComponentNameResolverManager {
    private final HybridData mHybridData;

    private native HybridData initHybrid(RuntimeExecutor runtimeExecutor, Object componentNameResolver);

    private native void installJSIBindings();

    static {
        staticInit();
    }

    public ComponentNameResolverManager(RuntimeExecutor runtimeExecutor, Object componentNameResolver) {
        this.mHybridData = initHybrid(runtimeExecutor, componentNameResolver);
        installJSIBindings();
    }

    private static void staticInit() {
        SoLoader.loadLibrary("uimanagerjni");
    }
}
