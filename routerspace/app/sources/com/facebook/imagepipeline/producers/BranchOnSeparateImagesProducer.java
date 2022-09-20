package com.facebook.imagepipeline.producers;

import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.request.ImageRequest;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class BranchOnSeparateImagesProducer implements Producer<EncodedImage> {
    private final Producer<EncodedImage> mInputProducer1;
    private final Producer<EncodedImage> mInputProducer2;

    public BranchOnSeparateImagesProducer(Producer<EncodedImage> inputProducer1, Producer<EncodedImage> inputProducer2) {
        this.mInputProducer1 = inputProducer1;
        this.mInputProducer2 = inputProducer2;
    }

    @Override // com.facebook.imagepipeline.producers.Producer
    public void produceResults(Consumer<EncodedImage> consumer, ProducerContext context) {
        this.mInputProducer1.produceResults(new OnFirstImageConsumer(consumer, context), context);
    }

    /* loaded from: classes.dex */
    private class OnFirstImageConsumer extends DelegatingConsumer<EncodedImage, EncodedImage> {
        private ProducerContext mProducerContext;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        private OnFirstImageConsumer(Consumer<EncodedImage> consumer, ProducerContext producerContext) {
            super(consumer);
            BranchOnSeparateImagesProducer.this = this$0;
            this.mProducerContext = producerContext;
        }

        public void onNewResultImpl(@Nullable EncodedImage newResult, int status) {
            ImageRequest imageRequest = this.mProducerContext.getImageRequest();
            boolean isLast = isLast(status);
            boolean isImageBigEnough = ThumbnailSizeChecker.isImageBigEnough(newResult, imageRequest.getResizeOptions());
            if (newResult != null && (isImageBigEnough || imageRequest.getLocalThumbnailPreviewsEnabled())) {
                if (isLast && isImageBigEnough) {
                    getConsumer().onNewResult(newResult, status);
                } else {
                    getConsumer().onNewResult(newResult, turnOffStatusFlag(status, 1));
                }
            }
            if (!isLast || isImageBigEnough) {
                return;
            }
            EncodedImage.closeSafely(newResult);
            BranchOnSeparateImagesProducer.this.mInputProducer2.produceResults(getConsumer(), this.mProducerContext);
        }

        @Override // com.facebook.imagepipeline.producers.DelegatingConsumer, com.facebook.imagepipeline.producers.BaseConsumer
        protected void onFailureImpl(Throwable t) {
            BranchOnSeparateImagesProducer.this.mInputProducer2.produceResults(getConsumer(), this.mProducerContext);
        }
    }
}
