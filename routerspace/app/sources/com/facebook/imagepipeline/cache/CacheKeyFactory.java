package com.facebook.imagepipeline.cache;

import android.net.Uri;
import com.facebook.cache.common.CacheKey;
import com.facebook.imagepipeline.request.ImageRequest;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public interface CacheKeyFactory {
    CacheKey getBitmapCacheKey(ImageRequest request, @Nullable Object callerContext);

    CacheKey getEncodedCacheKey(ImageRequest request, Uri sourceUri, @Nullable Object callerContext);

    CacheKey getEncodedCacheKey(ImageRequest request, @Nullable Object callerContext);

    CacheKey getPostprocessedBitmapCacheKey(ImageRequest request, @Nullable Object callerContext);
}
