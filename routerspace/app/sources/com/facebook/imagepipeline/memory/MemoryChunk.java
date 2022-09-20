package com.facebook.imagepipeline.memory;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public interface MemoryChunk {
    void close();

    void copy(final int offset, final MemoryChunk other, final int otherOffset, final int count);

    @Nullable
    ByteBuffer getByteBuffer();

    long getNativePtr() throws UnsupportedOperationException;

    int getSize();

    long getUniqueId();

    boolean isClosed();

    byte read(final int offset);

    int read(final int memoryOffset, final byte[] byteArray, final int byteArrayOffset, final int count);

    int write(final int memoryOffset, final byte[] byteArray, final int byteArrayOffset, final int count);
}
