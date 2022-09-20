package com.facebook.fbreact.specs;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReactModuleWithSpec;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.turbomodule.core.interfaces.TurboModule;
/* loaded from: classes.dex */
public abstract class NativeVibrationSpec extends ReactContextBaseJavaModule implements ReactModuleWithSpec, TurboModule {
    @ReactMethod
    public abstract void cancel();

    @ReactMethod
    public abstract void vibrate(double pattern);

    @ReactMethod
    public abstract void vibrateByPattern(ReadableArray pattern, double repeat);

    public NativeVibrationSpec(ReactApplicationContext reactContext) {
        super(reactContext);
    }
}
