package com.facebook.react.jscexecutor;

import com.facebook.jni.HybridData;
import com.facebook.react.bridge.JavaScriptExecutor;
import com.facebook.react.bridge.ReadableNativeMap;
import com.facebook.soloader.SoLoader;
/* loaded from: classes.dex */
class JSCExecutor extends JavaScriptExecutor {
    private static native HybridData initHybrid(ReadableNativeMap jscConfig);

    @Override // com.facebook.react.bridge.JavaScriptExecutor
    public String getName() {
        return "JSCExecutor";
    }

    static {
        SoLoader.loadLibrary("jscexecutor");
    }

    public JSCExecutor(ReadableNativeMap jscConfig) {
        super(initHybrid(jscConfig));
    }
}
