package com.facebook.react.uimanager.events;

import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
@Deprecated
/* loaded from: classes.dex */
public interface RCTEventEmitter extends JavaScriptModule {
    @Deprecated
    void receiveEvent(int targetTag, String eventName, WritableMap event);

    void receiveTouches(String eventName, WritableArray touches, WritableArray changedIndices);
}
