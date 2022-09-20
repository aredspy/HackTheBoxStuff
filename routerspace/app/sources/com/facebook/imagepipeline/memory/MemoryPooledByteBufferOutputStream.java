package com.facebook.imagepipeline.memory;

import com.facebook.common.internal.Preconditions;
import com.facebook.common.memory.PooledByteBufferOutputStream;
import com.facebook.common.references.CloseableReference;
import java.io.IOException;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class MemoryPooledByteBufferOutputStream extends PooledByteBufferOutputStream {
    @Nullable
    private CloseableReference<MemoryChunk> mBufRef;
    private int mCount;
    private final MemoryChunkPool mPool;

    public MemoryPooledByteBufferOutputStream(MemoryChunkPool pool) {
        this(pool, pool.getMinBufferSize());
    }

    public MemoryPooledByteBufferOutputStream(MemoryChunkPool pool, int initialCapacity) {
        Preconditions.checkArgument(Boolean.valueOf(initialCapacity > 0));
        MemoryChunkPool memoryChunkPool = (MemoryChunkPool) Preconditions.checkNotNull(pool);
        this.mPool = memoryChunkPool;
        this.mCount = 0;
        this.mBufRef = CloseableReference.of(memoryChunkPool.get(initialCapacity), memoryChunkPool);
    }

    @Override // com.facebook.common.memory.PooledByteBufferOutputStream
    public MemoryPooledByteBuffer toByteBuffer() {
        ensureValid();
        return new MemoryPooledByteBuffer((CloseableReference) Preconditions.checkNotNull(this.mBufRef), this.mCount);
    }

    @Override // com.facebook.common.memory.PooledByteBufferOutputStream
    public int size() {
        return this.mCount;
    }

    @Override // java.io.OutputStream
    public void write(int oneByte) throws IOException {
        write(new byte[]{(byte) oneByte});
    }

    @Override // java.io.OutputStream
    public void write(byte[] buffer, int offset, int count) throws IOException {
        if (offset < 0 || count < 0 || offset + count > buffer.length) {
            throw new ArrayIndexOutOfBoundsException("length=" + buffer.length + "; regionStart=" + offset + "; regionLength=" + count);
        }
        ensureValid();
        realloc(this.mCount + count);
        ((MemoryChunk) ((CloseableReference) Preconditions.checkNotNull(this.mBufRef)).get()).write(this.mCount, buffer, offset, count);
        this.mCount += count;
    }

    @Override // com.facebook.common.memory.PooledByteBufferOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        CloseableReference.closeSafely(this.mBufRef);
        this.mBufRef = null;
        this.mCount = -1;
        super.close();
    }

    void realloc(int newLength) {
        ensureValid();
        Preconditions.checkNotNull(this.mBufRef);
        if (newLength <= this.mBufRef.get().getSize()) {
            return;
        }
        MemoryChunk memoryChunk = this.mPool.get(newLength);
        Preconditions.checkNotNull(this.mBufRef);
        this.mBufRef.get().copy(0, memoryChunk, 0, this.mCount);
        this.mBufRef.close();
        this.mBufRef = CloseableReference.of(memoryChunk, this.mPool);
    }

    private void ensureValid() {
        if (CloseableReference.isValid(this.mBufRef)) {
            return;
        }
        throw new InvalidStreamException();
    }

    /* loaded from: classes.dex */
    public static class InvalidStreamException extends RuntimeException {
        public InvalidStreamException() {
            super("OutputStream no longer valid");
        }
    }
}
