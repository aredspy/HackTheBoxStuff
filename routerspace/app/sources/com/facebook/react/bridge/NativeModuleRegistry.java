package com.facebook.react.bridge;

import com.facebook.infer.annotation.Assertions;
import com.facebook.react.config.ReactFeatureFlags;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.systrace.Systrace;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
public class NativeModuleRegistry {
    private final String TAG = NativeModuleRegistry.class.getSimpleName();
    private final Map<String, ModuleHolder> mModules;
    private final ReactApplicationContext mReactApplicationContext;

    public NativeModuleRegistry(ReactApplicationContext reactApplicationContext, Map<String, ModuleHolder> modules) {
        this.mReactApplicationContext = reactApplicationContext;
        this.mModules = modules;
    }

    private Map<String, ModuleHolder> getModuleMap() {
        return this.mModules;
    }

    private ReactApplicationContext getReactApplicationContext() {
        return this.mReactApplicationContext;
    }

    public Collection<JavaModuleWrapper> getJavaModules(JSInstance jsInstance) {
        ArrayList arrayList = new ArrayList();
        for (Map.Entry<String, ModuleHolder> entry : this.mModules.entrySet()) {
            if (!entry.getValue().isCxxModule()) {
                if (ReactFeatureFlags.warnOnLegacyNativeModuleSystemUse) {
                    String str = this.TAG;
                    ReactSoftExceptionLogger.logSoftException(str, new ReactNoCrashSoftException("Registering legacy NativeModule: Java NativeModule (name = \"" + entry.getValue().getName() + "\", className = " + entry.getValue().getClassName() + ")."));
                }
                arrayList.add(new JavaModuleWrapper(jsInstance, entry.getValue()));
            }
        }
        return arrayList;
    }

    public Collection<ModuleHolder> getCxxModules() {
        ArrayList arrayList = new ArrayList();
        for (Map.Entry<String, ModuleHolder> entry : this.mModules.entrySet()) {
            if (entry.getValue().isCxxModule()) {
                if (ReactFeatureFlags.warnOnLegacyNativeModuleSystemUse) {
                    String str = this.TAG;
                    ReactSoftExceptionLogger.logSoftException(str, new ReactNoCrashSoftException("Registering legacy NativeModule: Cxx NativeModule (name = \"" + entry.getValue().getName() + "\", className = " + entry.getValue().getClassName() + ")."));
                }
                arrayList.add(entry.getValue());
            }
        }
        return arrayList;
    }

    public void registerModules(NativeModuleRegistry newRegister) {
        Assertions.assertCondition(this.mReactApplicationContext.equals(newRegister.getReactApplicationContext()), "Extending native modules with non-matching application contexts.");
        for (Map.Entry<String, ModuleHolder> entry : newRegister.getModuleMap().entrySet()) {
            String key = entry.getKey();
            if (!this.mModules.containsKey(key)) {
                this.mModules.put(key, entry.getValue());
            }
        }
    }

    public void notifyJSInstanceDestroy() {
        this.mReactApplicationContext.assertOnNativeModulesQueueThread();
        Systrace.beginSection(0L, "NativeModuleRegistry_notifyJSInstanceDestroy");
        try {
            for (ModuleHolder moduleHolder : this.mModules.values()) {
                moduleHolder.destroy();
            }
            if (ReactFeatureFlags.enableReactContextCleanupFix) {
                this.mModules.clear();
            }
        } finally {
            Systrace.endSection(0L);
        }
    }

    public void notifyJSInstanceInitialized() {
        this.mReactApplicationContext.assertOnNativeModulesQueueThread("From version React Native v0.44, native modules are explicitly not initialized on the UI thread. See https://github.com/facebook/react-native/wiki/Breaking-Changes#d4611211-reactnativeandroidbreaking-move-nativemodule-initialization-off-ui-thread---aaachiuuu  for more details.");
        ReactMarker.logMarker(ReactMarkerConstants.NATIVE_MODULE_INITIALIZE_START);
        Systrace.beginSection(0L, "NativeModuleRegistry_notifyJSInstanceInitialized");
        try {
            for (ModuleHolder moduleHolder : this.mModules.values()) {
                moduleHolder.markInitializable();
            }
        } finally {
            Systrace.endSection(0L);
            ReactMarker.logMarker(ReactMarkerConstants.NATIVE_MODULE_INITIALIZE_END);
        }
    }

    public void onBatchComplete() {
        ModuleHolder moduleHolder = this.mModules.get(UIManagerModule.NAME);
        if (moduleHolder == null || !moduleHolder.hasInstance()) {
            return;
        }
        ((OnBatchCompleteListener) moduleHolder.getModule()).onBatchComplete();
    }

    public <T extends NativeModule> boolean hasModule(Class<T> moduleInterface) {
        return this.mModules.containsKey(((ReactModule) moduleInterface.getAnnotation(ReactModule.class)).name());
    }

    public <T extends NativeModule> T getModule(Class<T> moduleInterface) {
        ReactModule reactModule = (ReactModule) moduleInterface.getAnnotation(ReactModule.class);
        if (reactModule == null) {
            throw new IllegalArgumentException("Could not find @ReactModule annotation in class " + moduleInterface.getName());
        }
        ModuleHolder moduleHolder = this.mModules.get(reactModule.name());
        return (T) ((ModuleHolder) Assertions.assertNotNull(moduleHolder, reactModule.name() + " could not be found. Is it defined in " + moduleInterface.getName())).getModule();
    }

    public boolean hasModule(String name) {
        return this.mModules.containsKey(name);
    }

    public NativeModule getModule(String name) {
        ModuleHolder moduleHolder = this.mModules.get(name);
        return ((ModuleHolder) Assertions.assertNotNull(moduleHolder, "Could not find module with name " + name)).getModule();
    }

    public List<NativeModule> getAllModules() {
        ArrayList arrayList = new ArrayList();
        for (ModuleHolder moduleHolder : this.mModules.values()) {
            arrayList.add(moduleHolder.getModule());
        }
        return arrayList;
    }
}
