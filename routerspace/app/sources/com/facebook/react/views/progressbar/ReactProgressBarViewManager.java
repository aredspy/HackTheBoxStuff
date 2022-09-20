package com.facebook.react.views.progressbar;

import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.widget.ProgressBar;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.BaseViewManager;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewManagerDelegate;
import com.facebook.react.uimanager.ViewProps;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.viewmanagers.AndroidProgressBarManagerDelegate;
import com.facebook.react.viewmanagers.AndroidProgressBarManagerInterface;
import com.facebook.yoga.YogaMeasureMode;
import com.facebook.yoga.YogaMeasureOutput;
import java.util.WeakHashMap;
@ReactModule(name = ReactProgressBarViewManager.REACT_CLASS)
/* loaded from: classes.dex */
public class ReactProgressBarViewManager extends BaseViewManager<ProgressBarContainerView, ProgressBarShadowNode> implements AndroidProgressBarManagerInterface<ProgressBarContainerView> {
    static final String DEFAULT_STYLE = "Normal";
    static final String PROP_ANIMATING = "animating";
    static final String PROP_INDETERMINATE = "indeterminate";
    static final String PROP_PROGRESS = "progress";
    static final String PROP_STYLE = "styleAttr";
    public static final String REACT_CLASS = "AndroidProgressBar";
    private static Object sProgressBarCtorLock = new Object();
    private final WeakHashMap<Integer, Pair<Integer, Integer>> mMeasuredStyles = new WeakHashMap<>();
    private final ViewManagerDelegate<ProgressBarContainerView> mDelegate = new AndroidProgressBarManagerDelegate(this);

    @Override // com.facebook.react.uimanager.ViewManager, com.facebook.react.bridge.NativeModule
    public String getName() {
        return REACT_CLASS;
    }

    public void setTypeAttr(ProgressBarContainerView view, String value) {
    }

    public void updateExtraData(ProgressBarContainerView root, Object extraData) {
    }

    public static ProgressBar createProgressBar(Context context, int style) {
        ProgressBar progressBar;
        synchronized (sProgressBarCtorLock) {
            progressBar = new ProgressBar(context, null, style);
        }
        return progressBar;
    }

    @Override // com.facebook.react.uimanager.ViewManager
    public ProgressBarContainerView createViewInstance(ThemedReactContext context) {
        return new ProgressBarContainerView(context);
    }

    @ReactProp(name = PROP_STYLE)
    public void setStyleAttr(ProgressBarContainerView view, String styleName) {
        view.setStyle(styleName);
    }

    @ReactProp(customType = "Color", name = ViewProps.COLOR)
    public void setColor(ProgressBarContainerView view, Integer color) {
        view.setColor(color);
    }

    @ReactProp(name = PROP_INDETERMINATE)
    public void setIndeterminate(ProgressBarContainerView view, boolean indeterminate) {
        view.setIndeterminate(indeterminate);
    }

    @ReactProp(name = "progress")
    public void setProgress(ProgressBarContainerView view, double progress) {
        view.setProgress(progress);
    }

    @ReactProp(name = PROP_ANIMATING)
    public void setAnimating(ProgressBarContainerView view, boolean animating) {
        view.setAnimating(animating);
    }

    public void setTestID(ProgressBarContainerView view, String value) {
        super.setTestId(view, value);
    }

    @Override // com.facebook.react.uimanager.ViewManager
    public ProgressBarShadowNode createShadowNodeInstance() {
        return new ProgressBarShadowNode();
    }

    @Override // com.facebook.react.uimanager.ViewManager
    public Class<ProgressBarShadowNode> getShadowNodeClass() {
        return ProgressBarShadowNode.class;
    }

    public void onAfterUpdateTransaction(ProgressBarContainerView view) {
        view.apply();
    }

    @Override // com.facebook.react.uimanager.ViewManager
    public ViewManagerDelegate<ProgressBarContainerView> getDelegate() {
        return this.mDelegate;
    }

    public static int getStyleFromString(String styleStr) {
        if (styleStr == null) {
            throw new JSApplicationIllegalArgumentException("ProgressBar needs to have a style, null received");
        }
        if (styleStr.equals("Horizontal")) {
            return 16842872;
        }
        if (styleStr.equals("Small")) {
            return 16842873;
        }
        if (styleStr.equals("Large")) {
            return 16842874;
        }
        if (styleStr.equals("Inverse")) {
            return 16843399;
        }
        if (styleStr.equals("SmallInverse")) {
            return 16843400;
        }
        if (styleStr.equals("LargeInverse")) {
            return 16843401;
        }
        if (styleStr.equals(DEFAULT_STYLE)) {
            return 16842871;
        }
        throw new JSApplicationIllegalArgumentException("Unknown ProgressBar style: " + styleStr);
    }

    @Override // com.facebook.react.uimanager.ViewManager
    public long measure(Context context, ReadableMap localData, ReadableMap props, ReadableMap state, float width, YogaMeasureMode widthMode, float height, YogaMeasureMode heightMode, float[] attachmentsPositions) {
        Integer valueOf = Integer.valueOf(getStyleFromString(props.getString(PROP_STYLE)));
        Pair<Integer, Integer> pair = this.mMeasuredStyles.get(valueOf);
        if (pair == null) {
            ProgressBar createProgressBar = createProgressBar(context, valueOf.intValue());
            int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(-2, 0);
            createProgressBar.measure(makeMeasureSpec, makeMeasureSpec);
            pair = Pair.create(Integer.valueOf(createProgressBar.getMeasuredWidth()), Integer.valueOf(createProgressBar.getMeasuredHeight()));
            this.mMeasuredStyles.put(valueOf, pair);
        }
        return YogaMeasureOutput.make(PixelUtil.toDIPFromPixel(((Integer) pair.first).intValue()), PixelUtil.toDIPFromPixel(((Integer) pair.second).intValue()));
    }
}
