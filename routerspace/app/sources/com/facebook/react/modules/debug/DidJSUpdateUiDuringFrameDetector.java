package com.facebook.react.modules.debug;

import com.facebook.react.bridge.NotThreadSafeBridgeIdleDebugListener;
import com.facebook.react.common.LongArray;
import com.facebook.react.uimanager.debug.NotThreadSafeViewHierarchyUpdateDebugListener;
/* loaded from: classes.dex */
public class DidJSUpdateUiDuringFrameDetector implements NotThreadSafeBridgeIdleDebugListener, NotThreadSafeViewHierarchyUpdateDebugListener {
    private final LongArray mTransitionToIdleEvents = LongArray.createWithInitialCapacity(20);
    private final LongArray mTransitionToBusyEvents = LongArray.createWithInitialCapacity(20);
    private final LongArray mViewHierarchyUpdateEnqueuedEvents = LongArray.createWithInitialCapacity(20);
    private final LongArray mViewHierarchyUpdateFinishedEvents = LongArray.createWithInitialCapacity(20);
    private volatile boolean mWasIdleAtEndOfLastFrame = true;

    @Override // com.facebook.react.bridge.NotThreadSafeBridgeIdleDebugListener
    public synchronized void onTransitionToBridgeIdle() {
        this.mTransitionToIdleEvents.add(System.nanoTime());
    }

    @Override // com.facebook.react.bridge.NotThreadSafeBridgeIdleDebugListener
    public synchronized void onTransitionToBridgeBusy() {
        this.mTransitionToBusyEvents.add(System.nanoTime());
    }

    @Override // com.facebook.react.bridge.NotThreadSafeBridgeIdleDebugListener
    public synchronized void onBridgeDestroyed() {
    }

    @Override // com.facebook.react.uimanager.debug.NotThreadSafeViewHierarchyUpdateDebugListener
    public synchronized void onViewHierarchyUpdateEnqueued() {
        this.mViewHierarchyUpdateEnqueuedEvents.add(System.nanoTime());
    }

    @Override // com.facebook.react.uimanager.debug.NotThreadSafeViewHierarchyUpdateDebugListener
    public synchronized void onViewHierarchyUpdateFinished() {
        this.mViewHierarchyUpdateFinishedEvents.add(System.nanoTime());
    }

    public synchronized boolean getDidJSHitFrameAndCleanup(long frameStartTimeNanos, long frameEndTimeNanos) {
        boolean z;
        boolean hasEventBetweenTimestamps = hasEventBetweenTimestamps(this.mViewHierarchyUpdateFinishedEvents, frameStartTimeNanos, frameEndTimeNanos);
        boolean didEndFrameIdle = didEndFrameIdle(frameStartTimeNanos, frameEndTimeNanos);
        z = true;
        if (!hasEventBetweenTimestamps && (!didEndFrameIdle || hasEventBetweenTimestamps(this.mViewHierarchyUpdateEnqueuedEvents, frameStartTimeNanos, frameEndTimeNanos))) {
            z = false;
        }
        cleanUp(this.mTransitionToIdleEvents, frameEndTimeNanos);
        cleanUp(this.mTransitionToBusyEvents, frameEndTimeNanos);
        cleanUp(this.mViewHierarchyUpdateEnqueuedEvents, frameEndTimeNanos);
        cleanUp(this.mViewHierarchyUpdateFinishedEvents, frameEndTimeNanos);
        this.mWasIdleAtEndOfLastFrame = didEndFrameIdle;
        return z;
    }

    private static boolean hasEventBetweenTimestamps(LongArray eventArray, long startTime, long endTime) {
        for (int i = 0; i < eventArray.size(); i++) {
            long j = eventArray.get(i);
            if (j >= startTime && j < endTime) {
                return true;
            }
        }
        return false;
    }

    private static long getLastEventBetweenTimestamps(LongArray eventArray, long startTime, long endTime) {
        long j = -1;
        for (int i = 0; i < eventArray.size(); i++) {
            long j2 = eventArray.get(i);
            if (j2 >= startTime && j2 < endTime) {
                j = j2;
            } else if (j2 >= endTime) {
                break;
            }
        }
        return j;
    }

    private boolean didEndFrameIdle(long startTime, long endTime) {
        long lastEventBetweenTimestamps = getLastEventBetweenTimestamps(this.mTransitionToIdleEvents, startTime, endTime);
        long lastEventBetweenTimestamps2 = getLastEventBetweenTimestamps(this.mTransitionToBusyEvents, startTime, endTime);
        if (lastEventBetweenTimestamps == -1 && lastEventBetweenTimestamps2 == -1) {
            return this.mWasIdleAtEndOfLastFrame;
        }
        return lastEventBetweenTimestamps > lastEventBetweenTimestamps2;
    }

    private static void cleanUp(LongArray eventArray, long endTime) {
        int size = eventArray.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            if (eventArray.get(i2) < endTime) {
                i++;
            }
        }
        if (i > 0) {
            for (int i3 = 0; i3 < size - i; i3++) {
                eventArray.set(i3, eventArray.get(i3 + i));
            }
            eventArray.dropTail(i);
        }
    }
}
