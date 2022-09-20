package com.facebook.react;

import com.facebook.react.bridge.NativeModule;
import javax.inject.Provider;
/* loaded from: classes.dex */
public class EagerModuleProvider implements Provider<NativeModule> {
    private final NativeModule mModule;

    public EagerModuleProvider(NativeModule module) {
        this.mModule = module;
    }

    @Override // javax.inject.Provider
    public NativeModule get() {
        return this.mModule;
    }
}
