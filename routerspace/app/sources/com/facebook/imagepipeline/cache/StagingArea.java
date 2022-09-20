package com.facebook.imagepipeline.cache;

import com.facebook.cache.common.CacheKey;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.logging.FLog;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.image.EncodedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class StagingArea {
    private static final Class<?> TAG = StagingArea.class;
    private Map<CacheKey, EncodedImage> mMap = new HashMap();

    private StagingArea() {
    }

    public static StagingArea getInstance() {
        return new StagingArea();
    }

    public synchronized void put(final CacheKey key, final EncodedImage encodedImage) {
        Preconditions.checkNotNull(key);
        Preconditions.checkArgument(Boolean.valueOf(EncodedImage.isValid(encodedImage)));
        EncodedImage.closeSafely(this.mMap.put(key, EncodedImage.cloneOrNull(encodedImage)));
        logStats();
    }

    public void clearAll() {
        ArrayList arrayList;
        synchronized (this) {
            arrayList = new ArrayList(this.mMap.values());
            this.mMap.clear();
        }
        for (int i = 0; i < arrayList.size(); i++) {
            EncodedImage encodedImage = (EncodedImage) arrayList.get(i);
            if (encodedImage != null) {
                encodedImage.close();
            }
        }
    }

    public boolean remove(final CacheKey key) {
        EncodedImage remove;
        Preconditions.checkNotNull(key);
        synchronized (this) {
            remove = this.mMap.remove(key);
        }
        if (remove == null) {
            return false;
        }
        try {
            return remove.isValid();
        } finally {
            remove.close();
        }
    }

    public synchronized boolean remove(final CacheKey key, final EncodedImage encodedImage) {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(encodedImage);
        Preconditions.checkArgument(Boolean.valueOf(EncodedImage.isValid(encodedImage)));
        EncodedImage encodedImage2 = this.mMap.get(key);
        if (encodedImage2 == null) {
            return false;
        }
        CloseableReference<PooledByteBuffer> byteBufferRef = encodedImage2.getByteBufferRef();
        CloseableReference<PooledByteBuffer> byteBufferRef2 = encodedImage.getByteBufferRef();
        if (byteBufferRef != null && byteBufferRef2 != null && byteBufferRef.get() == byteBufferRef2.get()) {
            this.mMap.remove(key);
            CloseableReference.closeSafely(byteBufferRef2);
            CloseableReference.closeSafely(byteBufferRef);
            EncodedImage.closeSafely(encodedImage2);
            logStats();
            return true;
        }
        CloseableReference.closeSafely(byteBufferRef2);
        CloseableReference.closeSafely(byteBufferRef);
        EncodedImage.closeSafely(encodedImage2);
        return false;
    }

    @Nullable
    public synchronized EncodedImage get(final CacheKey key) {
        Preconditions.checkNotNull(key);
        EncodedImage encodedImage = this.mMap.get(key);
        if (encodedImage != null) {
            synchronized (encodedImage) {
                if (!EncodedImage.isValid(encodedImage)) {
                    this.mMap.remove(key);
                    FLog.w(TAG, "Found closed reference %d for key %s (%d)", Integer.valueOf(System.identityHashCode(encodedImage)), key.getUriString(), Integer.valueOf(System.identityHashCode(key)));
                    return null;
                }
                encodedImage = EncodedImage.cloneOrNull(encodedImage);
            }
        }
        return encodedImage;
    }

    public synchronized boolean containsKey(CacheKey key) {
        Preconditions.checkNotNull(key);
        if (!this.mMap.containsKey(key)) {
            return false;
        }
        EncodedImage encodedImage = this.mMap.get(key);
        synchronized (encodedImage) {
            if (EncodedImage.isValid(encodedImage)) {
                return true;
            }
            this.mMap.remove(key);
            FLog.w(TAG, "Found closed reference %d for key %s (%d)", Integer.valueOf(System.identityHashCode(encodedImage)), key.getUriString(), Integer.valueOf(System.identityHashCode(key)));
            return false;
        }
    }

    private synchronized void logStats() {
        FLog.v(TAG, "Count = %d", Integer.valueOf(this.mMap.size()));
    }
}
