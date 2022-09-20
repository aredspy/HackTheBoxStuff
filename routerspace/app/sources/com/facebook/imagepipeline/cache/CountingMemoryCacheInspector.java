package com.facebook.imagepipeline.cache;

import android.graphics.Bitmap;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.cache.CountingMemoryCache;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class CountingMemoryCacheInspector<K, V> {
    private final CountingMemoryCache<K, V> mCountingBitmapCache;

    /* loaded from: classes.dex */
    public static class DumpInfoEntry<K, V> {
        public final K key;
        @Nullable
        public final CloseableReference<V> value;

        public DumpInfoEntry(final K key, final CloseableReference<V> valueRef) {
            this.key = (K) Preconditions.checkNotNull(key);
            this.value = CloseableReference.cloneOrNull(valueRef);
        }

        public void release() {
            CloseableReference.closeSafely((CloseableReference<?>) this.value);
        }
    }

    /* loaded from: classes.dex */
    public static class DumpInfo<K, V> {
        public final int lruSize;
        public final int maxEntriesCount;
        public final int maxEntrySize;
        public final int maxSize;
        public final int size;
        public final List<DumpInfoEntry<K, V>> lruEntries = new ArrayList();
        public final List<DumpInfoEntry<K, V>> sharedEntries = new ArrayList();
        public final Map<Bitmap, Object> otherEntries = new HashMap();

        public DumpInfo(int size, int lruSize, MemoryCacheParams params) {
            this.maxSize = params.maxCacheSize;
            this.maxEntriesCount = params.maxCacheEntries;
            this.maxEntrySize = params.maxCacheEntrySize;
            this.size = size;
            this.lruSize = lruSize;
        }

        public void release() {
            for (DumpInfoEntry<K, V> dumpInfoEntry : this.lruEntries) {
                dumpInfoEntry.release();
            }
            for (DumpInfoEntry<K, V> dumpInfoEntry2 : this.sharedEntries) {
                dumpInfoEntry2.release();
            }
        }
    }

    public CountingMemoryCacheInspector(CountingMemoryCache<K, V> countingBitmapCache) {
        this.mCountingBitmapCache = countingBitmapCache;
    }

    public DumpInfo dumpCacheContent() {
        DumpInfo dumpInfo;
        synchronized (this.mCountingBitmapCache) {
            dumpInfo = new DumpInfo(this.mCountingBitmapCache.getSizeInBytes(), this.mCountingBitmapCache.getEvictionQueueSizeInBytes(), this.mCountingBitmapCache.getMemoryCacheParams());
            for (Map.Entry<K, CountingMemoryCache.Entry<K, V>> entry : this.mCountingBitmapCache.getCachedEntries().getMatchingEntries(null)) {
                CountingMemoryCache.Entry<K, V> value = entry.getValue();
                DumpInfoEntry<K, V> dumpInfoEntry = new DumpInfoEntry<>(value.key, value.valueRef);
                if (value.clientCount > 0) {
                    dumpInfo.sharedEntries.add(dumpInfoEntry);
                } else {
                    dumpInfo.lruEntries.add(dumpInfoEntry);
                }
            }
            for (Map.Entry<Bitmap, Object> entry2 : this.mCountingBitmapCache.getOtherEntries().entrySet()) {
                if (entry2 != null && !entry2.getKey().isRecycled()) {
                    dumpInfo.otherEntries.put(entry2.getKey(), entry2.getValue());
                }
            }
        }
        return dumpInfo;
    }
}
