package com.facebook.cache.common;

import android.net.Uri;
import com.facebook.common.internal.Preconditions;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class SimpleCacheKey implements CacheKey {
    final boolean mIsResourceIdForDebugging;
    final String mKey;

    public SimpleCacheKey(final String key) {
        this(key, false);
    }

    public SimpleCacheKey(final String key, boolean isResourceIdForDebugging) {
        this.mKey = (String) Preconditions.checkNotNull(key);
        this.mIsResourceIdForDebugging = isResourceIdForDebugging;
    }

    @Override // com.facebook.cache.common.CacheKey
    public String toString() {
        return this.mKey;
    }

    @Override // com.facebook.cache.common.CacheKey
    public boolean equals(@Nullable Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SimpleCacheKey)) {
            return false;
        }
        return this.mKey.equals(((SimpleCacheKey) o).mKey);
    }

    @Override // com.facebook.cache.common.CacheKey
    public int hashCode() {
        return this.mKey.hashCode();
    }

    @Override // com.facebook.cache.common.CacheKey
    public boolean containsUri(Uri uri) {
        return this.mKey.contains(uri.toString());
    }

    @Override // com.facebook.cache.common.CacheKey
    public String getUriString() {
        return this.mKey;
    }

    @Override // com.facebook.cache.common.CacheKey
    public boolean isResourceIdForDebugging() {
        return this.mIsResourceIdForDebugging;
    }
}
