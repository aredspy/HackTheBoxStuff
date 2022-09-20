package com.facebook.imagepipeline.producers;

import android.graphics.Bitmap;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.CloseableStaticBitmap;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class BitmapPrepareProducer implements Producer<CloseableReference<CloseableImage>> {
    public static final String PRODUCER_NAME = "BitmapPrepareProducer";
    private final Producer<CloseableReference<CloseableImage>> mInputProducer;
    private final int mMaxBitmapSizeBytes;
    private final int mMinBitmapSizeBytes;
    private final boolean mPreparePrefetch;

    public BitmapPrepareProducer(final Producer<CloseableReference<CloseableImage>> inputProducer, int minBitmapSizeBytes, int maxBitmapSizeBytes, boolean preparePrefetch) {
        Preconditions.checkArgument(Boolean.valueOf(minBitmapSizeBytes <= maxBitmapSizeBytes));
        this.mInputProducer = (Producer) Preconditions.checkNotNull(inputProducer);
        this.mMinBitmapSizeBytes = minBitmapSizeBytes;
        this.mMaxBitmapSizeBytes = maxBitmapSizeBytes;
        this.mPreparePrefetch = preparePrefetch;
    }

    @Override // com.facebook.imagepipeline.producers.Producer
    public void produceResults(final Consumer<CloseableReference<CloseableImage>> consumer, final ProducerContext producerContext) {
        if (producerContext.isPrefetch() && !this.mPreparePrefetch) {
            this.mInputProducer.produceResults(consumer, producerContext);
        } else {
            this.mInputProducer.produceResults(new BitmapPrepareConsumer(consumer, this.mMinBitmapSizeBytes, this.mMaxBitmapSizeBytes), producerContext);
        }
    }

    /* loaded from: classes.dex */
    private static class BitmapPrepareConsumer extends DelegatingConsumer<CloseableReference<CloseableImage>, CloseableReference<CloseableImage>> {
        private final int mMaxBitmapSizeBytes;
        private final int mMinBitmapSizeBytes;

        BitmapPrepareConsumer(Consumer<CloseableReference<CloseableImage>> consumer, int minBitmapSizeBytes, int maxBitmapSizeBytes) {
            super(consumer);
            this.mMinBitmapSizeBytes = minBitmapSizeBytes;
            this.mMaxBitmapSizeBytes = maxBitmapSizeBytes;
        }

        public void onNewResultImpl(@Nullable CloseableReference<CloseableImage> newResult, int status) {
            internalPrepareBitmap(newResult);
            getConsumer().onNewResult(newResult, status);
        }

        private void internalPrepareBitmap(@Nullable CloseableReference<CloseableImage> newResult) {
            CloseableImage closeableImage;
            Bitmap underlyingBitmap;
            int rowBytes;
            if (newResult == null || !newResult.isValid() || (closeableImage = newResult.get()) == null || closeableImage.isClosed() || !(closeableImage instanceof CloseableStaticBitmap) || (underlyingBitmap = ((CloseableStaticBitmap) closeableImage).getUnderlyingBitmap()) == null || (rowBytes = underlyingBitmap.getRowBytes() * underlyingBitmap.getHeight()) < this.mMinBitmapSizeBytes || rowBytes > this.mMaxBitmapSizeBytes) {
                return;
            }
            underlyingBitmap.prepareToDraw();
        }
    }
}
