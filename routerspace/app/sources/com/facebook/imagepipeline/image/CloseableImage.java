package com.facebook.imagepipeline.image;

import com.facebook.common.logging.FLog;
import com.facebook.imagepipeline.producers.ProducerContext;
import java.io.Closeable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public abstract class CloseableImage implements Closeable, ImageInfo {
    private static final String TAG = "CloseableImage";
    private static final Set<String> mImageExtrasList = new HashSet(Arrays.asList(ProducerContext.ExtraKeys.ENCODED_SIZE, ProducerContext.ExtraKeys.ENCODED_WIDTH, ProducerContext.ExtraKeys.ENCODED_HEIGHT, ProducerContext.ExtraKeys.SOURCE_URI, ProducerContext.ExtraKeys.IMAGE_FORMAT, "bitmap_config", "is_rounded"));
    private Map<String, Object> mExtras = new HashMap();

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public abstract void close();

    public abstract int getSizeInBytes();

    public abstract boolean isClosed();

    public boolean isStateful() {
        return false;
    }

    @Override // com.facebook.imagepipeline.image.ImageInfo
    public QualityInfo getQualityInfo() {
        return ImmutableQualityInfo.FULL_QUALITY;
    }

    @Override // com.facebook.imagepipeline.image.HasImageMetadata
    public Map<String, Object> getExtras() {
        return this.mExtras;
    }

    public void setImageExtras(@Nullable Map<String, Object> extras) {
        if (extras == null) {
            return;
        }
        for (String str : mImageExtrasList) {
            Object obj = extras.get(str);
            if (obj != null) {
                this.mExtras.put(str, obj);
            }
        }
    }

    public void setImageExtra(String extra, Object value) {
        if (mImageExtrasList.contains(extra)) {
            this.mExtras.put(extra, value);
        }
    }

    protected void finalize() throws Throwable {
        if (isClosed()) {
            return;
        }
        FLog.w(TAG, "finalize: %s %x still open.", getClass().getSimpleName(), Integer.valueOf(System.identityHashCode(this)));
        try {
            close();
        } finally {
            super.finalize();
        }
    }
}
