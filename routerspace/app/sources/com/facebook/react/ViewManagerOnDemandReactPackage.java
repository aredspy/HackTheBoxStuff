package com.facebook.react;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import java.util.List;
/* loaded from: classes.dex */
public interface ViewManagerOnDemandReactPackage {
    ViewManager createViewManager(ReactApplicationContext reactContext, String viewManagerName);

    List<String> getViewManagerNames(ReactApplicationContext reactContext);
}
