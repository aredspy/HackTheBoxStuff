package com.facebook.imagepipeline.producers;

import java.util.Map;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class InternalProducerListener implements ProducerListener2 {
    @Nullable
    private final ProducerListener mProducerListener;
    @Nullable
    private final ProducerListener2 mProducerListener2;

    public InternalProducerListener(@Nullable ProducerListener producerListener, @Nullable ProducerListener2 producerListener2) {
        this.mProducerListener = producerListener;
        this.mProducerListener2 = producerListener2;
    }

    @Nullable
    public ProducerListener getProducerListener() {
        return this.mProducerListener;
    }

    @Nullable
    public ProducerListener2 getProducerListener2() {
        return this.mProducerListener2;
    }

    @Override // com.facebook.imagepipeline.producers.ProducerListener2
    public void onProducerStart(ProducerContext context, String producerName) {
        ProducerListener producerListener = this.mProducerListener;
        if (producerListener != null) {
            producerListener.onProducerStart(context.getId(), producerName);
        }
        ProducerListener2 producerListener2 = this.mProducerListener2;
        if (producerListener2 != null) {
            producerListener2.onProducerStart(context, producerName);
        }
    }

    @Override // com.facebook.imagepipeline.producers.ProducerListener2
    public void onProducerEvent(ProducerContext context, String producerName, String eventName) {
        ProducerListener producerListener = this.mProducerListener;
        if (producerListener != null) {
            producerListener.onProducerEvent(context.getId(), producerName, eventName);
        }
        ProducerListener2 producerListener2 = this.mProducerListener2;
        if (producerListener2 != null) {
            producerListener2.onProducerEvent(context, producerName, eventName);
        }
    }

    @Override // com.facebook.imagepipeline.producers.ProducerListener2
    public void onProducerFinishWithSuccess(ProducerContext context, String producerName, @Nullable Map<String, String> extraMap) {
        ProducerListener producerListener = this.mProducerListener;
        if (producerListener != null) {
            producerListener.onProducerFinishWithSuccess(context.getId(), producerName, extraMap);
        }
        ProducerListener2 producerListener2 = this.mProducerListener2;
        if (producerListener2 != null) {
            producerListener2.onProducerFinishWithSuccess(context, producerName, extraMap);
        }
    }

    @Override // com.facebook.imagepipeline.producers.ProducerListener2
    public void onProducerFinishWithFailure(ProducerContext context, String producerName, Throwable t, @Nullable Map<String, String> extraMap) {
        ProducerListener producerListener = this.mProducerListener;
        if (producerListener != null) {
            producerListener.onProducerFinishWithFailure(context.getId(), producerName, t, extraMap);
        }
        ProducerListener2 producerListener2 = this.mProducerListener2;
        if (producerListener2 != null) {
            producerListener2.onProducerFinishWithFailure(context, producerName, t, extraMap);
        }
    }

    @Override // com.facebook.imagepipeline.producers.ProducerListener2
    public void onProducerFinishWithCancellation(ProducerContext context, String producerName, @Nullable Map<String, String> extraMap) {
        ProducerListener producerListener = this.mProducerListener;
        if (producerListener != null) {
            producerListener.onProducerFinishWithCancellation(context.getId(), producerName, extraMap);
        }
        ProducerListener2 producerListener2 = this.mProducerListener2;
        if (producerListener2 != null) {
            producerListener2.onProducerFinishWithCancellation(context, producerName, extraMap);
        }
    }

    @Override // com.facebook.imagepipeline.producers.ProducerListener2
    public void onUltimateProducerReached(ProducerContext context, String producerName, boolean successful) {
        ProducerListener producerListener = this.mProducerListener;
        if (producerListener != null) {
            producerListener.onUltimateProducerReached(context.getId(), producerName, successful);
        }
        ProducerListener2 producerListener2 = this.mProducerListener2;
        if (producerListener2 != null) {
            producerListener2.onUltimateProducerReached(context, producerName, successful);
        }
    }

    @Override // com.facebook.imagepipeline.producers.ProducerListener2
    public boolean requiresExtraMap(ProducerContext context, String producerName) {
        ProducerListener2 producerListener2;
        ProducerListener producerListener = this.mProducerListener;
        boolean requiresExtraMap = producerListener != null ? producerListener.requiresExtraMap(context.getId()) : false;
        return (requiresExtraMap || (producerListener2 = this.mProducerListener2) == null) ? requiresExtraMap : producerListener2.requiresExtraMap(context, producerName);
    }
}
