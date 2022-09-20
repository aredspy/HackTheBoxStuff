package com.facebook.fbreact.specs;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReactModuleWithSpec;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.turbomodule.core.interfaces.TurboModule;
/* loaded from: classes.dex */
public abstract class NativePermissionsAndroidSpec extends ReactContextBaseJavaModule implements ReactModuleWithSpec, TurboModule {
    @ReactMethod
    public abstract void checkPermission(String permission, Promise promise);

    @ReactMethod
    public abstract void requestMultiplePermissions(ReadableArray permissions, Promise promise);

    @ReactMethod
    public abstract void requestPermission(String permission, Promise promise);

    @ReactMethod
    public abstract void shouldShowRequestPermissionRationale(String permission, Promise promise);

    public NativePermissionsAndroidSpec(ReactApplicationContext reactContext) {
        super(reactContext);
    }
}
