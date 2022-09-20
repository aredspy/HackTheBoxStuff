package com.facebook.imagepipeline.postprocessors;

import android.graphics.Bitmap;
import com.facebook.cache.common.CacheKey;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.common.internal.Preconditions;
import com.facebook.imagepipeline.filter.InPlaceRoundFilter;
import com.facebook.imagepipeline.filter.XferRoundFilter;
import com.facebook.imagepipeline.request.BasePostprocessor;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class RoundPostprocessor extends BasePostprocessor {
    private static final boolean ENABLE_ANTI_ALIASING = true;
    private static final boolean canUseXferRoundFilter = XferRoundFilter.canUseXferRoundFilter();
    @Nullable
    private CacheKey mCacheKey;
    private final boolean mEnableAntiAliasing;

    public RoundPostprocessor() {
        this(ENABLE_ANTI_ALIASING);
    }

    public RoundPostprocessor(boolean enableAntiAliasing) {
        this.mEnableAntiAliasing = enableAntiAliasing;
    }

    @Override // com.facebook.imagepipeline.request.BasePostprocessor
    public void process(Bitmap destBitmap, Bitmap sourceBitmap) {
        Preconditions.checkNotNull(destBitmap);
        Preconditions.checkNotNull(sourceBitmap);
        if (canUseXferRoundFilter) {
            XferRoundFilter.xferRoundBitmap(destBitmap, sourceBitmap, this.mEnableAntiAliasing);
        } else {
            super.process(destBitmap, sourceBitmap);
        }
    }

    @Override // com.facebook.imagepipeline.request.BasePostprocessor
    public void process(Bitmap bitmap) {
        InPlaceRoundFilter.roundBitmapInPlace(bitmap);
    }

    @Override // com.facebook.imagepipeline.request.BasePostprocessor, com.facebook.imagepipeline.request.Postprocessor
    @Nullable
    public CacheKey getPostprocessorCacheKey() {
        if (this.mCacheKey == null) {
            if (canUseXferRoundFilter) {
                this.mCacheKey = new SimpleCacheKey("XferRoundFilter");
            } else {
                this.mCacheKey = new SimpleCacheKey("InPlaceRoundFilter");
            }
        }
        return this.mCacheKey;
    }
}
