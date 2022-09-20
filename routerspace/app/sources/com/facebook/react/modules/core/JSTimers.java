package com.facebook.react.modules.core;

import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.WritableArray;
/* loaded from: classes.dex */
public interface JSTimers extends JavaScriptModule {
    void callIdleCallbacks(double frameTime);

    void callTimers(WritableArray timerIDs);

    void emitTimeDriftWarning(String warningMessage);
}
