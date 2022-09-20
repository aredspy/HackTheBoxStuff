package com.facebook.react.views.text;

import android.content.res.AssetManager;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;
/* loaded from: classes.dex */
public class CustomStyleSpan extends MetricAffectingSpan implements ReactSpan {
    private final AssetManager mAssetManager;
    private final String mFeatureSettings;
    private final String mFontFamily;
    private final int mStyle;
    private final int mWeight;

    public CustomStyleSpan(int fontStyle, int fontWeight, String fontFeatureSettings, String fontFamily, AssetManager assetManager) {
        this.mStyle = fontStyle;
        this.mWeight = fontWeight;
        this.mFeatureSettings = fontFeatureSettings;
        this.mFontFamily = fontFamily;
        this.mAssetManager = assetManager;
    }

    @Override // android.text.style.CharacterStyle
    public void updateDrawState(TextPaint ds) {
        apply(ds, this.mStyle, this.mWeight, this.mFeatureSettings, this.mFontFamily, this.mAssetManager);
    }

    @Override // android.text.style.MetricAffectingSpan
    public void updateMeasureState(TextPaint paint) {
        apply(paint, this.mStyle, this.mWeight, this.mFeatureSettings, this.mFontFamily, this.mAssetManager);
    }

    public int getStyle() {
        int i = this.mStyle;
        if (i == -1) {
            return 0;
        }
        return i;
    }

    public int getWeight() {
        int i = this.mWeight;
        if (i == -1) {
            return 400;
        }
        return i;
    }

    public String getFontFamily() {
        return this.mFontFamily;
    }

    private static void apply(Paint paint, int style, int weight, String fontFeatureSettings, String family, AssetManager assetManager) {
        Typeface applyStyles = ReactTypefaceUtils.applyStyles(paint.getTypeface(), style, weight, family, assetManager);
        paint.setFontFeatureSettings(fontFeatureSettings);
        paint.setTypeface(applyStyles);
        paint.setSubpixelText(true);
    }
}
