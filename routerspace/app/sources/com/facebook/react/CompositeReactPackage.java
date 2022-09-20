package com.facebook.react;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
/* loaded from: classes.dex */
public class CompositeReactPackage implements ViewManagerOnDemandReactPackage, ReactPackage {
    private final List<ReactPackage> mChildReactPackages;

    public CompositeReactPackage(ReactPackage arg1, ReactPackage arg2, ReactPackage... args) {
        ArrayList arrayList = new ArrayList();
        this.mChildReactPackages = arrayList;
        arrayList.add(arg1);
        arrayList.add(arg2);
        Collections.addAll(arrayList, args);
    }

    @Override // com.facebook.react.ReactPackage
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        HashMap hashMap = new HashMap();
        for (ReactPackage reactPackage : this.mChildReactPackages) {
            if (reactPackage instanceof TurboReactPackage) {
                TurboReactPackage turboReactPackage = (TurboReactPackage) reactPackage;
                for (String str : turboReactPackage.getReactModuleInfoProvider().getReactModuleInfos().keySet()) {
                    hashMap.put(str, turboReactPackage.getModule(str, reactContext));
                }
            } else {
                for (NativeModule nativeModule : reactPackage.createNativeModules(reactContext)) {
                    hashMap.put(nativeModule.getName(), nativeModule);
                }
            }
        }
        return new ArrayList(hashMap.values());
    }

    @Override // com.facebook.react.ReactPackage
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        HashMap hashMap = new HashMap();
        for (ReactPackage reactPackage : this.mChildReactPackages) {
            for (ViewManager viewManager : reactPackage.createViewManagers(reactContext)) {
                hashMap.put(viewManager.getName(), viewManager);
            }
        }
        return new ArrayList(hashMap.values());
    }

    @Override // com.facebook.react.ViewManagerOnDemandReactPackage
    public List<String> getViewManagerNames(ReactApplicationContext reactContext) {
        List<String> viewManagerNames;
        HashSet hashSet = new HashSet();
        for (ReactPackage reactPackage : this.mChildReactPackages) {
            if ((reactPackage instanceof ViewManagerOnDemandReactPackage) && (viewManagerNames = ((ViewManagerOnDemandReactPackage) reactPackage).getViewManagerNames(reactContext)) != null) {
                hashSet.addAll(viewManagerNames);
            }
        }
        return new ArrayList(hashSet);
    }

    @Override // com.facebook.react.ViewManagerOnDemandReactPackage
    public ViewManager createViewManager(ReactApplicationContext reactContext, String viewManagerName) {
        ViewManager createViewManager;
        List<ReactPackage> list = this.mChildReactPackages;
        ListIterator<ReactPackage> listIterator = list.listIterator(list.size());
        while (listIterator.hasPrevious()) {
            ReactPackage previous = listIterator.previous();
            if ((previous instanceof ViewManagerOnDemandReactPackage) && (createViewManager = ((ViewManagerOnDemandReactPackage) previous).createViewManager(reactContext, viewManagerName)) != null) {
                return createViewManager;
            }
        }
        return null;
    }
}
