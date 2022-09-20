package com.facebook.fbreact.specs;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReactModuleWithSpec;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.turbomodule.core.interfaces.TurboModule;
/* loaded from: classes.dex */
public abstract class NativeImageLoaderAndroidSpec extends ReactContextBaseJavaModule implements ReactModuleWithSpec, TurboModule {
    @ReactMethod
    public abstract void abortRequest(double requestId);

    @ReactMethod
    public abstract void getSize(String uri, Promise promise);

    @ReactMethod
    public abstract void getSizeWithHeaders(String uri, ReadableMap headers, Promise promise);

    @ReactMethod
    public abstract void prefetchImage(String uri, double requestId, Promise promise);

    @ReactMethod
    public abstract void queryCache(ReadableArray uris, Promise promise);

    public NativeImageLoaderAndroidSpec(ReactApplicationContext reactContext) {
        super(reactContext);
    }
}
