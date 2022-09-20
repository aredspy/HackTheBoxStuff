package com.facebook.imagepipeline.producers;

import com.facebook.common.internal.ImmutableSet;
import com.facebook.imagepipeline.common.Priority;
import com.facebook.imagepipeline.core.ImagePipelineConfigInterface;
import com.facebook.imagepipeline.image.EncodedImageOrigin;
import com.facebook.imagepipeline.producers.ProducerContext;
import com.facebook.imagepipeline.request.ImageRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class BaseProducerContext implements ProducerContext {
    public static final Set<String> INITIAL_KEYS = ImmutableSet.of((Object[]) new String[]{"id", ProducerContext.ExtraKeys.SOURCE_URI});
    private static final String ORIGIN_SUBCATEGORY_DEFAULT = "default";
    private final List<ProducerContextCallbacks> mCallbacks;
    private final Object mCallerContext;
    private EncodedImageOrigin mEncodedImageOrigin;
    private final Map<String, Object> mExtras;
    private final String mId;
    private final ImagePipelineConfigInterface mImagePipelineConfig;
    private final ImageRequest mImageRequest;
    private boolean mIsCancelled;
    private boolean mIsIntermediateResultExpected;
    private boolean mIsPrefetch;
    private final ImageRequest.RequestLevel mLowestPermittedRequestLevel;
    private Priority mPriority;
    private final ProducerListener2 mProducerListener;
    @Nullable
    private final String mUiComponentId;

    public BaseProducerContext(ImageRequest imageRequest, String id, ProducerListener2 producerListener, Object callerContext, ImageRequest.RequestLevel lowestPermittedRequestLevel, boolean isPrefetch, boolean isIntermediateResultExpected, Priority priority, ImagePipelineConfigInterface imagePipelineConfig) {
        this(imageRequest, id, null, producerListener, callerContext, lowestPermittedRequestLevel, isPrefetch, isIntermediateResultExpected, priority, imagePipelineConfig);
    }

    public BaseProducerContext(ImageRequest imageRequest, String id, @Nullable String uiComponentId, ProducerListener2 producerListener, Object callerContext, ImageRequest.RequestLevel lowestPermittedRequestLevel, boolean isPrefetch, boolean isIntermediateResultExpected, Priority priority, ImagePipelineConfigInterface imagePipelineConfig) {
        this.mEncodedImageOrigin = EncodedImageOrigin.NOT_SET;
        this.mImageRequest = imageRequest;
        this.mId = id;
        HashMap hashMap = new HashMap();
        this.mExtras = hashMap;
        hashMap.put("id", id);
        hashMap.put(ProducerContext.ExtraKeys.SOURCE_URI, imageRequest == null ? "null-request" : imageRequest.getSourceUri());
        this.mUiComponentId = uiComponentId;
        this.mProducerListener = producerListener;
        this.mCallerContext = callerContext;
        this.mLowestPermittedRequestLevel = lowestPermittedRequestLevel;
        this.mIsPrefetch = isPrefetch;
        this.mPriority = priority;
        this.mIsIntermediateResultExpected = isIntermediateResultExpected;
        this.mIsCancelled = false;
        this.mCallbacks = new ArrayList();
        this.mImagePipelineConfig = imagePipelineConfig;
    }

    @Override // com.facebook.imagepipeline.producers.ProducerContext
    public ImageRequest getImageRequest() {
        return this.mImageRequest;
    }

    @Override // com.facebook.imagepipeline.producers.ProducerContext
    public String getId() {
        return this.mId;
    }

    @Override // com.facebook.imagepipeline.producers.ProducerContext
    @Nullable
    public String getUiComponentId() {
        return this.mUiComponentId;
    }

    @Override // com.facebook.imagepipeline.producers.ProducerContext
    public ProducerListener2 getProducerListener() {
        return this.mProducerListener;
    }

    @Override // com.facebook.imagepipeline.producers.ProducerContext
    public Object getCallerContext() {
        return this.mCallerContext;
    }

    @Override // com.facebook.imagepipeline.producers.ProducerContext
    public ImageRequest.RequestLevel getLowestPermittedRequestLevel() {
        return this.mLowestPermittedRequestLevel;
    }

    @Override // com.facebook.imagepipeline.producers.ProducerContext
    public synchronized boolean isPrefetch() {
        return this.mIsPrefetch;
    }

    @Override // com.facebook.imagepipeline.producers.ProducerContext
    public synchronized Priority getPriority() {
        return this.mPriority;
    }

    @Override // com.facebook.imagepipeline.producers.ProducerContext
    public synchronized boolean isIntermediateResultExpected() {
        return this.mIsIntermediateResultExpected;
    }

    public synchronized boolean isCancelled() {
        return this.mIsCancelled;
    }

    @Override // com.facebook.imagepipeline.producers.ProducerContext
    public void addCallbacks(ProducerContextCallbacks callbacks) {
        boolean z;
        synchronized (this) {
            this.mCallbacks.add(callbacks);
            z = this.mIsCancelled;
        }
        if (z) {
            callbacks.onCancellationRequested();
        }
    }

    @Override // com.facebook.imagepipeline.producers.ProducerContext
    public ImagePipelineConfigInterface getImagePipelineConfig() {
        return this.mImagePipelineConfig;
    }

    @Override // com.facebook.imagepipeline.producers.ProducerContext
    public EncodedImageOrigin getEncodedImageOrigin() {
        return this.mEncodedImageOrigin;
    }

    @Override // com.facebook.imagepipeline.producers.ProducerContext
    public void setEncodedImageOrigin(EncodedImageOrigin encodedImageOrigin) {
        this.mEncodedImageOrigin = encodedImageOrigin;
    }

    public void cancel() {
        callOnCancellationRequested(cancelNoCallbacks());
    }

    @Nullable
    public synchronized List<ProducerContextCallbacks> setIsPrefetchNoCallbacks(boolean isPrefetch) {
        if (isPrefetch == this.mIsPrefetch) {
            return null;
        }
        this.mIsPrefetch = isPrefetch;
        return new ArrayList(this.mCallbacks);
    }

    @Nullable
    public synchronized List<ProducerContextCallbacks> setPriorityNoCallbacks(Priority priority) {
        if (priority == this.mPriority) {
            return null;
        }
        this.mPriority = priority;
        return new ArrayList(this.mCallbacks);
    }

    @Nullable
    public synchronized List<ProducerContextCallbacks> setIsIntermediateResultExpectedNoCallbacks(boolean isIntermediateResultExpected) {
        if (isIntermediateResultExpected == this.mIsIntermediateResultExpected) {
            return null;
        }
        this.mIsIntermediateResultExpected = isIntermediateResultExpected;
        return new ArrayList(this.mCallbacks);
    }

    @Nullable
    public synchronized List<ProducerContextCallbacks> cancelNoCallbacks() {
        if (this.mIsCancelled) {
            return null;
        }
        this.mIsCancelled = true;
        return new ArrayList(this.mCallbacks);
    }

    public static void callOnCancellationRequested(@Nullable List<ProducerContextCallbacks> callbacks) {
        if (callbacks == null) {
            return;
        }
        for (ProducerContextCallbacks producerContextCallbacks : callbacks) {
            producerContextCallbacks.onCancellationRequested();
        }
    }

    public static void callOnIsPrefetchChanged(@Nullable List<ProducerContextCallbacks> callbacks) {
        if (callbacks == null) {
            return;
        }
        for (ProducerContextCallbacks producerContextCallbacks : callbacks) {
            producerContextCallbacks.onIsPrefetchChanged();
        }
    }

    public static void callOnIsIntermediateResultExpectedChanged(@Nullable List<ProducerContextCallbacks> callbacks) {
        if (callbacks == null) {
            return;
        }
        for (ProducerContextCallbacks producerContextCallbacks : callbacks) {
            producerContextCallbacks.onIsIntermediateResultExpectedChanged();
        }
    }

    public static void callOnPriorityChanged(@Nullable List<ProducerContextCallbacks> callbacks) {
        if (callbacks == null) {
            return;
        }
        for (ProducerContextCallbacks producerContextCallbacks : callbacks) {
            producerContextCallbacks.onPriorityChanged();
        }
    }

    @Override // com.facebook.imagepipeline.producers.ProducerContext
    public void setExtra(String key, @Nullable Object value) {
        if (INITIAL_KEYS.contains(key)) {
            return;
        }
        this.mExtras.put(key, value);
    }

    @Override // com.facebook.imagepipeline.producers.ProducerContext
    public void putExtras(@Nullable Map<String, ?> extras) {
        if (extras == null) {
            return;
        }
        for (Map.Entry<String, ?> entry : extras.entrySet()) {
            setExtra(entry.getKey(), entry.getValue());
        }
    }

    @Override // com.facebook.imagepipeline.producers.ProducerContext
    @Nullable
    public <T> T getExtra(String key) {
        return (T) this.mExtras.get(key);
    }

    @Override // com.facebook.imagepipeline.producers.ProducerContext
    @Nullable
    public <E> E getExtra(String key, @Nullable E valueIfNotFound) {
        E e = (E) this.mExtras.get(key);
        return e == null ? valueIfNotFound : e;
    }

    @Override // com.facebook.imagepipeline.producers.ProducerContext
    public Map<String, Object> getExtras() {
        return this.mExtras;
    }

    @Override // com.facebook.imagepipeline.producers.ProducerContext
    public void putOriginExtra(@Nullable String origin, @Nullable String subcategory) {
        this.mExtras.put(ProducerContext.ExtraKeys.ORIGIN, origin);
        this.mExtras.put(ProducerContext.ExtraKeys.ORIGIN_SUBCATEGORY, subcategory);
    }

    @Override // com.facebook.imagepipeline.producers.ProducerContext
    public void putOriginExtra(@Nullable String origin) {
        putOriginExtra(origin, ORIGIN_SUBCATEGORY_DEFAULT);
    }
}
