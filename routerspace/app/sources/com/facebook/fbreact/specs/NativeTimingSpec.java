package com.facebook.fbreact.specs;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReactModuleWithSpec;
import com.facebook.react.turbomodule.core.interfaces.TurboModule;
/* loaded from: classes.dex */
public abstract class NativeTimingSpec extends ReactContextBaseJavaModule implements ReactModuleWithSpec, TurboModule {
    @ReactMethod
    public abstract void createTimer(double callbackID, double duration, double jsSchedulingTime, boolean repeats);

    @ReactMethod
    public abstract void deleteTimer(double timerID);

    @ReactMethod
    public abstract void setSendIdleEvents(boolean sendIdleEvents);

    public NativeTimingSpec(ReactApplicationContext reactContext) {
        super(reactContext);
    }
}
