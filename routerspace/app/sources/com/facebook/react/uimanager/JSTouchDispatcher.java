package com.facebook.react.uimanager;

import android.view.MotionEvent;
import android.view.ViewGroup;
import com.facebook.common.logging.FLog;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.common.ReactConstants;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.react.uimanager.events.TouchEvent;
import com.facebook.react.uimanager.events.TouchEventCoalescingKeyHelper;
import com.facebook.react.uimanager.events.TouchEventType;
/* loaded from: classes.dex */
public class JSTouchDispatcher {
    private final ViewGroup mRootViewGroup;
    private int mTargetTag = -1;
    private final float[] mTargetCoordinates = new float[2];
    private boolean mChildIsHandlingNativeGesture = false;
    private long mGestureStartTime = Long.MIN_VALUE;
    private final TouchEventCoalescingKeyHelper mTouchEventCoalescingKeyHelper = new TouchEventCoalescingKeyHelper();

    public JSTouchDispatcher(ViewGroup viewGroup) {
        this.mRootViewGroup = viewGroup;
    }

    public void onChildStartedNativeGesture(MotionEvent androidEvent, EventDispatcher eventDispatcher) {
        if (this.mChildIsHandlingNativeGesture) {
            return;
        }
        dispatchCancelEvent(androidEvent, eventDispatcher);
        this.mChildIsHandlingNativeGesture = true;
        this.mTargetTag = -1;
    }

    private int getSurfaceId() {
        ViewGroup viewGroup = this.mRootViewGroup;
        if (viewGroup == null || !(viewGroup instanceof ReactRoot) || ((ReactRoot) viewGroup).getUIManagerType() != 2) {
            return -1;
        }
        if (this.mRootViewGroup.getContext() instanceof ThemedReactContext) {
            return ((ThemedReactContext) this.mRootViewGroup.getContext()).getSurfaceId();
        }
        return ((ReactRoot) this.mRootViewGroup).getRootViewTag();
    }

    public void handleTouchEvent(MotionEvent ev, EventDispatcher eventDispatcher) {
        int action = ev.getAction() & 255;
        if (action == 0) {
            if (this.mTargetTag != -1) {
                FLog.e(ReactConstants.TAG, "Got DOWN touch before receiving UP or CANCEL from last gesture");
            }
            this.mChildIsHandlingNativeGesture = false;
            this.mGestureStartTime = ev.getEventTime();
            this.mTargetTag = findTargetTagAndSetCoordinates(ev);
            int surfaceId = getSurfaceId();
            int i = this.mTargetTag;
            TouchEventType touchEventType = TouchEventType.START;
            long j = this.mGestureStartTime;
            float[] fArr = this.mTargetCoordinates;
            eventDispatcher.dispatchEvent(TouchEvent.obtain(surfaceId, i, touchEventType, ev, j, fArr[0], fArr[1], this.mTouchEventCoalescingKeyHelper));
        } else if (this.mChildIsHandlingNativeGesture) {
        } else {
            if (this.mTargetTag == -1) {
                FLog.e(ReactConstants.TAG, "Unexpected state: received touch event but didn't get starting ACTION_DOWN for this gesture before");
            } else if (action == 1) {
                findTargetTagAndSetCoordinates(ev);
                int surfaceId2 = getSurfaceId();
                int i2 = this.mTargetTag;
                TouchEventType touchEventType2 = TouchEventType.END;
                long j2 = this.mGestureStartTime;
                float[] fArr2 = this.mTargetCoordinates;
                eventDispatcher.dispatchEvent(TouchEvent.obtain(surfaceId2, i2, touchEventType2, ev, j2, fArr2[0], fArr2[1], this.mTouchEventCoalescingKeyHelper));
                this.mTargetTag = -1;
                this.mGestureStartTime = Long.MIN_VALUE;
            } else if (action == 2) {
                findTargetTagAndSetCoordinates(ev);
                int surfaceId3 = getSurfaceId();
                int i3 = this.mTargetTag;
                TouchEventType touchEventType3 = TouchEventType.MOVE;
                long j3 = this.mGestureStartTime;
                float[] fArr3 = this.mTargetCoordinates;
                eventDispatcher.dispatchEvent(TouchEvent.obtain(surfaceId3, i3, touchEventType3, ev, j3, fArr3[0], fArr3[1], this.mTouchEventCoalescingKeyHelper));
            } else if (action == 5) {
                int surfaceId4 = getSurfaceId();
                int i4 = this.mTargetTag;
                TouchEventType touchEventType4 = TouchEventType.START;
                long j4 = this.mGestureStartTime;
                float[] fArr4 = this.mTargetCoordinates;
                eventDispatcher.dispatchEvent(TouchEvent.obtain(surfaceId4, i4, touchEventType4, ev, j4, fArr4[0], fArr4[1], this.mTouchEventCoalescingKeyHelper));
            } else if (action == 6) {
                int surfaceId5 = getSurfaceId();
                int i5 = this.mTargetTag;
                TouchEventType touchEventType5 = TouchEventType.END;
                long j5 = this.mGestureStartTime;
                float[] fArr5 = this.mTargetCoordinates;
                eventDispatcher.dispatchEvent(TouchEvent.obtain(surfaceId5, i5, touchEventType5, ev, j5, fArr5[0], fArr5[1], this.mTouchEventCoalescingKeyHelper));
            } else if (action == 3) {
                if (this.mTouchEventCoalescingKeyHelper.hasCoalescingKey(ev.getDownTime())) {
                    dispatchCancelEvent(ev, eventDispatcher);
                } else {
                    FLog.e(ReactConstants.TAG, "Received an ACTION_CANCEL touch event for which we have no corresponding ACTION_DOWN");
                }
                this.mTargetTag = -1;
                this.mGestureStartTime = Long.MIN_VALUE;
            } else {
                FLog.w(ReactConstants.TAG, "Warning : touch event was ignored. Action=" + action + " Target=" + this.mTargetTag);
            }
        }
    }

    private int findTargetTagAndSetCoordinates(MotionEvent ev) {
        return TouchTargetHelper.findTargetTagAndCoordinatesForTouch(ev.getX(), ev.getY(), this.mRootViewGroup, this.mTargetCoordinates, null);
    }

    private void dispatchCancelEvent(MotionEvent androidEvent, EventDispatcher eventDispatcher) {
        if (this.mTargetTag == -1) {
            FLog.w(ReactConstants.TAG, "Can't cancel already finished gesture. Is a child View trying to start a gesture from an UP/CANCEL event?");
            return;
        }
        Assertions.assertCondition(!this.mChildIsHandlingNativeGesture, "Expected to not have already sent a cancel for this gesture");
        int surfaceId = getSurfaceId();
        int i = this.mTargetTag;
        TouchEventType touchEventType = TouchEventType.CANCEL;
        long j = this.mGestureStartTime;
        float[] fArr = this.mTargetCoordinates;
        ((EventDispatcher) Assertions.assertNotNull(eventDispatcher)).dispatchEvent(TouchEvent.obtain(surfaceId, i, touchEventType, androidEvent, j, fArr[0], fArr[1], this.mTouchEventCoalescingKeyHelper));
    }
}
