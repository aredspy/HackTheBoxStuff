package com.facebook.fbreact.specs;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReactModuleWithSpec;
import com.facebook.react.turbomodule.core.interfaces.TurboModule;
/* loaded from: classes.dex */
public abstract class NativeLinkingManagerSpec extends ReactContextBaseJavaModule implements ReactModuleWithSpec, TurboModule {
    @ReactMethod
    public abstract void addListener(String eventName);

    @ReactMethod
    public abstract void canOpenURL(String url, Promise promise);

    @ReactMethod
    public abstract void getInitialURL(Promise promise);

    @ReactMethod
    public abstract void openSettings(Promise promise);

    @ReactMethod
    public abstract void openURL(String url, Promise promise);

    @ReactMethod
    public abstract void removeListeners(double count);

    public NativeLinkingManagerSpec(ReactApplicationContext reactContext) {
        super(reactContext);
    }
}
