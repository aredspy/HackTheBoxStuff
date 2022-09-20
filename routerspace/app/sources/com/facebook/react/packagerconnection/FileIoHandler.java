package com.facebook.react.packagerconnection;

import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import com.facebook.common.logging.FLog;
import com.facebook.common.util.UriUtil;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONObject;
/* loaded from: classes.dex */
public class FileIoHandler implements Runnable {
    private static final long FILE_TTL = 30000;
    private static final String TAG = JSPackagerClient.class.getSimpleName();
    private final Map<String, RequestHandler> mRequestHandlers;
    private int mNextHandle = 1;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final Map<Integer, TtlFileInputStream> mOpenFiles = new HashMap();

    /* loaded from: classes.dex */
    public static class TtlFileInputStream {
        private final FileInputStream mStream;
        private long mTtl = System.currentTimeMillis() + FileIoHandler.FILE_TTL;

        public TtlFileInputStream(String path) throws FileNotFoundException {
            this.mStream = new FileInputStream(path);
        }

        private void extendTtl() {
            this.mTtl = System.currentTimeMillis() + FileIoHandler.FILE_TTL;
        }

        public boolean expiredTtl() {
            return System.currentTimeMillis() >= this.mTtl;
        }

        public String read(int size) throws IOException {
            extendTtl();
            byte[] bArr = new byte[size];
            return Base64.encodeToString(bArr, 0, this.mStream.read(bArr), 0);
        }

        public void close() throws IOException {
            this.mStream.close();
        }
    }

    public FileIoHandler() {
        HashMap hashMap = new HashMap();
        this.mRequestHandlers = hashMap;
        hashMap.put("fopen", new RequestOnlyHandler() { // from class: com.facebook.react.packagerconnection.FileIoHandler.1
            @Override // com.facebook.react.packagerconnection.RequestOnlyHandler, com.facebook.react.packagerconnection.RequestHandler
            public void onRequest(Object params, Responder responder) {
                JSONObject jSONObject;
                synchronized (FileIoHandler.this.mOpenFiles) {
                    try {
                        jSONObject = (JSONObject) params;
                    } catch (Exception e) {
                        responder.error(e.toString());
                    }
                    if (jSONObject == null) {
                        throw new Exception("params must be an object { mode: string, filename: string }");
                    }
                    String optString = jSONObject.optString("mode");
                    if (optString == null) {
                        throw new Exception("missing params.mode");
                    }
                    String optString2 = jSONObject.optString("filename");
                    if (optString2 == null) {
                        throw new Exception("missing params.filename");
                    }
                    if (optString.equals("r")) {
                        responder.respond(Integer.valueOf(FileIoHandler.this.addOpenFile(optString2)));
                    } else {
                        throw new IllegalArgumentException("unsupported mode: " + optString);
                    }
                }
            }
        });
        hashMap.put("fclose", new RequestOnlyHandler() { // from class: com.facebook.react.packagerconnection.FileIoHandler.2
            @Override // com.facebook.react.packagerconnection.RequestOnlyHandler, com.facebook.react.packagerconnection.RequestHandler
            public void onRequest(Object params, Responder responder) {
                synchronized (FileIoHandler.this.mOpenFiles) {
                    try {
                    } catch (Exception e) {
                        responder.error(e.toString());
                    }
                    if (params instanceof Number) {
                        TtlFileInputStream ttlFileInputStream = (TtlFileInputStream) FileIoHandler.this.mOpenFiles.get(Integer.valueOf(((Integer) params).intValue()));
                        if (ttlFileInputStream != null) {
                            FileIoHandler.this.mOpenFiles.remove(Integer.valueOf(((Integer) params).intValue()));
                            ttlFileInputStream.close();
                            responder.respond("");
                        } else {
                            throw new Exception("invalid file handle, it might have timed out");
                        }
                    } else {
                        throw new Exception("params must be a file handle");
                    }
                }
            }
        });
        hashMap.put("fread", new RequestOnlyHandler() { // from class: com.facebook.react.packagerconnection.FileIoHandler.3
            @Override // com.facebook.react.packagerconnection.RequestOnlyHandler, com.facebook.react.packagerconnection.RequestHandler
            public void onRequest(Object params, Responder responder) {
                JSONObject jSONObject;
                synchronized (FileIoHandler.this.mOpenFiles) {
                    try {
                        jSONObject = (JSONObject) params;
                    } catch (Exception e) {
                        responder.error(e.toString());
                    }
                    if (jSONObject == null) {
                        throw new Exception("params must be an object { file: handle, size: number }");
                    }
                    int optInt = jSONObject.optInt(UriUtil.LOCAL_FILE_SCHEME);
                    if (optInt == 0) {
                        throw new Exception("invalid or missing file handle");
                    }
                    int optInt2 = jSONObject.optInt("size");
                    if (optInt2 != 0) {
                        TtlFileInputStream ttlFileInputStream = (TtlFileInputStream) FileIoHandler.this.mOpenFiles.get(Integer.valueOf(optInt));
                        if (ttlFileInputStream == null) {
                            throw new Exception("invalid file handle, it might have timed out");
                        }
                        responder.respond(ttlFileInputStream.read(optInt2));
                    } else {
                        throw new Exception("invalid or missing read size");
                    }
                }
            }
        });
    }

    public Map<String, RequestHandler> handlers() {
        return this.mRequestHandlers;
    }

    public int addOpenFile(String filename) throws FileNotFoundException {
        int i = this.mNextHandle;
        this.mNextHandle = i + 1;
        this.mOpenFiles.put(Integer.valueOf(i), new TtlFileInputStream(filename));
        if (this.mOpenFiles.size() == 1) {
            this.mHandler.postDelayed(this, FILE_TTL);
        }
        return i;
    }

    @Override // java.lang.Runnable
    public void run() {
        synchronized (this.mOpenFiles) {
            Iterator<TtlFileInputStream> it = this.mOpenFiles.values().iterator();
            while (it.hasNext()) {
                TtlFileInputStream next = it.next();
                if (next.expiredTtl()) {
                    it.remove();
                    try {
                        next.close();
                    } catch (IOException e) {
                        String str = TAG;
                        FLog.e(str, "closing expired file failed: " + e.toString());
                    }
                }
            }
            if (!this.mOpenFiles.isEmpty()) {
                this.mHandler.postDelayed(this, FILE_TTL);
            }
        }
    }
}
