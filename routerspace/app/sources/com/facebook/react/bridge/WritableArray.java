package com.facebook.react.bridge;
/* loaded from: classes.dex */
public interface WritableArray extends ReadableArray {
    void pushArray(ReadableArray array);

    void pushBoolean(boolean value);

    void pushDouble(double value);

    void pushInt(int value);

    void pushMap(ReadableMap map);

    void pushNull();

    void pushString(String value);
}
