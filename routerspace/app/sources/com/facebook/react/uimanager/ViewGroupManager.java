package com.facebook.react.uimanager;

import android.view.View;
import android.view.ViewGroup;
import com.facebook.react.bridge.UiThreadUtil;
import java.util.List;
import java.util.WeakHashMap;
/* loaded from: classes.dex */
public abstract class ViewGroupManager<T extends ViewGroup> extends BaseViewManager<T, LayoutShadowNode> implements IViewManagerWithChildren {
    private static WeakHashMap<View, Integer> mZIndexHash = new WeakHashMap<>();

    @Override // com.facebook.react.uimanager.IViewManagerWithChildren
    public boolean needsCustomLayoutForChildren() {
        return false;
    }

    public boolean shouldPromoteGrandchildren() {
        return false;
    }

    public void updateExtraData(T root, Object extraData) {
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.facebook.react.uimanager.ViewManager
    public /* bridge */ /* synthetic */ void updateExtraData(View root, Object extraData) {
        updateExtraData((ViewGroupManager<T>) ((ViewGroup) root), extraData);
    }

    @Override // com.facebook.react.uimanager.ViewManager
    public LayoutShadowNode createShadowNodeInstance() {
        return new LayoutShadowNode();
    }

    @Override // com.facebook.react.uimanager.ViewManager
    public Class<? extends LayoutShadowNode> getShadowNodeClass() {
        return LayoutShadowNode.class;
    }

    public void addView(T parent, View child, int index) {
        parent.addView(child, index);
    }

    public void addViews(T parent, List<View> views) {
        UiThreadUtil.assertOnUiThread();
        int size = views.size();
        for (int i = 0; i < size; i++) {
            addView(parent, views.get(i), i);
        }
    }

    public static void setViewZIndex(View view, int zIndex) {
        mZIndexHash.put(view, Integer.valueOf(zIndex));
    }

    public static Integer getViewZIndex(View view) {
        return mZIndexHash.get(view);
    }

    public int getChildCount(T parent) {
        return parent.getChildCount();
    }

    public View getChildAt(T parent, int index) {
        return parent.getChildAt(index);
    }

    public void removeViewAt(T parent, int index) {
        UiThreadUtil.assertOnUiThread();
        parent.removeViewAt(index);
    }

    public void removeView(T parent, View view) {
        UiThreadUtil.assertOnUiThread();
        for (int i = 0; i < getChildCount(parent); i++) {
            if (getChildAt(parent, i) == view) {
                removeViewAt(parent, i);
                return;
            }
        }
    }

    public void removeAllViews(T parent) {
        UiThreadUtil.assertOnUiThread();
        for (int childCount = getChildCount(parent) - 1; childCount >= 0; childCount--) {
            removeViewAt(parent, childCount);
        }
    }
}
