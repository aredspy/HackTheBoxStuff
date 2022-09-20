package com.facebook.common.memory;

import com.facebook.common.internal.Preconditions;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
/* loaded from: classes.dex */
public class PooledByteStreams {
    private static final int DEFAULT_TEMP_BUF_SIZE = 16384;
    private final ByteArrayPool mByteArrayPool;
    private final int mTempBufSize;

    public PooledByteStreams(ByteArrayPool byteArrayPool) {
        this(byteArrayPool, 16384);
    }

    public PooledByteStreams(ByteArrayPool byteArrayPool, int tempBufSize) {
        Preconditions.checkArgument(Boolean.valueOf(tempBufSize > 0));
        this.mTempBufSize = tempBufSize;
        this.mByteArrayPool = byteArrayPool;
    }

    public long copy(final InputStream from, final OutputStream to) throws IOException {
        byte[] bArr = this.mByteArrayPool.get(this.mTempBufSize);
        long j = 0;
        while (true) {
            try {
                int read = from.read(bArr, 0, this.mTempBufSize);
                if (read == -1) {
                    return j;
                }
                to.write(bArr, 0, read);
                j += read;
            } finally {
                this.mByteArrayPool.release(bArr);
            }
        }
    }

    public long copy(final InputStream from, final OutputStream to, final long bytesToCopy) throws IOException {
        long j = 0;
        Preconditions.checkState(bytesToCopy > 0);
        byte[] bArr = this.mByteArrayPool.get(this.mTempBufSize);
        while (j < bytesToCopy) {
            try {
                int read = from.read(bArr, 0, (int) Math.min(this.mTempBufSize, bytesToCopy - j));
                if (read == -1) {
                    break;
                }
                to.write(bArr, 0, read);
                j += read;
            } finally {
                this.mByteArrayPool.release(bArr);
            }
        }
        return j;
    }
}
