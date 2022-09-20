package com.facebook.react.fabric;

import com.facebook.jni.HybridData;
import com.facebook.react.bridge.NativeMap;
import com.facebook.react.fabric.mounting.LayoutMetricsConversions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes.dex */
public class SurfaceHandlerBinding implements SurfaceHandler {
    public static final int DISPLAY_MODE_HIDDEN = 2;
    public static final int DISPLAY_MODE_SUSPENDED = 1;
    public static final int DISPLAY_MODE_VISIBLE = 0;
    private static final int NO_SURFACE_ID = 0;
    private final HybridData mHybridData;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface DisplayModeTypeDef {
    }

    private native String getModuleNameNative();

    private native int getSurfaceIdNative();

    private static native HybridData initHybrid(int surfaceId, String moduleName);

    private native boolean isRunningNative();

    private native void setDisplayModeNative(int mode);

    private native void setLayoutConstraintsNative(float minWidth, float maxWidth, float minHeight, float maxHeight, float offsetX, float offsetY, boolean doLeftAndRightSwapInRTL, boolean isRTL, float pixelDensity);

    private native void setPropsNative(NativeMap props);

    private native void setSurfaceIdNative(int surfaceId);

    private native void startNative();

    private native void stopNative();

    static {
        FabricSoLoader.staticInit();
    }

    public SurfaceHandlerBinding(String moduleName) {
        this.mHybridData = initHybrid(0, moduleName);
    }

    @Override // com.facebook.react.fabric.SurfaceHandler
    public int getSurfaceId() {
        return getSurfaceIdNative();
    }

    @Override // com.facebook.react.fabric.SurfaceHandler
    public void setSurfaceId(int surfaceId) {
        setSurfaceIdNative(surfaceId);
    }

    @Override // com.facebook.react.fabric.SurfaceHandler
    public String getModuleName() {
        return getModuleNameNative();
    }

    @Override // com.facebook.react.fabric.SurfaceHandler
    public void start() {
        startNative();
    }

    @Override // com.facebook.react.fabric.SurfaceHandler
    public void stop() {
        stopNative();
    }

    @Override // com.facebook.react.fabric.SurfaceHandler
    public boolean isRunning() {
        return isRunningNative();
    }

    @Override // com.facebook.react.fabric.SurfaceHandler
    public void setLayoutConstraints(int widthMeasureSpec, int heightMeasureSpec, int offsetX, int offsetY, boolean doLeftAndRightSwapInRTL, boolean isRTL, float pixelDensity) {
        setLayoutConstraintsNative(LayoutMetricsConversions.getMinSize(widthMeasureSpec) / pixelDensity, LayoutMetricsConversions.getMaxSize(widthMeasureSpec) / pixelDensity, LayoutMetricsConversions.getMinSize(heightMeasureSpec) / pixelDensity, LayoutMetricsConversions.getMaxSize(heightMeasureSpec) / pixelDensity, offsetX / pixelDensity, offsetY / pixelDensity, doLeftAndRightSwapInRTL, isRTL, pixelDensity);
    }

    @Override // com.facebook.react.fabric.SurfaceHandler
    public void setProps(NativeMap props) {
        setPropsNative(props);
    }

    @Override // com.facebook.react.fabric.SurfaceHandler
    public void setMountable(boolean mountable) {
        setDisplayModeNative(!mountable ? 1 : 0);
    }
}
