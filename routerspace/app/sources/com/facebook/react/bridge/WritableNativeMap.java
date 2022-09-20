package com.facebook.react.bridge;

import com.facebook.infer.annotation.Assertions;
import com.facebook.jni.HybridData;
/* loaded from: classes.dex */
public class WritableNativeMap extends ReadableNativeMap implements WritableMap {
    private static native HybridData initHybrid();

    private native void mergeNativeMap(ReadableNativeMap source);

    private native void putNativeArray(String key, WritableNativeArray value);

    private native void putNativeMap(String key, WritableNativeMap value);

    @Override // com.facebook.react.bridge.WritableMap
    public native void putBoolean(String key, boolean value);

    @Override // com.facebook.react.bridge.WritableMap
    public native void putDouble(String key, double value);

    @Override // com.facebook.react.bridge.WritableMap
    public native void putInt(String key, int value);

    @Override // com.facebook.react.bridge.WritableMap
    public native void putNull(String key);

    @Override // com.facebook.react.bridge.WritableMap
    public native void putString(String key, String value);

    static {
        ReactBridge.staticInit();
    }

    @Override // com.facebook.react.bridge.WritableMap
    public void putMap(String key, ReadableMap value) {
        Assertions.assertCondition(value == null || (value instanceof WritableNativeMap), "Illegal type provided");
        putNativeMap(key, (WritableNativeMap) value);
    }

    @Override // com.facebook.react.bridge.WritableMap
    public void putArray(String key, ReadableArray value) {
        Assertions.assertCondition(value == null || (value instanceof WritableNativeArray), "Illegal type provided");
        putNativeArray(key, (WritableNativeArray) value);
    }

    @Override // com.facebook.react.bridge.WritableMap
    public void merge(ReadableMap source) {
        Assertions.assertCondition(source instanceof ReadableNativeMap, "Illegal type provided");
        mergeNativeMap((ReadableNativeMap) source);
    }

    @Override // com.facebook.react.bridge.WritableMap
    public WritableMap copy() {
        WritableNativeMap writableNativeMap = new WritableNativeMap();
        writableNativeMap.merge(this);
        return writableNativeMap;
    }

    public WritableNativeMap() {
        super(initHybrid());
    }
}
