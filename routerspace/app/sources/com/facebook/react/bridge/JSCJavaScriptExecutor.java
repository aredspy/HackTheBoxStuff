package com.facebook.react.bridge;

import com.facebook.jni.HybridData;
/* loaded from: classes.dex */
class JSCJavaScriptExecutor extends JavaScriptExecutor {
    private static native HybridData initHybrid(ReadableNativeMap jscConfig);

    @Override // com.facebook.react.bridge.JavaScriptExecutor
    public String getName() {
        return "JSCJavaScriptExecutor";
    }

    static {
        ReactBridge.staticInit();
    }

    public JSCJavaScriptExecutor(ReadableNativeMap jscConfig) {
        super(initHybrid(jscConfig));
    }
}
