package com.facebook.fbreact.specs;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReactModuleWithSpec;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.turbomodule.core.interfaces.TurboModule;
/* loaded from: classes.dex */
public abstract class NativeAccessibilityManagerSpec extends ReactContextBaseJavaModule implements ReactModuleWithSpec, TurboModule {
    @ReactMethod
    public abstract void announceForAccessibility(String announcement);

    @ReactMethod
    public abstract void getCurrentBoldTextState(Callback onSuccess, Callback onError);

    @ReactMethod
    public abstract void getCurrentGrayscaleState(Callback onSuccess, Callback onError);

    @ReactMethod
    public abstract void getCurrentInvertColorsState(Callback onSuccess, Callback onError);

    @ReactMethod
    public abstract void getCurrentReduceMotionState(Callback onSuccess, Callback onError);

    @ReactMethod
    public abstract void getCurrentReduceTransparencyState(Callback onSuccess, Callback onError);

    @ReactMethod
    public abstract void getCurrentVoiceOverState(Callback onSuccess, Callback onError);

    @ReactMethod
    public abstract void setAccessibilityContentSizeMultipliers(ReadableMap JSMultipliers);

    @ReactMethod
    public abstract void setAccessibilityFocus(double reactTag);

    public NativeAccessibilityManagerSpec(ReactApplicationContext reactContext) {
        super(reactContext);
    }
}
