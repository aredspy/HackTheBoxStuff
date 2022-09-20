package com.th3rdwave.safeareacontext;

import com.facebook.react.bridge.Dynamic;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.uimanager.LayoutShadowNode;
import com.facebook.react.uimanager.NativeViewHierarchyOptimizer;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.ViewProps;
import com.facebook.react.uimanager.annotations.ReactPropGroup;
import java.util.EnumSet;
/* loaded from: classes.dex */
public class SafeAreaViewShadowNode extends LayoutShadowNode {
    private SafeAreaViewLocalData mLocalData;
    private boolean mNeedsUpdate = false;
    private float[] mPaddings = new float[ViewProps.PADDING_MARGIN_SPACING_TYPES.length];
    private float[] mMargins = new float[ViewProps.PADDING_MARGIN_SPACING_TYPES.length];

    public SafeAreaViewShadowNode() {
        for (int i = 0; i < ViewProps.PADDING_MARGIN_SPACING_TYPES.length; i++) {
            this.mPaddings[i] = Float.NaN;
            this.mMargins[i] = Float.NaN;
        }
    }

    private void updateInsets() {
        float f;
        float f2;
        float f3;
        SafeAreaViewLocalData safeAreaViewLocalData = this.mLocalData;
        if (safeAreaViewLocalData == null) {
            return;
        }
        float[] fArr = safeAreaViewLocalData.getMode() == SafeAreaViewMode.PADDING ? this.mPaddings : this.mMargins;
        float f4 = fArr[8];
        float f5 = 0.0f;
        if (!Float.isNaN(f4)) {
            f3 = f4;
            f2 = f3;
            f = f2;
        } else {
            f4 = 0.0f;
            f3 = 0.0f;
            f2 = 0.0f;
            f = 0.0f;
        }
        float f6 = fArr[7];
        if (!Float.isNaN(f6)) {
            f4 = f6;
            f2 = f4;
        }
        float f7 = fArr[6];
        if (!Float.isNaN(f7)) {
            f3 = f7;
            f = f3;
        }
        float f8 = fArr[1];
        if (!Float.isNaN(f8)) {
            f4 = f8;
        }
        float f9 = fArr[2];
        if (!Float.isNaN(f9)) {
            f3 = f9;
        }
        float f10 = fArr[3];
        if (!Float.isNaN(f10)) {
            f2 = f10;
        }
        float f11 = fArr[0];
        if (!Float.isNaN(f11)) {
            f = f11;
        }
        float pixelFromDIP = PixelUtil.toPixelFromDIP(f4);
        float pixelFromDIP2 = PixelUtil.toPixelFromDIP(f3);
        float pixelFromDIP3 = PixelUtil.toPixelFromDIP(f2);
        float pixelFromDIP4 = PixelUtil.toPixelFromDIP(f);
        EnumSet<SafeAreaViewEdges> edges = this.mLocalData.getEdges();
        EdgeInsets insets = this.mLocalData.getInsets();
        float f12 = edges.contains(SafeAreaViewEdges.TOP) ? insets.top : 0.0f;
        float f13 = edges.contains(SafeAreaViewEdges.RIGHT) ? insets.right : 0.0f;
        float f14 = edges.contains(SafeAreaViewEdges.BOTTOM) ? insets.bottom : 0.0f;
        if (edges.contains(SafeAreaViewEdges.LEFT)) {
            f5 = insets.left;
        }
        if (this.mLocalData.getMode() == SafeAreaViewMode.PADDING) {
            super.setPadding(1, f12 + pixelFromDIP);
            super.setPadding(2, f13 + pixelFromDIP2);
            super.setPadding(3, f14 + pixelFromDIP3);
            super.setPadding(0, f5 + pixelFromDIP4);
            return;
        }
        super.setMargin(1, f12 + pixelFromDIP);
        super.setMargin(2, f13 + pixelFromDIP2);
        super.setMargin(3, f14 + pixelFromDIP3);
        super.setMargin(0, f5 + pixelFromDIP4);
    }

    private void resetInsets(SafeAreaViewMode mode) {
        if (mode == SafeAreaViewMode.PADDING) {
            super.setPadding(1, this.mPaddings[1]);
            super.setPadding(2, this.mPaddings[1]);
            super.setPadding(3, this.mPaddings[3]);
            super.setPadding(0, this.mPaddings[0]);
            return;
        }
        super.setMargin(1, this.mMargins[1]);
        super.setMargin(2, this.mMargins[1]);
        super.setMargin(3, this.mMargins[3]);
        super.setMargin(0, this.mMargins[0]);
    }

    public void onBeforeLayout() {
        if (this.mNeedsUpdate) {
            this.mNeedsUpdate = false;
            updateInsets();
        }
    }

    @Override // com.facebook.react.uimanager.ReactShadowNodeImpl, com.facebook.react.uimanager.ReactShadowNode
    public void onBeforeLayout(NativeViewHierarchyOptimizer nativeViewHierarchyOptimizer) {
        if (this.mNeedsUpdate) {
            this.mNeedsUpdate = false;
            updateInsets();
        }
    }

    @Override // com.facebook.react.uimanager.ReactShadowNodeImpl, com.facebook.react.uimanager.ReactShadowNode
    public void setLocalData(Object data) {
        if (!(data instanceof SafeAreaViewLocalData)) {
            return;
        }
        SafeAreaViewLocalData safeAreaViewLocalData = (SafeAreaViewLocalData) data;
        SafeAreaViewLocalData safeAreaViewLocalData2 = this.mLocalData;
        if (safeAreaViewLocalData2 != null && safeAreaViewLocalData2.getMode() != safeAreaViewLocalData.getMode()) {
            resetInsets(this.mLocalData.getMode());
        }
        this.mLocalData = safeAreaViewLocalData;
        this.mNeedsUpdate = false;
        updateInsets();
    }

    @Override // com.facebook.react.uimanager.LayoutShadowNode
    @ReactPropGroup(names = {ViewProps.PADDING, ViewProps.PADDING_VERTICAL, ViewProps.PADDING_HORIZONTAL, ViewProps.PADDING_START, ViewProps.PADDING_END, ViewProps.PADDING_TOP, ViewProps.PADDING_BOTTOM, ViewProps.PADDING_LEFT, ViewProps.PADDING_RIGHT})
    public void setPaddings(int index, Dynamic padding) {
        this.mPaddings[ViewProps.PADDING_MARGIN_SPACING_TYPES[index]] = padding.getType() == ReadableType.Number ? (float) padding.asDouble() : Float.NaN;
        super.setPaddings(index, padding);
        this.mNeedsUpdate = true;
    }

    @Override // com.facebook.react.uimanager.LayoutShadowNode
    @ReactPropGroup(names = {ViewProps.MARGIN, ViewProps.MARGIN_VERTICAL, ViewProps.MARGIN_HORIZONTAL, ViewProps.MARGIN_START, ViewProps.MARGIN_END, ViewProps.MARGIN_TOP, ViewProps.MARGIN_BOTTOM, ViewProps.MARGIN_LEFT, ViewProps.MARGIN_RIGHT})
    public void setMargins(int index, Dynamic margin) {
        this.mMargins[ViewProps.PADDING_MARGIN_SPACING_TYPES[index]] = margin.getType() == ReadableType.Number ? (float) margin.asDouble() : Float.NaN;
        super.setMargins(index, margin);
        this.mNeedsUpdate = true;
    }
}
