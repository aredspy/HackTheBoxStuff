package com.facebook.imagepipeline.producers;

import java.util.Map;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public interface ProducerListener2 {
    void onProducerEvent(ProducerContext producerContext, String producerName, String eventName);

    void onProducerFinishWithCancellation(ProducerContext producerContext, String producerName, @Nullable Map<String, String> extraMap);

    void onProducerFinishWithFailure(ProducerContext producerContext, String producerName, Throwable t, @Nullable Map<String, String> extraMap);

    void onProducerFinishWithSuccess(ProducerContext producerContext, String producerName, @Nullable Map<String, String> extraMap);

    void onProducerStart(ProducerContext producerContext, String producerName);

    void onUltimateProducerReached(ProducerContext producerContext, String producerName, boolean successful);

    boolean requiresExtraMap(ProducerContext producerContext, String producerName);
}
