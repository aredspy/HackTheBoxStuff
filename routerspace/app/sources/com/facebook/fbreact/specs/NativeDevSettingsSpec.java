package com.facebook.fbreact.specs;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReactModuleWithSpec;
import com.facebook.react.turbomodule.core.interfaces.TurboModule;
/* loaded from: classes.dex */
public abstract class NativeDevSettingsSpec extends ReactContextBaseJavaModule implements ReactModuleWithSpec, TurboModule {
    @ReactMethod
    public abstract void addListener(String eventName);

    @ReactMethod
    public abstract void addMenuItem(String title);

    @ReactMethod
    public void onFastRefresh() {
    }

    @ReactMethod
    public abstract void reload();

    @ReactMethod
    public void reloadWithReason(String reason) {
    }

    @ReactMethod
    public abstract void removeListeners(double count);

    @ReactMethod
    public abstract void setHotLoadingEnabled(boolean isHotLoadingEnabled);

    @ReactMethod
    public abstract void setIsDebuggingRemotely(boolean isDebuggingRemotelyEnabled);

    @ReactMethod
    public abstract void setIsShakeToShowDevMenuEnabled(boolean enabled);

    @ReactMethod
    public abstract void setProfilingEnabled(boolean isProfilingEnabled);

    @ReactMethod
    public abstract void toggleElementInspector();

    public NativeDevSettingsSpec(ReactApplicationContext reactContext) {
        super(reactContext);
    }
}
