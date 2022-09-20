package com.facebook.imagepipeline.core;

import android.content.Context;
import com.facebook.cache.common.CacheKey;
import com.facebook.common.internal.Supplier;
import com.facebook.common.internal.Suppliers;
import com.facebook.common.memory.ByteArrayPool;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.memory.PooledByteBufferFactory;
import com.facebook.common.memory.PooledByteStreams;
import com.facebook.common.webp.WebpBitmapFactory;
import com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory;
import com.facebook.imagepipeline.cache.BufferedDiskCache;
import com.facebook.imagepipeline.cache.CacheKeyFactory;
import com.facebook.imagepipeline.cache.MemoryCache;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.decoder.ImageDecoder;
import com.facebook.imagepipeline.decoder.ProgressiveJpegConfig;
import com.facebook.imagepipeline.image.CloseableImage;
/* loaded from: classes.dex */
public class ImagePipelineExperiments {
    private final boolean mAllowDelay;
    private final int mBitmapCloseableRefType;
    private boolean mBitmapPrepareToDrawForPrefetch;
    private final int mBitmapPrepareToDrawMaxSizeBytes;
    private final int mBitmapPrepareToDrawMinSizeBytes;
    private final boolean mDecodeCancellationEnabled;
    private boolean mDownsampleIfLargeBitmap;
    private final boolean mDownscaleFrameToDrawableDimensions;
    private boolean mEncodedCacheEnabled;
    private final boolean mEnsureTranscoderLibraryLoaded;
    private final boolean mExperimentalThreadHandoffQueueEnabled;
    private final boolean mGingerbreadDecoderEnabled;
    private final boolean mIsDiskCacheProbingEnabled;
    private final boolean mIsEncodedMemoryCacheProbingEnabled;
    private boolean mKeepCancelledFetchAsLowPriority;
    private final Supplier<Boolean> mLazyDataSource;
    private final int mMaxBitmapSize;
    private final long mMemoryType;
    private final boolean mNativeCodeDisabled;
    private final boolean mPartialImageCachingEnabled;
    private final ProducerFactoryMethod mProducerFactoryMethod;
    private final Supplier<Boolean> mSuppressBitmapPrefetchingSupplier;
    private final int mTrackedKeysSize;
    private final boolean mUseBitmapPrepareToDraw;
    private final boolean mUseCombinedNetworkAndCacheProducer;
    private final boolean mUseDownsamplingRatioForResizing;
    private final WebpBitmapFactory mWebpBitmapFactory;
    private final WebpBitmapFactory.WebpErrorLogger mWebpErrorLogger;
    private final boolean mWebpSupportEnabled;

    /* loaded from: classes.dex */
    public interface ProducerFactoryMethod {
        ProducerFactory createProducerFactory(Context context, ByteArrayPool byteArrayPool, ImageDecoder imageDecoder, ProgressiveJpegConfig progressiveJpegConfig, boolean downsampleEnabled, boolean resizeAndRotateEnabledForNetwork, boolean decodeCancellationEnabled, ExecutorSupplier executorSupplier, PooledByteBufferFactory pooledByteBufferFactory, PooledByteStreams pooledByteStreams, MemoryCache<CacheKey, CloseableImage> bitmapMemoryCache, MemoryCache<CacheKey, PooledByteBuffer> encodedMemoryCache, BufferedDiskCache defaultBufferedDiskCache, BufferedDiskCache smallImageBufferedDiskCache, CacheKeyFactory cacheKeyFactory, PlatformBitmapFactory platformBitmapFactory, int bitmapPrepareToDrawMinSizeBytes, int bitmapPrepareToDrawMaxSizeBytes, boolean bitmapPrepareToDrawForPrefetch, int maxBitmapSize, CloseableReferenceFactory closeableReferenceFactory, boolean keepCancelledFetchAsLowPriority, int trackedKeysSize);
    }

    private ImagePipelineExperiments(Builder builder) {
        this.mWebpSupportEnabled = builder.mWebpSupportEnabled;
        this.mWebpErrorLogger = builder.mWebpErrorLogger;
        this.mDecodeCancellationEnabled = builder.mDecodeCancellationEnabled;
        this.mWebpBitmapFactory = builder.mWebpBitmapFactory;
        this.mUseDownsamplingRatioForResizing = builder.mUseDownsamplingRatioForResizing;
        this.mUseBitmapPrepareToDraw = builder.mUseBitmapPrepareToDraw;
        this.mBitmapPrepareToDrawMinSizeBytes = builder.mBitmapPrepareToDrawMinSizeBytes;
        this.mBitmapPrepareToDrawMaxSizeBytes = builder.mBitmapPrepareToDrawMaxSizeBytes;
        this.mBitmapPrepareToDrawForPrefetch = builder.mBitmapPrepareToDrawForPrefetch;
        this.mMaxBitmapSize = builder.mMaxBitmapSize;
        this.mNativeCodeDisabled = builder.mNativeCodeDisabled;
        this.mPartialImageCachingEnabled = builder.mPartialImageCachingEnabled;
        if (builder.mProducerFactoryMethod == null) {
            this.mProducerFactoryMethod = new DefaultProducerFactoryMethod();
        } else {
            this.mProducerFactoryMethod = builder.mProducerFactoryMethod;
        }
        this.mLazyDataSource = builder.mLazyDataSource;
        this.mGingerbreadDecoderEnabled = builder.mGingerbreadDecoderEnabled;
        this.mDownscaleFrameToDrawableDimensions = builder.mDownscaleFrameToDrawableDimensions;
        this.mBitmapCloseableRefType = builder.mBitmapCloseableRefType;
        this.mSuppressBitmapPrefetchingSupplier = builder.mSuppressBitmapPrefetchingSupplier;
        this.mExperimentalThreadHandoffQueueEnabled = builder.mExperimentalThreadHandoffQueueEnabled;
        this.mMemoryType = builder.mMemoryType;
        this.mKeepCancelledFetchAsLowPriority = builder.mKeepCancelledFetchAsLowPriority;
        this.mDownsampleIfLargeBitmap = builder.mDownsampleIfLargeBitmap;
        this.mEncodedCacheEnabled = builder.mEncodedCacheEnabled;
        this.mEnsureTranscoderLibraryLoaded = builder.mEnsureTranscoderLibraryLoaded;
        this.mIsEncodedMemoryCacheProbingEnabled = builder.mIsEncodedMemoryCacheProbingEnabled;
        this.mIsDiskCacheProbingEnabled = builder.mIsDiskCacheProbingEnabled;
        this.mTrackedKeysSize = builder.mTrackedKeysSize;
        this.mUseCombinedNetworkAndCacheProducer = builder.mUseCombinedNetworkAndCacheProducer;
        this.mAllowDelay = builder.mAllowDelay;
    }

    public boolean isEncodedCacheEnabled() {
        return this.mEncodedCacheEnabled;
    }

    public boolean shouldDownsampleIfLargeBitmap() {
        return this.mDownsampleIfLargeBitmap;
    }

    public boolean getUseDownsamplingRatioForResizing() {
        return this.mUseDownsamplingRatioForResizing;
    }

    public boolean isWebpSupportEnabled() {
        return this.mWebpSupportEnabled;
    }

    public boolean isDecodeCancellationEnabled() {
        return this.mDecodeCancellationEnabled;
    }

    public WebpBitmapFactory.WebpErrorLogger getWebpErrorLogger() {
        return this.mWebpErrorLogger;
    }

    public WebpBitmapFactory getWebpBitmapFactory() {
        return this.mWebpBitmapFactory;
    }

    public boolean getUseBitmapPrepareToDraw() {
        return this.mUseBitmapPrepareToDraw;
    }

    public int getBitmapPrepareToDrawMinSizeBytes() {
        return this.mBitmapPrepareToDrawMinSizeBytes;
    }

    public int getBitmapPrepareToDrawMaxSizeBytes() {
        return this.mBitmapPrepareToDrawMaxSizeBytes;
    }

    public boolean isNativeCodeDisabled() {
        return this.mNativeCodeDisabled;
    }

    public boolean isPartialImageCachingEnabled() {
        return this.mPartialImageCachingEnabled;
    }

    public ProducerFactoryMethod getProducerFactoryMethod() {
        return this.mProducerFactoryMethod;
    }

    public boolean isExperimentalThreadHandoffQueueEnabled() {
        return this.mExperimentalThreadHandoffQueueEnabled;
    }

    public static Builder newBuilder(ImagePipelineConfig.Builder configBuilder) {
        return new Builder(configBuilder);
    }

    public boolean getBitmapPrepareToDrawForPrefetch() {
        return this.mBitmapPrepareToDrawForPrefetch;
    }

    public int getMaxBitmapSize() {
        return this.mMaxBitmapSize;
    }

    public Supplier<Boolean> isLazyDataSource() {
        return this.mLazyDataSource;
    }

    public boolean isEnsureTranscoderLibraryLoaded() {
        return this.mEnsureTranscoderLibraryLoaded;
    }

    public boolean isDiskCacheProbingEnabled() {
        return this.mIsDiskCacheProbingEnabled;
    }

    public boolean isEncodedMemoryCacheProbingEnabled() {
        return this.mIsEncodedMemoryCacheProbingEnabled;
    }

    public boolean isGingerbreadDecoderEnabled() {
        return this.mGingerbreadDecoderEnabled;
    }

    public boolean shouldDownscaleFrameToDrawableDimensions() {
        return this.mDownscaleFrameToDrawableDimensions;
    }

    public int getBitmapCloseableRefType() {
        return this.mBitmapCloseableRefType;
    }

    public Supplier<Boolean> getSuppressBitmapPrefetchingSupplier() {
        return this.mSuppressBitmapPrefetchingSupplier;
    }

    public long getMemoryType() {
        return this.mMemoryType;
    }

    public int getTrackedKeysSize() {
        return this.mTrackedKeysSize;
    }

    public boolean shouldKeepCancelledFetchAsLowPriority() {
        return this.mKeepCancelledFetchAsLowPriority;
    }

    public boolean shouldUseCombinedNetworkAndCacheProducer() {
        return this.mUseCombinedNetworkAndCacheProducer;
    }

    public boolean allowDelay() {
        return this.mAllowDelay;
    }

    /* loaded from: classes.dex */
    public static class Builder {
        public int mBitmapCloseableRefType;
        private final ImagePipelineConfig.Builder mConfigBuilder;
        public boolean mDownsampleIfLargeBitmap;
        public boolean mDownscaleFrameToDrawableDimensions;
        public boolean mExperimentalThreadHandoffQueueEnabled;
        public boolean mGingerbreadDecoderEnabled;
        private boolean mKeepCancelledFetchAsLowPriority;
        public Supplier<Boolean> mLazyDataSource;
        private ProducerFactoryMethod mProducerFactoryMethod;
        private WebpBitmapFactory mWebpBitmapFactory;
        private WebpBitmapFactory.WebpErrorLogger mWebpErrorLogger;
        private boolean mWebpSupportEnabled = false;
        private boolean mDecodeCancellationEnabled = false;
        private boolean mUseDownsamplingRatioForResizing = false;
        private boolean mUseBitmapPrepareToDraw = false;
        private int mBitmapPrepareToDrawMinSizeBytes = 0;
        private int mBitmapPrepareToDrawMaxSizeBytes = 0;
        public boolean mBitmapPrepareToDrawForPrefetch = false;
        private int mMaxBitmapSize = 2048;
        private boolean mNativeCodeDisabled = false;
        private boolean mPartialImageCachingEnabled = false;
        public Supplier<Boolean> mSuppressBitmapPrefetchingSupplier = Suppliers.of(false);
        public long mMemoryType = 0;
        public boolean mEncodedCacheEnabled = true;
        public boolean mEnsureTranscoderLibraryLoaded = true;
        private boolean mIsEncodedMemoryCacheProbingEnabled = false;
        private boolean mIsDiskCacheProbingEnabled = false;
        private int mTrackedKeysSize = 20;
        private boolean mUseCombinedNetworkAndCacheProducer = false;
        private boolean mAllowDelay = false;

        public Builder(ImagePipelineConfig.Builder configBuilder) {
            this.mConfigBuilder = configBuilder;
        }

        public ImagePipelineConfig.Builder setWebpSupportEnabled(boolean webpSupportEnabled) {
            this.mWebpSupportEnabled = webpSupportEnabled;
            return this.mConfigBuilder;
        }

        public ImagePipelineConfig.Builder setUseDownsampligRatioForResizing(boolean useDownsamplingRatioForResizing) {
            this.mUseDownsamplingRatioForResizing = useDownsamplingRatioForResizing;
            return this.mConfigBuilder;
        }

        public ImagePipelineConfig.Builder setPartialImageCachingEnabled(boolean partialImageCachingEnabled) {
            this.mPartialImageCachingEnabled = partialImageCachingEnabled;
            return this.mConfigBuilder;
        }

        public boolean isPartialImageCachingEnabled() {
            return this.mPartialImageCachingEnabled;
        }

        public ImagePipelineConfig.Builder setDecodeCancellationEnabled(boolean decodeCancellationEnabled) {
            this.mDecodeCancellationEnabled = decodeCancellationEnabled;
            return this.mConfigBuilder;
        }

        public ImagePipelineConfig.Builder setWebpErrorLogger(WebpBitmapFactory.WebpErrorLogger webpErrorLogger) {
            this.mWebpErrorLogger = webpErrorLogger;
            return this.mConfigBuilder;
        }

        public ImagePipelineConfig.Builder setWebpBitmapFactory(WebpBitmapFactory webpBitmapFactory) {
            this.mWebpBitmapFactory = webpBitmapFactory;
            return this.mConfigBuilder;
        }

        public ImagePipelineConfig.Builder setBitmapPrepareToDraw(boolean useBitmapPrepareToDraw, int minBitmapSizeBytes, int maxBitmapSizeBytes, boolean preparePrefetch) {
            this.mUseBitmapPrepareToDraw = useBitmapPrepareToDraw;
            this.mBitmapPrepareToDrawMinSizeBytes = minBitmapSizeBytes;
            this.mBitmapPrepareToDrawMaxSizeBytes = maxBitmapSizeBytes;
            this.mBitmapPrepareToDrawForPrefetch = preparePrefetch;
            return this.mConfigBuilder;
        }

        public ImagePipelineConfig.Builder setMaxBitmapSize(int maxBitmapSize) {
            this.mMaxBitmapSize = maxBitmapSize;
            return this.mConfigBuilder;
        }

        public ImagePipelineConfig.Builder setNativeCodeDisabled(boolean nativeCodeDisabled) {
            this.mNativeCodeDisabled = nativeCodeDisabled;
            return this.mConfigBuilder;
        }

        public ImagePipelineConfig.Builder setProducerFactoryMethod(ProducerFactoryMethod producerFactoryMethod) {
            this.mProducerFactoryMethod = producerFactoryMethod;
            return this.mConfigBuilder;
        }

        public ImagePipelineConfig.Builder setLazyDataSource(Supplier<Boolean> lazyDataSource) {
            this.mLazyDataSource = lazyDataSource;
            return this.mConfigBuilder;
        }

        public ImagePipelineConfig.Builder setGingerbreadDecoderEnabled(boolean gingerbreadDecoderEnabled) {
            this.mGingerbreadDecoderEnabled = gingerbreadDecoderEnabled;
            return this.mConfigBuilder;
        }

        public ImagePipelineConfig.Builder setShouldDownscaleFrameToDrawableDimensions(boolean downscaleFrameToDrawableDimensions) {
            this.mDownscaleFrameToDrawableDimensions = downscaleFrameToDrawableDimensions;
            return this.mConfigBuilder;
        }

        public ImagePipelineConfig.Builder setBitmapCloseableRefType(int bitmapCloseableRefType) {
            this.mBitmapCloseableRefType = bitmapCloseableRefType;
            return this.mConfigBuilder;
        }

        public ImagePipelineConfig.Builder setSuppressBitmapPrefetchingSupplier(Supplier<Boolean> suppressBitmapPrefetchingSupplier) {
            this.mSuppressBitmapPrefetchingSupplier = suppressBitmapPrefetchingSupplier;
            return this.mConfigBuilder;
        }

        public ImagePipelineConfig.Builder setExperimentalThreadHandoffQueueEnabled(boolean experimentalThreadHandoffQueueEnabled) {
            this.mExperimentalThreadHandoffQueueEnabled = experimentalThreadHandoffQueueEnabled;
            return this.mConfigBuilder;
        }

        public ImagePipelineConfig.Builder setExperimentalMemoryType(long MemoryType) {
            this.mMemoryType = MemoryType;
            return this.mConfigBuilder;
        }

        public ImagePipelineConfig.Builder setKeepCancelledFetchAsLowPriority(boolean keepCancelledFetchAsLowPriority) {
            this.mKeepCancelledFetchAsLowPriority = keepCancelledFetchAsLowPriority;
            return this.mConfigBuilder;
        }

        public ImagePipelineConfig.Builder setDownsampleIfLargeBitmap(boolean downsampleIfLargeBitmap) {
            this.mDownsampleIfLargeBitmap = downsampleIfLargeBitmap;
            return this.mConfigBuilder;
        }

        public ImagePipelineConfig.Builder setEncodedCacheEnabled(boolean encodedCacheEnabled) {
            this.mEncodedCacheEnabled = encodedCacheEnabled;
            return this.mConfigBuilder;
        }

        public ImagePipelineConfig.Builder setEnsureTranscoderLibraryLoaded(boolean ensureTranscoderLibraryLoaded) {
            this.mEnsureTranscoderLibraryLoaded = ensureTranscoderLibraryLoaded;
            return this.mConfigBuilder;
        }

        public ImagePipelineConfig.Builder setIsDiskCacheProbingEnabled(boolean isDiskCacheProbingEnabled) {
            this.mIsDiskCacheProbingEnabled = isDiskCacheProbingEnabled;
            return this.mConfigBuilder;
        }

        public ImagePipelineConfig.Builder setIsEncodedMemoryCacheProbingEnabled(boolean isEncodedMemoryCacheProbingEnabled) {
            this.mIsEncodedMemoryCacheProbingEnabled = isEncodedMemoryCacheProbingEnabled;
            return this.mConfigBuilder;
        }

        public ImagePipelineConfig.Builder setTrackedKeysSize(int trackedKeysSize) {
            this.mTrackedKeysSize = trackedKeysSize;
            return this.mConfigBuilder;
        }

        public ImagePipelineConfig.Builder setUseCombinedNetworkAndCacheProducer(boolean useCombinedNetworkAndCacheProducer) {
            this.mUseCombinedNetworkAndCacheProducer = useCombinedNetworkAndCacheProducer;
            return this.mConfigBuilder;
        }

        public ImagePipelineConfig.Builder setAllowDelay(boolean allowDelay) {
            this.mAllowDelay = allowDelay;
            return this.mConfigBuilder;
        }

        public ImagePipelineExperiments build() {
            return new ImagePipelineExperiments(this);
        }
    }

    /* loaded from: classes.dex */
    public static class DefaultProducerFactoryMethod implements ProducerFactoryMethod {
        @Override // com.facebook.imagepipeline.core.ImagePipelineExperiments.ProducerFactoryMethod
        public ProducerFactory createProducerFactory(Context context, ByteArrayPool byteArrayPool, ImageDecoder imageDecoder, ProgressiveJpegConfig progressiveJpegConfig, boolean downsampleEnabled, boolean resizeAndRotateEnabledForNetwork, boolean decodeCancellationEnabled, ExecutorSupplier executorSupplier, PooledByteBufferFactory pooledByteBufferFactory, PooledByteStreams pooledByteStreams, MemoryCache<CacheKey, CloseableImage> bitmapMemoryCache, MemoryCache<CacheKey, PooledByteBuffer> encodedMemoryCache, BufferedDiskCache defaultBufferedDiskCache, BufferedDiskCache smallImageBufferedDiskCache, CacheKeyFactory cacheKeyFactory, PlatformBitmapFactory platformBitmapFactory, int bitmapPrepareToDrawMinSizeBytes, int bitmapPrepareToDrawMaxSizeBytes, boolean bitmapPrepareToDrawForPrefetch, int maxBitmapSize, CloseableReferenceFactory closeableReferenceFactory, boolean keepCancelledFetchAsLowPriority, int trackedKeysSize) {
            return new ProducerFactory(context, byteArrayPool, imageDecoder, progressiveJpegConfig, downsampleEnabled, resizeAndRotateEnabledForNetwork, decodeCancellationEnabled, executorSupplier, pooledByteBufferFactory, bitmapMemoryCache, encodedMemoryCache, defaultBufferedDiskCache, smallImageBufferedDiskCache, cacheKeyFactory, platformBitmapFactory, bitmapPrepareToDrawMinSizeBytes, bitmapPrepareToDrawMaxSizeBytes, bitmapPrepareToDrawForPrefetch, maxBitmapSize, closeableReferenceFactory, keepCancelledFetchAsLowPriority, trackedKeysSize);
        }
    }
}
