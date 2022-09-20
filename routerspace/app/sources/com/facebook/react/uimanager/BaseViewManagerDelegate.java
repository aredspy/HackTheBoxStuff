package com.facebook.react.uimanager;

import android.view.View;
import com.facebook.react.bridge.ColorPropConverter;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.BaseViewManagerInterface;
/* loaded from: classes.dex */
public abstract class BaseViewManagerDelegate<T extends View, U extends BaseViewManagerInterface<T>> implements ViewManagerDelegate<T> {
    protected final U mViewManager;

    @Override // com.facebook.react.uimanager.ViewManagerDelegate
    public void receiveCommand(T view, String commandName, ReadableArray args) {
    }

    public BaseViewManagerDelegate(U viewManager) {
        this.mViewManager = viewManager;
    }

    @Override // com.facebook.react.uimanager.ViewManagerDelegate
    public void setProperty(T view, String propName, Object value) {
        propName.hashCode();
        int i = 0;
        int i2 = 0;
        boolean z = false;
        char c = 65535;
        switch (propName.hashCode()) {
            case -1721943862:
                if (propName.equals(ViewProps.TRANSLATE_X)) {
                    c = 0;
                    break;
                }
                break;
            case -1721943861:
                if (propName.equals(ViewProps.TRANSLATE_Y)) {
                    c = 1;
                    break;
                }
                break;
            case -1589741021:
                if (propName.equals(ViewProps.SHADOW_COLOR)) {
                    c = 2;
                    break;
                }
                break;
            case -1267206133:
                if (propName.equals(ViewProps.OPACITY)) {
                    c = 3;
                    break;
                }
                break;
            case -1228066334:
                if (propName.equals(ViewProps.BORDER_TOP_LEFT_RADIUS)) {
                    c = 4;
                    break;
                }
                break;
            case -908189618:
                if (propName.equals(ViewProps.SCALE_X)) {
                    c = 5;
                    break;
                }
                break;
            case -908189617:
                if (propName.equals(ViewProps.SCALE_Y)) {
                    c = 6;
                    break;
                }
                break;
            case -877170387:
                if (propName.equals(ViewProps.TEST_ID)) {
                    c = 7;
                    break;
                }
                break;
            case -731417480:
                if (propName.equals(ViewProps.Z_INDEX)) {
                    c = '\b';
                    break;
                }
                break;
            case -101663499:
                if (propName.equals(ViewProps.ACCESSIBILITY_HINT)) {
                    c = '\t';
                    break;
                }
                break;
            case -101359900:
                if (propName.equals(ViewProps.ACCESSIBILITY_ROLE)) {
                    c = '\n';
                    break;
                }
                break;
            case -80891667:
                if (propName.equals(ViewProps.RENDER_TO_HARDWARE_TEXTURE)) {
                    c = 11;
                    break;
                }
                break;
            case -40300674:
                if (propName.equals(ViewProps.ROTATION)) {
                    c = '\f';
                    break;
                }
                break;
            case -4379043:
                if (propName.equals(ViewProps.ELEVATION)) {
                    c = '\r';
                    break;
                }
                break;
            case 36255470:
                if (propName.equals(ViewProps.ACCESSIBILITY_LIVE_REGION)) {
                    c = 14;
                    break;
                }
                break;
            case 333432965:
                if (propName.equals(ViewProps.BORDER_TOP_RIGHT_RADIUS)) {
                    c = 15;
                    break;
                }
                break;
            case 581268560:
                if (propName.equals(ViewProps.BORDER_BOTTOM_LEFT_RADIUS)) {
                    c = 16;
                    break;
                }
                break;
            case 588239831:
                if (propName.equals(ViewProps.BORDER_BOTTOM_RIGHT_RADIUS)) {
                    c = 17;
                    break;
                }
                break;
            case 746986311:
                if (propName.equals(ViewProps.IMPORTANT_FOR_ACCESSIBILITY)) {
                    c = 18;
                    break;
                }
                break;
            case 1052666732:
                if (propName.equals(ViewProps.TRANSFORM)) {
                    c = 19;
                    break;
                }
                break;
            case 1146842694:
                if (propName.equals(ViewProps.ACCESSIBILITY_LABEL)) {
                    c = 20;
                    break;
                }
                break;
            case 1153872867:
                if (propName.equals(ViewProps.ACCESSIBILITY_STATE)) {
                    c = 21;
                    break;
                }
                break;
            case 1287124693:
                if (propName.equals(ViewProps.BACKGROUND_COLOR)) {
                    c = 22;
                    break;
                }
                break;
            case 1349188574:
                if (propName.equals(ViewProps.BORDER_RADIUS)) {
                    c = 23;
                    break;
                }
                break;
            case 1505602511:
                if (propName.equals(ViewProps.ACCESSIBILITY_ACTIONS)) {
                    c = 24;
                    break;
                }
                break;
            case 2045685618:
                if (propName.equals(ViewProps.NATIVE_ID)) {
                    c = 25;
                    break;
                }
                break;
        }
        float f = 1.0f;
        float f2 = 0.0f;
        float f3 = Float.NaN;
        switch (c) {
            case 0:
                U u = this.mViewManager;
                if (value != null) {
                    f2 = ((Double) value).floatValue();
                }
                u.setTranslateX(view, f2);
                return;
            case 1:
                U u2 = this.mViewManager;
                if (value != null) {
                    f2 = ((Double) value).floatValue();
                }
                u2.setTranslateY(view, f2);
                return;
            case 2:
                U u3 = this.mViewManager;
                if (value != null) {
                    i = ColorPropConverter.getColor(value, view.getContext()).intValue();
                }
                u3.setShadowColor(view, i);
                return;
            case 3:
                U u4 = this.mViewManager;
                if (value != null) {
                    f = ((Double) value).floatValue();
                }
                u4.setOpacity(view, f);
                return;
            case 4:
                U u5 = this.mViewManager;
                if (value != null) {
                    f3 = ((Double) value).floatValue();
                }
                u5.setBorderTopLeftRadius(view, f3);
                return;
            case 5:
                U u6 = this.mViewManager;
                if (value != null) {
                    f = ((Double) value).floatValue();
                }
                u6.setScaleX(view, f);
                return;
            case 6:
                U u7 = this.mViewManager;
                if (value != null) {
                    f = ((Double) value).floatValue();
                }
                u7.setScaleY(view, f);
                return;
            case 7:
                this.mViewManager.setTestId(view, (String) value);
                return;
            case '\b':
                U u8 = this.mViewManager;
                if (value != null) {
                    f2 = ((Double) value).floatValue();
                }
                u8.setZIndex(view, f2);
                return;
            case '\t':
                this.mViewManager.setAccessibilityHint(view, (String) value);
                return;
            case '\n':
                this.mViewManager.setAccessibilityRole(view, (String) value);
                return;
            case 11:
                U u9 = this.mViewManager;
                if (value != null) {
                    z = ((Boolean) value).booleanValue();
                }
                u9.setRenderToHardwareTexture(view, z);
                return;
            case '\f':
                U u10 = this.mViewManager;
                if (value != null) {
                    f2 = ((Double) value).floatValue();
                }
                u10.setRotation(view, f2);
                return;
            case '\r':
                U u11 = this.mViewManager;
                if (value != null) {
                    f2 = ((Double) value).floatValue();
                }
                u11.setElevation(view, f2);
                return;
            case 14:
                this.mViewManager.setAccessibilityLiveRegion(view, (String) value);
                return;
            case 15:
                U u12 = this.mViewManager;
                if (value != null) {
                    f3 = ((Double) value).floatValue();
                }
                u12.setBorderTopRightRadius(view, f3);
                return;
            case 16:
                U u13 = this.mViewManager;
                if (value != null) {
                    f3 = ((Double) value).floatValue();
                }
                u13.setBorderBottomLeftRadius(view, f3);
                return;
            case 17:
                U u14 = this.mViewManager;
                if (value != null) {
                    f3 = ((Double) value).floatValue();
                }
                u14.setBorderBottomRightRadius(view, f3);
                return;
            case 18:
                this.mViewManager.setImportantForAccessibility(view, (String) value);
                return;
            case 19:
                this.mViewManager.setTransform(view, (ReadableArray) value);
                return;
            case 20:
                this.mViewManager.setAccessibilityLabel(view, (String) value);
                return;
            case 21:
                this.mViewManager.setViewState(view, (ReadableMap) value);
                return;
            case 22:
                U u15 = this.mViewManager;
                if (value != null) {
                    i2 = ColorPropConverter.getColor(value, view.getContext()).intValue();
                }
                u15.setBackgroundColor(view, i2);
                return;
            case 23:
                U u16 = this.mViewManager;
                if (value != null) {
                    f3 = ((Double) value).floatValue();
                }
                u16.setBorderRadius(view, f3);
                return;
            case 24:
                this.mViewManager.setAccessibilityActions(view, (ReadableArray) value);
                return;
            case 25:
                this.mViewManager.setNativeId(view, (String) value);
                return;
            default:
                return;
        }
    }
}
