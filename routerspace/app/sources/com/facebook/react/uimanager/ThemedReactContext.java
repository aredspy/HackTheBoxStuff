package com.facebook.react.uimanager;

import android.app.Activity;
import android.content.Context;
import com.facebook.react.bridge.JSIModule;
import com.facebook.react.bridge.JSIModuleType;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
/* loaded from: classes.dex */
public class ThemedReactContext extends ReactContext {
    private final String mModuleName;
    private final ReactApplicationContext mReactApplicationContext;
    private final int mSurfaceId;

    @Deprecated
    public ThemedReactContext(ReactApplicationContext reactApplicationContext, Context base) {
        this(reactApplicationContext, base, null, -1);
    }

    @Deprecated
    public ThemedReactContext(ReactApplicationContext reactApplicationContext, Context base, String moduleName) {
        this(reactApplicationContext, base, moduleName, -1);
    }

    public ThemedReactContext(ReactApplicationContext reactApplicationContext, Context base, String moduleName, int surfaceId) {
        super(base);
        if (reactApplicationContext.hasCatalystInstance()) {
            initializeWithInstance(reactApplicationContext.getCatalystInstance());
        }
        this.mReactApplicationContext = reactApplicationContext;
        this.mModuleName = moduleName;
        this.mSurfaceId = surfaceId;
    }

    @Override // com.facebook.react.bridge.ReactContext
    public void addLifecycleEventListener(LifecycleEventListener listener) {
        this.mReactApplicationContext.addLifecycleEventListener(listener);
    }

    @Override // com.facebook.react.bridge.ReactContext
    public void removeLifecycleEventListener(LifecycleEventListener listener) {
        this.mReactApplicationContext.removeLifecycleEventListener(listener);
    }

    @Override // com.facebook.react.bridge.ReactContext
    public boolean hasCurrentActivity() {
        return this.mReactApplicationContext.hasCurrentActivity();
    }

    @Override // com.facebook.react.bridge.ReactContext
    public Activity getCurrentActivity() {
        return this.mReactApplicationContext.getCurrentActivity();
    }

    @Deprecated
    public String getSurfaceID() {
        return this.mModuleName;
    }

    public String getModuleName() {
        return this.mModuleName;
    }

    public int getSurfaceId() {
        return this.mSurfaceId;
    }

    public ReactApplicationContext getReactApplicationContext() {
        return this.mReactApplicationContext;
    }

    @Override // com.facebook.react.bridge.ReactContext
    public boolean isBridgeless() {
        return this.mReactApplicationContext.isBridgeless();
    }

    @Override // com.facebook.react.bridge.ReactContext
    public JSIModule getJSIModule(JSIModuleType moduleType) {
        if (isBridgeless()) {
            return this.mReactApplicationContext.getJSIModule(moduleType);
        }
        return super.getJSIModule(moduleType);
    }
}
