package com.facebook.react.uimanager.events;

import android.util.SparseIntArray;
/* loaded from: classes.dex */
public class TouchEventCoalescingKeyHelper {
    private final SparseIntArray mDownTimeToCoalescingKey = new SparseIntArray();

    public void addCoalescingKey(long downTime) {
        this.mDownTimeToCoalescingKey.put((int) downTime, 0);
    }

    public void incrementCoalescingKey(long downTime) {
        int i = (int) downTime;
        int i2 = this.mDownTimeToCoalescingKey.get(i, -1);
        if (i2 == -1) {
            throw new RuntimeException("Tried to increment non-existent cookie");
        }
        this.mDownTimeToCoalescingKey.put(i, i2 + 1);
    }

    public short getCoalescingKey(long downTime) {
        int i = this.mDownTimeToCoalescingKey.get((int) downTime, -1);
        if (i != -1) {
            return (short) (65535 & i);
        }
        throw new RuntimeException("Tried to get non-existent cookie");
    }

    public void removeCoalescingKey(long downTime) {
        this.mDownTimeToCoalescingKey.delete((int) downTime);
    }

    public boolean hasCoalescingKey(long downTime) {
        return this.mDownTimeToCoalescingKey.get((int) downTime, -1) != -1;
    }
}
