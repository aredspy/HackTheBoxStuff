package com.facebook.react.modules.network;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
/* loaded from: classes.dex */
public class CountingOutputStream extends FilterOutputStream {
    private long mCount = 0;

    public CountingOutputStream(OutputStream out) {
        super(out);
    }

    public long getCount() {
        return this.mCount;
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] b, int off, int len) throws IOException {
        this.out.write(b, off, len);
        this.mCount += len;
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(int b) throws IOException {
        this.out.write(b);
        this.mCount++;
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.out.close();
    }
}
