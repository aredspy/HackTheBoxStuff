package com.facebook.react.common;
/* loaded from: classes.dex */
public class LongArray {
    private static final double INNER_ARRAY_GROWTH_FACTOR = 1.8d;
    private long[] mArray;
    private int mLength = 0;

    public static LongArray createWithInitialCapacity(int initialCapacity) {
        return new LongArray(initialCapacity);
    }

    private LongArray(int initialCapacity) {
        this.mArray = new long[initialCapacity];
    }

    public void add(long value) {
        growArrayIfNeeded();
        long[] jArr = this.mArray;
        int i = this.mLength;
        this.mLength = i + 1;
        jArr[i] = value;
    }

    public long get(int index) {
        if (index >= this.mLength) {
            throw new IndexOutOfBoundsException("" + index + " >= " + this.mLength);
        }
        return this.mArray[index];
    }

    public void set(int index, long value) {
        if (index >= this.mLength) {
            throw new IndexOutOfBoundsException("" + index + " >= " + this.mLength);
        }
        this.mArray[index] = value;
    }

    public int size() {
        return this.mLength;
    }

    public boolean isEmpty() {
        return this.mLength == 0;
    }

    public void dropTail(int n) {
        int i = this.mLength;
        if (n > i) {
            throw new IndexOutOfBoundsException("Trying to drop " + n + " items from array of length " + this.mLength);
        }
        this.mLength = i - n;
    }

    private void growArrayIfNeeded() {
        int i = this.mLength;
        if (i == this.mArray.length) {
            long[] jArr = new long[Math.max(i + 1, (int) (i * INNER_ARRAY_GROWTH_FACTOR))];
            System.arraycopy(this.mArray, 0, jArr, 0, this.mLength);
            this.mArray = jArr;
        }
    }
}
