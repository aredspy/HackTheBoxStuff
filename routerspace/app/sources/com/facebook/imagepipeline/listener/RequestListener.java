package com.facebook.imagepipeline.listener;

import com.facebook.imagepipeline.producers.ProducerListener;
import com.facebook.imagepipeline.request.ImageRequest;
/* loaded from: classes.dex */
public interface RequestListener extends ProducerListener {
    void onRequestCancellation(String requestId);

    void onRequestFailure(ImageRequest request, String requestId, Throwable throwable, boolean isPrefetch);

    void onRequestStart(ImageRequest request, Object callerContext, String requestId, boolean isPrefetch);

    void onRequestSuccess(ImageRequest request, String requestId, boolean isPrefetch);
}
