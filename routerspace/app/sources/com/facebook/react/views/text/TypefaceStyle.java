package com.facebook.react.views.text;

import android.graphics.Typeface;
import android.os.Build;
/* loaded from: classes.dex */
class TypefaceStyle {
    public static final int BOLD = 700;
    private static final int MAX_WEIGHT = 1000;
    private static final int MIN_WEIGHT = 1;
    public static final int NORMAL = 400;
    private final boolean mItalic;
    private final int mWeight;

    public TypefaceStyle(int weight, boolean italic) {
        this.mItalic = italic;
        this.mWeight = weight == -1 ? 400 : weight;
    }

    public TypefaceStyle(int style) {
        boolean z = false;
        style = style == -1 ? 0 : style;
        this.mItalic = (style & 2) != 0 ? true : z;
        this.mWeight = (style & 1) != 0 ? BOLD : 400;
    }

    public TypefaceStyle(int style, int weight) {
        boolean z = false;
        style = style == -1 ? 0 : style;
        this.mItalic = (style & 2) != 0 ? true : z;
        this.mWeight = weight == -1 ? (style & 1) != 0 ? BOLD : 400 : weight;
    }

    public int getNearestStyle() {
        return this.mWeight < 700 ? this.mItalic ? 2 : 0 : this.mItalic ? 3 : 1;
    }

    public Typeface apply(Typeface typeface) {
        if (Build.VERSION.SDK_INT < 28) {
            return Typeface.create(typeface, getNearestStyle());
        }
        return Typeface.create(typeface, this.mWeight, this.mItalic);
    }
}
