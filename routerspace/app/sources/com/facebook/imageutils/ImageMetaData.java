package com.facebook.imageutils;

import android.graphics.ColorSpace;
import android.util.Pair;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class ImageMetaData {
    @Nullable
    private final ColorSpace mColorSpace;
    @Nullable
    private final Pair<Integer, Integer> mDimensions;

    public ImageMetaData(int width, int height, @Nullable ColorSpace colorSpace) {
        this.mDimensions = (width == -1 || height == -1) ? null : new Pair<>(Integer.valueOf(width), Integer.valueOf(height));
        this.mColorSpace = colorSpace;
    }

    @Nullable
    public Pair<Integer, Integer> getDimensions() {
        return this.mDimensions;
    }

    @Nullable
    public ColorSpace getColorSpace() {
        return this.mColorSpace;
    }
}
