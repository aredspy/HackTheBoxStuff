package com.facebook.imagepipeline.producers;

import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class DelayProducer implements Producer<CloseableReference<CloseableImage>> {
    @Nullable
    private final ScheduledExecutorService mBackgroundTasksExecutor;
    private final Producer<CloseableReference<CloseableImage>> mInputProducer;

    public DelayProducer(final Producer<CloseableReference<CloseableImage>> inputProducer, @Nullable final ScheduledExecutorService backgroundTasksExecutor) {
        this.mInputProducer = inputProducer;
        this.mBackgroundTasksExecutor = backgroundTasksExecutor;
    }

    @Override // com.facebook.imagepipeline.producers.Producer
    public void produceResults(final Consumer<CloseableReference<CloseableImage>> consumer, final ProducerContext context) {
        ImageRequest imageRequest = context.getImageRequest();
        ScheduledExecutorService scheduledExecutorService = this.mBackgroundTasksExecutor;
        if (scheduledExecutorService != null) {
            scheduledExecutorService.schedule(new Runnable() { // from class: com.facebook.imagepipeline.producers.DelayProducer.1
                @Override // java.lang.Runnable
                public void run() {
                    DelayProducer.this.mInputProducer.produceResults(consumer, context);
                }
            }, imageRequest.getDelayMs(), TimeUnit.MILLISECONDS);
        } else {
            this.mInputProducer.produceResults(consumer, context);
        }
    }
}
