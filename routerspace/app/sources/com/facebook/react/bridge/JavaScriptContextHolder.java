package com.facebook.react.bridge;
/* loaded from: classes.dex */
public class JavaScriptContextHolder {
    private long mContext;

    public JavaScriptContextHolder(long context) {
        this.mContext = context;
    }

    public long get() {
        return this.mContext;
    }

    public synchronized void clear() {
        this.mContext = 0L;
    }
}
