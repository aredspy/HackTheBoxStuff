package com.facebook.fbreact.specs;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReactModuleWithSpec;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.turbomodule.core.interfaces.TurboModule;
/* loaded from: classes.dex */
public abstract class NativeExceptionsManagerSpec extends ReactContextBaseJavaModule implements ReactModuleWithSpec, TurboModule {
    @ReactMethod
    public void dismissRedbox() {
    }

    @ReactMethod
    public void reportException(ReadableMap data) {
    }

    @ReactMethod
    public abstract void reportFatalException(String message, ReadableArray stack, double exceptionId);

    @ReactMethod
    public abstract void reportSoftException(String message, ReadableArray stack, double exceptionId);

    @ReactMethod
    public abstract void updateExceptionMessage(String message, ReadableArray stack, double exceptionId);

    public NativeExceptionsManagerSpec(ReactApplicationContext reactContext) {
        super(reactContext);
    }
}
