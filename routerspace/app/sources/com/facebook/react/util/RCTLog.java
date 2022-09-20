package com.facebook.react.util;

import com.facebook.react.bridge.JavaScriptModule;
/* loaded from: classes.dex */
public interface RCTLog extends JavaScriptModule {
    void logIfNoNativeHook(String level, String message);
}
