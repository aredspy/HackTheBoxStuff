package com.facebook.imagepipeline.producers;

import com.facebook.imagepipeline.common.Priority;
import com.facebook.imagepipeline.core.ImagePipelineConfigInterface;
import com.facebook.imagepipeline.request.ImageRequest;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class SettableProducerContext extends BaseProducerContext {
    public SettableProducerContext(ProducerContext context) {
        this(context.getImageRequest(), context.getId(), context.getUiComponentId(), context.getProducerListener(), context.getCallerContext(), context.getLowestPermittedRequestLevel(), context.isPrefetch(), context.isIntermediateResultExpected(), context.getPriority(), context.getImagePipelineConfig());
    }

    public SettableProducerContext(ImageRequest overrideRequest, ProducerContext context) {
        this(overrideRequest, context.getId(), context.getUiComponentId(), context.getProducerListener(), context.getCallerContext(), context.getLowestPermittedRequestLevel(), context.isPrefetch(), context.isIntermediateResultExpected(), context.getPriority(), context.getImagePipelineConfig());
    }

    public SettableProducerContext(ImageRequest imageRequest, String id, ProducerListener2 producerListener, Object callerContext, ImageRequest.RequestLevel lowestPermittedRequestLevel, boolean isPrefetch, boolean isIntermediateResultExpected, Priority priority, ImagePipelineConfigInterface imagePipelineConfig) {
        super(imageRequest, id, producerListener, callerContext, lowestPermittedRequestLevel, isPrefetch, isIntermediateResultExpected, priority, imagePipelineConfig);
    }

    public SettableProducerContext(ImageRequest imageRequest, String id, @Nullable String uiComponentId, ProducerListener2 producerListener, Object callerContext, ImageRequest.RequestLevel lowestPermittedRequestLevel, boolean isPrefetch, boolean isIntermediateResultExpected, Priority priority, ImagePipelineConfigInterface imagePipelineConfig) {
        super(imageRequest, id, uiComponentId, producerListener, callerContext, lowestPermittedRequestLevel, isPrefetch, isIntermediateResultExpected, priority, imagePipelineConfig);
    }

    public void setIsPrefetch(boolean isPrefetch) {
        BaseProducerContext.callOnIsPrefetchChanged(setIsPrefetchNoCallbacks(isPrefetch));
    }

    public void setIsIntermediateResultExpected(boolean isIntermediateResultExpected) {
        BaseProducerContext.callOnIsIntermediateResultExpectedChanged(setIsIntermediateResultExpectedNoCallbacks(isIntermediateResultExpected));
    }

    public void setPriority(Priority priority) {
        BaseProducerContext.callOnPriorityChanged(setPriorityNoCallbacks(priority));
    }
}
