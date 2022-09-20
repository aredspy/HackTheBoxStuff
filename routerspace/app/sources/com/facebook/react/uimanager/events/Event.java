package com.facebook.react.uimanager.events;

import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.SystemClock;
import com.facebook.react.uimanager.IllegalViewOperationException;
import com.facebook.react.uimanager.events.Event;
/* loaded from: classes.dex */
public abstract class Event<T extends Event> {
    public static final long sInitialClockTimeUnixOffset = SystemClock.currentTimeMillis() - SystemClock.uptimeMillis();
    private static int sUniqueID;
    private boolean mInitialized;
    private int mSurfaceId;
    private long mTimestampMs;
    private int mUIManagerType;
    private int mUniqueID;
    private int mViewTag;

    public boolean canCoalesce() {
        return true;
    }

    public short getCoalescingKey() {
        return (short) 0;
    }

    protected WritableMap getEventData() {
        return null;
    }

    public abstract String getEventName();

    public void onDispose() {
    }

    public Event() {
        int i = sUniqueID;
        sUniqueID = i + 1;
        this.mUniqueID = i;
    }

    @Deprecated
    public Event(int viewTag) {
        int i = sUniqueID;
        sUniqueID = i + 1;
        this.mUniqueID = i;
        init(viewTag);
    }

    public Event(int surfaceId, int viewTag) {
        int i = sUniqueID;
        sUniqueID = i + 1;
        this.mUniqueID = i;
        init(surfaceId, viewTag);
    }

    @Deprecated
    protected void init(int viewTag) {
        init(-1, viewTag);
    }

    public void init(int surfaceId, int viewTag) {
        this.mSurfaceId = surfaceId;
        this.mViewTag = viewTag;
        this.mUIManagerType = surfaceId == -1 ? 1 : 2;
        this.mTimestampMs = SystemClock.uptimeMillis();
        this.mInitialized = true;
    }

    public final int getViewTag() {
        return this.mViewTag;
    }

    public final int getSurfaceId() {
        return this.mSurfaceId;
    }

    public final long getTimestampMs() {
        return this.mTimestampMs;
    }

    public final long getUnixTimestampMs() {
        return sInitialClockTimeUnixOffset + this.mTimestampMs;
    }

    public T coalesce(T otherEvent) {
        return getTimestampMs() >= otherEvent.getTimestampMs() ? this : otherEvent;
    }

    public int getUniqueID() {
        return this.mUniqueID;
    }

    public boolean isInitialized() {
        return this.mInitialized;
    }

    public final void dispose() {
        this.mInitialized = false;
        onDispose();
    }

    public final int getUIManagerType() {
        return this.mUIManagerType;
    }

    @Deprecated
    public void dispatch(RCTEventEmitter rctEventEmitter) {
        WritableMap eventData = getEventData();
        if (eventData == null) {
            throw new IllegalViewOperationException("Event: you must return a valid, non-null value from `getEventData`, or override `dispatch` and `disatchModern`. Event: " + getEventName());
        }
        rctEventEmitter.receiveEvent(getViewTag(), getEventName(), eventData);
    }

    @Deprecated
    public void dispatchModern(RCTModernEventEmitter rctEventEmitter) {
        if (getSurfaceId() != -1 && getEventData() != null) {
            rctEventEmitter.receiveEvent(getSurfaceId(), getViewTag(), getEventName(), getEventData());
        } else {
            dispatch(rctEventEmitter);
        }
    }

    @Deprecated
    public void dispatchModernV2(RCTModernEventEmitter rctEventEmitter) {
        if (getSurfaceId() != -1 && getEventData() != null) {
            rctEventEmitter.receiveEvent(getSurfaceId(), getViewTag(), getEventName(), canCoalesce(), getCoalescingKey(), getEventData());
        } else {
            dispatch(rctEventEmitter);
        }
    }
}
