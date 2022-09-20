package com.facebook.imagepipeline.producers;

import com.facebook.common.internal.Preconditions;
import com.facebook.imagepipeline.instrumentation.FrescoInstrumenter;
import com.facebook.imagepipeline.systrace.FrescoSystrace;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class ThreadHandoffProducer<T> implements Producer<T> {
    public static final String PRODUCER_NAME = "BackgroundThreadHandoffProducer";
    private final Producer<T> mInputProducer;
    private final ThreadHandoffProducerQueue mThreadHandoffProducerQueue;

    public ThreadHandoffProducer(final Producer<T> inputProducer, final ThreadHandoffProducerQueue inputThreadHandoffProducerQueue) {
        this.mInputProducer = (Producer) Preconditions.checkNotNull(inputProducer);
        this.mThreadHandoffProducerQueue = inputThreadHandoffProducerQueue;
    }

    @Override // com.facebook.imagepipeline.producers.Producer
    public void produceResults(final Consumer<T> consumer, final ProducerContext context) {
        try {
            if (FrescoSystrace.isTracing()) {
                FrescoSystrace.beginSection("ThreadHandoffProducer#produceResults");
            }
            final ProducerListener2 producerListener = context.getProducerListener();
            final StatefulProducerRunnable<T> statefulProducerRunnable = new StatefulProducerRunnable<T>(consumer, producerListener, context, PRODUCER_NAME) { // from class: com.facebook.imagepipeline.producers.ThreadHandoffProducer.1
                @Override // com.facebook.imagepipeline.producers.StatefulProducerRunnable, com.facebook.common.executors.StatefulRunnable
                protected void disposeResult(@Nullable T ignored) {
                }

                @Override // com.facebook.common.executors.StatefulRunnable
                @Nullable
                protected T getResult() throws Exception {
                    return null;
                }

                @Override // com.facebook.imagepipeline.producers.StatefulProducerRunnable, com.facebook.common.executors.StatefulRunnable
                public void onSuccess(@Nullable T ignored) {
                    producerListener.onProducerFinishWithSuccess(context, ThreadHandoffProducer.PRODUCER_NAME, null);
                    ThreadHandoffProducer.this.mInputProducer.produceResults(consumer, context);
                }
            };
            context.addCallbacks(new BaseProducerContextCallbacks() { // from class: com.facebook.imagepipeline.producers.ThreadHandoffProducer.2
                @Override // com.facebook.imagepipeline.producers.BaseProducerContextCallbacks, com.facebook.imagepipeline.producers.ProducerContextCallbacks
                public void onCancellationRequested() {
                    statefulProducerRunnable.cancel();
                    ThreadHandoffProducer.this.mThreadHandoffProducerQueue.remove(statefulProducerRunnable);
                }
            });
            this.mThreadHandoffProducerQueue.addToQueueOrExecute(FrescoInstrumenter.decorateRunnable(statefulProducerRunnable, getInstrumentationTag(context)));
        } finally {
            if (FrescoSystrace.isTracing()) {
                FrescoSystrace.endSection();
            }
        }
    }

    @Nullable
    private static String getInstrumentationTag(ProducerContext context) {
        if (FrescoInstrumenter.isTracing()) {
            return "ThreadHandoffProducer_produceResults_" + context.getId();
        }
        return null;
    }
}
