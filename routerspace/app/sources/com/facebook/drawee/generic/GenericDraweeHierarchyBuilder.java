package com.facebook.drawee.generic;

import android.content.res.Resources;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import com.facebook.common.internal.Preconditions;
import com.facebook.drawee.drawable.ScalingUtils;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class GenericDraweeHierarchyBuilder {
    public static final int DEFAULT_FADE_DURATION = 300;
    @Nullable
    private ColorFilter mActualImageColorFilter;
    @Nullable
    private PointF mActualImageFocusPoint;
    @Nullable
    private Matrix mActualImageMatrix;
    @Nullable
    private ScalingUtils.ScaleType mActualImageScaleType;
    @Nullable
    private Drawable mBackground;
    private float mDesiredAspectRatio;
    private int mFadeDuration;
    @Nullable
    private Drawable mFailureImage;
    @Nullable
    private ScalingUtils.ScaleType mFailureImageScaleType;
    @Nullable
    private List<Drawable> mOverlays;
    @Nullable
    private Drawable mPlaceholderImage;
    @Nullable
    private ScalingUtils.ScaleType mPlaceholderImageScaleType;
    @Nullable
    private Drawable mPressedStateOverlay;
    @Nullable
    private Drawable mProgressBarImage;
    @Nullable
    private ScalingUtils.ScaleType mProgressBarImageScaleType;
    private Resources mResources;
    @Nullable
    private Drawable mRetryImage;
    @Nullable
    private ScalingUtils.ScaleType mRetryImageScaleType;
    @Nullable
    private RoundingParams mRoundingParams;
    public static final ScalingUtils.ScaleType DEFAULT_SCALE_TYPE = ScalingUtils.ScaleType.CENTER_INSIDE;
    public static final ScalingUtils.ScaleType DEFAULT_ACTUAL_IMAGE_SCALE_TYPE = ScalingUtils.ScaleType.CENTER_CROP;

    public GenericDraweeHierarchyBuilder(Resources resources) {
        this.mResources = resources;
        init();
    }

    public static GenericDraweeHierarchyBuilder newInstance(Resources resources) {
        return new GenericDraweeHierarchyBuilder(resources);
    }

    private void init() {
        this.mFadeDuration = 300;
        this.mDesiredAspectRatio = 0.0f;
        this.mPlaceholderImage = null;
        ScalingUtils.ScaleType scaleType = DEFAULT_SCALE_TYPE;
        this.mPlaceholderImageScaleType = scaleType;
        this.mRetryImage = null;
        this.mRetryImageScaleType = scaleType;
        this.mFailureImage = null;
        this.mFailureImageScaleType = scaleType;
        this.mProgressBarImage = null;
        this.mProgressBarImageScaleType = scaleType;
        this.mActualImageScaleType = DEFAULT_ACTUAL_IMAGE_SCALE_TYPE;
        this.mActualImageMatrix = null;
        this.mActualImageFocusPoint = null;
        this.mActualImageColorFilter = null;
        this.mBackground = null;
        this.mOverlays = null;
        this.mPressedStateOverlay = null;
        this.mRoundingParams = null;
    }

    public GenericDraweeHierarchyBuilder reset() {
        init();
        return this;
    }

    public Resources getResources() {
        return this.mResources;
    }

    public GenericDraweeHierarchyBuilder setFadeDuration(int fadeDuration) {
        this.mFadeDuration = fadeDuration;
        return this;
    }

    public int getFadeDuration() {
        return this.mFadeDuration;
    }

    public GenericDraweeHierarchyBuilder setDesiredAspectRatio(float desiredAspectRatio) {
        this.mDesiredAspectRatio = desiredAspectRatio;
        return this;
    }

    public float getDesiredAspectRatio() {
        return this.mDesiredAspectRatio;
    }

    public GenericDraweeHierarchyBuilder setPlaceholderImage(@Nullable Drawable placeholderDrawable) {
        this.mPlaceholderImage = placeholderDrawable;
        return this;
    }

    public GenericDraweeHierarchyBuilder setPlaceholderImage(int resourceId) {
        this.mPlaceholderImage = this.mResources.getDrawable(resourceId);
        return this;
    }

    @Nullable
    public Drawable getPlaceholderImage() {
        return this.mPlaceholderImage;
    }

    public GenericDraweeHierarchyBuilder setPlaceholderImageScaleType(@Nullable ScalingUtils.ScaleType placeholderImageScaleType) {
        this.mPlaceholderImageScaleType = placeholderImageScaleType;
        return this;
    }

    @Nullable
    public ScalingUtils.ScaleType getPlaceholderImageScaleType() {
        return this.mPlaceholderImageScaleType;
    }

    public GenericDraweeHierarchyBuilder setPlaceholderImage(Drawable placeholderDrawable, @Nullable ScalingUtils.ScaleType placeholderImageScaleType) {
        this.mPlaceholderImage = placeholderDrawable;
        this.mPlaceholderImageScaleType = placeholderImageScaleType;
        return this;
    }

    public GenericDraweeHierarchyBuilder setPlaceholderImage(int resourceId, @Nullable ScalingUtils.ScaleType placeholderImageScaleType) {
        this.mPlaceholderImage = this.mResources.getDrawable(resourceId);
        this.mPlaceholderImageScaleType = placeholderImageScaleType;
        return this;
    }

    public GenericDraweeHierarchyBuilder setRetryImage(@Nullable Drawable retryDrawable) {
        this.mRetryImage = retryDrawable;
        return this;
    }

    public GenericDraweeHierarchyBuilder setRetryImage(int resourceId) {
        this.mRetryImage = this.mResources.getDrawable(resourceId);
        return this;
    }

    @Nullable
    public Drawable getRetryImage() {
        return this.mRetryImage;
    }

    public GenericDraweeHierarchyBuilder setRetryImageScaleType(@Nullable ScalingUtils.ScaleType retryImageScaleType) {
        this.mRetryImageScaleType = retryImageScaleType;
        return this;
    }

    @Nullable
    public ScalingUtils.ScaleType getRetryImageScaleType() {
        return this.mRetryImageScaleType;
    }

    public GenericDraweeHierarchyBuilder setRetryImage(Drawable retryDrawable, @Nullable ScalingUtils.ScaleType retryImageScaleType) {
        this.mRetryImage = retryDrawable;
        this.mRetryImageScaleType = retryImageScaleType;
        return this;
    }

    public GenericDraweeHierarchyBuilder setRetryImage(int resourceId, @Nullable ScalingUtils.ScaleType retryImageScaleType) {
        this.mRetryImage = this.mResources.getDrawable(resourceId);
        this.mRetryImageScaleType = retryImageScaleType;
        return this;
    }

    public GenericDraweeHierarchyBuilder setFailureImage(@Nullable Drawable failureDrawable) {
        this.mFailureImage = failureDrawable;
        return this;
    }

    public GenericDraweeHierarchyBuilder setFailureImage(int resourceId) {
        this.mFailureImage = this.mResources.getDrawable(resourceId);
        return this;
    }

    @Nullable
    public Drawable getFailureImage() {
        return this.mFailureImage;
    }

    public GenericDraweeHierarchyBuilder setFailureImageScaleType(@Nullable ScalingUtils.ScaleType failureImageScaleType) {
        this.mFailureImageScaleType = failureImageScaleType;
        return this;
    }

    @Nullable
    public ScalingUtils.ScaleType getFailureImageScaleType() {
        return this.mFailureImageScaleType;
    }

    public GenericDraweeHierarchyBuilder setFailureImage(Drawable failureDrawable, @Nullable ScalingUtils.ScaleType failureImageScaleType) {
        this.mFailureImage = failureDrawable;
        this.mFailureImageScaleType = failureImageScaleType;
        return this;
    }

    public GenericDraweeHierarchyBuilder setFailureImage(int resourceId, @Nullable ScalingUtils.ScaleType failureImageScaleType) {
        this.mFailureImage = this.mResources.getDrawable(resourceId);
        this.mFailureImageScaleType = failureImageScaleType;
        return this;
    }

    public GenericDraweeHierarchyBuilder setProgressBarImage(@Nullable Drawable progressBarDrawable) {
        this.mProgressBarImage = progressBarDrawable;
        return this;
    }

    public GenericDraweeHierarchyBuilder setProgressBarImage(int resourceId) {
        this.mProgressBarImage = this.mResources.getDrawable(resourceId);
        return this;
    }

    @Nullable
    public Drawable getProgressBarImage() {
        return this.mProgressBarImage;
    }

    public GenericDraweeHierarchyBuilder setProgressBarImageScaleType(@Nullable ScalingUtils.ScaleType progressBarImageScaleType) {
        this.mProgressBarImageScaleType = progressBarImageScaleType;
        return this;
    }

    @Nullable
    public ScalingUtils.ScaleType getProgressBarImageScaleType() {
        return this.mProgressBarImageScaleType;
    }

    public GenericDraweeHierarchyBuilder setProgressBarImage(Drawable progressBarDrawable, @Nullable ScalingUtils.ScaleType progressBarImageScaleType) {
        this.mProgressBarImage = progressBarDrawable;
        this.mProgressBarImageScaleType = progressBarImageScaleType;
        return this;
    }

    public GenericDraweeHierarchyBuilder setProgressBarImage(int resourceId, @Nullable ScalingUtils.ScaleType progressBarImageScaleType) {
        this.mProgressBarImage = this.mResources.getDrawable(resourceId);
        this.mProgressBarImageScaleType = progressBarImageScaleType;
        return this;
    }

    public GenericDraweeHierarchyBuilder setActualImageScaleType(@Nullable ScalingUtils.ScaleType actualImageScaleType) {
        this.mActualImageScaleType = actualImageScaleType;
        this.mActualImageMatrix = null;
        return this;
    }

    @Nullable
    public ScalingUtils.ScaleType getActualImageScaleType() {
        return this.mActualImageScaleType;
    }

    public GenericDraweeHierarchyBuilder setActualImageFocusPoint(@Nullable PointF focusPoint) {
        this.mActualImageFocusPoint = focusPoint;
        return this;
    }

    @Nullable
    public PointF getActualImageFocusPoint() {
        return this.mActualImageFocusPoint;
    }

    public GenericDraweeHierarchyBuilder setActualImageColorFilter(@Nullable ColorFilter colorFilter) {
        this.mActualImageColorFilter = colorFilter;
        return this;
    }

    @Nullable
    public ColorFilter getActualImageColorFilter() {
        return this.mActualImageColorFilter;
    }

    public GenericDraweeHierarchyBuilder setBackground(@Nullable Drawable background) {
        this.mBackground = background;
        return this;
    }

    @Nullable
    public Drawable getBackground() {
        return this.mBackground;
    }

    public GenericDraweeHierarchyBuilder setOverlays(@Nullable List<Drawable> overlays) {
        this.mOverlays = overlays;
        return this;
    }

    public GenericDraweeHierarchyBuilder setOverlay(@Nullable Drawable overlay) {
        if (overlay == null) {
            this.mOverlays = null;
        } else {
            this.mOverlays = Arrays.asList(overlay);
        }
        return this;
    }

    @Nullable
    public List<Drawable> getOverlays() {
        return this.mOverlays;
    }

    public GenericDraweeHierarchyBuilder setPressedStateOverlay(@Nullable Drawable drawable) {
        if (drawable == null) {
            this.mPressedStateOverlay = null;
        } else {
            StateListDrawable stateListDrawable = new StateListDrawable();
            stateListDrawable.addState(new int[]{16842919}, drawable);
            this.mPressedStateOverlay = stateListDrawable;
        }
        return this;
    }

    @Nullable
    public Drawable getPressedStateOverlay() {
        return this.mPressedStateOverlay;
    }

    public GenericDraweeHierarchyBuilder setRoundingParams(@Nullable RoundingParams roundingParams) {
        this.mRoundingParams = roundingParams;
        return this;
    }

    @Nullable
    public RoundingParams getRoundingParams() {
        return this.mRoundingParams;
    }

    private void validate() {
        List<Drawable> list = this.mOverlays;
        if (list != null) {
            for (Drawable drawable : list) {
                Preconditions.checkNotNull(drawable);
            }
        }
    }

    public GenericDraweeHierarchy build() {
        validate();
        return new GenericDraweeHierarchy(this);
    }
}
