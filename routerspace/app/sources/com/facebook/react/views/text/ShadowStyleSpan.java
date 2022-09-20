package com.facebook.react.views.text;

import android.text.TextPaint;
import android.text.style.CharacterStyle;
/* loaded from: classes.dex */
public class ShadowStyleSpan extends CharacterStyle implements ReactSpan {
    private final int mColor;
    private final float mDx;
    private final float mDy;
    private final float mRadius;

    public ShadowStyleSpan(float dx, float dy, float radius, int color) {
        this.mDx = dx;
        this.mDy = dy;
        this.mRadius = radius;
        this.mColor = color;
    }

    @Override // android.text.style.CharacterStyle
    public void updateDrawState(TextPaint textPaint) {
        textPaint.setShadowLayer(this.mRadius, this.mDx, this.mDy, this.mColor);
    }
}
