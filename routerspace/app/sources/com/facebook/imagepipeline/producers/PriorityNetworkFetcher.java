package com.facebook.imagepipeline.producers;

import com.facebook.common.logging.FLog;
import com.facebook.common.time.MonotonicClock;
import com.facebook.common.time.RealtimeSinceBootClock;
import com.facebook.imagepipeline.common.Priority;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.producers.FetchState;
import com.facebook.imagepipeline.producers.NetworkFetcher;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class PriorityNetworkFetcher<FETCH_STATE extends FetchState> implements NetworkFetcher<PriorityFetchState<FETCH_STATE>> {
    static final int INFINITE_REQUEUE = -1;
    static final int NO_DELAYED_REQUESTS = -1;
    public static final String TAG = "PriorityNetworkFetcher";
    private final boolean doNotCancelRequests;
    private long firstDelayedRequestEnqueuedTimeStamp;
    private final int immediateRequeueCount;
    private final boolean inflightFetchesCanBeCancelled;
    private volatile boolean isRunning;
    private final MonotonicClock mClock;
    private final HashSet<PriorityFetchState<FETCH_STATE>> mCurrentlyFetching;
    private final LinkedList<PriorityFetchState<FETCH_STATE>> mDelayedQueue;
    private final NetworkFetcher<FETCH_STATE> mDelegate;
    private final LinkedList<PriorityFetchState<FETCH_STATE>> mHiPriQueue;
    private final boolean mIsHiPriFifo;
    private final Object mLock;
    private final LinkedList<PriorityFetchState<FETCH_STATE>> mLowPriQueue;
    private final int mMaxOutstandingHiPri;
    private final int mMaxOutstandingLowPri;
    private final int maxNumberOfRequeue;
    private final boolean multipleDequeue;
    private final long requeueDelayTimeInMillis;

    @Override // com.facebook.imagepipeline.producers.NetworkFetcher
    public /* bridge */ /* synthetic */ void fetch(final FetchState fetchState, final NetworkFetcher.Callback callback) {
        fetch((PriorityFetchState) ((PriorityFetchState) fetchState), callback);
    }

    @Override // com.facebook.imagepipeline.producers.NetworkFetcher
    @Nullable
    public /* bridge */ /* synthetic */ Map getExtraMap(FetchState fetchState, int byteSize) {
        return getExtraMap((PriorityFetchState) ((PriorityFetchState) fetchState), byteSize);
    }

    @Override // com.facebook.imagepipeline.producers.NetworkFetcher
    public /* bridge */ /* synthetic */ void onFetchCompletion(FetchState fetchState, int byteSize) {
        onFetchCompletion((PriorityFetchState) ((PriorityFetchState) fetchState), byteSize);
    }

    @Override // com.facebook.imagepipeline.producers.NetworkFetcher
    public /* bridge */ /* synthetic */ boolean shouldPropagate(FetchState fetchState) {
        return shouldPropagate((PriorityFetchState) ((PriorityFetchState) fetchState));
    }

    public PriorityNetworkFetcher(NetworkFetcher<FETCH_STATE> delegate, boolean isHiPriFifo, int maxOutstandingHiPri, int maxOutstandingLowPri, boolean inflightFetchesCanBeCancelled, int maxNumberOfRequeue, boolean doNotCancelRequests, int immediateRequeueCount, int requeueDelayTimeInMillis, boolean multipleDequeue) {
        this(delegate, isHiPriFifo, maxOutstandingHiPri, maxOutstandingLowPri, inflightFetchesCanBeCancelled, maxNumberOfRequeue, doNotCancelRequests, immediateRequeueCount, requeueDelayTimeInMillis, multipleDequeue, RealtimeSinceBootClock.get());
    }

    public PriorityNetworkFetcher(NetworkFetcher<FETCH_STATE> delegate, boolean isHiPriFifo, int maxOutstandingHiPri, int maxOutstandingLowPri, boolean inflightFetchesCanBeCancelled, boolean infiniteRequeue, boolean doNotCancelRequests) {
        this(delegate, isHiPriFifo, maxOutstandingHiPri, maxOutstandingLowPri, inflightFetchesCanBeCancelled, infiniteRequeue ? -1 : 0, doNotCancelRequests, -1, 0, false, RealtimeSinceBootClock.get());
    }

    public PriorityNetworkFetcher(NetworkFetcher<FETCH_STATE> delegate, boolean isHiPriFifo, int maxOutstandingHiPri, int maxOutstandingLowPri, boolean inflightFetchesCanBeCancelled, int maxNumberOfRequeue, boolean doNotCancelRequests, int immediateRequeueCount, int requeueDelayTimeInMillis, boolean multipleDequeue, MonotonicClock clock) {
        this.mLock = new Object();
        this.mHiPriQueue = new LinkedList<>();
        this.mLowPriQueue = new LinkedList<>();
        this.mCurrentlyFetching = new HashSet<>();
        this.mDelayedQueue = new LinkedList<>();
        this.isRunning = true;
        this.mDelegate = delegate;
        this.mIsHiPriFifo = isHiPriFifo;
        this.mMaxOutstandingHiPri = maxOutstandingHiPri;
        this.mMaxOutstandingLowPri = maxOutstandingLowPri;
        if (maxOutstandingHiPri <= maxOutstandingLowPri) {
            throw new IllegalArgumentException("maxOutstandingHiPri should be > maxOutstandingLowPri");
        }
        this.inflightFetchesCanBeCancelled = inflightFetchesCanBeCancelled;
        this.maxNumberOfRequeue = maxNumberOfRequeue;
        this.doNotCancelRequests = doNotCancelRequests;
        this.immediateRequeueCount = immediateRequeueCount;
        this.requeueDelayTimeInMillis = requeueDelayTimeInMillis;
        this.multipleDequeue = multipleDequeue;
        this.mClock = clock;
    }

    public void pause() {
        this.isRunning = false;
    }

    public void resume() {
        this.isRunning = true;
        dequeueIfAvailableSlots();
    }

    public void fetch(final PriorityFetchState<FETCH_STATE> fetchState, final NetworkFetcher.Callback callback) {
        fetchState.getContext().addCallbacks(new BaseProducerContextCallbacks() { // from class: com.facebook.imagepipeline.producers.PriorityNetworkFetcher.1
            @Override // com.facebook.imagepipeline.producers.BaseProducerContextCallbacks, com.facebook.imagepipeline.producers.ProducerContextCallbacks
            public void onCancellationRequested() {
                if (PriorityNetworkFetcher.this.doNotCancelRequests) {
                    return;
                }
                if (!PriorityNetworkFetcher.this.inflightFetchesCanBeCancelled && PriorityNetworkFetcher.this.mCurrentlyFetching.contains(fetchState)) {
                    return;
                }
                PriorityNetworkFetcher.this.removeFromQueue(fetchState, "CANCEL");
                callback.onCancellation();
            }

            @Override // com.facebook.imagepipeline.producers.BaseProducerContextCallbacks, com.facebook.imagepipeline.producers.ProducerContextCallbacks
            public void onPriorityChanged() {
                PriorityNetworkFetcher priorityNetworkFetcher = PriorityNetworkFetcher.this;
                PriorityFetchState priorityFetchState = fetchState;
                priorityNetworkFetcher.changePriority(priorityFetchState, priorityFetchState.getContext().getPriority() == Priority.HIGH);
            }
        });
        synchronized (this.mLock) {
            if (this.mCurrentlyFetching.contains(fetchState)) {
                String str = TAG;
                FLog.e(str, "fetch state was enqueued twice: " + fetchState);
                return;
            }
            boolean z = fetchState.getContext().getPriority() == Priority.HIGH;
            FLog.v(TAG, "enqueue: %s %s", z ? "HI-PRI" : "LOW-PRI", fetchState.getUri());
            fetchState.callback = callback;
            putInQueue(fetchState, z);
            dequeueIfAvailableSlots();
        }
    }

    public void onFetchCompletion(PriorityFetchState<FETCH_STATE> fetchState, int byteSize) {
        removeFromQueue(fetchState, "SUCCESS");
        this.mDelegate.onFetchCompletion(fetchState.delegatedState, byteSize);
    }

    public void removeFromQueue(PriorityFetchState<FETCH_STATE> fetchState, String reasonForLogging) {
        synchronized (this.mLock) {
            FLog.v(TAG, "remove: %s %s", reasonForLogging, fetchState.getUri());
            this.mCurrentlyFetching.remove(fetchState);
            if (!this.mHiPriQueue.remove(fetchState)) {
                this.mLowPriQueue.remove(fetchState);
            }
        }
        dequeueIfAvailableSlots();
    }

    private void moveDelayedRequestsToPriorityQueues() {
        if (this.mDelayedQueue.isEmpty() || this.mClock.now() - this.firstDelayedRequestEnqueuedTimeStamp <= this.requeueDelayTimeInMillis) {
            return;
        }
        Iterator<PriorityFetchState<FETCH_STATE>> it = this.mDelayedQueue.iterator();
        while (it.hasNext()) {
            PriorityFetchState<FETCH_STATE> next = it.next();
            putInQueue(next, next.getContext().getPriority() == Priority.HIGH);
        }
        this.mDelayedQueue.clear();
    }

    private void putInDelayedQueue(PriorityFetchState<FETCH_STATE> fetchState) {
        if (this.mDelayedQueue.isEmpty()) {
            this.firstDelayedRequestEnqueuedTimeStamp = this.mClock.now();
        }
        fetchState.delayCount++;
        this.mDelayedQueue.addLast(fetchState);
    }

    public void requeue(PriorityFetchState<FETCH_STATE> fetchState) {
        synchronized (this.mLock) {
            FLog.v(TAG, "requeue: %s", fetchState.getUri());
            boolean z = true;
            fetchState.requeueCount++;
            fetchState.delegatedState = this.mDelegate.createFetchState(fetchState.getConsumer(), fetchState.getContext());
            this.mCurrentlyFetching.remove(fetchState);
            if (!this.mHiPriQueue.remove(fetchState)) {
                this.mLowPriQueue.remove(fetchState);
            }
            if (this.immediateRequeueCount != -1 && fetchState.requeueCount > this.immediateRequeueCount) {
                putInDelayedQueue(fetchState);
            } else {
                if (fetchState.getContext().getPriority() != Priority.HIGH) {
                    z = false;
                }
                putInQueue(fetchState, z);
            }
        }
        dequeueIfAvailableSlots();
    }

    private void dequeueIfAvailableSlots() {
        if (!this.isRunning) {
            return;
        }
        PriorityFetchState<FETCH_STATE> priorityFetchState = null;
        synchronized (this.mLock) {
            moveDelayedRequestsToPriorityQueues();
            int size = this.mCurrentlyFetching.size();
            if (size < this.mMaxOutstandingHiPri) {
                priorityFetchState = this.mHiPriQueue.pollFirst();
            }
            if (priorityFetchState == null && size < this.mMaxOutstandingLowPri) {
                priorityFetchState = this.mLowPriQueue.pollFirst();
            }
            if (priorityFetchState == null) {
                return;
            }
            priorityFetchState.dequeuedTimestamp = this.mClock.now();
            this.mCurrentlyFetching.add(priorityFetchState);
            FLog.v(TAG, "fetching: %s (concurrent: %s hi-pri queue: %s low-pri queue: %s)", priorityFetchState.getUri(), Integer.valueOf(size), Integer.valueOf(this.mHiPriQueue.size()), Integer.valueOf(this.mLowPriQueue.size()));
            delegateFetch(priorityFetchState);
            if (!this.multipleDequeue) {
                return;
            }
            dequeueIfAvailableSlots();
        }
    }

    private void delegateFetch(final PriorityFetchState<FETCH_STATE> fetchState) {
        try {
            this.mDelegate.fetch(fetchState.delegatedState, new NetworkFetcher.Callback() { // from class: com.facebook.imagepipeline.producers.PriorityNetworkFetcher.2
                @Override // com.facebook.imagepipeline.producers.NetworkFetcher.Callback
                public void onResponse(InputStream response, int responseLength) throws IOException {
                    NetworkFetcher.Callback callback = fetchState.callback;
                    if (callback != null) {
                        callback.onResponse(response, responseLength);
                    }
                }

                @Override // com.facebook.imagepipeline.producers.NetworkFetcher.Callback
                public void onFailure(Throwable throwable) {
                    if (!(PriorityNetworkFetcher.this.maxNumberOfRequeue == -1 || fetchState.requeueCount < PriorityNetworkFetcher.this.maxNumberOfRequeue) || (throwable instanceof NonrecoverableException)) {
                        PriorityNetworkFetcher.this.removeFromQueue(fetchState, "FAIL");
                        NetworkFetcher.Callback callback = fetchState.callback;
                        if (callback == null) {
                            return;
                        }
                        callback.onFailure(throwable);
                        return;
                    }
                    PriorityNetworkFetcher.this.requeue(fetchState);
                }

                @Override // com.facebook.imagepipeline.producers.NetworkFetcher.Callback
                public void onCancellation() {
                    PriorityNetworkFetcher.this.removeFromQueue(fetchState, "CANCEL");
                    NetworkFetcher.Callback callback = fetchState.callback;
                    if (callback != null) {
                        callback.onCancellation();
                    }
                }
            });
        } catch (Exception unused) {
            removeFromQueue(fetchState, "FAIL");
        }
    }

    private void changePriorityInDelayedQueue(PriorityFetchState<FETCH_STATE> fetchState) {
        if (!this.mDelayedQueue.remove(fetchState)) {
            return;
        }
        fetchState.priorityChangedCount++;
        this.mDelayedQueue.addLast(fetchState);
    }

    public void changePriority(PriorityFetchState<FETCH_STATE> fetchState, boolean isNewHiPri) {
        synchronized (this.mLock) {
            if (!(isNewHiPri ? this.mLowPriQueue : this.mHiPriQueue).remove(fetchState)) {
                changePriorityInDelayedQueue(fetchState);
                return;
            }
            FLog.v(TAG, "change-pri: %s %s", isNewHiPri ? "HIPRI" : "LOWPRI", fetchState.getUri());
            fetchState.priorityChangedCount++;
            putInQueue(fetchState, isNewHiPri);
            dequeueIfAvailableSlots();
        }
    }

    private void putInQueue(PriorityFetchState<FETCH_STATE> entry, boolean isHiPri) {
        if (isHiPri) {
            if (this.mIsHiPriFifo) {
                this.mHiPriQueue.addLast(entry);
                return;
            } else {
                this.mHiPriQueue.addFirst(entry);
                return;
            }
        }
        this.mLowPriQueue.addLast(entry);
    }

    List<PriorityFetchState<FETCH_STATE>> getHiPriQueue() {
        return this.mHiPriQueue;
    }

    List<PriorityFetchState<FETCH_STATE>> getLowPriQueue() {
        return this.mLowPriQueue;
    }

    List<PriorityFetchState<FETCH_STATE>> getDelayedQeueue() {
        return this.mDelayedQueue;
    }

    HashSet<PriorityFetchState<FETCH_STATE>> getCurrentlyFetching() {
        return this.mCurrentlyFetching;
    }

    /* loaded from: classes.dex */
    public static class PriorityFetchState<FETCH_STATE extends FetchState> extends FetchState {
        @Nullable
        NetworkFetcher.Callback callback;
        final int currentlyFetchingCountWhenCreated;
        int delayCount;
        public FETCH_STATE delegatedState;
        long dequeuedTimestamp;
        final long enqueuedTimestamp;
        final int hiPriCountWhenCreated;
        final boolean isInitialPriorityHigh;
        final int lowPriCountWhenCreated;
        int priorityChangedCount;
        int requeueCount;

        private PriorityFetchState(Consumer<EncodedImage> consumer, ProducerContext producerContext, FETCH_STATE delegatedState, long enqueuedTimestamp, int hiPriCountWhenCreated, int lowPriCountWhenCreated, int currentlyFetchingCountWhenCreated) {
            super(consumer, producerContext);
            boolean z = false;
            this.requeueCount = 0;
            this.delayCount = 0;
            this.priorityChangedCount = 0;
            this.delegatedState = delegatedState;
            this.enqueuedTimestamp = enqueuedTimestamp;
            this.hiPriCountWhenCreated = hiPriCountWhenCreated;
            this.lowPriCountWhenCreated = lowPriCountWhenCreated;
            this.isInitialPriorityHigh = producerContext.getPriority() == Priority.HIGH ? true : z;
            this.currentlyFetchingCountWhenCreated = currentlyFetchingCountWhenCreated;
        }
    }

    /* loaded from: classes.dex */
    public static class NonrecoverableException extends Throwable {
        public NonrecoverableException(String message) {
            super(message);
        }
    }

    @Override // com.facebook.imagepipeline.producers.NetworkFetcher
    public PriorityFetchState<FETCH_STATE> createFetchState(Consumer<EncodedImage> consumer, ProducerContext producerContext) {
        return new PriorityFetchState<>(consumer, producerContext, this.mDelegate.createFetchState(consumer, producerContext), this.mClock.now(), this.mHiPriQueue.size(), this.mLowPriQueue.size(), this.mCurrentlyFetching.size());
    }

    public boolean shouldPropagate(PriorityFetchState<FETCH_STATE> fetchState) {
        return this.mDelegate.shouldPropagate(fetchState.delegatedState);
    }

    @Nullable
    public Map<String, String> getExtraMap(PriorityFetchState<FETCH_STATE> fetchState, int byteSize) {
        Map<String, String> extraMap = this.mDelegate.getExtraMap(fetchState.delegatedState, byteSize);
        HashMap hashMap = extraMap != null ? new HashMap(extraMap) : new HashMap();
        hashMap.put("pri_queue_time", "" + (fetchState.dequeuedTimestamp - fetchState.enqueuedTimestamp));
        hashMap.put("hipri_queue_size", "" + fetchState.hiPriCountWhenCreated);
        hashMap.put("lowpri_queue_size", "" + fetchState.lowPriCountWhenCreated);
        hashMap.put("requeueCount", "" + fetchState.requeueCount);
        hashMap.put("priority_changed_count", "" + fetchState.priorityChangedCount);
        hashMap.put("request_initial_priority_is_high", "" + fetchState.isInitialPriorityHigh);
        hashMap.put("currently_fetching_size", "" + fetchState.currentlyFetchingCountWhenCreated);
        hashMap.put("delay_count", "" + fetchState.delayCount);
        return hashMap;
    }
}
