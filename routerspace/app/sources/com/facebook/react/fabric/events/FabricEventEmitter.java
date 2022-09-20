package com.facebook.react.fabric.events;

import android.util.Pair;
import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.fabric.FabricUIManager;
import com.facebook.react.uimanager.events.RCTModernEventEmitter;
import com.facebook.react.uimanager.events.TouchesHelper;
import com.facebook.systrace.Systrace;
import java.util.HashSet;
/* loaded from: classes.dex */
public class FabricEventEmitter implements RCTModernEventEmitter {
    private static final String TAG = "FabricEventEmitter";
    private final FabricUIManager mUIManager;

    public FabricEventEmitter(FabricUIManager uiManager) {
        this.mUIManager = uiManager;
    }

    @Override // com.facebook.react.uimanager.events.RCTEventEmitter
    public void receiveEvent(int reactTag, String eventName, WritableMap params) {
        receiveEvent(-1, reactTag, eventName, params);
    }

    @Override // com.facebook.react.uimanager.events.RCTModernEventEmitter
    public void receiveEvent(int surfaceId, int reactTag, String eventName, WritableMap params) {
        receiveEvent(surfaceId, reactTag, eventName, false, 0, params);
    }

    @Override // com.facebook.react.uimanager.events.RCTModernEventEmitter
    public void receiveEvent(int surfaceId, int reactTag, String eventName, boolean canCoalesceEvent, int customCoalesceKey, WritableMap params) {
        Systrace.beginSection(0L, "FabricEventEmitter.receiveEvent('" + eventName + "')");
        this.mUIManager.receiveEvent(surfaceId, reactTag, eventName, canCoalesceEvent, customCoalesceKey, params);
        Systrace.endSection(0L);
    }

    @Override // com.facebook.react.uimanager.events.RCTEventEmitter
    public void receiveTouches(String eventTopLevelType, WritableArray touches, WritableArray changedIndices) {
        Pair<WritableArray, WritableArray> pair;
        int i;
        if (TouchesHelper.TOP_TOUCH_END_KEY.equalsIgnoreCase(eventTopLevelType) || TouchesHelper.TOP_TOUCH_CANCEL_KEY.equalsIgnoreCase(eventTopLevelType)) {
            pair = removeTouchesAtIndices(touches, changedIndices);
        } else {
            pair = touchSubsequence(touches, changedIndices);
        }
        WritableArray writableArray = (WritableArray) pair.first;
        WritableArray writableArray2 = (WritableArray) pair.second;
        for (int i2 = 0; i2 < writableArray.size(); i2++) {
            WritableMap writableMap = getWritableMap(writableArray.getMap(i2));
            writableMap.putArray(TouchesHelper.CHANGED_TOUCHES_KEY, copyWritableArray(writableArray));
            writableMap.putArray(TouchesHelper.TOUCHES_KEY, copyWritableArray(writableArray2));
            int i3 = writableMap.getInt(TouchesHelper.TARGET_SURFACE_KEY);
            int i4 = writableMap.getInt(TouchesHelper.TARGET_KEY);
            if (i4 < 1) {
                FLog.e(TAG, "A view is reporting that a touch occurred on tag zero.");
                i = 0;
            } else {
                i = i4;
            }
            receiveEvent(i3, i, eventTopLevelType, false, 0, writableMap);
        }
    }

    private WritableArray copyWritableArray(WritableArray array) {
        WritableNativeArray writableNativeArray = new WritableNativeArray();
        for (int i = 0; i < array.size(); i++) {
            writableNativeArray.pushMap(getWritableMap(array.getMap(i)));
        }
        return writableNativeArray;
    }

    private Pair<WritableArray, WritableArray> removeTouchesAtIndices(WritableArray touches, WritableArray indices) {
        WritableNativeArray writableNativeArray = new WritableNativeArray();
        WritableNativeArray writableNativeArray2 = new WritableNativeArray();
        HashSet hashSet = new HashSet();
        for (int i = 0; i < indices.size(); i++) {
            int i2 = indices.getInt(i);
            writableNativeArray.pushMap(getWritableMap(touches.getMap(i2)));
            hashSet.add(Integer.valueOf(i2));
        }
        for (int i3 = 0; i3 < touches.size(); i3++) {
            if (!hashSet.contains(Integer.valueOf(i3))) {
                writableNativeArray2.pushMap(getWritableMap(touches.getMap(i3)));
            }
        }
        return new Pair<>(writableNativeArray, writableNativeArray2);
    }

    private Pair<WritableArray, WritableArray> touchSubsequence(WritableArray touches, WritableArray changedIndices) {
        WritableNativeArray writableNativeArray = new WritableNativeArray();
        for (int i = 0; i < changedIndices.size(); i++) {
            writableNativeArray.pushMap(getWritableMap(touches.getMap(changedIndices.getInt(i))));
        }
        return new Pair<>(writableNativeArray, touches);
    }

    private WritableMap getWritableMap(ReadableMap readableMap) {
        WritableNativeMap writableNativeMap = new WritableNativeMap();
        writableNativeMap.merge(readableMap);
        return writableNativeMap;
    }
}
