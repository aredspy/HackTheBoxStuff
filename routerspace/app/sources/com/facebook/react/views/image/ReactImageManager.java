package com.facebook.react.views.image;

import android.graphics.PorterDuff;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.AbstractDraweeControllerBuilder;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewProps;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.annotations.ReactPropGroup;
import com.facebook.yoga.YogaConstants;
import java.util.Map;
@ReactModule(name = ReactImageManager.REACT_CLASS)
/* loaded from: classes.dex */
public class ReactImageManager extends SimpleViewManager<ReactImageView> {
    public static final String REACT_CLASS = "RCTImageView";
    private final Object mCallerContext;
    private final ReactCallerContextFactory mCallerContextFactory;
    private AbstractDraweeControllerBuilder mDraweeControllerBuilder;
    private GlobalImageLoadListener mGlobalImageLoadListener;

    @Override // com.facebook.react.uimanager.ViewManager, com.facebook.react.bridge.NativeModule
    public String getName() {
        return REACT_CLASS;
    }

    @Deprecated
    public ReactImageManager(AbstractDraweeControllerBuilder draweeControllerBuilder, Object callerContext) {
        this(draweeControllerBuilder, (GlobalImageLoadListener) null, callerContext);
    }

    @Deprecated
    public ReactImageManager(AbstractDraweeControllerBuilder draweeControllerBuilder, GlobalImageLoadListener globalImageLoadListener, Object callerContext) {
        this.mDraweeControllerBuilder = draweeControllerBuilder;
        this.mGlobalImageLoadListener = globalImageLoadListener;
        this.mCallerContext = callerContext;
        this.mCallerContextFactory = null;
    }

    public ReactImageManager(AbstractDraweeControllerBuilder draweeControllerBuilder, ReactCallerContextFactory callerContextFactory) {
        this(draweeControllerBuilder, (GlobalImageLoadListener) null, callerContextFactory);
    }

    public ReactImageManager(AbstractDraweeControllerBuilder draweeControllerBuilder, GlobalImageLoadListener globalImageLoadListener, ReactCallerContextFactory callerContextFactory) {
        this.mDraweeControllerBuilder = draweeControllerBuilder;
        this.mGlobalImageLoadListener = globalImageLoadListener;
        this.mCallerContextFactory = callerContextFactory;
        this.mCallerContext = null;
    }

    public ReactImageManager() {
        this.mDraweeControllerBuilder = null;
        this.mCallerContext = null;
        this.mCallerContextFactory = null;
    }

    public AbstractDraweeControllerBuilder getDraweeControllerBuilder() {
        if (this.mDraweeControllerBuilder == null) {
            this.mDraweeControllerBuilder = Fresco.newDraweeControllerBuilder();
        }
        return this.mDraweeControllerBuilder;
    }

    @Deprecated
    public Object getCallerContext() {
        return this.mCallerContext;
    }

    @Override // com.facebook.react.uimanager.ViewManager
    public ReactImageView createViewInstance(ThemedReactContext context) {
        Object obj;
        ReactCallerContextFactory reactCallerContextFactory = this.mCallerContextFactory;
        if (reactCallerContextFactory != null) {
            obj = reactCallerContextFactory.getOrCreateCallerContext(context.getModuleName(), null);
        } else {
            obj = getCallerContext();
        }
        return new ReactImageView(context, getDraweeControllerBuilder(), this.mGlobalImageLoadListener, obj);
    }

    @ReactProp(name = "accessible")
    public void setAccessible(ReactImageView view, boolean accessible) {
        view.setFocusable(accessible);
    }

    @ReactProp(name = "src")
    public void setSource(ReactImageView view, ReadableArray sources) {
        view.setSource(sources);
    }

    @ReactProp(name = "blurRadius")
    public void setBlurRadius(ReactImageView view, float blurRadius) {
        view.setBlurRadius(blurRadius);
    }

    @ReactProp(name = "internal_analyticTag")
    public void setInternal_AnalyticsTag(ReactImageView view, String analyticTag) {
        ReactCallerContextFactory reactCallerContextFactory = this.mCallerContextFactory;
        if (reactCallerContextFactory != null) {
            view.updateCallerContext(reactCallerContextFactory.getOrCreateCallerContext(((ThemedReactContext) view.getContext()).getModuleName(), analyticTag));
        }
    }

    @ReactProp(name = "defaultSrc")
    public void setDefaultSource(ReactImageView view, String source) {
        view.setDefaultSource(source);
    }

    @ReactProp(name = "loadingIndicatorSrc")
    public void setLoadingIndicatorSource(ReactImageView view, String source) {
        view.setLoadingIndicatorSource(source);
    }

    @ReactProp(customType = "Color", name = ViewProps.BORDER_COLOR)
    public void setBorderColor(ReactImageView view, Integer borderColor) {
        if (borderColor == null) {
            view.setBorderColor(0);
        } else {
            view.setBorderColor(borderColor.intValue());
        }
    }

    @ReactProp(customType = "Color", name = "overlayColor")
    public void setOverlayColor(ReactImageView view, Integer overlayColor) {
        if (overlayColor == null) {
            view.setOverlayColor(0);
        } else {
            view.setOverlayColor(overlayColor.intValue());
        }
    }

    @ReactProp(name = ViewProps.BORDER_WIDTH)
    public void setBorderWidth(ReactImageView view, float borderWidth) {
        view.setBorderWidth(borderWidth);
    }

    @ReactPropGroup(defaultFloat = Float.NaN, names = {ViewProps.BORDER_RADIUS, ViewProps.BORDER_TOP_LEFT_RADIUS, ViewProps.BORDER_TOP_RIGHT_RADIUS, ViewProps.BORDER_BOTTOM_RIGHT_RADIUS, ViewProps.BORDER_BOTTOM_LEFT_RADIUS})
    public void setBorderRadius(ReactImageView view, int index, float borderRadius) {
        if (!YogaConstants.isUndefined(borderRadius)) {
            borderRadius = PixelUtil.toPixelFromDIP(borderRadius);
        }
        if (index == 0) {
            view.setBorderRadius(borderRadius);
        } else {
            view.setBorderRadius(borderRadius, index - 1);
        }
    }

    @ReactProp(name = ViewProps.RESIZE_MODE)
    public void setResizeMode(ReactImageView view, String resizeMode) {
        view.setScaleType(ImageResizeMode.toScaleType(resizeMode));
        view.setTileMode(ImageResizeMode.toTileMode(resizeMode));
    }

    @ReactProp(name = ViewProps.RESIZE_METHOD)
    public void setResizeMethod(ReactImageView view, String resizeMethod) {
        if (resizeMethod == null || "auto".equals(resizeMethod)) {
            view.setResizeMethod(ImageResizeMethod.AUTO);
        } else if ("resize".equals(resizeMethod)) {
            view.setResizeMethod(ImageResizeMethod.RESIZE);
        } else if ("scale".equals(resizeMethod)) {
            view.setResizeMethod(ImageResizeMethod.SCALE);
        } else {
            throw new JSApplicationIllegalArgumentException("Invalid resize method: '" + resizeMethod + "'");
        }
    }

    @ReactProp(customType = "Color", name = "tintColor")
    public void setTintColor(ReactImageView view, Integer tintColor) {
        if (tintColor == null) {
            view.clearColorFilter();
        } else {
            view.setColorFilter(tintColor.intValue(), PorterDuff.Mode.SRC_IN);
        }
    }

    @ReactProp(name = "progressiveRenderingEnabled")
    public void setProgressiveRenderingEnabled(ReactImageView view, boolean enabled) {
        view.setProgressiveRenderingEnabled(enabled);
    }

    @ReactProp(name = "fadeDuration")
    public void setFadeDuration(ReactImageView view, int durationMs) {
        view.setFadeDuration(durationMs);
    }

    @ReactProp(name = "shouldNotifyLoadEvents")
    public void setLoadHandlersRegistered(ReactImageView view, boolean shouldNotifyLoadEvents) {
        view.setShouldNotifyLoadEvents(shouldNotifyLoadEvents);
    }

    @ReactProp(name = "headers")
    public void setHeaders(ReactImageView view, ReadableMap headers) {
        view.setHeaders(headers);
    }

    @Override // com.facebook.react.uimanager.BaseViewManager, com.facebook.react.uimanager.ViewManager
    public Map getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.of(ImageLoadEvent.eventNameForType(4), MapBuilder.of("registrationName", "onLoadStart"), ImageLoadEvent.eventNameForType(5), MapBuilder.of("registrationName", "onProgress"), ImageLoadEvent.eventNameForType(2), MapBuilder.of("registrationName", "onLoad"), ImageLoadEvent.eventNameForType(1), MapBuilder.of("registrationName", "onError"), ImageLoadEvent.eventNameForType(3), MapBuilder.of("registrationName", "onLoadEnd"));
    }

    public void onAfterUpdateTransaction(ReactImageView view) {
        super.onAfterUpdateTransaction((ReactImageManager) view);
        view.maybeUpdateView();
    }
}
