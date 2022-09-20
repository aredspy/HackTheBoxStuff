package com.facebook.imagepipeline.producers;

import com.facebook.common.executors.StatefulRunnable;
import java.util.Map;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public abstract class StatefulProducerRunnable<T> extends StatefulRunnable<T> {
    private final Consumer<T> mConsumer;
    private final ProducerContext mProducerContext;
    private final ProducerListener2 mProducerListener;
    private final String mProducerName;

    @Override // com.facebook.common.executors.StatefulRunnable
    protected abstract void disposeResult(@Nullable T result);

    @Nullable
    protected Map<String, String> getExtraMapOnCancellation() {
        return null;
    }

    @Nullable
    protected Map<String, String> getExtraMapOnFailure(Exception exception) {
        return null;
    }

    @Nullable
    protected Map<String, String> getExtraMapOnSuccess(@Nullable T result) {
        return null;
    }

    public StatefulProducerRunnable(Consumer<T> consumer, ProducerListener2 producerListener, ProducerContext producerContext, String producerName) {
        this.mConsumer = consumer;
        this.mProducerListener = producerListener;
        this.mProducerName = producerName;
        this.mProducerContext = producerContext;
        producerListener.onProducerStart(producerContext, producerName);
    }

    @Override // com.facebook.common.executors.StatefulRunnable
    public void onSuccess(@Nullable T result) {
        ProducerListener2 producerListener2 = this.mProducerListener;
        ProducerContext producerContext = this.mProducerContext;
        String str = this.mProducerName;
        producerListener2.onProducerFinishWithSuccess(producerContext, str, producerListener2.requiresExtraMap(producerContext, str) ? getExtraMapOnSuccess(result) : null);
        this.mConsumer.onNewResult(result, 1);
    }

    @Override // com.facebook.common.executors.StatefulRunnable
    public void onFailure(Exception e) {
        ProducerListener2 producerListener2 = this.mProducerListener;
        ProducerContext producerContext = this.mProducerContext;
        String str = this.mProducerName;
        producerListener2.onProducerFinishWithFailure(producerContext, str, e, producerListener2.requiresExtraMap(producerContext, str) ? getExtraMapOnFailure(e) : null);
        this.mConsumer.onFailure(e);
    }

    @Override // com.facebook.common.executors.StatefulRunnable
    public void onCancellation() {
        ProducerListener2 producerListener2 = this.mProducerListener;
        ProducerContext producerContext = this.mProducerContext;
        String str = this.mProducerName;
        producerListener2.onProducerFinishWithCancellation(producerContext, str, producerListener2.requiresExtraMap(producerContext, str) ? getExtraMapOnCancellation() : null);
        this.mConsumer.onCancellation();
    }
}
