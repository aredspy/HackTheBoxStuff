package com.facebook.drawee.backends.pipeline.info.internal;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.Supplier;
import com.facebook.common.time.MonotonicClock;
import com.facebook.drawee.backends.pipeline.info.ImagePerfNotifier;
import com.facebook.drawee.backends.pipeline.info.ImagePerfState;
import com.facebook.fresco.ui.common.BaseControllerListener2;
import com.facebook.fresco.ui.common.ControllerListener2;
import com.facebook.fresco.ui.common.DimensionsInfo;
import com.facebook.fresco.ui.common.OnDrawControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;
import java.io.Closeable;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class ImagePerfControllerListener2 extends BaseControllerListener2<ImageInfo> implements OnDrawControllerListener<ImageInfo>, Closeable {
    private static final int WHAT_STATUS = 1;
    private static final int WHAT_VISIBILITY = 2;
    private final Supplier<Boolean> mAsyncLogging;
    private final MonotonicClock mClock;
    @Nullable
    private Handler mHandler;
    private final ImagePerfNotifier mImagePerfNotifier;
    private final ImagePerfState mImagePerfState;
    private final Supplier<Boolean> mUseNewState;

    /* loaded from: classes.dex */
    public static class LogHandler extends Handler {
        private final ImagePerfNotifier mNotifier;

        public LogHandler(Looper looper, ImagePerfNotifier notifier) {
            super(looper);
            this.mNotifier = notifier;
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            ImagePerfState imagePerfState = (ImagePerfState) Preconditions.checkNotNull(msg.obj);
            int i = msg.what;
            if (i == 1) {
                this.mNotifier.notifyStatusUpdated(imagePerfState, msg.arg1);
            } else if (i != 2) {
            } else {
                this.mNotifier.notifyListenersOfVisibilityStateUpdate(imagePerfState, msg.arg1);
            }
        }
    }

    public ImagePerfControllerListener2(MonotonicClock clock, ImagePerfState imagePerfState, ImagePerfNotifier imagePerfNotifier, Supplier<Boolean> asyncLogging, Supplier<Boolean> useNewState) {
        this.mClock = clock;
        this.mImagePerfState = imagePerfState;
        this.mImagePerfNotifier = imagePerfNotifier;
        this.mAsyncLogging = asyncLogging;
        this.mUseNewState = useNewState;
    }

    @Override // com.facebook.fresco.ui.common.BaseControllerListener2, com.facebook.fresco.ui.common.ControllerListener2
    public void onSubmit(String id, @Nullable Object callerContext, @Nullable ControllerListener2.Extras extraData) {
        long now = this.mClock.now();
        ImagePerfState obtainState = obtainState();
        obtainState.resetPointsTimestamps();
        obtainState.setControllerSubmitTimeMs(now);
        obtainState.setControllerId(id);
        obtainState.setCallerContext(callerContext);
        obtainState.setExtraData(extraData);
        updateStatus(obtainState, 0);
        reportViewVisible(obtainState, now);
    }

    public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
        long now = this.mClock.now();
        ImagePerfState obtainState = obtainState();
        obtainState.setControllerIntermediateImageSetTimeMs(now);
        obtainState.setControllerId(id);
        obtainState.setImageInfo(imageInfo);
        updateStatus(obtainState, 2);
    }

    public void onFinalImageSet(String id, @Nullable ImageInfo imageInfo, @Nullable ControllerListener2.Extras extraData) {
        long now = this.mClock.now();
        ImagePerfState obtainState = obtainState();
        obtainState.setExtraData(extraData);
        obtainState.setControllerFinalImageSetTimeMs(now);
        obtainState.setImageRequestEndTimeMs(now);
        obtainState.setControllerId(id);
        obtainState.setImageInfo(imageInfo);
        updateStatus(obtainState, 3);
    }

    @Override // com.facebook.fresco.ui.common.BaseControllerListener2, com.facebook.fresco.ui.common.ControllerListener2
    public void onFailure(String id, @Nullable Throwable throwable, @Nullable ControllerListener2.Extras extras) {
        long now = this.mClock.now();
        ImagePerfState obtainState = obtainState();
        obtainState.setExtraData(extras);
        obtainState.setControllerFailureTimeMs(now);
        obtainState.setControllerId(id);
        obtainState.setErrorThrowable(throwable);
        updateStatus(obtainState, 5);
        reportViewInvisible(obtainState, now);
    }

    @Override // com.facebook.fresco.ui.common.BaseControllerListener2, com.facebook.fresco.ui.common.ControllerListener2
    public void onRelease(String id, @Nullable ControllerListener2.Extras extras) {
        long now = this.mClock.now();
        ImagePerfState obtainState = obtainState();
        obtainState.setExtraData(extras);
        obtainState.setControllerId(id);
        int imageLoadStatus = obtainState.getImageLoadStatus();
        if (imageLoadStatus != 3 && imageLoadStatus != 5 && imageLoadStatus != 6) {
            obtainState.setControllerCancelTimeMs(now);
            updateStatus(obtainState, 4);
        }
        reportViewInvisible(obtainState, now);
    }

    public void onImageDrawn(String id, ImageInfo info, DimensionsInfo dimensionsInfo) {
        ImagePerfState obtainState = obtainState();
        obtainState.setControllerId(id);
        obtainState.setImageDrawTimeMs(this.mClock.now());
        obtainState.setDimensionsInfo(dimensionsInfo);
        updateStatus(obtainState, 6);
    }

    public void reportViewVisible(ImagePerfState state, long now) {
        state.setVisible(true);
        state.setVisibilityEventTimeMs(now);
        updateVisibility(state, 1);
    }

    public void resetState() {
        obtainState().reset();
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        resetState();
    }

    private void reportViewInvisible(ImagePerfState state, long time) {
        state.setVisible(false);
        state.setInvisibilityEventTimeMs(time);
        updateVisibility(state, 2);
    }

    private void updateStatus(ImagePerfState state, int imageLoadStatus) {
        if (shouldDispatchAsync()) {
            Message obtainMessage = ((Handler) Preconditions.checkNotNull(this.mHandler)).obtainMessage();
            obtainMessage.what = 1;
            obtainMessage.arg1 = imageLoadStatus;
            obtainMessage.obj = state;
            this.mHandler.sendMessage(obtainMessage);
            return;
        }
        this.mImagePerfNotifier.notifyStatusUpdated(state, imageLoadStatus);
    }

    private void updateVisibility(ImagePerfState state, int visibilityState) {
        if (shouldDispatchAsync()) {
            Message obtainMessage = ((Handler) Preconditions.checkNotNull(this.mHandler)).obtainMessage();
            obtainMessage.what = 2;
            obtainMessage.arg1 = visibilityState;
            obtainMessage.obj = state;
            this.mHandler.sendMessage(obtainMessage);
            return;
        }
        this.mImagePerfNotifier.notifyListenersOfVisibilityStateUpdate(state, visibilityState);
    }

    private synchronized void initHandler() {
        if (this.mHandler != null) {
            return;
        }
        HandlerThread handlerThread = new HandlerThread("ImagePerfControllerListener2Thread");
        handlerThread.start();
        this.mHandler = new LogHandler((Looper) Preconditions.checkNotNull(handlerThread.getLooper()), this.mImagePerfNotifier);
    }

    private boolean shouldDispatchAsync() {
        boolean booleanValue = this.mAsyncLogging.get().booleanValue();
        if (booleanValue && this.mHandler == null) {
            initHandler();
        }
        return booleanValue;
    }

    private ImagePerfState obtainState() {
        return this.mUseNewState.get().booleanValue() ? new ImagePerfState() : this.mImagePerfState;
    }
}
