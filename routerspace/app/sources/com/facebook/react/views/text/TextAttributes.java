package com.facebook.react.views.text;

import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.uimanager.PixelUtil;
/* loaded from: classes.dex */
public class TextAttributes {
    public static final float DEFAULT_MAX_FONT_SIZE_MULTIPLIER = 0.0f;
    private boolean mAllowFontScaling = true;
    private float mFontSize = Float.NaN;
    private float mLineHeight = Float.NaN;
    private float mLetterSpacing = Float.NaN;
    private float mMaxFontSizeMultiplier = Float.NaN;
    private float mHeightOfTallestInlineViewOrImage = Float.NaN;
    private TextTransform mTextTransform = TextTransform.UNSET;

    public TextAttributes applyChild(TextAttributes child) {
        TextAttributes textAttributes = new TextAttributes();
        textAttributes.mAllowFontScaling = this.mAllowFontScaling;
        textAttributes.mFontSize = !Float.isNaN(child.mFontSize) ? child.mFontSize : this.mFontSize;
        textAttributes.mLineHeight = !Float.isNaN(child.mLineHeight) ? child.mLineHeight : this.mLineHeight;
        textAttributes.mLetterSpacing = !Float.isNaN(child.mLetterSpacing) ? child.mLetterSpacing : this.mLetterSpacing;
        textAttributes.mMaxFontSizeMultiplier = !Float.isNaN(child.mMaxFontSizeMultiplier) ? child.mMaxFontSizeMultiplier : this.mMaxFontSizeMultiplier;
        textAttributes.mHeightOfTallestInlineViewOrImage = !Float.isNaN(child.mHeightOfTallestInlineViewOrImage) ? child.mHeightOfTallestInlineViewOrImage : this.mHeightOfTallestInlineViewOrImage;
        textAttributes.mTextTransform = child.mTextTransform != TextTransform.UNSET ? child.mTextTransform : this.mTextTransform;
        return textAttributes;
    }

    public boolean getAllowFontScaling() {
        return this.mAllowFontScaling;
    }

    public void setAllowFontScaling(boolean value) {
        this.mAllowFontScaling = value;
    }

    public float getFontSize() {
        return this.mFontSize;
    }

    public void setFontSize(float value) {
        this.mFontSize = value;
    }

    public float getLineHeight() {
        return this.mLineHeight;
    }

    public void setLineHeight(float value) {
        this.mLineHeight = value;
    }

    public float getLetterSpacing() {
        return this.mLetterSpacing;
    }

    public void setLetterSpacing(float value) {
        this.mLetterSpacing = value;
    }

    public float getMaxFontSizeMultiplier() {
        return this.mMaxFontSizeMultiplier;
    }

    public void setMaxFontSizeMultiplier(float maxFontSizeMultiplier) {
        if (maxFontSizeMultiplier != 0.0f && maxFontSizeMultiplier < 1.0f) {
            throw new JSApplicationIllegalArgumentException("maxFontSizeMultiplier must be NaN, 0, or >= 1");
        }
        this.mMaxFontSizeMultiplier = maxFontSizeMultiplier;
    }

    public float getHeightOfTallestInlineViewOrImage() {
        return this.mHeightOfTallestInlineViewOrImage;
    }

    public void setHeightOfTallestInlineViewOrImage(float value) {
        this.mHeightOfTallestInlineViewOrImage = value;
    }

    public TextTransform getTextTransform() {
        return this.mTextTransform;
    }

    public void setTextTransform(TextTransform textTransform) {
        this.mTextTransform = textTransform;
    }

    public int getEffectiveFontSize() {
        double d;
        float f = !Float.isNaN(this.mFontSize) ? this.mFontSize : 14.0f;
        if (this.mAllowFontScaling) {
            d = Math.ceil(PixelUtil.toPixelFromSP(f, getEffectiveMaxFontSizeMultiplier()));
        } else {
            d = Math.ceil(PixelUtil.toPixelFromDIP(f));
        }
        return (int) d;
    }

    public float getEffectiveLineHeight() {
        float f;
        if (Float.isNaN(this.mLineHeight)) {
            return Float.NaN;
        }
        if (this.mAllowFontScaling) {
            f = PixelUtil.toPixelFromSP(this.mLineHeight, getEffectiveMaxFontSizeMultiplier());
        } else {
            f = PixelUtil.toPixelFromDIP(this.mLineHeight);
        }
        return !Float.isNaN(this.mHeightOfTallestInlineViewOrImage) && (this.mHeightOfTallestInlineViewOrImage > f ? 1 : (this.mHeightOfTallestInlineViewOrImage == f ? 0 : -1)) > 0 ? this.mHeightOfTallestInlineViewOrImage : f;
    }

    public float getEffectiveLetterSpacing() {
        float f;
        if (Float.isNaN(this.mLetterSpacing)) {
            return Float.NaN;
        }
        if (this.mAllowFontScaling) {
            f = PixelUtil.toPixelFromSP(this.mLetterSpacing, getEffectiveMaxFontSizeMultiplier());
        } else {
            f = PixelUtil.toPixelFromDIP(this.mLetterSpacing);
        }
        return f / getEffectiveFontSize();
    }

    public float getEffectiveMaxFontSizeMultiplier() {
        if (!Float.isNaN(this.mMaxFontSizeMultiplier)) {
            return this.mMaxFontSizeMultiplier;
        }
        return 0.0f;
    }

    public String toString() {
        return "TextAttributes {\n  getAllowFontScaling(): " + getAllowFontScaling() + "\n  getFontSize(): " + getFontSize() + "\n  getEffectiveFontSize(): " + getEffectiveFontSize() + "\n  getHeightOfTallestInlineViewOrImage(): " + getHeightOfTallestInlineViewOrImage() + "\n  getLetterSpacing(): " + getLetterSpacing() + "\n  getEffectiveLetterSpacing(): " + getEffectiveLetterSpacing() + "\n  getLineHeight(): " + getLineHeight() + "\n  getEffectiveLineHeight(): " + getEffectiveLineHeight() + "\n  getTextTransform(): " + getTextTransform() + "\n  getMaxFontSizeMultiplier(): " + getMaxFontSizeMultiplier() + "\n  getEffectiveMaxFontSizeMultiplier(): " + getEffectiveMaxFontSizeMultiplier() + "\n}";
    }
}
