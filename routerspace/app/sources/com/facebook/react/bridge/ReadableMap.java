package com.facebook.react.bridge;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
/* loaded from: classes.dex */
public interface ReadableMap {
    ReadableArray getArray(String name);

    boolean getBoolean(String name);

    double getDouble(String name);

    Dynamic getDynamic(String name);

    Iterator<Map.Entry<String, Object>> getEntryIterator();

    int getInt(String name);

    ReadableMap getMap(String name);

    String getString(String name);

    ReadableType getType(String name);

    boolean hasKey(String name);

    boolean isNull(String name);

    ReadableMapKeySetIterator keySetIterator();

    HashMap<String, Object> toHashMap();
}
