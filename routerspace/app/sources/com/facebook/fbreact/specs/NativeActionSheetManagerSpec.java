package com.facebook.fbreact.specs;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReactModuleWithSpec;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.turbomodule.core.interfaces.TurboModule;
/* loaded from: classes.dex */
public abstract class NativeActionSheetManagerSpec extends ReactContextBaseJavaModule implements ReactModuleWithSpec, TurboModule {
    @ReactMethod
    public abstract void showActionSheetWithOptions(ReadableMap options, Callback callback);

    @ReactMethod
    public abstract void showShareActionSheetWithOptions(ReadableMap options, Callback failureCallback, Callback successCallback);

    public NativeActionSheetManagerSpec(ReactApplicationContext reactContext) {
        super(reactContext);
    }
}
