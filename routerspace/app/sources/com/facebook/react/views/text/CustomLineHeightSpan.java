package com.facebook.react.views.text;

import android.graphics.Paint;
import android.text.style.LineHeightSpan;
/* loaded from: classes.dex */
public class CustomLineHeightSpan implements LineHeightSpan, ReactSpan {
    private final int mHeight;

    public CustomLineHeightSpan(float height) {
        this.mHeight = (int) Math.ceil(height);
    }

    @Override // android.text.style.LineHeightSpan
    public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int v, Paint.FontMetricsInt fm) {
        int i = fm.descent;
        int i2 = this.mHeight;
        if (i > i2) {
            int min = Math.min(i2, fm.descent);
            fm.descent = min;
            fm.bottom = min;
            fm.ascent = 0;
            fm.top = 0;
        } else if ((-fm.ascent) + fm.descent > this.mHeight) {
            fm.bottom = fm.descent;
            int i3 = (-this.mHeight) + fm.descent;
            fm.ascent = i3;
            fm.top = i3;
        } else if ((-fm.ascent) + fm.bottom > this.mHeight) {
            fm.top = fm.ascent;
            fm.bottom = fm.ascent + this.mHeight;
        } else {
            int i4 = (-fm.top) + fm.bottom;
            int i5 = this.mHeight;
            if (i4 > i5) {
                fm.top = fm.bottom - this.mHeight;
                return;
            }
            double d = (i5 - ((-fm.top) + fm.bottom)) / 2.0f;
            fm.top = (int) (fm.top - Math.ceil(d));
            fm.bottom = (int) (fm.bottom + Math.floor(d));
            fm.ascent = fm.top;
            fm.descent = fm.bottom;
        }
    }
}
