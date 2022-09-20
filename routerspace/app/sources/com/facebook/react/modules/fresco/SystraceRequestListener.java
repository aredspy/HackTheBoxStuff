package com.facebook.react.modules.fresco;

import android.util.Pair;
import com.facebook.imagepipeline.listener.BaseRequestListener;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.systrace.Systrace;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes.dex */
public class SystraceRequestListener extends BaseRequestListener {
    int mCurrentID = 0;
    Map<String, Pair<Integer, String>> mProducerID = new HashMap();
    Map<String, Pair<Integer, String>> mRequestsID = new HashMap();

    @Override // com.facebook.imagepipeline.listener.BaseRequestListener, com.facebook.imagepipeline.producers.ProducerListener
    public boolean requiresExtraMap(String id) {
        return false;
    }

    @Override // com.facebook.imagepipeline.listener.BaseRequestListener, com.facebook.imagepipeline.producers.ProducerListener
    public void onProducerStart(String requestId, String producerName) {
        if (!Systrace.isTracing(0L)) {
            return;
        }
        Pair<Integer, String> create = Pair.create(Integer.valueOf(this.mCurrentID), "FRESCO_PRODUCER_" + producerName.replace(':', '_'));
        Systrace.beginAsyncSection(0L, (String) create.second, this.mCurrentID);
        this.mProducerID.put(requestId, create);
        this.mCurrentID = this.mCurrentID + 1;
    }

    @Override // com.facebook.imagepipeline.listener.BaseRequestListener, com.facebook.imagepipeline.producers.ProducerListener
    public void onProducerFinishWithSuccess(String requestId, String producerName, Map<String, String> extraMap) {
        if (Systrace.isTracing(0L) && this.mProducerID.containsKey(requestId)) {
            Pair<Integer, String> pair = this.mProducerID.get(requestId);
            Systrace.endAsyncSection(0L, (String) pair.second, ((Integer) pair.first).intValue());
            this.mProducerID.remove(requestId);
        }
    }

    @Override // com.facebook.imagepipeline.listener.BaseRequestListener, com.facebook.imagepipeline.producers.ProducerListener
    public void onProducerFinishWithFailure(String requestId, String producerName, Throwable throwable, Map<String, String> extraMap) {
        if (Systrace.isTracing(0L) && this.mProducerID.containsKey(requestId)) {
            Pair<Integer, String> pair = this.mProducerID.get(requestId);
            Systrace.endAsyncSection(0L, (String) pair.second, ((Integer) pair.first).intValue());
            this.mProducerID.remove(requestId);
        }
    }

    @Override // com.facebook.imagepipeline.listener.BaseRequestListener, com.facebook.imagepipeline.producers.ProducerListener
    public void onProducerFinishWithCancellation(String requestId, String producerName, Map<String, String> extraMap) {
        if (Systrace.isTracing(0L) && this.mProducerID.containsKey(requestId)) {
            Pair<Integer, String> pair = this.mProducerID.get(requestId);
            Systrace.endAsyncSection(0L, (String) pair.second, ((Integer) pair.first).intValue());
            this.mProducerID.remove(requestId);
        }
    }

    @Override // com.facebook.imagepipeline.listener.BaseRequestListener, com.facebook.imagepipeline.producers.ProducerListener
    public void onProducerEvent(String requestId, String producerName, String producerEventName) {
        if (!Systrace.isTracing(0L)) {
            return;
        }
        Systrace.traceInstant(0L, "FRESCO_PRODUCER_EVENT_" + requestId.replace(':', '_') + "_" + producerName.replace(':', '_') + "_" + producerEventName.replace(':', '_'), Systrace.EventScope.THREAD);
    }

    @Override // com.facebook.imagepipeline.listener.BaseRequestListener, com.facebook.imagepipeline.listener.RequestListener
    public void onRequestStart(ImageRequest request, Object callerContext, String requestId, boolean isPrefetch) {
        if (!Systrace.isTracing(0L)) {
            return;
        }
        Pair<Integer, String> create = Pair.create(Integer.valueOf(this.mCurrentID), "FRESCO_REQUEST_" + request.getSourceUri().toString().replace(':', '_'));
        Systrace.beginAsyncSection(0L, (String) create.second, this.mCurrentID);
        this.mRequestsID.put(requestId, create);
        this.mCurrentID = this.mCurrentID + 1;
    }

    @Override // com.facebook.imagepipeline.listener.BaseRequestListener, com.facebook.imagepipeline.listener.RequestListener
    public void onRequestSuccess(ImageRequest request, String requestId, boolean isPrefetch) {
        if (Systrace.isTracing(0L) && this.mRequestsID.containsKey(requestId)) {
            Pair<Integer, String> pair = this.mRequestsID.get(requestId);
            Systrace.endAsyncSection(0L, (String) pair.second, ((Integer) pair.first).intValue());
            this.mRequestsID.remove(requestId);
        }
    }

    @Override // com.facebook.imagepipeline.listener.BaseRequestListener, com.facebook.imagepipeline.listener.RequestListener
    public void onRequestFailure(ImageRequest request, String requestId, Throwable throwable, boolean isPrefetch) {
        if (Systrace.isTracing(0L) && this.mRequestsID.containsKey(requestId)) {
            Pair<Integer, String> pair = this.mRequestsID.get(requestId);
            Systrace.endAsyncSection(0L, (String) pair.second, ((Integer) pair.first).intValue());
            this.mRequestsID.remove(requestId);
        }
    }

    @Override // com.facebook.imagepipeline.listener.BaseRequestListener, com.facebook.imagepipeline.listener.RequestListener
    public void onRequestCancellation(String requestId) {
        if (Systrace.isTracing(0L) && this.mRequestsID.containsKey(requestId)) {
            Pair<Integer, String> pair = this.mRequestsID.get(requestId);
            Systrace.endAsyncSection(0L, (String) pair.second, ((Integer) pair.first).intValue());
            this.mRequestsID.remove(requestId);
        }
    }
}
