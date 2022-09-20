package com.facebook.react.uimanager;

import android.view.View;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
/* loaded from: classes.dex */
public abstract class BaseViewManagerAdapter<T extends View> implements BaseViewManagerInterface<T> {
    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    public void setAccessibilityActions(T view, ReadableArray accessibilityActions) {
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    public void setAccessibilityHint(T view, String accessibilityHint) {
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    public void setAccessibilityLabel(T view, String accessibilityLabel) {
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    public void setAccessibilityLiveRegion(T view, String liveRegion) {
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    public void setAccessibilityRole(T view, String accessibilityRole) {
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    public void setBackgroundColor(T view, int backgroundColor) {
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    public void setBorderBottomLeftRadius(T view, float borderRadius) {
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    public void setBorderBottomRightRadius(T view, float borderRadius) {
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    public void setBorderRadius(T view, float borderRadius) {
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    public void setBorderTopLeftRadius(T view, float borderRadius) {
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    public void setBorderTopRightRadius(T view, float borderRadius) {
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    public void setElevation(T view, float elevation) {
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    public void setImportantForAccessibility(T view, String importantForAccessibility) {
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    public void setNativeId(T view, String nativeId) {
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    public void setOpacity(T view, float opacity) {
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    public void setRenderToHardwareTexture(T view, boolean useHWTexture) {
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    public void setRotation(T view, float rotation) {
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    public void setScaleX(T view, float scaleX) {
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    public void setScaleY(T view, float scaleY) {
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    public void setShadowColor(T view, int shadowColor) {
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    public void setTestId(T view, String testId) {
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    public void setTransform(T view, ReadableArray matrix) {
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    public void setTranslateX(T view, float translateX) {
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    public void setTranslateY(T view, float translateY) {
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    public void setViewState(T view, ReadableMap accessibilityState) {
    }

    @Override // com.facebook.react.uimanager.BaseViewManagerInterface
    public void setZIndex(T view, float zIndex) {
    }
}
