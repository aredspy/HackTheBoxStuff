package com.facebook.react;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMarker;
import com.facebook.react.bridge.ReactMarkerConstants;
import com.facebook.react.devsupport.LogBoxModule;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.module.model.ReactModuleInfo;
import com.facebook.react.module.model.ReactModuleInfoProvider;
import com.facebook.react.modules.bundleloader.NativeDevSplitBundleLoaderModule;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.modules.core.ExceptionsManagerModule;
import com.facebook.react.modules.core.HeadlessJsTaskSupportModule;
import com.facebook.react.modules.core.TimingModule;
import com.facebook.react.modules.debug.DevSettingsModule;
import com.facebook.react.modules.debug.SourceCodeModule;
import com.facebook.react.modules.deviceinfo.DeviceInfoModule;
import com.facebook.react.modules.systeminfo.AndroidInfoModule;
import com.facebook.react.turbomodule.core.interfaces.TurboModule;
import com.facebook.react.uimanager.UIImplementationProvider;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.ViewManager;
import com.facebook.react.uimanager.ViewManagerResolver;
import com.facebook.systrace.Systrace;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
public class CoreModulesPackage extends TurboReactPackage implements ReactPackageLogger {
    private final DefaultHardwareBackBtnHandler mHardwareBackBtnHandler;
    private final boolean mLazyViewManagersEnabled;
    private final int mMinTimeLeftInFrameForNonBatchedOperationMs;
    private final ReactInstanceManager mReactInstanceManager;

    public CoreModulesPackage(ReactInstanceManager reactInstanceManager, DefaultHardwareBackBtnHandler hardwareBackBtnHandler, UIImplementationProvider uiImplementationProvider, boolean lazyViewManagersEnabled, int minTimeLeftInFrameForNonBatchedOperationMs) {
        this.mReactInstanceManager = reactInstanceManager;
        this.mHardwareBackBtnHandler = hardwareBackBtnHandler;
        this.mLazyViewManagersEnabled = lazyViewManagersEnabled;
        this.mMinTimeLeftInFrameForNonBatchedOperationMs = minTimeLeftInFrameForNonBatchedOperationMs;
    }

    @Override // com.facebook.react.TurboReactPackage
    public ReactModuleInfoProvider getReactModuleInfoProvider() {
        try {
            return (ReactModuleInfoProvider) Class.forName("com.facebook.react.CoreModulesPackage$$ReactModuleInfoProvider").newInstance();
        } catch (ClassNotFoundException unused) {
            Class[] clsArr = {AndroidInfoModule.class, DeviceEventManagerModule.class, DeviceInfoModule.class, DevSettingsModule.class, ExceptionsManagerModule.class, LogBoxModule.class, HeadlessJsTaskSupportModule.class, SourceCodeModule.class, TimingModule.class, UIManagerModule.class, NativeDevSplitBundleLoaderModule.class};
            final HashMap hashMap = new HashMap();
            for (int i = 0; i < 11; i++) {
                Class cls = clsArr[i];
                ReactModule reactModule = (ReactModule) cls.getAnnotation(ReactModule.class);
                hashMap.put(reactModule.name(), new ReactModuleInfo(reactModule.name(), cls.getName(), reactModule.canOverrideExistingModule(), reactModule.needsEagerInit(), reactModule.hasConstants(), reactModule.isCxxModule(), TurboModule.class.isAssignableFrom(cls)));
            }
            return new ReactModuleInfoProvider() { // from class: com.facebook.react.CoreModulesPackage.1
                @Override // com.facebook.react.module.model.ReactModuleInfoProvider
                public Map<String, ReactModuleInfo> getReactModuleInfos() {
                    return hashMap;
                }
            };
        } catch (IllegalAccessException e) {
            throw new RuntimeException("No ReactModuleInfoProvider for CoreModulesPackage$$ReactModuleInfoProvider", e);
        } catch (InstantiationException e2) {
            throw new RuntimeException("No ReactModuleInfoProvider for CoreModulesPackage$$ReactModuleInfoProvider", e2);
        }
    }

    @Override // com.facebook.react.TurboReactPackage
    public NativeModule getModule(String name, ReactApplicationContext reactContext) {
        name.hashCode();
        char c = 65535;
        switch (name.hashCode()) {
            case -2013505529:
                if (name.equals(LogBoxModule.NAME)) {
                    c = 0;
                    break;
                }
                break;
            case -1789797270:
                if (name.equals(TimingModule.NAME)) {
                    c = 1;
                    break;
                }
                break;
            case -1633589448:
                if (name.equals(DevSettingsModule.NAME)) {
                    c = 2;
                    break;
                }
                break;
            case -1520650172:
                if (name.equals(DeviceInfoModule.NAME)) {
                    c = 3;
                    break;
                }
                break;
            case -1037217463:
                if (name.equals(DeviceEventManagerModule.NAME)) {
                    c = 4;
                    break;
                }
                break;
            case -790603268:
                if (name.equals(AndroidInfoModule.NAME)) {
                    c = 5;
                    break;
                }
                break;
            case -508954630:
                if (name.equals(NativeDevSplitBundleLoaderModule.NAME)) {
                    c = 6;
                    break;
                }
                break;
            case 512434409:
                if (name.equals(ExceptionsManagerModule.NAME)) {
                    c = 7;
                    break;
                }
                break;
            case 881516744:
                if (name.equals(SourceCodeModule.NAME)) {
                    c = '\b';
                    break;
                }
                break;
            case 1256514152:
                if (name.equals(HeadlessJsTaskSupportModule.NAME)) {
                    c = '\t';
                    break;
                }
                break;
            case 1861242489:
                if (name.equals(UIManagerModule.NAME)) {
                    c = '\n';
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return new LogBoxModule(reactContext, this.mReactInstanceManager.getDevSupportManager());
            case 1:
                return new TimingModule(reactContext, this.mReactInstanceManager.getDevSupportManager());
            case 2:
                return new DevSettingsModule(reactContext, this.mReactInstanceManager.getDevSupportManager());
            case 3:
                return new DeviceInfoModule(reactContext);
            case 4:
                return new DeviceEventManagerModule(reactContext, this.mHardwareBackBtnHandler);
            case 5:
                return new AndroidInfoModule(reactContext);
            case 6:
                return new NativeDevSplitBundleLoaderModule(reactContext, this.mReactInstanceManager.getDevSupportManager());
            case 7:
                return new ExceptionsManagerModule(this.mReactInstanceManager.getDevSupportManager());
            case '\b':
                return new SourceCodeModule(reactContext);
            case '\t':
                return new HeadlessJsTaskSupportModule(reactContext);
            case '\n':
                return createUIManager(reactContext);
            default:
                throw new IllegalArgumentException("In CoreModulesPackage, could not find Native module for " + name);
        }
    }

    private UIManagerModule createUIManager(final ReactApplicationContext reactContext) {
        ReactMarker.logMarker(ReactMarkerConstants.CREATE_UI_MANAGER_MODULE_START);
        Systrace.beginSection(0L, "createUIManagerModule");
        try {
            if (this.mLazyViewManagersEnabled) {
                return new UIManagerModule(reactContext, new ViewManagerResolver() { // from class: com.facebook.react.CoreModulesPackage.2
                    @Override // com.facebook.react.uimanager.ViewManagerResolver
                    public ViewManager getViewManager(String viewManagerName) {
                        return CoreModulesPackage.this.mReactInstanceManager.createViewManager(viewManagerName);
                    }

                    @Override // com.facebook.react.uimanager.ViewManagerResolver
                    public List<String> getViewManagerNames() {
                        return CoreModulesPackage.this.mReactInstanceManager.getViewManagerNames();
                    }
                }, this.mMinTimeLeftInFrameForNonBatchedOperationMs);
            }
            return new UIManagerModule(reactContext, this.mReactInstanceManager.getOrCreateViewManagers(reactContext), this.mMinTimeLeftInFrameForNonBatchedOperationMs);
        } finally {
            Systrace.endSection(0L);
            ReactMarker.logMarker(ReactMarkerConstants.CREATE_UI_MANAGER_MODULE_END);
        }
    }

    @Override // com.facebook.react.ReactPackageLogger
    public void startProcessPackage() {
        ReactMarker.logMarker(ReactMarkerConstants.PROCESS_CORE_REACT_PACKAGE_START);
    }

    @Override // com.facebook.react.ReactPackageLogger
    public void endProcessPackage() {
        ReactMarker.logMarker(ReactMarkerConstants.PROCESS_CORE_REACT_PACKAGE_END);
    }
}
