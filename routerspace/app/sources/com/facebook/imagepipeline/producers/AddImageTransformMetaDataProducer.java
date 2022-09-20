package com.facebook.imagepipeline.producers;

import com.facebook.imagepipeline.image.EncodedImage;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class AddImageTransformMetaDataProducer implements Producer<EncodedImage> {
    private final Producer<EncodedImage> mInputProducer;

    public AddImageTransformMetaDataProducer(Producer<EncodedImage> inputProducer) {
        this.mInputProducer = inputProducer;
    }

    @Override // com.facebook.imagepipeline.producers.Producer
    public void produceResults(Consumer<EncodedImage> consumer, ProducerContext context) {
        this.mInputProducer.produceResults(new AddImageTransformMetaDataConsumer(consumer), context);
    }

    /* loaded from: classes.dex */
    private static class AddImageTransformMetaDataConsumer extends DelegatingConsumer<EncodedImage, EncodedImage> {
        private AddImageTransformMetaDataConsumer(Consumer<EncodedImage> consumer) {
            super(consumer);
        }

        public void onNewResultImpl(@Nullable EncodedImage newResult, int status) {
            if (newResult == null) {
                getConsumer().onNewResult(null, status);
                return;
            }
            if (!EncodedImage.isMetaDataAvailable(newResult)) {
                newResult.parseMetaData();
            }
            getConsumer().onNewResult(newResult, status);
        }
    }
}
