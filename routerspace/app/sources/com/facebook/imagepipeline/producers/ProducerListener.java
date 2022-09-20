package com.facebook.imagepipeline.producers;

import java.util.Map;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public interface ProducerListener {
    void onProducerEvent(String requestId, String producerName, String eventName);

    void onProducerFinishWithCancellation(String requestId, String producerName, @Nullable Map<String, String> extraMap);

    void onProducerFinishWithFailure(String requestId, String producerName, Throwable t, @Nullable Map<String, String> extraMap);

    void onProducerFinishWithSuccess(String requestId, String producerName, @Nullable Map<String, String> extraMap);

    void onProducerStart(String requestId, String producerName);

    void onUltimateProducerReached(String requestId, String producerName, boolean successful);

    boolean requiresExtraMap(String requestId);
}
