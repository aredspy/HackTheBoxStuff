package com.facebook.imagepipeline.producers;

import com.facebook.common.internal.Preconditions;
import com.facebook.common.memory.PooledByteBufferFactory;
import com.facebook.common.memory.PooledByteBufferOutputStream;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.util.TriState;
import com.facebook.imageformat.DefaultImageFormats;
import com.facebook.imageformat.ImageFormat;
import com.facebook.imageformat.ImageFormatChecker;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.nativecode.WebpTranscoder;
import com.facebook.imagepipeline.nativecode.WebpTranscoderFactory;
import java.io.InputStream;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class WebpTranscodeProducer implements Producer<EncodedImage> {
    private static final int DEFAULT_JPEG_QUALITY = 80;
    public static final String PRODUCER_NAME = "WebpTranscodeProducer";
    private final Executor mExecutor;
    private final Producer<EncodedImage> mInputProducer;
    private final PooledByteBufferFactory mPooledByteBufferFactory;

    public WebpTranscodeProducer(Executor executor, PooledByteBufferFactory pooledByteBufferFactory, Producer<EncodedImage> inputProducer) {
        this.mExecutor = (Executor) Preconditions.checkNotNull(executor);
        this.mPooledByteBufferFactory = (PooledByteBufferFactory) Preconditions.checkNotNull(pooledByteBufferFactory);
        this.mInputProducer = (Producer) Preconditions.checkNotNull(inputProducer);
    }

    @Override // com.facebook.imagepipeline.producers.Producer
    public void produceResults(final Consumer<EncodedImage> consumer, final ProducerContext context) {
        this.mInputProducer.produceResults(new WebpTranscodeConsumer(consumer, context), context);
    }

    /* loaded from: classes.dex */
    private class WebpTranscodeConsumer extends DelegatingConsumer<EncodedImage, EncodedImage> {
        private final ProducerContext mContext;
        private TriState mShouldTranscodeWhenFinished = TriState.UNSET;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public WebpTranscodeConsumer(final Consumer<EncodedImage> consumer, final ProducerContext context) {
            super(consumer);
            WebpTranscodeProducer.this = this$0;
            this.mContext = context;
        }

        public void onNewResultImpl(@Nullable EncodedImage newResult, int status) {
            if (this.mShouldTranscodeWhenFinished == TriState.UNSET && newResult != null) {
                this.mShouldTranscodeWhenFinished = WebpTranscodeProducer.shouldTranscode(newResult);
            }
            if (this.mShouldTranscodeWhenFinished == TriState.NO) {
                getConsumer().onNewResult(newResult, status);
            } else if (!isLast(status)) {
            } else {
                if (this.mShouldTranscodeWhenFinished == TriState.YES && newResult != null) {
                    WebpTranscodeProducer.this.transcodeLastResult(newResult, getConsumer(), this.mContext);
                } else {
                    getConsumer().onNewResult(newResult, status);
                }
            }
        }
    }

    public void transcodeLastResult(final EncodedImage originalResult, final Consumer<EncodedImage> consumer, final ProducerContext producerContext) {
        Preconditions.checkNotNull(originalResult);
        final EncodedImage cloneOrNull = EncodedImage.cloneOrNull(originalResult);
        this.mExecutor.execute(new StatefulProducerRunnable<EncodedImage>(consumer, producerContext.getProducerListener(), producerContext, PRODUCER_NAME) { // from class: com.facebook.imagepipeline.producers.WebpTranscodeProducer.1
            @Override // com.facebook.common.executors.StatefulRunnable
            public EncodedImage getResult() throws Exception {
                PooledByteBufferOutputStream newOutputStream = WebpTranscodeProducer.this.mPooledByteBufferFactory.newOutputStream();
                try {
                    WebpTranscodeProducer.doTranscode(cloneOrNull, newOutputStream);
                    CloseableReference of = CloseableReference.of(newOutputStream.toByteBuffer());
                    EncodedImage encodedImage = new EncodedImage(of);
                    encodedImage.copyMetaDataFrom(cloneOrNull);
                    CloseableReference.closeSafely(of);
                    return encodedImage;
                } finally {
                    newOutputStream.close();
                }
            }

            public void disposeResult(EncodedImage result) {
                EncodedImage.closeSafely(result);
            }

            public void onSuccess(EncodedImage result) {
                EncodedImage.closeSafely(cloneOrNull);
                super.onSuccess((AnonymousClass1) result);
            }

            @Override // com.facebook.imagepipeline.producers.StatefulProducerRunnable, com.facebook.common.executors.StatefulRunnable
            public void onFailure(Exception e) {
                EncodedImage.closeSafely(cloneOrNull);
                super.onFailure(e);
            }

            @Override // com.facebook.imagepipeline.producers.StatefulProducerRunnable, com.facebook.common.executors.StatefulRunnable
            public void onCancellation() {
                EncodedImage.closeSafely(cloneOrNull);
                super.onCancellation();
            }
        });
    }

    public static TriState shouldTranscode(final EncodedImage encodedImage) {
        Preconditions.checkNotNull(encodedImage);
        ImageFormat imageFormat_WrapIOException = ImageFormatChecker.getImageFormat_WrapIOException((InputStream) Preconditions.checkNotNull(encodedImage.getInputStream()));
        if (DefaultImageFormats.isStaticWebpFormat(imageFormat_WrapIOException)) {
            WebpTranscoder webpTranscoder = WebpTranscoderFactory.getWebpTranscoder();
            if (webpTranscoder == null) {
                return TriState.NO;
            }
            return TriState.valueOf(!webpTranscoder.isWebpNativelySupported(imageFormat_WrapIOException));
        } else if (imageFormat_WrapIOException == ImageFormat.UNKNOWN) {
            return TriState.UNSET;
        } else {
            return TriState.NO;
        }
    }

    public static void doTranscode(final EncodedImage encodedImage, final PooledByteBufferOutputStream outputStream) throws Exception {
        InputStream inputStream = (InputStream) Preconditions.checkNotNull(encodedImage.getInputStream());
        ImageFormat imageFormat_WrapIOException = ImageFormatChecker.getImageFormat_WrapIOException(inputStream);
        if (imageFormat_WrapIOException == DefaultImageFormats.WEBP_SIMPLE || imageFormat_WrapIOException == DefaultImageFormats.WEBP_EXTENDED) {
            WebpTranscoderFactory.getWebpTranscoder().transcodeWebpToJpeg(inputStream, outputStream, 80);
            encodedImage.setImageFormat(DefaultImageFormats.JPEG);
        } else if (imageFormat_WrapIOException == DefaultImageFormats.WEBP_LOSSLESS || imageFormat_WrapIOException == DefaultImageFormats.WEBP_EXTENDED_WITH_ALPHA) {
            WebpTranscoderFactory.getWebpTranscoder().transcodeWebpToPng(inputStream, outputStream);
            encodedImage.setImageFormat(DefaultImageFormats.PNG);
        } else {
            throw new IllegalArgumentException("Wrong image format");
        }
    }
}
