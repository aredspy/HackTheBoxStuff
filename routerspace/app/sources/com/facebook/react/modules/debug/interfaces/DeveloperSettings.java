package com.facebook.react.modules.debug.interfaces;
/* loaded from: classes.dex */
public interface DeveloperSettings {
    void addMenuItem(String title);

    boolean isAnimationFpsDebugEnabled();

    boolean isDeviceDebugEnabled();

    boolean isElementInspectorEnabled();

    boolean isFpsDebugEnabled();

    boolean isJSDevModeEnabled();

    boolean isJSMinifyEnabled();

    boolean isRemoteJSDebugEnabled();

    boolean isStartSamplingProfilerOnInit();

    void setRemoteJSDebugEnabled(boolean remoteJSDebugEnabled);
}
