package com.facebook.react.views.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import com.facebook.common.internal.Objects;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.controller.AbstractDraweeControllerBuilder;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.controller.ForwardingControllerListener;
import com.facebook.drawee.drawable.AutoRotateDrawable;
import com.facebook.drawee.drawable.RoundedColorDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.GenericDraweeView;
import com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.postprocessors.IterativeBoxBlurPostProcessor;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.request.Postprocessor;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.modules.fresco.ReactNetworkImageRequest;
import com.facebook.react.uimanager.FloatUtil;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.react.views.imagehelper.ImageSource;
import com.facebook.react.views.imagehelper.MultiSourceHelper;
import com.facebook.react.views.imagehelper.ResourceDrawableIdHelper;
import com.facebook.yoga.YogaConstants;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
/* loaded from: classes.dex */
public class ReactImageView extends GenericDraweeView {
    public static final int REMOTE_IMAGE_FADE_DURATION_MS = 300;
    public static final String REMOTE_TRANSPARENT_BITMAP_URI = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkYAAAAAYAAjCB0C8AAAAASUVORK5CYII=";
    private RoundedColorDrawable mBackgroundImageDrawable;
    private int mBorderColor;
    private float[] mBorderCornerRadii;
    private float mBorderWidth;
    private ImageSource mCachedImageSource;
    private Object mCallerContext;
    private ControllerListener mControllerForTesting;
    private Drawable mDefaultImageDrawable;
    private ReactImageDownloadListener mDownloadListener;
    private final AbstractDraweeControllerBuilder mDraweeControllerBuilder;
    private GlobalImageLoadListener mGlobalImageLoadListener;
    private ReadableMap mHeaders;
    private ImageSource mImageSource;
    private boolean mIsDirty;
    private IterativeBoxBlurPostProcessor mIterativeBoxBlurPostProcessor;
    private Drawable mLoadingImageDrawable;
    private int mOverlayColor;
    private boolean mProgressiveRenderingEnabled;
    private static float[] sComputedCornerRadii = new float[4];
    private static final Matrix sMatrix = new Matrix();
    private static final Matrix sInverse = new Matrix();
    private static final Matrix sTileMatrix = new Matrix();
    private ImageResizeMethod mResizeMethod = ImageResizeMethod.AUTO;
    private int mBackgroundColor = 0;
    private float mBorderRadius = Float.NaN;
    private Shader.TileMode mTileMode = ImageResizeMode.defaultTileMode();
    private int mFadeDurationMs = -1;
    private ScalingUtils.ScaleType mScaleType = ImageResizeMode.defaultValue();
    private final RoundedCornerPostprocessor mRoundedCornerPostprocessor = new RoundedCornerPostprocessor();
    private final TilePostprocessor mTilePostprocessor = new TilePostprocessor();
    private final List<ImageSource> mSources = new LinkedList();

    private void warnImageSource(String uri) {
    }

    @Override // android.widget.ImageView, android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    public void updateCallerContext(Object callerContext) {
        if (!Objects.equal(this.mCallerContext, callerContext)) {
            this.mCallerContext = callerContext;
            this.mIsDirty = true;
        }
    }

    /* loaded from: classes.dex */
    public class RoundedCornerPostprocessor extends BasePostprocessor {
        private RoundedCornerPostprocessor() {
            ReactImageView.this = this$0;
        }

        void getRadii(Bitmap source, float[] computedCornerRadii, float[] mappedRadii) {
            ReactImageView.this.mScaleType.getTransform(ReactImageView.sMatrix, new Rect(0, 0, source.getWidth(), source.getHeight()), source.getWidth(), source.getHeight(), 0.0f, 0.0f);
            ReactImageView.sMatrix.invert(ReactImageView.sInverse);
            mappedRadii[0] = ReactImageView.sInverse.mapRadius(computedCornerRadii[0]);
            mappedRadii[1] = mappedRadii[0];
            mappedRadii[2] = ReactImageView.sInverse.mapRadius(computedCornerRadii[1]);
            mappedRadii[3] = mappedRadii[2];
            mappedRadii[4] = ReactImageView.sInverse.mapRadius(computedCornerRadii[2]);
            mappedRadii[5] = mappedRadii[4];
            mappedRadii[6] = ReactImageView.sInverse.mapRadius(computedCornerRadii[3]);
            mappedRadii[7] = mappedRadii[6];
        }

        @Override // com.facebook.imagepipeline.request.BasePostprocessor
        public void process(Bitmap output, Bitmap source) {
            ReactImageView.this.cornerRadii(ReactImageView.sComputedCornerRadii);
            output.setHasAlpha(true);
            if (FloatUtil.floatsEqual(ReactImageView.sComputedCornerRadii[0], 0.0f) && FloatUtil.floatsEqual(ReactImageView.sComputedCornerRadii[1], 0.0f) && FloatUtil.floatsEqual(ReactImageView.sComputedCornerRadii[2], 0.0f) && FloatUtil.floatsEqual(ReactImageView.sComputedCornerRadii[3], 0.0f)) {
                super.process(output, source);
                return;
            }
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
            Canvas canvas = new Canvas(output);
            float[] fArr = new float[8];
            getRadii(source, ReactImageView.sComputedCornerRadii, fArr);
            Path path = new Path();
            path.addRoundRect(new RectF(0.0f, 0.0f, source.getWidth(), source.getHeight()), fArr, Path.Direction.CW);
            canvas.drawPath(path, paint);
        }
    }

    /* loaded from: classes.dex */
    public class TilePostprocessor extends BasePostprocessor {
        private TilePostprocessor() {
            ReactImageView.this = this$0;
        }

        @Override // com.facebook.imagepipeline.request.BasePostprocessor, com.facebook.imagepipeline.request.Postprocessor
        public CloseableReference<Bitmap> process(Bitmap source, PlatformBitmapFactory bitmapFactory) {
            Rect rect = new Rect(0, 0, ReactImageView.this.getWidth(), ReactImageView.this.getHeight());
            ReactImageView.this.mScaleType.getTransform(ReactImageView.sTileMatrix, rect, source.getWidth(), source.getHeight(), 0.0f, 0.0f);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            BitmapShader bitmapShader = new BitmapShader(source, ReactImageView.this.mTileMode, ReactImageView.this.mTileMode);
            bitmapShader.setLocalMatrix(ReactImageView.sTileMatrix);
            paint.setShader(bitmapShader);
            CloseableReference<Bitmap> createBitmap = bitmapFactory.createBitmap(ReactImageView.this.getWidth(), ReactImageView.this.getHeight());
            try {
                new Canvas(createBitmap.get()).drawRect(rect, paint);
                return createBitmap.clone();
            } finally {
                CloseableReference.closeSafely(createBitmap);
            }
        }
    }

    private static GenericDraweeHierarchy buildHierarchy(Context context) {
        return new GenericDraweeHierarchyBuilder(context.getResources()).setRoundingParams(RoundingParams.fromCornersRadius(0.0f)).build();
    }

    public ReactImageView(Context context, AbstractDraweeControllerBuilder draweeControllerBuilder, GlobalImageLoadListener globalImageLoadListener, Object callerContext) {
        super(context, buildHierarchy(context));
        this.mDraweeControllerBuilder = draweeControllerBuilder;
        this.mGlobalImageLoadListener = globalImageLoadListener;
        this.mCallerContext = callerContext;
    }

    public void setShouldNotifyLoadEvents(boolean shouldNotify) {
        if (shouldNotify == (this.mDownloadListener != null)) {
            return;
        }
        if (!shouldNotify) {
            this.mDownloadListener = null;
        } else {
            final EventDispatcher eventDispatcherForReactTag = UIManagerHelper.getEventDispatcherForReactTag((ReactContext) getContext(), getId());
            this.mDownloadListener = new ReactImageDownloadListener<ImageInfo>() { // from class: com.facebook.react.views.image.ReactImageView.1
                @Override // com.facebook.react.views.image.ReactImageDownloadListener
                public void onProgressChange(int loaded, int total) {
                    eventDispatcherForReactTag.dispatchEvent(ImageLoadEvent.createProgressEvent(UIManagerHelper.getSurfaceId(ReactImageView.this), ReactImageView.this.getId(), ReactImageView.this.mImageSource.getSource(), loaded, total));
                }

                @Override // com.facebook.react.views.image.ReactImageDownloadListener, com.facebook.drawee.controller.ControllerListener
                public void onSubmit(String id, Object callerContext) {
                    eventDispatcherForReactTag.dispatchEvent(ImageLoadEvent.createLoadStartEvent(UIManagerHelper.getSurfaceId(ReactImageView.this), ReactImageView.this.getId()));
                }

                public void onFinalImageSet(String id, final ImageInfo imageInfo, Animatable animatable) {
                    if (imageInfo != null) {
                        eventDispatcherForReactTag.dispatchEvent(ImageLoadEvent.createLoadEvent(UIManagerHelper.getSurfaceId(ReactImageView.this), ReactImageView.this.getId(), ReactImageView.this.mImageSource.getSource(), imageInfo.getWidth(), imageInfo.getHeight()));
                        eventDispatcherForReactTag.dispatchEvent(ImageLoadEvent.createLoadEndEvent(UIManagerHelper.getSurfaceId(ReactImageView.this), ReactImageView.this.getId()));
                    }
                }

                @Override // com.facebook.react.views.image.ReactImageDownloadListener, com.facebook.drawee.controller.ControllerListener
                public void onFailure(String id, Throwable throwable) {
                    eventDispatcherForReactTag.dispatchEvent(ImageLoadEvent.createErrorEvent(UIManagerHelper.getSurfaceId(ReactImageView.this), ReactImageView.this.getId(), throwable));
                }
            };
        }
        this.mIsDirty = true;
    }

    public void setBlurRadius(float blurRadius) {
        int pixelFromDIP = ((int) PixelUtil.toPixelFromDIP(blurRadius)) / 2;
        if (pixelFromDIP == 0) {
            this.mIterativeBoxBlurPostProcessor = null;
        } else {
            this.mIterativeBoxBlurPostProcessor = new IterativeBoxBlurPostProcessor(2, pixelFromDIP);
        }
        this.mIsDirty = true;
    }

    @Override // android.view.View
    public void setBackgroundColor(int backgroundColor) {
        if (this.mBackgroundColor != backgroundColor) {
            this.mBackgroundColor = backgroundColor;
            this.mBackgroundImageDrawable = new RoundedColorDrawable(backgroundColor);
            this.mIsDirty = true;
        }
    }

    public void setBorderColor(int borderColor) {
        if (this.mBorderColor != borderColor) {
            this.mBorderColor = borderColor;
            this.mIsDirty = true;
        }
    }

    public void setOverlayColor(int overlayColor) {
        if (this.mOverlayColor != overlayColor) {
            this.mOverlayColor = overlayColor;
            this.mIsDirty = true;
        }
    }

    public void setBorderWidth(float borderWidth) {
        float pixelFromDIP = PixelUtil.toPixelFromDIP(borderWidth);
        if (!FloatUtil.floatsEqual(this.mBorderWidth, pixelFromDIP)) {
            this.mBorderWidth = pixelFromDIP;
            this.mIsDirty = true;
        }
    }

    public void setBorderRadius(float borderRadius) {
        if (!FloatUtil.floatsEqual(this.mBorderRadius, borderRadius)) {
            this.mBorderRadius = borderRadius;
            this.mIsDirty = true;
        }
    }

    public void setBorderRadius(float borderRadius, int position) {
        if (this.mBorderCornerRadii == null) {
            float[] fArr = new float[4];
            this.mBorderCornerRadii = fArr;
            Arrays.fill(fArr, Float.NaN);
        }
        if (!FloatUtil.floatsEqual(this.mBorderCornerRadii[position], borderRadius)) {
            this.mBorderCornerRadii[position] = borderRadius;
            this.mIsDirty = true;
        }
    }

    public void setScaleType(ScalingUtils.ScaleType scaleType) {
        if (this.mScaleType != scaleType) {
            this.mScaleType = scaleType;
            this.mIsDirty = true;
        }
    }

    public void setTileMode(Shader.TileMode tileMode) {
        if (this.mTileMode != tileMode) {
            this.mTileMode = tileMode;
            this.mIsDirty = true;
        }
    }

    public void setResizeMethod(ImageResizeMethod resizeMethod) {
        if (this.mResizeMethod != resizeMethod) {
            this.mResizeMethod = resizeMethod;
            this.mIsDirty = true;
        }
    }

    public void setSource(ReadableArray sources) {
        LinkedList<ImageSource> linkedList = new LinkedList();
        if (sources == null || sources.size() == 0) {
            linkedList.add(new ImageSource(getContext(), REMOTE_TRANSPARENT_BITMAP_URI));
        } else {
            if (sources.size() == 1) {
                String string = sources.getMap(0).getString("uri");
                ImageSource imageSource = new ImageSource(getContext(), string);
                linkedList.add(imageSource);
                if (Uri.EMPTY.equals(imageSource.getUri())) {
                    warnImageSource(string);
                }
            } else {
                for (int i = 0; i < sources.size(); i++) {
                    ReadableMap map = sources.getMap(i);
                    String string2 = map.getString("uri");
                    ImageSource imageSource2 = new ImageSource(getContext(), string2, map.getDouble("width"), map.getDouble("height"));
                    linkedList.add(imageSource2);
                    if (Uri.EMPTY.equals(imageSource2.getUri())) {
                        warnImageSource(string2);
                    }
                }
            }
        }
        if (this.mSources.equals(linkedList)) {
            return;
        }
        this.mSources.clear();
        for (ImageSource imageSource3 : linkedList) {
            this.mSources.add(imageSource3);
        }
        this.mIsDirty = true;
    }

    public void setDefaultSource(String name) {
        Drawable resourceDrawable = ResourceDrawableIdHelper.getInstance().getResourceDrawable(getContext(), name);
        if (!Objects.equal(this.mDefaultImageDrawable, resourceDrawable)) {
            this.mDefaultImageDrawable = resourceDrawable;
            this.mIsDirty = true;
        }
    }

    public void setLoadingIndicatorSource(String name) {
        Drawable resourceDrawable = ResourceDrawableIdHelper.getInstance().getResourceDrawable(getContext(), name);
        AutoRotateDrawable autoRotateDrawable = resourceDrawable != null ? new AutoRotateDrawable(resourceDrawable, 1000) : null;
        if (!Objects.equal(this.mLoadingImageDrawable, autoRotateDrawable)) {
            this.mLoadingImageDrawable = autoRotateDrawable;
            this.mIsDirty = true;
        }
    }

    public void setProgressiveRenderingEnabled(boolean enabled) {
        this.mProgressiveRenderingEnabled = enabled;
    }

    public void setFadeDuration(int durationMs) {
        this.mFadeDurationMs = durationMs;
    }

    public void cornerRadii(float[] computedCorners) {
        float f = !YogaConstants.isUndefined(this.mBorderRadius) ? this.mBorderRadius : 0.0f;
        float[] fArr = this.mBorderCornerRadii;
        computedCorners[0] = (fArr == null || YogaConstants.isUndefined(fArr[0])) ? f : this.mBorderCornerRadii[0];
        float[] fArr2 = this.mBorderCornerRadii;
        computedCorners[1] = (fArr2 == null || YogaConstants.isUndefined(fArr2[1])) ? f : this.mBorderCornerRadii[1];
        float[] fArr3 = this.mBorderCornerRadii;
        computedCorners[2] = (fArr3 == null || YogaConstants.isUndefined(fArr3[2])) ? f : this.mBorderCornerRadii[2];
        float[] fArr4 = this.mBorderCornerRadii;
        if (fArr4 != null && !YogaConstants.isUndefined(fArr4[3])) {
            f = this.mBorderCornerRadii[3];
        }
        computedCorners[3] = f;
    }

    public void setHeaders(ReadableMap headers) {
        this.mHeaders = headers;
    }

    public void maybeUpdateView() {
        if (!this.mIsDirty) {
            return;
        }
        if (hasMultipleSources() && (getWidth() <= 0 || getHeight() <= 0)) {
            return;
        }
        setSourceImage();
        ImageSource imageSource = this.mImageSource;
        if (imageSource == null) {
            return;
        }
        boolean shouldResize = shouldResize(imageSource);
        if (shouldResize && (getWidth() <= 0 || getHeight() <= 0)) {
            return;
        }
        if (isTiled() && (getWidth() <= 0 || getHeight() <= 0)) {
            return;
        }
        GenericDraweeHierarchy hierarchy = getHierarchy();
        hierarchy.setActualImageScaleType(this.mScaleType);
        Drawable drawable = this.mDefaultImageDrawable;
        if (drawable != null) {
            hierarchy.setPlaceholderImage(drawable, this.mScaleType);
        }
        Drawable drawable2 = this.mLoadingImageDrawable;
        if (drawable2 != null) {
            hierarchy.setPlaceholderImage(drawable2, ScalingUtils.ScaleType.CENTER);
        }
        boolean z = (this.mScaleType == ScalingUtils.ScaleType.CENTER_CROP || this.mScaleType == ScalingUtils.ScaleType.FOCUS_CROP) ? false : true;
        RoundingParams roundingParams = hierarchy.getRoundingParams();
        cornerRadii(sComputedCornerRadii);
        float[] fArr = sComputedCornerRadii;
        roundingParams.setCornersRadii(fArr[0], fArr[1], fArr[2], fArr[3]);
        RoundedColorDrawable roundedColorDrawable = this.mBackgroundImageDrawable;
        if (roundedColorDrawable != null) {
            roundedColorDrawable.setBorder(this.mBorderColor, this.mBorderWidth);
            this.mBackgroundImageDrawable.setRadii(roundingParams.getCornersRadii());
            hierarchy.setBackgroundImage(this.mBackgroundImageDrawable);
        }
        if (z) {
            roundingParams.setCornersRadius(0.0f);
        }
        roundingParams.setBorder(this.mBorderColor, this.mBorderWidth);
        int i = this.mOverlayColor;
        if (i != 0) {
            roundingParams.setOverlayColor(i);
        } else {
            roundingParams.setRoundingMethod(RoundingParams.RoundingMethod.BITMAP_ONLY);
        }
        hierarchy.setRoundingParams(roundingParams);
        int i2 = this.mFadeDurationMs;
        if (i2 < 0) {
            i2 = this.mImageSource.isResource() ? 0 : 300;
        }
        hierarchy.setFadeDuration(i2);
        LinkedList linkedList = new LinkedList();
        if (z) {
            linkedList.add(this.mRoundedCornerPostprocessor);
        }
        IterativeBoxBlurPostProcessor iterativeBoxBlurPostProcessor = this.mIterativeBoxBlurPostProcessor;
        if (iterativeBoxBlurPostProcessor != null) {
            linkedList.add(iterativeBoxBlurPostProcessor);
        }
        if (isTiled()) {
            linkedList.add(this.mTilePostprocessor);
        }
        Postprocessor from = MultiPostprocessor.from(linkedList);
        ResizeOptions resizeOptions = shouldResize ? new ResizeOptions(getWidth(), getHeight()) : null;
        ReactNetworkImageRequest fromBuilderWithHeaders = ReactNetworkImageRequest.fromBuilderWithHeaders(ImageRequestBuilder.newBuilderWithSource(this.mImageSource.getUri()).setPostprocessor(from).setResizeOptions(resizeOptions).setAutoRotateEnabled(true).setProgressiveRenderingEnabled(this.mProgressiveRenderingEnabled), this.mHeaders);
        GlobalImageLoadListener globalImageLoadListener = this.mGlobalImageLoadListener;
        if (globalImageLoadListener != null) {
            globalImageLoadListener.onLoadAttempt(this.mImageSource.getUri());
        }
        this.mDraweeControllerBuilder.reset();
        this.mDraweeControllerBuilder.setAutoPlayAnimations(true).setCallerContext(this.mCallerContext).setOldController(getController()).setImageRequest(fromBuilderWithHeaders);
        ImageSource imageSource2 = this.mCachedImageSource;
        if (imageSource2 != null) {
            this.mDraweeControllerBuilder.setLowResImageRequest(ImageRequestBuilder.newBuilderWithSource(imageSource2.getUri()).setPostprocessor(from).setResizeOptions(resizeOptions).setAutoRotateEnabled(true).setProgressiveRenderingEnabled(this.mProgressiveRenderingEnabled).build());
        }
        ReactImageDownloadListener reactImageDownloadListener = this.mDownloadListener;
        if (reactImageDownloadListener != null && this.mControllerForTesting != null) {
            ForwardingControllerListener forwardingControllerListener = new ForwardingControllerListener();
            forwardingControllerListener.addListener(this.mDownloadListener);
            forwardingControllerListener.addListener(this.mControllerForTesting);
            this.mDraweeControllerBuilder.setControllerListener(forwardingControllerListener);
        } else {
            ControllerListener controllerListener = this.mControllerForTesting;
            if (controllerListener != null) {
                this.mDraweeControllerBuilder.setControllerListener(controllerListener);
            } else if (reactImageDownloadListener != null) {
                this.mDraweeControllerBuilder.setControllerListener(reactImageDownloadListener);
            }
        }
        ReactImageDownloadListener reactImageDownloadListener2 = this.mDownloadListener;
        if (reactImageDownloadListener2 != null) {
            hierarchy.setProgressBarImage(reactImageDownloadListener2);
        }
        setController(this.mDraweeControllerBuilder.build());
        this.mIsDirty = false;
        this.mDraweeControllerBuilder.reset();
    }

    public void setControllerListener(ControllerListener controllerListener) {
        this.mControllerForTesting = controllerListener;
        this.mIsDirty = true;
        maybeUpdateView();
    }

    @Override // android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w <= 0 || h <= 0) {
            return;
        }
        this.mIsDirty = this.mIsDirty || hasMultipleSources() || isTiled();
        maybeUpdateView();
    }

    private boolean hasMultipleSources() {
        return this.mSources.size() > 1;
    }

    private boolean isTiled() {
        return this.mTileMode != Shader.TileMode.CLAMP;
    }

    private void setSourceImage() {
        this.mImageSource = null;
        if (this.mSources.isEmpty()) {
            this.mSources.add(new ImageSource(getContext(), REMOTE_TRANSPARENT_BITMAP_URI));
        } else if (hasMultipleSources()) {
            MultiSourceHelper.MultiSourceResult bestSourceForSize = MultiSourceHelper.getBestSourceForSize(getWidth(), getHeight(), this.mSources);
            this.mImageSource = bestSourceForSize.getBestResult();
            this.mCachedImageSource = bestSourceForSize.getBestResultInCache();
            return;
        }
        this.mImageSource = this.mSources.get(0);
    }

    private boolean shouldResize(ImageSource imageSource) {
        return this.mResizeMethod == ImageResizeMethod.AUTO ? UriUtil.isLocalContentUri(imageSource.getUri()) || UriUtil.isLocalFileUri(imageSource.getUri()) : this.mResizeMethod == ImageResizeMethod.RESIZE;
    }
}
