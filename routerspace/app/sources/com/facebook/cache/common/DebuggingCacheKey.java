package com.facebook.cache.common;

import android.net.Uri;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class DebuggingCacheKey extends SimpleCacheKey {
    @Nullable
    private final Object mCallerContext;
    private final Uri mSourceUri;

    public DebuggingCacheKey(String key, @Nullable Object callerContext, Uri sourceUri) {
        super(key);
        this.mCallerContext = callerContext;
        this.mSourceUri = sourceUri;
    }

    @Nullable
    public Object getCallerContext() {
        return this.mCallerContext;
    }

    public Uri getSourceUri() {
        return this.mSourceUri;
    }
}
