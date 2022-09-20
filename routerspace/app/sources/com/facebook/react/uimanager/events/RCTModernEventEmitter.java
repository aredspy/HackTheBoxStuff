package com.facebook.react.uimanager.events;

import com.facebook.react.bridge.WritableMap;
/* loaded from: classes.dex */
public interface RCTModernEventEmitter extends RCTEventEmitter {
    void receiveEvent(int surfaceId, int targetTag, String eventName, WritableMap event);

    void receiveEvent(int surfaceId, int targetTag, String eventName, boolean canCoalesceEvent, int customCoalesceKey, WritableMap event);
}
