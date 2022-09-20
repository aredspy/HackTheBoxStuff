package com.facebook.common.internal;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
/* loaded from: classes.dex */
public final class ByteStreams {
    private static final int BUF_SIZE = 4096;

    private ByteStreams() {
    }

    public static long copy(InputStream from, OutputStream to) throws IOException {
        Preconditions.checkNotNull(from);
        Preconditions.checkNotNull(to);
        byte[] bArr = new byte[4096];
        long j = 0;
        while (true) {
            int read = from.read(bArr);
            if (read == -1) {
                return j;
            }
            to.write(bArr, 0, read);
            j += read;
        }
    }

    public static int read(InputStream in, byte[] b, int off, int len) throws IOException {
        Preconditions.checkNotNull(in);
        Preconditions.checkNotNull(b);
        if (len >= 0) {
            int i = 0;
            while (i < len) {
                int read = in.read(b, off + i, len - i);
                if (read == -1) {
                    break;
                }
                i += read;
            }
            return i;
        }
        throw new IndexOutOfBoundsException("len is negative");
    }

    public static byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        copy(in, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static byte[] toByteArray(InputStream in, int expectedSize) throws IOException {
        byte[] bArr = new byte[expectedSize];
        int i = expectedSize;
        while (i > 0) {
            int i2 = expectedSize - i;
            int read = in.read(bArr, i2, i);
            if (read == -1) {
                return Arrays.copyOf(bArr, i2);
            }
            i -= read;
        }
        int read2 = in.read();
        if (read2 == -1) {
            return bArr;
        }
        FastByteArrayOutputStream fastByteArrayOutputStream = new FastByteArrayOutputStream();
        fastByteArrayOutputStream.write(read2);
        copy(in, fastByteArrayOutputStream);
        byte[] bArr2 = new byte[fastByteArrayOutputStream.size() + expectedSize];
        System.arraycopy(bArr, 0, bArr2, 0, expectedSize);
        fastByteArrayOutputStream.writeTo(bArr2, expectedSize);
        return bArr2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class FastByteArrayOutputStream extends ByteArrayOutputStream {
        private FastByteArrayOutputStream() {
        }

        void writeTo(byte[] b, int off) {
            System.arraycopy(this.buf, 0, b, off, this.count);
        }
    }

    public static void readFully(InputStream in, byte[] b, int off, int len) throws IOException {
        int read = read(in, b, off, len);
        if (read == len) {
            return;
        }
        throw new EOFException("reached end of stream after reading " + read + " bytes; " + len + " bytes expected");
    }
}
