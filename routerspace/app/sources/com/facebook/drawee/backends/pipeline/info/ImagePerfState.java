package com.facebook.drawee.backends.pipeline.info;

import com.facebook.fresco.ui.common.ControllerListener2;
import com.facebook.fresco.ui.common.DimensionsInfo;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class ImagePerfState {
    @Nullable
    private Object mCallerContext;
    @Nullable
    private String mComponentTag;
    @Nullable
    private ImageRequest[] mControllerFirstAvailableImageRequests;
    @Nullable
    private String mControllerId;
    @Nullable
    private ImageRequest mControllerImageRequest;
    @Nullable
    private ImageRequest mControllerLowResImageRequest;
    @Nullable
    private DimensionsInfo mDimensionsInfo;
    @Nullable
    private Throwable mErrorThrowable;
    @Nullable
    private ControllerListener2.Extras mExtraData;
    @Nullable
    private ImageInfo mImageInfo;
    @Nullable
    private ImageRequest mImageRequest;
    private boolean mIsPrefetch;
    @Nullable
    private String mRequestId;
    @Nullable
    private String mUltimateProducerName;
    private long mControllerSubmitTimeMs = -1;
    private long mControllerIntermediateImageSetTimeMs = -1;
    private long mControllerFinalImageSetTimeMs = -1;
    private long mControllerFailureTimeMs = -1;
    private long mControllerCancelTimeMs = -1;
    private long mImageRequestStartTimeMs = -1;
    private long mImageRequestEndTimeMs = -1;
    private int mImageOrigin = 1;
    private int mOnScreenWidthPx = -1;
    private int mOnScreenHeightPx = -1;
    private int mImageLoadStatus = -1;
    private int mVisibilityState = -1;
    private long mVisibilityEventTimeMs = -1;
    private long mInvisibilityEventTimeMs = -1;
    private long mImageDrawTimeMs = -1;

    public void reset() {
        this.mRequestId = null;
        this.mImageRequest = null;
        this.mCallerContext = null;
        this.mImageInfo = null;
        this.mControllerImageRequest = null;
        this.mControllerLowResImageRequest = null;
        this.mControllerFirstAvailableImageRequests = null;
        this.mImageOrigin = 1;
        this.mUltimateProducerName = null;
        this.mIsPrefetch = false;
        this.mOnScreenWidthPx = -1;
        this.mOnScreenHeightPx = -1;
        this.mErrorThrowable = null;
        this.mImageLoadStatus = -1;
        this.mVisibilityState = -1;
        this.mComponentTag = null;
        this.mDimensionsInfo = null;
        this.mExtraData = null;
        resetPointsTimestamps();
    }

    public void resetPointsTimestamps() {
        this.mImageRequestStartTimeMs = -1L;
        this.mImageRequestEndTimeMs = -1L;
        this.mControllerSubmitTimeMs = -1L;
        this.mControllerFinalImageSetTimeMs = -1L;
        this.mControllerFailureTimeMs = -1L;
        this.mControllerCancelTimeMs = -1L;
        this.mVisibilityEventTimeMs = -1L;
        this.mInvisibilityEventTimeMs = -1L;
        this.mImageDrawTimeMs = -1L;
    }

    public void setImageLoadStatus(int imageLoadStatus) {
        this.mImageLoadStatus = imageLoadStatus;
    }

    public int getImageLoadStatus() {
        return this.mImageLoadStatus;
    }

    public void setControllerId(@Nullable String controllerId) {
        this.mControllerId = controllerId;
    }

    public void setRequestId(@Nullable String requestId) {
        this.mRequestId = requestId;
    }

    public void setImageRequest(@Nullable ImageRequest imageRequest) {
        this.mImageRequest = imageRequest;
    }

    public void setControllerImageRequests(@Nullable ImageRequest imageRequest, @Nullable ImageRequest lowResImageRequest, @Nullable ImageRequest[] firstAvailableImageRequests) {
        this.mControllerImageRequest = imageRequest;
        this.mControllerLowResImageRequest = lowResImageRequest;
        this.mControllerFirstAvailableImageRequests = firstAvailableImageRequests;
    }

    public void setCallerContext(@Nullable Object callerContext) {
        this.mCallerContext = callerContext;
    }

    public void setControllerSubmitTimeMs(long controllerSubmitTimeMs) {
        this.mControllerSubmitTimeMs = controllerSubmitTimeMs;
    }

    public void setControllerIntermediateImageSetTimeMs(long controllerIntermediateImageSetTimeMs) {
        this.mControllerIntermediateImageSetTimeMs = controllerIntermediateImageSetTimeMs;
    }

    public void setControllerFinalImageSetTimeMs(long controllerFinalImageSetTimeMs) {
        this.mControllerFinalImageSetTimeMs = controllerFinalImageSetTimeMs;
    }

    public void setControllerFailureTimeMs(long controllerFailureTimeMs) {
        this.mControllerFailureTimeMs = controllerFailureTimeMs;
    }

    public void setControllerCancelTimeMs(long controllerCancelTimeMs) {
        this.mControllerCancelTimeMs = controllerCancelTimeMs;
    }

    public void setImageRequestStartTimeMs(long imageRequestStartTimeMs) {
        this.mImageRequestStartTimeMs = imageRequestStartTimeMs;
    }

    public void setImageRequestEndTimeMs(long imageRequestEndTimeMs) {
        this.mImageRequestEndTimeMs = imageRequestEndTimeMs;
    }

    public void setVisibilityEventTimeMs(long visibilityEventTimeMs) {
        this.mVisibilityEventTimeMs = visibilityEventTimeMs;
    }

    public void setInvisibilityEventTimeMs(long invisibilityEventTimeMs) {
        this.mInvisibilityEventTimeMs = invisibilityEventTimeMs;
    }

    public void setImageOrigin(int imageOrigin) {
        this.mImageOrigin = imageOrigin;
    }

    public void setUltimateProducerName(@Nullable String ultimateProducerName) {
        this.mUltimateProducerName = ultimateProducerName;
    }

    public void setPrefetch(boolean prefetch) {
        this.mIsPrefetch = prefetch;
    }

    public void setImageInfo(@Nullable ImageInfo imageInfo) {
        this.mImageInfo = imageInfo;
    }

    public void setOnScreenWidth(int onScreenWidthPx) {
        this.mOnScreenWidthPx = onScreenWidthPx;
    }

    public void setOnScreenHeight(int onScreenHeightPx) {
        this.mOnScreenHeightPx = onScreenHeightPx;
    }

    public void setErrorThrowable(@Nullable Throwable errorThrowable) {
        this.mErrorThrowable = errorThrowable;
    }

    public void setVisible(boolean visible) {
        this.mVisibilityState = visible ? 1 : 2;
    }

    public void setComponentTag(@Nullable String componentTag) {
        this.mComponentTag = componentTag;
    }

    public void setImageDrawTimeMs(long imageDrawTimeMs) {
        this.mImageDrawTimeMs = imageDrawTimeMs;
    }

    public ImagePerfData snapshot() {
        return new ImagePerfData(this.mControllerId, this.mRequestId, this.mImageRequest, this.mCallerContext, this.mImageInfo, this.mControllerImageRequest, this.mControllerLowResImageRequest, this.mControllerFirstAvailableImageRequests, this.mControllerSubmitTimeMs, this.mControllerIntermediateImageSetTimeMs, this.mControllerFinalImageSetTimeMs, this.mControllerFailureTimeMs, this.mControllerCancelTimeMs, this.mImageRequestStartTimeMs, this.mImageRequestEndTimeMs, this.mImageOrigin, this.mUltimateProducerName, this.mIsPrefetch, this.mOnScreenWidthPx, this.mOnScreenHeightPx, this.mErrorThrowable, this.mVisibilityState, this.mVisibilityEventTimeMs, this.mInvisibilityEventTimeMs, this.mComponentTag, this.mImageDrawTimeMs, this.mDimensionsInfo, this.mExtraData);
    }

    public long getImageDrawTimeMs() {
        return this.mImageDrawTimeMs;
    }

    public void setDimensionsInfo(DimensionsInfo dimensionsInfo) {
        this.mDimensionsInfo = dimensionsInfo;
    }

    @Nullable
    public DimensionsInfo getDimensionsInfo() {
        return this.mDimensionsInfo;
    }

    public void setExtraData(@Nullable ControllerListener2.Extras extraData) {
        this.mExtraData = extraData;
    }

    @Nullable
    public Object getExtraData() {
        return this.mExtraData;
    }
}
