package com.facebook.react.views.swiperefresh;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.Event;
/* loaded from: classes.dex */
public class RefreshEvent extends Event<RefreshEvent> {
    @Override // com.facebook.react.uimanager.events.Event
    public String getEventName() {
        return "topRefresh";
    }

    @Deprecated
    protected RefreshEvent(int viewTag) {
        this(-1, viewTag);
    }

    public RefreshEvent(int surfaceId, int viewTag) {
        super(surfaceId, viewTag);
    }

    @Override // com.facebook.react.uimanager.events.Event
    protected WritableMap getEventData() {
        return Arguments.createMap();
    }
}
