package com.facebook.react.uimanager.events;

import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactNoCrashSoftException;
import com.facebook.react.bridge.ReactSoftExceptionLogger;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.common.ViewUtil;
/* loaded from: classes.dex */
public class ReactEventEmitter implements RCTModernEventEmitter {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final String TAG = "ReactEventEmitter";
    private RCTModernEventEmitter mFabricEventEmitter = null;
    private RCTEventEmitter mRCTEventEmitter = null;
    private final ReactApplicationContext mReactContext;

    public ReactEventEmitter(ReactApplicationContext reactContext) {
        this.mReactContext = reactContext;
    }

    public void register(int uiManagerType, RCTModernEventEmitter eventEmitter) {
        this.mFabricEventEmitter = eventEmitter;
    }

    public void register(int uiManagerType, RCTEventEmitter eventEmitter) {
        this.mRCTEventEmitter = eventEmitter;
    }

    public void unregister(int uiManagerType) {
        if (uiManagerType == 1) {
            this.mRCTEventEmitter = null;
        } else {
            this.mFabricEventEmitter = null;
        }
    }

    @Override // com.facebook.react.uimanager.events.RCTEventEmitter
    public void receiveEvent(int targetReactTag, String eventName, WritableMap event) {
        receiveEvent(-1, targetReactTag, eventName, event);
    }

    @Override // com.facebook.react.uimanager.events.RCTModernEventEmitter
    public void receiveEvent(int surfaceId, int targetTag, String eventName, boolean canCoalesceEvent, int customCoalesceKey, WritableMap event) {
        receiveEvent(surfaceId, targetTag, eventName, event);
    }

    @Override // com.facebook.react.uimanager.events.RCTEventEmitter
    public void receiveTouches(String eventName, WritableArray touches, WritableArray changedIndices) {
        RCTModernEventEmitter rCTModernEventEmitter;
        Assertions.assertCondition(touches.size() > 0);
        int i = touches.getMap(0).getInt(TouchesHelper.TARGET_KEY);
        int uIManagerType = ViewUtil.getUIManagerType(i);
        if (uIManagerType == 2 && (rCTModernEventEmitter = this.mFabricEventEmitter) != null) {
            rCTModernEventEmitter.receiveTouches(eventName, touches, changedIndices);
        } else if (uIManagerType == 1 && getEventEmitter(i) != null) {
            this.mRCTEventEmitter.receiveTouches(eventName, touches, changedIndices);
        } else {
            ReactSoftExceptionLogger.logSoftException(TAG, new ReactNoCrashSoftException("Cannot find EventEmitter for receivedTouches: ReactTag[" + i + "] UIManagerType[" + uIManagerType + "] EventName[" + eventName + "]"));
        }
    }

    private RCTEventEmitter getEventEmitter(int reactTag) {
        int uIManagerType = ViewUtil.getUIManagerType(reactTag);
        if (this.mRCTEventEmitter == null) {
            if (this.mReactContext.hasActiveReactInstance()) {
                this.mRCTEventEmitter = (RCTEventEmitter) this.mReactContext.getJSModule(RCTEventEmitter.class);
            } else {
                ReactSoftExceptionLogger.logSoftException(TAG, new ReactNoCrashSoftException("Cannot get RCTEventEmitter from Context for reactTag: " + reactTag + " - uiManagerType: " + uIManagerType + " - No active Catalyst instance!"));
            }
        }
        return this.mRCTEventEmitter;
    }

    @Override // com.facebook.react.uimanager.events.RCTModernEventEmitter
    public void receiveEvent(int surfaceId, int targetReactTag, String eventName, WritableMap event) {
        RCTModernEventEmitter rCTModernEventEmitter;
        int uIManagerType = ViewUtil.getUIManagerType(targetReactTag);
        if (uIManagerType == 2 && (rCTModernEventEmitter = this.mFabricEventEmitter) != null) {
            rCTModernEventEmitter.receiveEvent(surfaceId, targetReactTag, eventName, event);
        } else if (uIManagerType == 1 && getEventEmitter(targetReactTag) != null) {
            this.mRCTEventEmitter.receiveEvent(targetReactTag, eventName, event);
        } else {
            ReactSoftExceptionLogger.logSoftException(TAG, new ReactNoCrashSoftException("Cannot find EventEmitter for receiveEvent: SurfaceId[" + surfaceId + "] ReactTag[" + targetReactTag + "] UIManagerType[" + uIManagerType + "] EventName[" + eventName + "]"));
        }
    }
}
