package com.facebook.fbreact.specs;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReactModuleWithSpec;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.turbomodule.core.interfaces.TurboModule;
/* loaded from: classes.dex */
public abstract class NativeWebSocketModuleSpec extends ReactContextBaseJavaModule implements ReactModuleWithSpec, TurboModule {
    @ReactMethod
    public abstract void addListener(String eventName);

    @ReactMethod
    public abstract void close(double code, String reason, double socketID);

    @ReactMethod
    public abstract void connect(String url, ReadableArray protocols, ReadableMap options, double socketID);

    @ReactMethod
    public abstract void ping(double socketID);

    @ReactMethod
    public abstract void removeListeners(double count);

    @ReactMethod
    public abstract void send(String message, double forSocketID);

    @ReactMethod
    public abstract void sendBinary(String base64String, double forSocketID);

    public NativeWebSocketModuleSpec(ReactApplicationContext reactContext) {
        super(reactContext);
    }
}
