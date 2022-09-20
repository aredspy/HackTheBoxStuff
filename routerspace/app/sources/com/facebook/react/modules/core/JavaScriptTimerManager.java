package com.facebook.react.modules.core;

import com.facebook.react.bridge.WritableArray;
/* loaded from: classes.dex */
public interface JavaScriptTimerManager {
    void callIdleCallbacks(double frameTime);

    void callTimers(WritableArray timerIDs);

    void emitTimeDriftWarning(String warningMessage);
}
