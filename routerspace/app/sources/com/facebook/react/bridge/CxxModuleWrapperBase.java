package com.facebook.react.bridge;

import com.facebook.jni.HybridData;
/* loaded from: classes.dex */
public class CxxModuleWrapperBase implements NativeModule {
    private HybridData mHybridData;

    @Override // com.facebook.react.bridge.NativeModule
    public boolean canOverrideExistingModule() {
        return false;
    }

    @Override // com.facebook.react.bridge.NativeModule
    public native String getName();

    @Override // com.facebook.react.bridge.NativeModule, com.facebook.react.turbomodule.core.interfaces.TurboModule
    public void initialize() {
    }

    @Override // com.facebook.react.bridge.NativeModule
    public void onCatalystInstanceDestroy() {
    }

    static {
        ReactBridge.staticInit();
    }

    @Override // com.facebook.react.bridge.NativeModule, com.facebook.react.turbomodule.core.interfaces.TurboModule
    public void invalidate() {
        this.mHybridData.resetNative();
    }

    public CxxModuleWrapperBase(HybridData hd) {
        this.mHybridData = hd;
    }

    protected void resetModule(HybridData hd) {
        HybridData hybridData = this.mHybridData;
        if (hd != hybridData) {
            hybridData.resetNative();
            this.mHybridData = hd;
        }
    }
}
