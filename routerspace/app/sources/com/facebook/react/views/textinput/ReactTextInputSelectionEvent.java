package com.facebook.react.views.textinput;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.ViewProps;
import com.facebook.react.uimanager.events.Event;
/* loaded from: classes.dex */
class ReactTextInputSelectionEvent extends Event<ReactTextInputSelectionEvent> {
    private static final String EVENT_NAME = "topSelectionChange";
    private int mSelectionEnd;
    private int mSelectionStart;

    @Override // com.facebook.react.uimanager.events.Event
    public String getEventName() {
        return EVENT_NAME;
    }

    @Deprecated
    public ReactTextInputSelectionEvent(int viewId, int selectionStart, int selectionEnd) {
        this(-1, viewId, selectionStart, selectionEnd);
    }

    public ReactTextInputSelectionEvent(int surfaceId, int viewId, int selectionStart, int selectionEnd) {
        super(surfaceId, viewId);
        this.mSelectionStart = selectionStart;
        this.mSelectionEnd = selectionEnd;
    }

    @Override // com.facebook.react.uimanager.events.Event
    protected WritableMap getEventData() {
        WritableMap createMap = Arguments.createMap();
        WritableMap createMap2 = Arguments.createMap();
        createMap2.putInt(ViewProps.END, this.mSelectionEnd);
        createMap2.putInt(ViewProps.START, this.mSelectionStart);
        createMap.putMap(ReactTextInputShadowNode.PROP_SELECTION, createMap2);
        return createMap;
    }
}
