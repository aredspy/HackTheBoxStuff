package com.th3rdwave.safeareacontext;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.uimanager.LayoutShadowNode;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewProps;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.views.view.ReactViewManager;
import java.util.EnumSet;
/* loaded from: classes.dex */
public class SafeAreaViewManager extends ReactViewManager {
    @Override // com.facebook.react.views.view.ReactViewManager, com.facebook.react.uimanager.ViewManager, com.facebook.react.bridge.NativeModule
    public String getName() {
        return "RNCSafeAreaView";
    }

    @Override // com.facebook.react.views.view.ReactViewManager, com.facebook.react.uimanager.ViewManager
    public SafeAreaView createViewInstance(ThemedReactContext context) {
        return new SafeAreaView(context);
    }

    @Override // com.facebook.react.uimanager.ViewGroupManager, com.facebook.react.uimanager.ViewManager
    public SafeAreaViewShadowNode createShadowNodeInstance() {
        return new SafeAreaViewShadowNode();
    }

    @Override // com.facebook.react.uimanager.ViewGroupManager, com.facebook.react.uimanager.ViewManager
    public Class<? extends LayoutShadowNode> getShadowNodeClass() {
        return SafeAreaViewShadowNode.class;
    }

    @ReactProp(name = "mode")
    public void setMode(SafeAreaView view, String mode) {
        if (ViewProps.PADDING.equals(mode)) {
            view.setMode(SafeAreaViewMode.PADDING);
        } else if (!ViewProps.MARGIN.equals(mode)) {
        } else {
            view.setMode(SafeAreaViewMode.MARGIN);
        }
    }

    @ReactProp(name = "edges")
    public void setEdges(SafeAreaView view, ReadableArray propList) {
        EnumSet<SafeAreaViewEdges> noneOf = EnumSet.noneOf(SafeAreaViewEdges.class);
        if (propList != null) {
            for (int i = 0; i < propList.size(); i++) {
                String string = propList.getString(i);
                if (ViewProps.TOP.equals(string)) {
                    noneOf.add(SafeAreaViewEdges.TOP);
                } else if (ViewProps.RIGHT.equals(string)) {
                    noneOf.add(SafeAreaViewEdges.RIGHT);
                } else if (ViewProps.BOTTOM.equals(string)) {
                    noneOf.add(SafeAreaViewEdges.BOTTOM);
                } else if (ViewProps.LEFT.equals(string)) {
                    noneOf.add(SafeAreaViewEdges.LEFT);
                }
            }
        }
        view.setEdges(noneOf);
    }
}
