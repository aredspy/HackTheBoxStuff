package com.facebook.react;

import android.view.KeyEvent;
import android.view.View;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.ViewProps;
import java.util.Map;
/* loaded from: classes.dex */
public class ReactAndroidHWInputDeviceHelper {
    private static final Map<Integer, String> KEY_EVENTS_ACTIONS = MapBuilder.builder().put(23, "select").put(66, "select").put(62, "select").put(85, "playPause").put(89, "rewind").put(90, "fastForward").put(86, "stop").put(87, "next").put(88, "previous").put(19, "up").put(22, ViewProps.RIGHT).put(20, "down").put(21, ViewProps.LEFT).put(165, "info").put(82, "menu").build();
    private int mLastFocusedViewId = -1;
    private final ReactRootView mReactRootView;

    public ReactAndroidHWInputDeviceHelper(ReactRootView mReactRootView) {
        this.mReactRootView = mReactRootView;
    }

    public void handleKeyEvent(KeyEvent ev) {
        int keyCode = ev.getKeyCode();
        int action = ev.getAction();
        if (action == 1 || action == 0) {
            Map<Integer, String> map = KEY_EVENTS_ACTIONS;
            if (!map.containsKey(Integer.valueOf(keyCode))) {
                return;
            }
            dispatchEvent(map.get(Integer.valueOf(keyCode)), this.mLastFocusedViewId, action);
        }
    }

    public void onFocusChanged(View newFocusedView) {
        if (this.mLastFocusedViewId == newFocusedView.getId()) {
            return;
        }
        int i = this.mLastFocusedViewId;
        if (i != -1) {
            dispatchEvent("blur", i);
        }
        this.mLastFocusedViewId = newFocusedView.getId();
        dispatchEvent("focus", newFocusedView.getId());
    }

    public void clearFocus() {
        int i = this.mLastFocusedViewId;
        if (i != -1) {
            dispatchEvent("blur", i);
        }
        this.mLastFocusedViewId = -1;
    }

    private void dispatchEvent(String eventType, int targetViewId) {
        dispatchEvent(eventType, targetViewId, -1);
    }

    private void dispatchEvent(String eventType, int targetViewId, int eventKeyAction) {
        WritableNativeMap writableNativeMap = new WritableNativeMap();
        writableNativeMap.putString("eventType", eventType);
        writableNativeMap.putInt("eventKeyAction", eventKeyAction);
        if (targetViewId != -1) {
            writableNativeMap.putInt("tag", targetViewId);
        }
        this.mReactRootView.sendEvent("onHWKeyEvent", writableNativeMap);
    }
}
