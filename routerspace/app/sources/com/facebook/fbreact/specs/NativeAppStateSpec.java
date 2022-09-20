package com.facebook.fbreact.specs;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReactModuleWithSpec;
import com.facebook.react.turbomodule.core.interfaces.TurboModule;
import java.util.Map;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public abstract class NativeAppStateSpec extends ReactContextBaseJavaModule implements ReactModuleWithSpec, TurboModule {
    @ReactMethod
    public abstract void addListener(String eventName);

    @ReactMethod
    public abstract void getCurrentAppState(Callback success, Callback error);

    protected abstract Map<String, Object> getTypedExportedConstants();

    @ReactMethod
    public abstract void removeListeners(double count);

    public NativeAppStateSpec(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override // com.facebook.react.bridge.BaseJavaModule
    @Nullable
    public final Map<String, Object> getConstants() {
        return getTypedExportedConstants();
    }
}
