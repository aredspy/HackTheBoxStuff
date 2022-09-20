package com.facebook.react.modules.storage;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
/* loaded from: classes.dex */
public class AsyncStorageErrorUtil {
    public static WritableMap getError(String key, String errorMessage) {
        WritableMap createMap = Arguments.createMap();
        createMap.putString("message", errorMessage);
        if (key != null) {
            createMap.putString("key", key);
        }
        return createMap;
    }

    public static WritableMap getInvalidKeyError(String key) {
        return getError(key, "Invalid key");
    }

    public static WritableMap getInvalidValueError(String key) {
        return getError(key, "Invalid Value");
    }

    public static WritableMap getDBError(String key) {
        return getError(key, "Database Error");
    }
}
