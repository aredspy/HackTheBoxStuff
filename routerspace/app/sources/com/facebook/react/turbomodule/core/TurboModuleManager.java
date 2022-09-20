package com.facebook.react.turbomodule.core;

import com.facebook.infer.annotation.Assertions;
import com.facebook.jni.HybridData;
import com.facebook.react.bridge.CxxModuleWrapper;
import com.facebook.react.bridge.JSIModule;
import com.facebook.react.bridge.RuntimeExecutor;
import com.facebook.react.config.ReactFeatureFlags;
import com.facebook.react.turbomodule.core.interfaces.CallInvokerHolder;
import com.facebook.react.turbomodule.core.interfaces.TurboModule;
import com.facebook.react.turbomodule.core.interfaces.TurboModuleRegistry;
import com.facebook.soloader.SoLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
public class TurboModuleManager implements JSIModule, TurboModuleRegistry {
    private static volatile boolean sIsSoLibraryLoaded;
    private final TurboModuleProvider mCxxModuleProvider;
    private final List<String> mEagerInitModuleNames;
    private final HybridData mHybridData;
    private final TurboModuleProvider mJavaModuleProvider;
    private final Object mTurboModuleCleanupLock = new Object();
    private boolean mTurboModuleCleanupStarted = false;
    private final Map<String, TurboModuleHolder> mTurboModuleHolders = new HashMap();

    /* loaded from: classes.dex */
    public interface TurboModuleProvider {
        TurboModule getModule(String name);
    }

    private native HybridData initHybrid(RuntimeExecutor runtimeExecutor, CallInvokerHolderImpl jsCallInvokerHolder, CallInvokerHolderImpl nativeCallInvokerHolder, TurboModuleManagerDelegate tmmDelegate, boolean useGlobalCallbackCleanupScopeUsingRetainJSCallback, boolean useTurboModuleManagerCallbackCleanupScope);

    private native void installJSIBindings();

    @Override // com.facebook.react.bridge.JSIModule
    public void initialize() {
    }

    public TurboModuleManager(RuntimeExecutor runtimeExecutor, final TurboModuleManagerDelegate delegate, CallInvokerHolder jsCallInvokerHolder, CallInvokerHolder nativeCallInvokerHolder) {
        List<String> list;
        maybeLoadSoLibrary();
        this.mHybridData = initHybrid(runtimeExecutor, (CallInvokerHolderImpl) jsCallInvokerHolder, (CallInvokerHolderImpl) nativeCallInvokerHolder, delegate, ReactFeatureFlags.useGlobalCallbackCleanupScopeUsingRetainJSCallback, ReactFeatureFlags.useTurboModuleManagerCallbackCleanupScope);
        installJSIBindings();
        if (delegate == null) {
            list = new ArrayList<>();
        } else {
            list = delegate.getEagerInitModuleNames();
        }
        this.mEagerInitModuleNames = list;
        this.mJavaModuleProvider = new TurboModuleProvider() { // from class: com.facebook.react.turbomodule.core.TurboModuleManager.1
            @Override // com.facebook.react.turbomodule.core.TurboModuleManager.TurboModuleProvider
            public TurboModule getModule(String moduleName) {
                TurboModuleManagerDelegate turboModuleManagerDelegate = delegate;
                if (turboModuleManagerDelegate == null) {
                    return null;
                }
                return turboModuleManagerDelegate.getModule(moduleName);
            }
        };
        this.mCxxModuleProvider = new TurboModuleProvider() { // from class: com.facebook.react.turbomodule.core.TurboModuleManager.2
            @Override // com.facebook.react.turbomodule.core.TurboModuleManager.TurboModuleProvider
            public TurboModule getModule(String moduleName) {
                CxxModuleWrapper legacyCxxModule;
                TurboModuleManagerDelegate turboModuleManagerDelegate = delegate;
                if (turboModuleManagerDelegate == null || (legacyCxxModule = turboModuleManagerDelegate.getLegacyCxxModule(moduleName)) == null) {
                    return null;
                }
                Assertions.assertCondition(legacyCxxModule instanceof TurboModule, "CxxModuleWrapper \"" + moduleName + "\" is not a TurboModule");
                return (TurboModule) legacyCxxModule;
            }
        };
    }

    @Override // com.facebook.react.turbomodule.core.interfaces.TurboModuleRegistry
    public List<String> getEagerInitModuleNames() {
        return this.mEagerInitModuleNames;
    }

    private CxxModuleWrapper getLegacyCxxModule(String moduleName) {
        TurboModule module = getModule(moduleName);
        if (!(module instanceof CxxModuleWrapper)) {
            return null;
        }
        return (CxxModuleWrapper) module;
    }

    private TurboModule getJavaModule(String moduleName) {
        TurboModule module = getModule(moduleName);
        if (module instanceof CxxModuleWrapper) {
            return null;
        }
        return module;
    }

    @Override // com.facebook.react.turbomodule.core.interfaces.TurboModuleRegistry
    public TurboModule getModule(String moduleName) {
        synchronized (this.mTurboModuleCleanupLock) {
            if (this.mTurboModuleCleanupStarted) {
                return null;
            }
            if (!this.mTurboModuleHolders.containsKey(moduleName)) {
                this.mTurboModuleHolders.put(moduleName, new TurboModuleHolder());
            }
            TurboModuleHolder turboModuleHolder = this.mTurboModuleHolders.get(moduleName);
            TurboModulePerfLogger.moduleCreateStart(moduleName, turboModuleHolder.getModuleId());
            TurboModule module = getModule(moduleName, turboModuleHolder, true);
            if (module != null) {
                TurboModulePerfLogger.moduleCreateEnd(moduleName, turboModuleHolder.getModuleId());
            } else {
                TurboModulePerfLogger.moduleCreateFail(moduleName, turboModuleHolder.getModuleId());
            }
            return module;
        }
    }

    private TurboModule getModule(String moduleName, TurboModuleHolder moduleHolder, boolean shouldPerfLog) {
        boolean z;
        TurboModule module;
        synchronized (moduleHolder) {
            if (moduleHolder.isDoneCreatingModule()) {
                if (shouldPerfLog) {
                    TurboModulePerfLogger.moduleCreateCacheHit(moduleName, moduleHolder.getModuleId());
                }
                return moduleHolder.getModule();
            }
            boolean z2 = false;
            if (!moduleHolder.isCreatingModule()) {
                moduleHolder.startCreatingModule();
                z = true;
            } else {
                z = false;
            }
            if (z) {
                TurboModulePerfLogger.moduleCreateConstructStart(moduleName, moduleHolder.getModuleId());
                TurboModule module2 = this.mJavaModuleProvider.getModule(moduleName);
                if (module2 == null) {
                    module2 = this.mCxxModuleProvider.getModule(moduleName);
                }
                TurboModulePerfLogger.moduleCreateConstructEnd(moduleName, moduleHolder.getModuleId());
                TurboModulePerfLogger.moduleCreateSetUpStart(moduleName, moduleHolder.getModuleId());
                if (module2 != null) {
                    synchronized (moduleHolder) {
                        moduleHolder.setModule(module2);
                    }
                    module2.initialize();
                }
                TurboModulePerfLogger.moduleCreateSetUpEnd(moduleName, moduleHolder.getModuleId());
                synchronized (moduleHolder) {
                    moduleHolder.endCreatingModule();
                    moduleHolder.notifyAll();
                }
                return module2;
            }
            synchronized (moduleHolder) {
                while (moduleHolder.isCreatingModule()) {
                    try {
                        moduleHolder.wait();
                    } catch (InterruptedException unused) {
                        z2 = true;
                    }
                }
                if (z2) {
                    Thread.currentThread().interrupt();
                }
                module = moduleHolder.getModule();
            }
            return module;
        }
    }

    @Override // com.facebook.react.turbomodule.core.interfaces.TurboModuleRegistry
    public Collection<TurboModule> getModules() {
        ArrayList<TurboModuleHolder> arrayList = new ArrayList();
        synchronized (this.mTurboModuleCleanupLock) {
            arrayList.addAll(this.mTurboModuleHolders.values());
        }
        ArrayList arrayList2 = new ArrayList();
        for (TurboModuleHolder turboModuleHolder : arrayList) {
            synchronized (turboModuleHolder) {
                if (turboModuleHolder.getModule() != null) {
                    arrayList2.add(turboModuleHolder.getModule());
                }
            }
        }
        return arrayList2;
    }

    @Override // com.facebook.react.turbomodule.core.interfaces.TurboModuleRegistry
    public boolean hasModule(String moduleName) {
        TurboModuleHolder turboModuleHolder;
        synchronized (this.mTurboModuleCleanupLock) {
            turboModuleHolder = this.mTurboModuleHolders.get(moduleName);
        }
        if (turboModuleHolder != null) {
            synchronized (turboModuleHolder) {
                return turboModuleHolder.getModule() != null;
            }
        }
        return false;
    }

    @Override // com.facebook.react.bridge.JSIModule
    public void onCatalystInstanceDestroy() {
        synchronized (this.mTurboModuleCleanupLock) {
            this.mTurboModuleCleanupStarted = true;
        }
        for (Map.Entry<String, TurboModuleHolder> entry : this.mTurboModuleHolders.entrySet()) {
            TurboModule module = getModule(entry.getKey(), entry.getValue(), false);
            if (module != null) {
                module.invalidate();
            }
        }
        this.mTurboModuleHolders.clear();
        this.mHybridData.resetNative();
    }

    private static synchronized void maybeLoadSoLibrary() {
        synchronized (TurboModuleManager.class) {
            if (!sIsSoLibraryLoaded) {
                SoLoader.loadLibrary("turbomodulejsijni");
                sIsSoLibraryLoaded = true;
            }
        }
    }

    /* loaded from: classes.dex */
    public static class TurboModuleHolder {
        private static volatile int sHolderCount;
        private volatile TurboModule mModule = null;
        private volatile boolean mIsTryingToCreate = false;
        private volatile boolean mIsDoneCreatingModule = false;
        private volatile int mModuleId = sHolderCount;

        public TurboModuleHolder() {
            sHolderCount++;
        }

        int getModuleId() {
            return this.mModuleId;
        }

        void setModule(TurboModule module) {
            this.mModule = module;
        }

        TurboModule getModule() {
            return this.mModule;
        }

        void startCreatingModule() {
            this.mIsTryingToCreate = true;
        }

        void endCreatingModule() {
            this.mIsTryingToCreate = false;
            this.mIsDoneCreatingModule = true;
        }

        boolean isDoneCreatingModule() {
            return this.mIsDoneCreatingModule;
        }

        boolean isCreatingModule() {
            return this.mIsTryingToCreate;
        }
    }
}
