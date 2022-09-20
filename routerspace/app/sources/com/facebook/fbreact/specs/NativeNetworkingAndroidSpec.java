package com.facebook.fbreact.specs;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReactModuleWithSpec;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.turbomodule.core.interfaces.TurboModule;
/* loaded from: classes.dex */
public abstract class NativeNetworkingAndroidSpec extends ReactContextBaseJavaModule implements ReactModuleWithSpec, TurboModule {
    @ReactMethod
    public abstract void abortRequest(double requestId);

    @ReactMethod
    public abstract void addListener(String eventName);

    @ReactMethod
    public abstract void clearCookies(Callback callback);

    @ReactMethod
    public abstract void removeListeners(double count);

    @ReactMethod
    public abstract void sendRequest(String method, String url, double requestId, ReadableArray headers, ReadableMap data, String responseType, boolean useIncrementalUpdates, double timeout, boolean withCredentials);

    public NativeNetworkingAndroidSpec(ReactApplicationContext reactContext) {
        super(reactContext);
    }
}
