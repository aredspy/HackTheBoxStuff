package com.facebook.fbreact.specs;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReactModuleWithSpec;
import com.facebook.react.turbomodule.core.interfaces.TurboModule;
/* loaded from: classes.dex */
public abstract class NativeHeadlessJsTaskSupportSpec extends ReactContextBaseJavaModule implements ReactModuleWithSpec, TurboModule {
    @ReactMethod
    public abstract void notifyTaskFinished(double taskId);

    @ReactMethod
    public abstract void notifyTaskRetry(double taskId, Promise promise);

    public NativeHeadlessJsTaskSupportSpec(ReactApplicationContext reactContext) {
        super(reactContext);
    }
}