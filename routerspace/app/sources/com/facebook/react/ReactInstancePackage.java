package com.facebook.react;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import java.util.List;
@Deprecated
/* loaded from: classes.dex */
public abstract class ReactInstancePackage implements ReactPackage {
    public abstract List<NativeModule> createNativeModules(ReactApplicationContext reactContext, ReactInstanceManager reactInstanceManager);

    @Override // com.facebook.react.ReactPackage
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        throw new RuntimeException("ReactInstancePackage must be passed in the ReactInstanceManager.");
    }
}
