package com.facebook.drawee.drawable;
/* loaded from: classes.dex */
public interface Rounded {
    int getBorderColor();

    float getBorderWidth();

    float getPadding();

    boolean getPaintFilterBitmap();

    float[] getRadii();

    boolean getScaleDownInsideBorders();

    boolean isCircle();

    void setBorder(int color, float width);

    void setCircle(boolean isCircle);

    void setPadding(float padding);

    void setPaintFilterBitmap(boolean paintFilterBitmap);

    void setRadii(float[] radii);

    void setRadius(float radius);

    void setScaleDownInsideBorders(boolean scaleDownInsideBorders);
}
