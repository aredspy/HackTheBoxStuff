package com.facebook.react.animated;

import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import java.util.List;
/* loaded from: classes.dex */
class EventAnimationDriver implements RCTEventEmitter {
    private List<String> mEventPath;
    ValueAnimatedNode mValueNode;

    public EventAnimationDriver(List<String> eventPath, ValueAnimatedNode valueNode) {
        this.mEventPath = eventPath;
        this.mValueNode = valueNode;
    }

    @Override // com.facebook.react.uimanager.events.RCTEventEmitter
    public void receiveEvent(int targetTag, String eventName, WritableMap event) {
        if (event == null) {
            throw new IllegalArgumentException("Native animated events must have event data.");
        }
        int i = 0;
        WritableMap writableMap = event;
        while (i < this.mEventPath.size() - 1) {
            i++;
            writableMap = writableMap.getMap(this.mEventPath.get(i));
        }
        ValueAnimatedNode valueAnimatedNode = this.mValueNode;
        List<String> list = this.mEventPath;
        valueAnimatedNode.mValue = writableMap.getDouble(list.get(list.size() - 1));
    }

    @Override // com.facebook.react.uimanager.events.RCTEventEmitter
    public void receiveTouches(String eventName, WritableArray touches, WritableArray changedIndices) {
        throw new RuntimeException("receiveTouches is not support by native animated events");
    }
}
