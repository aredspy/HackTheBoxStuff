package com.th3rdwave.safeareacontext;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.th3rdwave.safeareacontext.SafeAreaProvider;
import java.util.Map;
/* loaded from: classes.dex */
public class SafeAreaProviderManager extends ViewGroupManager<SafeAreaProvider> {
    private final ReactApplicationContext mContext;

    @Override // com.facebook.react.uimanager.ViewManager, com.facebook.react.bridge.NativeModule
    public String getName() {
        return "RNCSafeAreaProvider";
    }

    public SafeAreaProviderManager(ReactApplicationContext context) {
        this.mContext = context;
    }

    @Override // com.facebook.react.uimanager.ViewManager
    public SafeAreaProvider createViewInstance(ThemedReactContext context) {
        return new SafeAreaProvider(context);
    }

    public void addEventEmitters(ThemedReactContext reactContext, final SafeAreaProvider view) {
        final EventDispatcher eventDispatcher = ((UIManagerModule) reactContext.getNativeModule(UIManagerModule.class)).getEventDispatcher();
        view.setOnInsetsChangeListener(new SafeAreaProvider.OnInsetsChangeListener() { // from class: com.th3rdwave.safeareacontext.SafeAreaProviderManager.1
            @Override // com.th3rdwave.safeareacontext.SafeAreaProvider.OnInsetsChangeListener
            public void onInsetsChange(SafeAreaProvider view2, EdgeInsets insets, Rect frame) {
                eventDispatcher.dispatchEvent(new InsetsChangeEvent(view2.getId(), insets, frame));
            }
        });
    }

    @Override // com.facebook.react.uimanager.BaseViewManager, com.facebook.react.uimanager.ViewManager
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.builder().put("topInsetsChange", MapBuilder.of("registrationName", "onInsetsChange")).build();
    }

    private Map<String, Object> getInitialWindowMetrics() {
        ViewGroup viewGroup;
        View findViewById;
        Activity currentActivity = this.mContext.getCurrentActivity();
        if (currentActivity == null || (viewGroup = (ViewGroup) currentActivity.getWindow().getDecorView()) == null || (findViewById = viewGroup.findViewById(16908290)) == null) {
            return null;
        }
        EdgeInsets safeAreaInsets = SafeAreaUtils.getSafeAreaInsets(viewGroup);
        Rect frame = SafeAreaUtils.getFrame(viewGroup, findViewById);
        if (safeAreaInsets != null && frame != null) {
            return MapBuilder.of("insets", SerializationUtils.edgeInsetsToJavaMap(safeAreaInsets), "frame", SerializationUtils.rectToJavaMap(frame));
        }
        return null;
    }

    @Override // com.facebook.react.uimanager.ViewManager
    public Map<String, Object> getExportedViewConstants() {
        return MapBuilder.of("initialWindowMetrics", getInitialWindowMetrics());
    }
}
