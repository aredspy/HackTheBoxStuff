package com.facebook.imagepipeline.memory;

import android.util.SparseArray;
import java.util.LinkedList;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class BucketMap<T> {
    @Nullable
    LinkedEntry<T> mHead;
    protected final SparseArray<LinkedEntry<T>> mMap = new SparseArray<>();
    @Nullable
    LinkedEntry<T> mTail;

    /* loaded from: classes.dex */
    public static class LinkedEntry<I> {
        int key;
        @Nullable
        LinkedEntry<I> next;
        @Nullable
        LinkedEntry<I> prev;
        LinkedList<I> value;

        private LinkedEntry(@Nullable LinkedEntry<I> prev, int key, LinkedList<I> value, @Nullable LinkedEntry<I> next) {
            this.prev = prev;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public String toString() {
            return "LinkedEntry(key: " + this.key + ")";
        }
    }

    @Nullable
    public synchronized T acquire(int key) {
        LinkedEntry<T> linkedEntry = this.mMap.get(key);
        if (linkedEntry == null) {
            return null;
        }
        T pollFirst = linkedEntry.value.pollFirst();
        moveToFront(linkedEntry);
        return pollFirst;
    }

    public synchronized void release(int key, T value) {
        LinkedEntry<T> linkedEntry = this.mMap.get(key);
        if (linkedEntry == null) {
            linkedEntry = new LinkedEntry<>(null, key, new LinkedList(), null);
            this.mMap.put(key, linkedEntry);
        }
        linkedEntry.value.addLast(value);
        moveToFront(linkedEntry);
    }

    public synchronized int valueCount() {
        int i;
        i = 0;
        for (LinkedEntry linkedEntry = this.mHead; linkedEntry != null; linkedEntry = linkedEntry.next) {
            if (linkedEntry.value != null) {
                i += linkedEntry.value.size();
            }
        }
        return i;
    }

    private synchronized void prune(LinkedEntry<T> bucket) {
        LinkedEntry linkedEntry = (LinkedEntry<T>) bucket.prev;
        LinkedEntry linkedEntry2 = (LinkedEntry<T>) bucket.next;
        if (linkedEntry != null) {
            linkedEntry.next = linkedEntry2;
        }
        if (linkedEntry2 != null) {
            linkedEntry2.prev = linkedEntry;
        }
        bucket.prev = null;
        bucket.next = null;
        if (bucket == this.mHead) {
            this.mHead = linkedEntry2;
        }
        if (bucket == this.mTail) {
            this.mTail = linkedEntry;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void moveToFront(LinkedEntry<T> bucket) {
        if (this.mHead == bucket) {
            return;
        }
        prune(bucket);
        LinkedEntry linkedEntry = (LinkedEntry<T>) this.mHead;
        if (linkedEntry == null) {
            this.mHead = bucket;
            this.mTail = bucket;
            return;
        }
        bucket.next = linkedEntry;
        this.mHead.prev = bucket;
        this.mHead = bucket;
    }

    @Nullable
    public synchronized T removeFromEnd() {
        LinkedEntry<T> linkedEntry = this.mTail;
        if (linkedEntry == null) {
            return null;
        }
        T pollLast = linkedEntry.value.pollLast();
        maybePrune(linkedEntry);
        return pollLast;
    }

    private void maybePrune(LinkedEntry<T> bucket) {
        if (bucket == null || !bucket.value.isEmpty()) {
            return;
        }
        prune(bucket);
        this.mMap.remove(bucket.key);
    }
}
