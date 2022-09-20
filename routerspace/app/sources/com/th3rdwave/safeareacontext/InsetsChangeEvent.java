package com.th3rdwave.safeareacontext;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.RCTEventEmitter;
/* loaded from: classes.dex */
class InsetsChangeEvent extends Event<InsetsChangeEvent> {
    static final String EVENT_NAME = "topInsetsChange";
    private Rect mFrame;
    private EdgeInsets mInsets;

    @Override // com.facebook.react.uimanager.events.Event
    public String getEventName() {
        return EVENT_NAME;
    }

    public InsetsChangeEvent(int viewTag, EdgeInsets insets, Rect frame) {
        super(viewTag);
        this.mInsets = insets;
        this.mFrame = frame;
    }

    @Override // com.facebook.react.uimanager.events.Event
    public void dispatch(RCTEventEmitter rctEventEmitter) {
        WritableMap createMap = Arguments.createMap();
        createMap.putMap("insets", SerializationUtils.edgeInsetsToJsMap(this.mInsets));
        createMap.putMap("frame", SerializationUtils.rectToJsMap(this.mFrame));
        rctEventEmitter.receiveEvent(getViewTag(), getEventName(), createMap);
    }
}
