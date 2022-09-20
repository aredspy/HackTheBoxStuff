package com.facebook.common.memory;

import java.io.IOException;
import java.io.InputStream;
/* loaded from: classes.dex */
public interface PooledByteBufferFactory {
    PooledByteBuffer newByteBuffer(int size);

    PooledByteBuffer newByteBuffer(InputStream inputStream) throws IOException;

    PooledByteBuffer newByteBuffer(InputStream inputStream, int initialCapacity) throws IOException;

    PooledByteBuffer newByteBuffer(byte[] bytes);

    PooledByteBufferOutputStream newOutputStream();

    PooledByteBufferOutputStream newOutputStream(int initialCapacity);
}
