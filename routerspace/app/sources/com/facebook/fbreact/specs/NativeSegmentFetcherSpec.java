package com.facebook.fbreact.specs;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReactModuleWithSpec;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.turbomodule.core.interfaces.TurboModule;
/* loaded from: classes.dex */
public abstract class NativeSegmentFetcherSpec extends ReactContextBaseJavaModule implements ReactModuleWithSpec, TurboModule {
    @ReactMethod
    public abstract void fetchSegment(double segmentId, ReadableMap options, Callback callback);

    @ReactMethod
    public void getSegment(double segmentId, ReadableMap options, Callback callback) {
    }

    public NativeSegmentFetcherSpec(ReactApplicationContext reactContext) {
        super(reactContext);
    }
}
