package com.facebook.imagepipeline.memory;

import android.graphics.Bitmap;
import com.facebook.common.memory.MemoryTrimType;
import com.facebook.common.memory.MemoryTrimmableRegistry;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class LruBitmapPool implements BitmapPool {
    private int mCurrentSize;
    private int mMaxBitmapSize;
    private final int mMaxPoolSize;
    private final PoolStatsTracker mPoolStatsTracker;
    protected final PoolBackend<Bitmap> mStrategy = new BitmapPoolBackend();

    public LruBitmapPool(int maxPoolSize, int maxBitmapSize, PoolStatsTracker poolStatsTracker, @Nullable MemoryTrimmableRegistry memoryTrimmableRegistry) {
        this.mMaxPoolSize = maxPoolSize;
        this.mMaxBitmapSize = maxBitmapSize;
        this.mPoolStatsTracker = poolStatsTracker;
        if (memoryTrimmableRegistry != null) {
            memoryTrimmableRegistry.registerMemoryTrimmable(this);
        }
    }

    @Override // com.facebook.common.memory.MemoryTrimmable
    public void trim(MemoryTrimType trimType) {
        trimTo((int) (this.mMaxPoolSize * (1.0d - trimType.getSuggestedTrimRatio())));
    }

    private synchronized void trimTo(int maxSize) {
        Bitmap pop;
        while (this.mCurrentSize > maxSize && (pop = this.mStrategy.pop()) != null) {
            int size = this.mStrategy.getSize(pop);
            this.mCurrentSize -= size;
            this.mPoolStatsTracker.onFree(size);
        }
    }

    @Override // com.facebook.common.memory.Pool
    public synchronized Bitmap get(final int size) {
        int i = this.mCurrentSize;
        int i2 = this.mMaxPoolSize;
        if (i > i2) {
            trimTo(i2);
        }
        Bitmap bitmap = this.mStrategy.get(size);
        if (bitmap != null) {
            int size2 = this.mStrategy.getSize(bitmap);
            this.mCurrentSize -= size2;
            this.mPoolStatsTracker.onValueReuse(size2);
            return bitmap;
        }
        return alloc(size);
    }

    private Bitmap alloc(int size) {
        this.mPoolStatsTracker.onAlloc(size);
        return Bitmap.createBitmap(1, size, Bitmap.Config.ALPHA_8);
    }

    public void release(final Bitmap value) {
        int size = this.mStrategy.getSize(value);
        if (size <= this.mMaxBitmapSize) {
            this.mPoolStatsTracker.onValueRelease(size);
            this.mStrategy.put(value);
            synchronized (this) {
                this.mCurrentSize += size;
            }
        }
    }
}
