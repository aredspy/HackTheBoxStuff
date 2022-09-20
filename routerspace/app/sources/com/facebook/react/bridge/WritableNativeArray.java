package com.facebook.react.bridge;

import com.facebook.infer.annotation.Assertions;
import com.facebook.jni.HybridData;
/* loaded from: classes.dex */
public class WritableNativeArray extends ReadableNativeArray implements WritableArray {
    private static native HybridData initHybrid();

    private native void pushNativeArray(WritableNativeArray array);

    private native void pushNativeMap(WritableNativeMap map);

    @Override // com.facebook.react.bridge.WritableArray
    public native void pushBoolean(boolean value);

    @Override // com.facebook.react.bridge.WritableArray
    public native void pushDouble(double value);

    @Override // com.facebook.react.bridge.WritableArray
    public native void pushInt(int value);

    @Override // com.facebook.react.bridge.WritableArray
    public native void pushNull();

    @Override // com.facebook.react.bridge.WritableArray
    public native void pushString(String value);

    static {
        ReactBridge.staticInit();
    }

    public WritableNativeArray() {
        super(initHybrid());
    }

    @Override // com.facebook.react.bridge.WritableArray
    public void pushArray(ReadableArray array) {
        Assertions.assertCondition(array == null || (array instanceof WritableNativeArray), "Illegal type provided");
        pushNativeArray((WritableNativeArray) array);
    }

    @Override // com.facebook.react.bridge.WritableArray
    public void pushMap(ReadableMap map) {
        Assertions.assertCondition(map == null || (map instanceof WritableNativeMap), "Illegal type provided");
        pushNativeMap((WritableNativeMap) map);
    }
}
