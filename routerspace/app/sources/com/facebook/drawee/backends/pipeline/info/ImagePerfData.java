package com.facebook.drawee.backends.pipeline.info;

import com.facebook.common.internal.Objects;
import com.facebook.fresco.ui.common.ControllerListener2;
import com.facebook.fresco.ui.common.DimensionsInfo;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.producers.ProducerContext;
import com.facebook.imagepipeline.request.ImageRequest;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class ImagePerfData {
    public static final int UNSET = -1;
    @Nullable
    private final Object mCallerContext;
    @Nullable
    private final String mComponentTag;
    private final long mControllerCancelTimeMs;
    private final long mControllerFailureTimeMs;
    private final long mControllerFinalImageSetTimeMs;
    @Nullable
    private final ImageRequest[] mControllerFirstAvailableImageRequests;
    @Nullable
    private final String mControllerId;
    @Nullable
    private final ImageRequest mControllerImageRequest;
    private final long mControllerIntermediateImageSetTimeMs;
    @Nullable
    private final ImageRequest mControllerLowResImageRequest;
    private final long mControllerSubmitTimeMs;
    @Nullable
    private final DimensionsInfo mDimensionsInfo;
    @Nullable
    private final Throwable mErrorThrowable;
    @Nullable
    private ControllerListener2.Extras mExtraData;
    private final long mImageDrawTimeMs;
    @Nullable
    private final ImageInfo mImageInfo;
    private final int mImageOrigin;
    @Nullable
    private final ImageRequest mImageRequest;
    private final long mImageRequestEndTimeMs;
    private final long mImageRequestStartTimeMs;
    private final long mInvisibilityEventTimeMs;
    private final boolean mIsPrefetch;
    private final int mOnScreenHeightPx;
    private final int mOnScreenWidthPx;
    @Nullable
    private final String mRequestId;
    @Nullable
    private final String mUltimateProducerName;
    private final long mVisibilityEventTimeMs;
    private final int mVisibilityState;

    public ImagePerfData(@Nullable String controllerId, @Nullable String requestId, @Nullable ImageRequest imageRequest, @Nullable Object callerContext, @Nullable ImageInfo imageInfo, @Nullable ImageRequest controllerImageRequest, @Nullable ImageRequest controllerLowResImageRequest, @Nullable ImageRequest[] controllerFirstAvailableImageRequests, long controllerSubmitTimeMs, long controllerIntermediateImageSetTimeMs, long controllerFinalImageSetTimeMs, long controllerFailureTimeMs, long controllerCancelTimeMs, long imageRequestStartTimeMs, long imageRequestEndTimeMs, int imageOrigin, @Nullable String ultimateProducerName, boolean isPrefetch, int onScreenWidthPx, int onScreenHeightPx, @Nullable Throwable errorThrowable, int visibilityState, long visibilityEventTimeMs, long invisibilityEventTime, @Nullable String componentTag, long imageDrawTimeMs, @Nullable DimensionsInfo dimensionsInfo, @Nullable ControllerListener2.Extras extraData) {
        this.mControllerId = controllerId;
        this.mRequestId = requestId;
        this.mImageRequest = imageRequest;
        this.mCallerContext = callerContext;
        this.mImageInfo = imageInfo;
        this.mControllerImageRequest = controllerImageRequest;
        this.mControllerLowResImageRequest = controllerLowResImageRequest;
        this.mControllerFirstAvailableImageRequests = controllerFirstAvailableImageRequests;
        this.mControllerSubmitTimeMs = controllerSubmitTimeMs;
        this.mControllerIntermediateImageSetTimeMs = controllerIntermediateImageSetTimeMs;
        this.mControllerFinalImageSetTimeMs = controllerFinalImageSetTimeMs;
        this.mControllerFailureTimeMs = controllerFailureTimeMs;
        this.mControllerCancelTimeMs = controllerCancelTimeMs;
        this.mImageRequestStartTimeMs = imageRequestStartTimeMs;
        this.mImageRequestEndTimeMs = imageRequestEndTimeMs;
        this.mImageOrigin = imageOrigin;
        this.mUltimateProducerName = ultimateProducerName;
        this.mIsPrefetch = isPrefetch;
        this.mOnScreenWidthPx = onScreenWidthPx;
        this.mOnScreenHeightPx = onScreenHeightPx;
        this.mErrorThrowable = errorThrowable;
        this.mVisibilityState = visibilityState;
        this.mVisibilityEventTimeMs = visibilityEventTimeMs;
        this.mInvisibilityEventTimeMs = invisibilityEventTime;
        this.mComponentTag = componentTag;
        this.mImageDrawTimeMs = imageDrawTimeMs;
        this.mDimensionsInfo = dimensionsInfo;
        this.mExtraData = extraData;
    }

    public long getImageDrawTimeMs() {
        return this.mImageDrawTimeMs;
    }

    @Nullable
    public String getControllerId() {
        return this.mControllerId;
    }

    @Nullable
    public String getRequestId() {
        return this.mRequestId;
    }

    @Nullable
    public ImageRequest getImageRequest() {
        return this.mImageRequest;
    }

    @Nullable
    public Object getCallerContext() {
        return this.mCallerContext;
    }

    @Nullable
    public ImageInfo getImageInfo() {
        return this.mImageInfo;
    }

    public long getControllerSubmitTimeMs() {
        return this.mControllerSubmitTimeMs;
    }

    public long getControllerIntermediateImageSetTimeMs() {
        return this.mControllerIntermediateImageSetTimeMs;
    }

    public long getControllerFinalImageSetTimeMs() {
        return this.mControllerFinalImageSetTimeMs;
    }

    public long getControllerFailureTimeMs() {
        return this.mControllerFailureTimeMs;
    }

    @Nullable
    public ImageRequest getControllerImageRequest() {
        return this.mControllerImageRequest;
    }

    @Nullable
    public ImageRequest getControllerLowResImageRequest() {
        return this.mControllerLowResImageRequest;
    }

    @Nullable
    public ImageRequest[] getControllerFirstAvailableImageRequests() {
        return this.mControllerFirstAvailableImageRequests;
    }

    public long getImageRequestStartTimeMs() {
        return this.mImageRequestStartTimeMs;
    }

    public long getImageRequestEndTimeMs() {
        return this.mImageRequestEndTimeMs;
    }

    public int getImageOrigin() {
        return this.mImageOrigin;
    }

    @Nullable
    public String getUltimateProducerName() {
        return this.mUltimateProducerName;
    }

    public boolean isPrefetch() {
        return this.mIsPrefetch;
    }

    public int getOnScreenWidthPx() {
        return this.mOnScreenWidthPx;
    }

    public int getOnScreenHeightPx() {
        return this.mOnScreenHeightPx;
    }

    @Nullable
    public Throwable getErrorThrowable() {
        return this.mErrorThrowable;
    }

    public long getFinalImageLoadTimeMs() {
        if (getImageRequestEndTimeMs() == -1 || getImageRequestStartTimeMs() == -1) {
            return -1L;
        }
        return getImageRequestEndTimeMs() - getImageRequestStartTimeMs();
    }

    public long getIntermediateImageLoadTimeMs() {
        if (getControllerIntermediateImageSetTimeMs() == -1 || getControllerSubmitTimeMs() == -1) {
            return -1L;
        }
        return getControllerIntermediateImageSetTimeMs() - getControllerSubmitTimeMs();
    }

    public int getVisibilityState() {
        return this.mVisibilityState;
    }

    public long getVisibilityEventTimeMs() {
        return this.mVisibilityEventTimeMs;
    }

    public long getInvisibilityEventTimeMs() {
        return this.mInvisibilityEventTimeMs;
    }

    @Nullable
    public String getComponentTag() {
        return this.mComponentTag;
    }

    @Nullable
    public DimensionsInfo getDimensionsInfo() {
        return this.mDimensionsInfo;
    }

    @Nullable
    public ControllerListener2.Extras getExtraData() {
        return this.mExtraData;
    }

    public void setExtraData(ControllerListener2.Extras extraData) {
        this.mExtraData = extraData;
    }

    public String createDebugString() {
        return Objects.toStringHelper(this).add("controller ID", this.mControllerId).add("request ID", this.mRequestId).add("controller image request", this.mControllerImageRequest).add("controller low res image request", this.mControllerLowResImageRequest).add("controller first available image requests", this.mControllerFirstAvailableImageRequests).add("controller submit", this.mControllerSubmitTimeMs).add("controller final image", this.mControllerFinalImageSetTimeMs).add("controller failure", this.mControllerFailureTimeMs).add("controller cancel", this.mControllerCancelTimeMs).add("start time", this.mImageRequestStartTimeMs).add("end time", this.mImageRequestEndTimeMs).add(ProducerContext.ExtraKeys.ORIGIN, ImageOriginUtils.toString(this.mImageOrigin)).add("ultimateProducerName", this.mUltimateProducerName).add("prefetch", this.mIsPrefetch).add("caller context", this.mCallerContext).add("image request", this.mImageRequest).add("image info", this.mImageInfo).add("on-screen width", this.mOnScreenWidthPx).add("on-screen height", this.mOnScreenHeightPx).add("visibility state", this.mVisibilityState).add("component tag", this.mComponentTag).add("visibility event", this.mVisibilityEventTimeMs).add("invisibility event", this.mInvisibilityEventTimeMs).add("image draw event", this.mImageDrawTimeMs).add("dimensions info", this.mDimensionsInfo).add("extra data", this.mExtraData).toString();
    }
}
