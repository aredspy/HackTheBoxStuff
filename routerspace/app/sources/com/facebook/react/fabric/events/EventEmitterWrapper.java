package com.facebook.react.fabric.events;

import com.facebook.jni.HybridData;
import com.facebook.react.bridge.NativeMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.fabric.FabricSoLoader;
/* loaded from: classes.dex */
public class EventEmitterWrapper {
    private final HybridData mHybridData = initHybrid();

    private static native HybridData initHybrid();

    private native void invokeEvent(String eventName, NativeMap params);

    private native void invokeUniqueEvent(String eventName, NativeMap params, int customCoalesceKey);

    static {
        FabricSoLoader.staticInit();
    }

    private EventEmitterWrapper() {
    }

    public synchronized void invoke(String eventName, WritableMap params) {
        if (!isValid()) {
            return;
        }
        invokeEvent(eventName, params == null ? new WritableNativeMap() : (NativeMap) params);
    }

    public synchronized void invokeUnique(String eventName, WritableMap params, int customCoalesceKey) {
        if (!isValid()) {
            return;
        }
        invokeUniqueEvent(eventName, params == null ? new WritableNativeMap() : (NativeMap) params, customCoalesceKey);
    }

    public synchronized void destroy() {
        HybridData hybridData = this.mHybridData;
        if (hybridData != null) {
            hybridData.resetNative();
        }
    }

    private boolean isValid() {
        HybridData hybridData = this.mHybridData;
        if (hybridData != null) {
            return hybridData.isValid();
        }
        return false;
    }
}
