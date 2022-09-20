package com.facebook.common.memory;

import com.facebook.common.internal.Preconditions;
import java.io.InputStream;
import kotlin.UByte;
/* loaded from: classes.dex */
public class PooledByteBufferInputStream extends InputStream {
    final PooledByteBuffer mPooledByteBuffer;
    int mOffset = 0;
    int mMark = 0;

    @Override // java.io.InputStream
    public boolean markSupported() {
        return true;
    }

    public PooledByteBufferInputStream(PooledByteBuffer pooledByteBuffer) {
        Preconditions.checkArgument(Boolean.valueOf(!pooledByteBuffer.isClosed()));
        this.mPooledByteBuffer = (PooledByteBuffer) Preconditions.checkNotNull(pooledByteBuffer);
    }

    @Override // java.io.InputStream
    public int available() {
        return this.mPooledByteBuffer.size() - this.mOffset;
    }

    @Override // java.io.InputStream
    public void mark(int readlimit) {
        this.mMark = this.mOffset;
    }

    @Override // java.io.InputStream
    public int read() {
        if (available() <= 0) {
            return -1;
        }
        PooledByteBuffer pooledByteBuffer = this.mPooledByteBuffer;
        int i = this.mOffset;
        this.mOffset = i + 1;
        return pooledByteBuffer.read(i) & UByte.MAX_VALUE;
    }

    @Override // java.io.InputStream
    public int read(byte[] buffer) {
        return read(buffer, 0, buffer.length);
    }

    @Override // java.io.InputStream
    public int read(byte[] buffer, int offset, int length) {
        if (offset < 0 || length < 0 || offset + length > buffer.length) {
            throw new ArrayIndexOutOfBoundsException("length=" + buffer.length + "; regionStart=" + offset + "; regionLength=" + length);
        }
        int available = available();
        if (available <= 0) {
            return -1;
        }
        if (length <= 0) {
            return 0;
        }
        int min = Math.min(available, length);
        this.mPooledByteBuffer.read(this.mOffset, buffer, offset, min);
        this.mOffset += min;
        return min;
    }

    @Override // java.io.InputStream
    public void reset() {
        this.mOffset = this.mMark;
    }

    @Override // java.io.InputStream
    public long skip(long byteCount) {
        Preconditions.checkArgument(Boolean.valueOf(byteCount >= 0));
        int min = Math.min((int) byteCount, available());
        this.mOffset += min;
        return min;
    }
}
