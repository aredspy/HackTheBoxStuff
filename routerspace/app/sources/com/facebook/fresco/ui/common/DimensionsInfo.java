package com.facebook.fresco.ui.common;
/* loaded from: classes.dex */
public class DimensionsInfo {
    private final int mDecodedImageHeight;
    private final int mDecodedImageWidth;
    private final int mEncodedImageHeight;
    private final int mEncodedImageWidth;
    private final String mScaleType;
    private final int mViewportHeight;
    private final int mViewportWidth;

    public DimensionsInfo(int viewportWidth, int viewportHeight, int encodedImageWidth, int encodedImageHeight, int decodedImageWidth, int decodedImageHeight, String scaleType) {
        this.mViewportWidth = viewportWidth;
        this.mViewportHeight = viewportHeight;
        this.mEncodedImageWidth = encodedImageWidth;
        this.mEncodedImageHeight = encodedImageHeight;
        this.mDecodedImageWidth = decodedImageWidth;
        this.mDecodedImageHeight = decodedImageHeight;
        this.mScaleType = scaleType;
    }

    public int getViewportWidth() {
        return this.mViewportWidth;
    }

    public int getViewportHeight() {
        return this.mViewportHeight;
    }

    public int getEncodedImageWidth() {
        return this.mEncodedImageWidth;
    }

    public int getEncodedImageHeight() {
        return this.mEncodedImageHeight;
    }

    public int getDecodedImageWidth() {
        return this.mDecodedImageWidth;
    }

    public int getDecodedImageHeight() {
        return this.mDecodedImageHeight;
    }

    public String getScaleType() {
        return this.mScaleType;
    }

    public String toString() {
        return "DimensionsInfo{mViewportWidth=" + this.mViewportWidth + ", mViewportHeight=" + this.mViewportHeight + ", mEncodedImageWidth=" + this.mEncodedImageWidth + ", mEncodedImageHeight=" + this.mEncodedImageHeight + ", mDecodedImageWidth=" + this.mDecodedImageWidth + ", mDecodedImageHeight=" + this.mDecodedImageHeight + ", mScaleType='" + this.mScaleType + "'}";
    }
}
