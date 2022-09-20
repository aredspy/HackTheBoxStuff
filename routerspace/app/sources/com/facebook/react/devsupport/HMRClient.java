package com.facebook.react.devsupport;

import com.facebook.react.bridge.JavaScriptModule;
/* loaded from: classes.dex */
public interface HMRClient extends JavaScriptModule {
    void disable();

    void enable();

    void registerBundle(String bundleUrl);

    void setup(String platform, String bundleEntry, String host, int port, boolean isEnabled);
}
