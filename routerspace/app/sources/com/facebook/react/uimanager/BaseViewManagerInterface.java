package com.facebook.react.uimanager;

import android.view.View;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
/* loaded from: classes.dex */
public interface BaseViewManagerInterface<T extends View> {
    void setAccessibilityActions(T view, ReadableArray accessibilityActions);

    void setAccessibilityHint(T view, String accessibilityHint);

    void setAccessibilityLabel(T view, String accessibilityLabel);

    void setAccessibilityLiveRegion(T view, String liveRegion);

    void setAccessibilityRole(T view, String accessibilityRole);

    void setBackgroundColor(T view, int backgroundColor);

    void setBorderBottomLeftRadius(T view, float borderRadius);

    void setBorderBottomRightRadius(T view, float borderRadius);

    void setBorderRadius(T view, float borderRadius);

    void setBorderTopLeftRadius(T view, float borderRadius);

    void setBorderTopRightRadius(T view, float borderRadius);

    void setElevation(T view, float elevation);

    void setImportantForAccessibility(T view, String importantForAccessibility);

    void setNativeId(T view, String nativeId);

    void setOpacity(T view, float opacity);

    void setRenderToHardwareTexture(T view, boolean useHWTexture);

    void setRotation(T view, float rotation);

    void setScaleX(T view, float scaleX);

    void setScaleY(T view, float scaleY);

    void setShadowColor(T view, int shadowColor);

    void setTestId(T view, String testId);

    void setTransform(T view, ReadableArray matrix);

    void setTranslateX(T view, float translateX);

    void setTranslateY(T view, float translateY);

    void setViewState(T view, ReadableMap accessibilityState);

    void setZIndex(T view, float zIndex);
}
