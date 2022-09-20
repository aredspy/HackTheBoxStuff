package com.facebook.react.views.view;

import android.view.View;
import android.view.ViewGroup;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.uimanager.ReactClippingViewGroupHelper;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.views.view.ReactViewGroup;
/* loaded from: classes.dex */
public abstract class ReactClippingViewManager<T extends ReactViewGroup> extends ViewGroupManager<T> {
    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.facebook.react.uimanager.ViewGroupManager
    public /* bridge */ /* synthetic */ void addView(ViewGroup parent, View child, int index) {
        addView((ReactClippingViewManager<T>) ((ReactViewGroup) parent), child, index);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.facebook.react.uimanager.ViewGroupManager
    public /* bridge */ /* synthetic */ View getChildAt(ViewGroup parent, int index) {
        return getChildAt((ReactClippingViewManager<T>) ((ReactViewGroup) parent), index);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.facebook.react.uimanager.ViewGroupManager
    public /* bridge */ /* synthetic */ int getChildCount(ViewGroup parent) {
        return getChildCount((ReactClippingViewManager<T>) ((ReactViewGroup) parent));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.facebook.react.uimanager.ViewGroupManager
    public /* bridge */ /* synthetic */ void removeAllViews(ViewGroup parent) {
        removeAllViews((ReactClippingViewManager<T>) ((ReactViewGroup) parent));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.facebook.react.uimanager.ViewGroupManager
    public /* bridge */ /* synthetic */ void removeViewAt(ViewGroup parent, int index) {
        removeViewAt((ReactClippingViewManager<T>) ((ReactViewGroup) parent), index);
    }

    @ReactProp(name = ReactClippingViewGroupHelper.PROP_REMOVE_CLIPPED_SUBVIEWS)
    public void setRemoveClippedSubviews(T view, boolean removeClippedSubviews) {
        UiThreadUtil.assertOnUiThread();
        view.setRemoveClippedSubviews(removeClippedSubviews);
    }

    public void addView(T parent, View child, int index) {
        UiThreadUtil.assertOnUiThread();
        if (parent.getRemoveClippedSubviews()) {
            parent.addViewWithSubviewClippingEnabled(child, index);
        } else {
            parent.addView(child, index);
        }
    }

    public int getChildCount(T parent) {
        if (parent.getRemoveClippedSubviews()) {
            return parent.getAllChildrenCount();
        }
        return parent.getChildCount();
    }

    public View getChildAt(T parent, int index) {
        if (parent.getRemoveClippedSubviews()) {
            return parent.getChildAtWithSubviewClippingEnabled(index);
        }
        return parent.getChildAt(index);
    }

    public void removeViewAt(T parent, int index) {
        UiThreadUtil.assertOnUiThread();
        if (parent.getRemoveClippedSubviews()) {
            View childAt = getChildAt((ReactClippingViewManager<T>) parent, index);
            if (childAt.getParent() != null) {
                parent.removeView(childAt);
            }
            parent.removeViewWithSubviewClippingEnabled(childAt);
            return;
        }
        parent.removeViewAt(index);
    }

    public void removeAllViews(T parent) {
        UiThreadUtil.assertOnUiThread();
        if (parent.getRemoveClippedSubviews()) {
            parent.removeAllViewsWithSubviewClippingEnabled();
        } else {
            parent.removeAllViews();
        }
    }
}
