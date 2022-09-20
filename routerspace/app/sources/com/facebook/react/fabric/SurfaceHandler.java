package com.facebook.react.fabric;

import com.facebook.react.bridge.NativeMap;
/* loaded from: classes.dex */
public interface SurfaceHandler {
    String getModuleName();

    int getSurfaceId();

    boolean isRunning();

    void setLayoutConstraints(int widthMeasureSpec, int heightMeasureSpec, int offsetX, int offsetY, boolean doLeftAndRightSwapInRTL, boolean isRTL, float pixelDensity);

    void setMountable(boolean mountable);

    void setProps(NativeMap props);

    void setSurfaceId(int surfaceId);

    void start();

    void stop();
}
