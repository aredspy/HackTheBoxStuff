package com.facebook.react.bridge;

import java.util.ArrayList;
/* loaded from: classes.dex */
public interface ReadableArray {
    ReadableArray getArray(int index);

    boolean getBoolean(int index);

    double getDouble(int index);

    Dynamic getDynamic(int index);

    int getInt(int index);

    ReadableMap getMap(int index);

    String getString(int index);

    ReadableType getType(int index);

    boolean isNull(int index);

    int size();

    ArrayList<Object> toArrayList();
}
