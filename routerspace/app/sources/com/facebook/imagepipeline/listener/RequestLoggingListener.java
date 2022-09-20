package com.facebook.imagepipeline.listener;

import android.os.SystemClock;
import android.util.Pair;
import com.facebook.common.logging.FLog;
import com.facebook.imagepipeline.request.ImageRequest;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class RequestLoggingListener implements RequestListener {
    private static final String TAG = "RequestLoggingListener";
    private final Map<Pair<String, String>, Long> mProducerStartTimeMap = new HashMap();
    private final Map<String, Long> mRequestStartTimeMap = new HashMap();

    @Override // com.facebook.imagepipeline.listener.RequestListener
    public synchronized void onRequestStart(ImageRequest request, Object callerContextObject, String requestId, boolean isPrefetch) {
        if (FLog.isLoggable(2)) {
            FLog.v(TAG, "time %d: onRequestSubmit: {requestId: %s, callerContext: %s, isPrefetch: %b}", Long.valueOf(getTime()), requestId, callerContextObject, Boolean.valueOf(isPrefetch));
            this.mRequestStartTimeMap.put(requestId, Long.valueOf(getTime()));
        }
    }

    @Override // com.facebook.imagepipeline.producers.ProducerListener
    public synchronized void onProducerStart(String requestId, String producerName) {
        if (FLog.isLoggable(2)) {
            Pair<String, String> create = Pair.create(requestId, producerName);
            long time = getTime();
            this.mProducerStartTimeMap.put(create, Long.valueOf(time));
            FLog.v(TAG, "time %d: onProducerStart: {requestId: %s, producer: %s}", Long.valueOf(time), requestId, producerName);
        }
    }

    @Override // com.facebook.imagepipeline.producers.ProducerListener
    public synchronized void onProducerFinishWithSuccess(String requestId, String producerName, @Nullable Map<String, String> extraMap) {
        if (FLog.isLoggable(2)) {
            Pair create = Pair.create(requestId, producerName);
            long time = getTime();
            FLog.v(TAG, "time %d: onProducerFinishWithSuccess: {requestId: %s, producer: %s, elapsedTime: %d ms, extraMap: %s}", Long.valueOf(time), requestId, producerName, Long.valueOf(getElapsedTime(this.mProducerStartTimeMap.remove(create), time)), extraMap);
        }
    }

    @Override // com.facebook.imagepipeline.producers.ProducerListener
    public synchronized void onProducerFinishWithFailure(String requestId, String producerName, Throwable throwable, @Nullable Map<String, String> extraMap) {
        if (FLog.isLoggable(5)) {
            Pair create = Pair.create(requestId, producerName);
            long time = getTime();
            FLog.w(TAG, throwable, "time %d: onProducerFinishWithFailure: {requestId: %s, stage: %s, elapsedTime: %d ms, extraMap: %s, throwable: %s}", Long.valueOf(time), requestId, producerName, Long.valueOf(getElapsedTime(this.mProducerStartTimeMap.remove(create), time)), extraMap, throwable.toString());
        }
    }

    @Override // com.facebook.imagepipeline.producers.ProducerListener
    public synchronized void onProducerFinishWithCancellation(String requestId, String producerName, @Nullable Map<String, String> extraMap) {
        if (FLog.isLoggable(2)) {
            Pair create = Pair.create(requestId, producerName);
            long time = getTime();
            FLog.v(TAG, "time %d: onProducerFinishWithCancellation: {requestId: %s, stage: %s, elapsedTime: %d ms, extraMap: %s}", Long.valueOf(time), requestId, producerName, Long.valueOf(getElapsedTime(this.mProducerStartTimeMap.remove(create), time)), extraMap);
        }
    }

    @Override // com.facebook.imagepipeline.producers.ProducerListener
    public synchronized void onProducerEvent(String requestId, String producerName, String producerEventName) {
        if (FLog.isLoggable(2)) {
            FLog.v(TAG, "time %d: onProducerEvent: {requestId: %s, stage: %s, eventName: %s; elapsedTime: %d ms}", Long.valueOf(getTime()), requestId, producerName, producerEventName, Long.valueOf(getElapsedTime(this.mProducerStartTimeMap.get(Pair.create(requestId, producerName)), getTime())));
        }
    }

    @Override // com.facebook.imagepipeline.producers.ProducerListener
    public synchronized void onUltimateProducerReached(String requestId, String producerName, boolean successful) {
        if (FLog.isLoggable(2)) {
            Pair create = Pair.create(requestId, producerName);
            long time = getTime();
            FLog.v(TAG, "time %d: onUltimateProducerReached: {requestId: %s, producer: %s, elapsedTime: %d ms, success: %b}", Long.valueOf(time), requestId, producerName, Long.valueOf(getElapsedTime(this.mProducerStartTimeMap.remove(create), time)), Boolean.valueOf(successful));
        }
    }

    @Override // com.facebook.imagepipeline.listener.RequestListener
    public synchronized void onRequestSuccess(ImageRequest request, String requestId, boolean isPrefetch) {
        if (FLog.isLoggable(2)) {
            long time = getTime();
            FLog.v(TAG, "time %d: onRequestSuccess: {requestId: %s, elapsedTime: %d ms}", Long.valueOf(time), requestId, Long.valueOf(getElapsedTime(this.mRequestStartTimeMap.remove(requestId), time)));
        }
    }

    @Override // com.facebook.imagepipeline.listener.RequestListener
    public synchronized void onRequestFailure(ImageRequest request, String requestId, Throwable throwable, boolean isPrefetch) {
        if (FLog.isLoggable(5)) {
            long time = getTime();
            FLog.w(TAG, "time %d: onRequestFailure: {requestId: %s, elapsedTime: %d ms, throwable: %s}", Long.valueOf(time), requestId, Long.valueOf(getElapsedTime(this.mRequestStartTimeMap.remove(requestId), time)), throwable.toString());
        }
    }

    @Override // com.facebook.imagepipeline.listener.RequestListener
    public synchronized void onRequestCancellation(String requestId) {
        if (FLog.isLoggable(2)) {
            long time = getTime();
            FLog.v(TAG, "time %d: onRequestCancellation: {requestId: %s, elapsedTime: %d ms}", Long.valueOf(time), requestId, Long.valueOf(getElapsedTime(this.mRequestStartTimeMap.remove(requestId), time)));
        }
    }

    @Override // com.facebook.imagepipeline.producers.ProducerListener
    public boolean requiresExtraMap(String id) {
        return FLog.isLoggable(2);
    }

    private static long getElapsedTime(@Nullable Long startTime, long endTime) {
        if (startTime != null) {
            return endTime - startTime.longValue();
        }
        return -1L;
    }

    private static long getTime() {
        return SystemClock.uptimeMillis();
    }
}
