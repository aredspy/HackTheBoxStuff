package com.facebook.imagepipeline.cache;

import android.graphics.Bitmap;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.memory.MemoryTrimmable;
import com.facebook.common.references.CloseableReference;
import java.util.Map;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public interface CountingMemoryCache<K, V> extends MemoryCache<K, V>, MemoryTrimmable {

    /* loaded from: classes.dex */
    public interface EntryStateObserver<K> {
        void onExclusivityChanged(K key, boolean isExclusive);
    }

    @Nullable
    CloseableReference<V> cache(K key, CloseableReference<V> valueRef, EntryStateObserver<K> observer);

    void clear();

    CountingLruMap<K, Entry<K, V>> getCachedEntries();

    int getEvictionQueueCount();

    int getEvictionQueueSizeInBytes();

    int getInUseSizeInBytes();

    MemoryCacheParams getMemoryCacheParams();

    Map<Bitmap, Object> getOtherEntries();

    void maybeEvictEntries();

    @Nullable
    CloseableReference<V> reuse(K key);

    /* loaded from: classes.dex */
    public static class Entry<K, V> {
        public final K key;
        @Nullable
        public final EntryStateObserver<K> observer;
        public final CloseableReference<V> valueRef;
        public int clientCount = 0;
        public boolean isOrphan = false;
        public int accessCount = 0;

        private Entry(K key, CloseableReference<V> valueRef, @Nullable EntryStateObserver<K> observer) {
            this.key = (K) Preconditions.checkNotNull(key);
            this.valueRef = (CloseableReference) Preconditions.checkNotNull(CloseableReference.cloneOrNull(valueRef));
            this.observer = observer;
        }

        public static <K, V> Entry<K, V> of(final K key, final CloseableReference<V> valueRef, @Nullable final EntryStateObserver<K> observer) {
            return new Entry<>(key, valueRef, observer);
        }
    }
}
