package com.facebook.react.bridge;
/* loaded from: classes.dex */
public interface WritableMap extends ReadableMap {
    WritableMap copy();

    void merge(ReadableMap source);

    void putArray(String key, ReadableArray value);

    void putBoolean(String key, boolean value);

    void putDouble(String key, double value);

    void putInt(String key, int value);

    void putMap(String key, ReadableMap value);

    void putNull(String key);

    void putString(String key, String value);
}
