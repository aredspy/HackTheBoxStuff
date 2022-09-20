package com.facebook.common.util;

import com.facebook.common.internal.ByteStreams;
import com.facebook.common.internal.Preconditions;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
/* loaded from: classes.dex */
public class StreamUtil {
    public static byte[] getBytesFromStream(final InputStream is) throws IOException {
        return getBytesFromStream(is, is.available());
    }

    public static byte[] getBytesFromStream(InputStream inputStream, int hint) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(hint) { // from class: com.facebook.common.util.StreamUtil.1
            @Override // java.io.ByteArrayOutputStream
            public byte[] toByteArray() {
                if (this.count == this.buf.length) {
                    return this.buf;
                }
                return super.toByteArray();
            }
        };
        ByteStreams.copy(inputStream, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static long skip(final InputStream inputStream, final long bytesCount) throws IOException {
        Preconditions.checkNotNull(inputStream);
        Preconditions.checkArgument(Boolean.valueOf(bytesCount >= 0));
        long j = bytesCount;
        while (j > 0) {
            long skip = inputStream.skip(j);
            if (skip <= 0) {
                if (inputStream.read() == -1) {
                    return bytesCount - j;
                }
                skip = 1;
            }
            j -= skip;
        }
        return bytesCount;
    }
}
