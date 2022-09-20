package com.facebook.react.common.mapbuffer;

import com.facebook.jni.HybridData;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;
/* loaded from: classes.dex */
public class ReadableMapBuffer implements Iterable<MapBufferEntry> {
    private static final int ALIGNMENT = 254;
    private static final int BUCKET_SIZE = 10;
    private static final int HEADER_SIZE = 8;
    private static final int INT_SIZE = 4;
    private static final int KEY_SIZE = 2;
    private static final short SHORT_ONE = 1;
    private static final int SHORT_SIZE = 2;
    ByteBuffer mBuffer;
    private short mCount;
    private HybridData mHybridData;
    private int mSizeOfData;

    public int getKeyOffsetForBucketIndex(int bucketIndex) {
        return (bucketIndex * 10) + 8;
    }

    private native ByteBuffer importByteBuffer();

    private native ByteBuffer importByteBufferAllocateDirect();

    static {
        ReadableMapBufferSoLoader.staticInit();
    }

    private ReadableMapBuffer(HybridData hybridData) {
        this.mBuffer = null;
        this.mSizeOfData = 0;
        this.mCount = (short) 0;
        this.mHybridData = hybridData;
    }

    private ReadableMapBuffer(ByteBuffer buffer) {
        this.mBuffer = null;
        this.mSizeOfData = 0;
        this.mCount = (short) 0;
        this.mBuffer = buffer;
        readHeader();
    }

    protected void finalize() throws Throwable {
        super.finalize();
        HybridData hybridData = this.mHybridData;
        if (hybridData != null) {
            hybridData.resetNative();
        }
    }

    private int getValueOffsetForKey(short key) {
        importByteBufferAndReadHeader();
        int bucketIndexForKey = getBucketIndexForKey(key);
        if (bucketIndexForKey == -1) {
            throw new IllegalArgumentException("Unable to find key: " + ((int) key));
        }
        assertKeyExists(key, bucketIndexForKey);
        return getKeyOffsetForBucketIndex(bucketIndexForKey) + 2;
    }

    private int getOffsetForDynamicData() {
        return getKeyOffsetForBucketIndex(this.mCount);
    }

    private int getBucketIndexForKey(short key) {
        short count = (short) (getCount() - 1);
        short s = 0;
        while (s <= count) {
            short s2 = (short) ((s + count) >>> 1);
            short readKey = readKey(getKeyOffsetForBucketIndex(s2));
            if (readKey < key) {
                s = (short) (s2 + 1);
            } else if (readKey <= key) {
                return s2;
            } else {
                count = (short) (s2 - 1);
            }
        }
        return -1;
    }

    public short readKey(int position) {
        return this.mBuffer.getShort(position);
    }

    public double readDoubleValue(int bufferPosition) {
        return this.mBuffer.getDouble(bufferPosition);
    }

    public int readIntValue(int bufferPosition) {
        return this.mBuffer.getInt(bufferPosition);
    }

    public boolean readBooleanValue(int bufferPosition) {
        return readIntValue(bufferPosition) == 1;
    }

    public String readStringValue(int bufferPosition) {
        int offsetForDynamicData = getOffsetForDynamicData() + this.mBuffer.getInt(bufferPosition);
        int i = this.mBuffer.getInt(offsetForDynamicData);
        byte[] bArr = new byte[i];
        this.mBuffer.position(offsetForDynamicData + 4);
        this.mBuffer.get(bArr, 0, i);
        return new String(bArr);
    }

    public ReadableMapBuffer readMapBufferValue(int position) {
        int offsetForDynamicData = getOffsetForDynamicData() + this.mBuffer.getInt(position);
        int i = this.mBuffer.getInt(offsetForDynamicData);
        byte[] bArr = new byte[i];
        this.mBuffer.position(offsetForDynamicData + 4);
        this.mBuffer.get(bArr, 0, i);
        return new ReadableMapBuffer(ByteBuffer.wrap(bArr));
    }

    private void readHeader() {
        if (this.mBuffer.getShort() != ALIGNMENT) {
            this.mBuffer.order(ByteOrder.LITTLE_ENDIAN);
        }
        this.mCount = this.mBuffer.getShort();
        this.mSizeOfData = this.mBuffer.getInt();
    }

    public boolean hasKey(short key) {
        return getBucketIndexForKey(key) != -1;
    }

    public short getCount() {
        importByteBufferAndReadHeader();
        return this.mCount;
    }

    public int getInt(short key) {
        return readIntValue(getValueOffsetForKey(key));
    }

    public double getDouble(short key) {
        return readDoubleValue(getValueOffsetForKey(key));
    }

    public String getString(short key) {
        return readStringValue(getValueOffsetForKey(key));
    }

    public boolean getBoolean(short key) {
        return readBooleanValue(getValueOffsetForKey(key));
    }

    public ReadableMapBuffer getMapBuffer(short key) {
        return readMapBufferValue(getValueOffsetForKey(key));
    }

    private ByteBuffer importByteBufferAndReadHeader() {
        ByteBuffer byteBuffer = this.mBuffer;
        if (byteBuffer != null) {
            return byteBuffer;
        }
        this.mBuffer = importByteBuffer();
        readHeader();
        return this.mBuffer;
    }

    private void assertKeyExists(short key, int bucketIndex) {
        short s = this.mBuffer.getShort(getKeyOffsetForBucketIndex(bucketIndex));
        if (s == key) {
            return;
        }
        throw new IllegalStateException("Stored key doesn't match parameter - expected: " + ((int) key) + " - found: " + ((int) s));
    }

    public int hashCode() {
        ByteBuffer importByteBufferAndReadHeader = importByteBufferAndReadHeader();
        importByteBufferAndReadHeader.rewind();
        return importByteBufferAndReadHeader.hashCode();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ReadableMapBuffer)) {
            return false;
        }
        ByteBuffer importByteBufferAndReadHeader = importByteBufferAndReadHeader();
        ByteBuffer importByteBufferAndReadHeader2 = ((ReadableMapBuffer) obj).importByteBufferAndReadHeader();
        if (importByteBufferAndReadHeader == importByteBufferAndReadHeader2) {
            return true;
        }
        importByteBufferAndReadHeader.rewind();
        importByteBufferAndReadHeader2.rewind();
        return importByteBufferAndReadHeader.equals(importByteBufferAndReadHeader2);
    }

    @Override // java.lang.Iterable
    public Iterator<MapBufferEntry> iterator() {
        return new Iterator<MapBufferEntry>() { // from class: com.facebook.react.common.mapbuffer.ReadableMapBuffer.1
            short current = 0;
            short last;

            {
                ReadableMapBuffer.this = this;
                this.last = (short) (this.getCount() - 1);
            }

            @Override // java.util.Iterator
            public boolean hasNext() {
                return this.current <= this.last;
            }

            @Override // java.util.Iterator
            public MapBufferEntry next() {
                ReadableMapBuffer readableMapBuffer = ReadableMapBuffer.this;
                short s = this.current;
                this.current = (short) (s + 1);
                return new MapBufferEntry(readableMapBuffer.getKeyOffsetForBucketIndex(s));
            }
        };
    }

    /* loaded from: classes.dex */
    public class MapBufferEntry {
        private final int mBucketOffset;

        private MapBufferEntry(int position) {
            ReadableMapBuffer.this = this$0;
            this.mBucketOffset = position;
        }

        public short getKey() {
            return ReadableMapBuffer.this.readKey(this.mBucketOffset);
        }

        public double getDouble(double defaultValue) {
            return ReadableMapBuffer.this.readDoubleValue(this.mBucketOffset + 2);
        }

        public int getInt(int defaultValue) {
            return ReadableMapBuffer.this.readIntValue(this.mBucketOffset + 2);
        }

        public boolean getBoolean(boolean defaultValue) {
            return ReadableMapBuffer.this.readBooleanValue(this.mBucketOffset + 2);
        }

        public String getString() {
            return ReadableMapBuffer.this.readStringValue(this.mBucketOffset + 2);
        }

        public ReadableMapBuffer getReadableMapBuffer() {
            return ReadableMapBuffer.this.readMapBufferValue(this.mBucketOffset + 2);
        }
    }
}
