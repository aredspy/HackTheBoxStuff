package com.facebook.react.devsupport;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;
/* loaded from: classes.dex */
public class MultipartStreamReader {
    private static final String CRLF = "\r\n";
    private final String mBoundary;
    private long mLastProgressEvent;
    private final BufferedSource mSource;

    /* loaded from: classes.dex */
    public interface ChunkListener {
        void onChunkComplete(Map<String, String> headers, Buffer body, boolean isLastChunk) throws IOException;

        void onChunkProgress(Map<String, String> headers, long loaded, long total) throws IOException;
    }

    public MultipartStreamReader(BufferedSource source, String boundary) {
        this.mSource = source;
        this.mBoundary = boundary;
    }

    private Map<String, String> parseHeaders(Buffer data) {
        String[] split;
        HashMap hashMap = new HashMap();
        for (String str : data.readUtf8().split(CRLF)) {
            int indexOf = str.indexOf(":");
            if (indexOf != -1) {
                hashMap.put(str.substring(0, indexOf).trim(), str.substring(indexOf + 1).trim());
            }
        }
        return hashMap;
    }

    private void emitChunk(Buffer chunk, boolean done, ChunkListener listener) throws IOException {
        ByteString encodeUtf8 = ByteString.encodeUtf8("\r\n\r\n");
        long indexOf = chunk.indexOf(encodeUtf8);
        if (indexOf == -1) {
            listener.onChunkComplete(null, chunk, done);
            return;
        }
        Buffer buffer = new Buffer();
        Buffer buffer2 = new Buffer();
        chunk.read(buffer, indexOf);
        chunk.skip(encodeUtf8.size());
        chunk.readAll(buffer2);
        listener.onChunkComplete(parseHeaders(buffer), buffer2, done);
    }

    private void emitProgress(Map<String, String> headers, long contentLength, boolean isFinal, ChunkListener listener) throws IOException {
        if (headers == null || listener == null) {
            return;
        }
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - this.mLastProgressEvent <= 16 && !isFinal) {
            return;
        }
        this.mLastProgressEvent = currentTimeMillis;
        listener.onChunkProgress(headers, contentLength, headers.get("Content-Length") != null ? Long.parseLong(headers.get("Content-Length")) : 0L);
    }

    public boolean readAllParts(ChunkListener listener) throws IOException {
        boolean z;
        long j;
        ByteString encodeUtf8 = ByteString.encodeUtf8("\r\n--" + this.mBoundary + CRLF);
        ByteString encodeUtf82 = ByteString.encodeUtf8("\r\n--" + this.mBoundary + "--" + CRLF);
        ByteString encodeUtf83 = ByteString.encodeUtf8("\r\n\r\n");
        Buffer buffer = new Buffer();
        long j2 = 0L;
        long j3 = 0L;
        long j4 = 0L;
        Map<String, String> map = null;
        while (true) {
            long max = Math.max(j2 - encodeUtf82.size(), j3);
            long indexOf = buffer.indexOf(encodeUtf8, max);
            if (indexOf == -1) {
                indexOf = buffer.indexOf(encodeUtf82, max);
                z = true;
            } else {
                z = false;
            }
            if (indexOf == -1) {
                long size = buffer.size();
                if (map == null) {
                    long indexOf2 = buffer.indexOf(encodeUtf83, max);
                    if (indexOf2 >= 0) {
                        this.mSource.read(buffer, indexOf2);
                        Buffer buffer2 = new Buffer();
                        j = j3;
                        buffer.copyTo(buffer2, max, indexOf2 - max);
                        j4 = buffer2.size() + encodeUtf83.size();
                        map = parseHeaders(buffer2);
                    } else {
                        j = j3;
                    }
                } else {
                    j = j3;
                    emitProgress(map, buffer.size() - j4, false, listener);
                }
                if (this.mSource.read(buffer, 4096) <= 0) {
                    return false;
                }
                j2 = size;
                j3 = j;
            } else {
                long j5 = j3;
                long j6 = indexOf - j5;
                if (j5 > 0) {
                    Buffer buffer3 = new Buffer();
                    buffer.skip(j5);
                    buffer.read(buffer3, j6);
                    emitProgress(map, buffer3.size() - j4, true, listener);
                    emitChunk(buffer3, z, listener);
                    j4 = 0;
                    map = null;
                } else {
                    buffer.skip(indexOf);
                }
                if (z) {
                    return true;
                }
                j3 = encodeUtf8.size();
                j2 = j3;
            }
        }
    }
}
