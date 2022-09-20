package com.facebook.imagepipeline.cache;
/* loaded from: classes.dex */
public interface MemoryCacheTracker<K> {
    void onCacheHit(K cacheKey);

    void onCacheMiss(K cacheKey);

    void onCachePut(K cacheKey);
}
