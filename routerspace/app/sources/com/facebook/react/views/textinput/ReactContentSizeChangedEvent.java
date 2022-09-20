package com.facebook.react.views.textinput;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.TouchesHelper;
/* loaded from: classes.dex */
public class ReactContentSizeChangedEvent extends Event<ReactTextChangedEvent> {
    public static final String EVENT_NAME = "topContentSizeChange";
    private float mContentHeight;
    private float mContentWidth;

    @Override // com.facebook.react.uimanager.events.Event
    public String getEventName() {
        return "topContentSizeChange";
    }

    @Deprecated
    public ReactContentSizeChangedEvent(int viewId, float contentSizeWidth, float contentSizeHeight) {
        this(-1, viewId, contentSizeWidth, contentSizeHeight);
    }

    public ReactContentSizeChangedEvent(int surfaceId, int viewId, float contentSizeWidth, float contentSizeHeight) {
        super(surfaceId, viewId);
        this.mContentWidth = contentSizeWidth;
        this.mContentHeight = contentSizeHeight;
    }

    @Override // com.facebook.react.uimanager.events.Event
    protected WritableMap getEventData() {
        WritableMap createMap = Arguments.createMap();
        WritableMap createMap2 = Arguments.createMap();
        createMap2.putDouble("width", this.mContentWidth);
        createMap2.putDouble("height", this.mContentHeight);
        createMap.putMap("contentSize", createMap2);
        createMap.putInt(TouchesHelper.TARGET_KEY, getViewTag());
        return createMap;
    }
}
