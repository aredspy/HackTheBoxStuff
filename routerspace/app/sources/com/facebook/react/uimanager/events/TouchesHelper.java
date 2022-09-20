package com.facebook.react.uimanager.events;

import android.view.MotionEvent;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.PixelUtil;
/* loaded from: classes.dex */
public class TouchesHelper {
    public static final String CHANGED_TOUCHES_KEY = "changedTouches";
    private static final String LOCATION_X_KEY = "locationX";
    private static final String LOCATION_Y_KEY = "locationY";
    private static final String PAGE_X_KEY = "pageX";
    private static final String PAGE_Y_KEY = "pageY";
    private static final String POINTER_IDENTIFIER_KEY = "identifier";
    public static final String TARGET_KEY = "target";
    public static final String TARGET_SURFACE_KEY = "targetSurface";
    private static final String TIMESTAMP_KEY = "timestamp";
    public static final String TOP_TOUCH_CANCEL_KEY = "topTouchCancel";
    public static final String TOP_TOUCH_END_KEY = "topTouchEnd";
    public static final String TOUCHES_KEY = "touches";

    private static WritableArray createsPointersArray(int surfaceId, int reactTarget, TouchEvent event) {
        WritableArray createArray = Arguments.createArray();
        MotionEvent motionEvent = event.getMotionEvent();
        float x = motionEvent.getX() - event.getViewX();
        float y = motionEvent.getY() - event.getViewY();
        for (int i = 0; i < motionEvent.getPointerCount(); i++) {
            WritableMap createMap = Arguments.createMap();
            createMap.putDouble(PAGE_X_KEY, PixelUtil.toDIPFromPixel(motionEvent.getX(i)));
            createMap.putDouble(PAGE_Y_KEY, PixelUtil.toDIPFromPixel(motionEvent.getY(i)));
            createMap.putDouble(LOCATION_X_KEY, PixelUtil.toDIPFromPixel(motionEvent.getX(i) - x));
            createMap.putDouble(LOCATION_Y_KEY, PixelUtil.toDIPFromPixel(motionEvent.getY(i) - y));
            createMap.putInt(TARGET_SURFACE_KEY, surfaceId);
            createMap.putInt(TARGET_KEY, reactTarget);
            createMap.putDouble(TIMESTAMP_KEY, event.getUnixTimestampMs());
            createMap.putDouble(POINTER_IDENTIFIER_KEY, motionEvent.getPointerId(i));
            createArray.pushMap(createMap);
        }
        return createArray;
    }

    public static void sendTouchEvent(RCTEventEmitter rctEventEmitter, TouchEventType type, int surfaceId, int reactTarget, TouchEvent touchEvent) {
        WritableArray createsPointersArray = createsPointersArray(surfaceId, reactTarget, touchEvent);
        MotionEvent motionEvent = touchEvent.getMotionEvent();
        WritableArray createArray = Arguments.createArray();
        if (type == TouchEventType.MOVE || type == TouchEventType.CANCEL) {
            for (int i = 0; i < motionEvent.getPointerCount(); i++) {
                createArray.pushInt(i);
            }
        } else if (type == TouchEventType.START || type == TouchEventType.END) {
            createArray.pushInt(motionEvent.getActionIndex());
        } else {
            throw new RuntimeException("Unknown touch type: " + type);
        }
        rctEventEmitter.receiveTouches(TouchEventType.getJSEventName(type), createsPointersArray, createArray);
    }
}
