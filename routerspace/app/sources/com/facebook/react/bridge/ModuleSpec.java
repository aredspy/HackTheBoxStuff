package com.facebook.react.bridge;

import com.facebook.common.logging.FLog;
import com.facebook.react.module.annotations.ReactModule;
import javax.inject.Provider;
/* loaded from: classes.dex */
public class ModuleSpec {
    private static final String TAG = "ModuleSpec";
    private final String mName;
    private final Provider<? extends NativeModule> mProvider;
    private final Class<? extends NativeModule> mType;

    public static ModuleSpec viewManagerSpec(Provider<? extends NativeModule> provider) {
        return new ModuleSpec(provider);
    }

    public static ModuleSpec nativeModuleSpec(Class<? extends NativeModule> type, Provider<? extends NativeModule> provider) {
        ReactModule reactModule = (ReactModule) type.getAnnotation(ReactModule.class);
        if (reactModule == null) {
            FLog.w(TAG, "Could not find @ReactModule annotation on " + type.getName() + ". So creating the module eagerly to get the name. Consider adding an annotation to make this Lazy");
            return new ModuleSpec(provider, provider.get().getName());
        }
        return new ModuleSpec(provider, reactModule.name());
    }

    public static ModuleSpec nativeModuleSpec(String className, Provider<? extends NativeModule> provider) {
        return new ModuleSpec(provider, className);
    }

    private ModuleSpec(Provider<? extends NativeModule> provider) {
        this.mType = null;
        this.mProvider = provider;
        this.mName = null;
    }

    private ModuleSpec(Provider<? extends NativeModule> provider, String name) {
        this.mType = null;
        this.mProvider = provider;
        this.mName = name;
    }

    public Class<? extends NativeModule> getType() {
        return this.mType;
    }

    public String getName() {
        return this.mName;
    }

    public Provider<? extends NativeModule> getProvider() {
        return this.mProvider;
    }
}
