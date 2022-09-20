package com.swmansion.rnscreens;

import android.view.View;
import com.facebook.react.bridge.JSApplicationCausedNativeException;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.ViewProps;
import com.facebook.react.uimanager.annotations.ReactProp;
import javax.annotation.Nonnull;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
/* compiled from: ScreenStackHeaderConfigViewManager.kt */
@ReactModule(name = ScreenStackHeaderConfigViewManager.REACT_CLASS)
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u001d\n\u0002\u0010\u0007\n\u0002\b\b\b\u0007\u0018\u0000 82\b\u0012\u0004\u0012\u00020\u00020\u0001:\u00018B\u0005¢\u0006\u0002\u0010\u0003J \u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00022\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0016J\u0010\u0010\u000b\u001a\u00020\u00022\u0006\u0010\f\u001a\u00020\rH\u0014J\u0018\u0010\u000e\u001a\u00020\b2\u0006\u0010\u0006\u001a\u00020\u00022\u0006\u0010\t\u001a\u00020\nH\u0016J\u0010\u0010\u000f\u001a\u00020\n2\u0006\u0010\u0006\u001a\u00020\u0002H\u0016J\b\u0010\u0010\u001a\u00020\u0011H\u0016J\b\u0010\u0012\u001a\u00020\u0013H\u0016J\u0010\u0010\u0014\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0002H\u0014J\u0012\u0010\u0015\u001a\u00020\u00052\b\b\u0001\u0010\u0016\u001a\u00020\u0002H\u0016J\u0010\u0010\u0017\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0002H\u0016J\u0018\u0010\u0018\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00022\u0006\u0010\t\u001a\u00020\nH\u0016J\u0018\u0010\u0019\u001a\u00020\u00052\u0006\u0010\u001a\u001a\u00020\u00022\u0006\u0010\u001b\u001a\u00020\u0013H\u0007J\u001f\u0010\u001c\u001a\u00020\u00052\u0006\u0010\u001a\u001a\u00020\u00022\b\u0010\u001d\u001a\u0004\u0018\u00010\nH\u0007¢\u0006\u0002\u0010\u001eJ\u0018\u0010\u001f\u001a\u00020\u00052\u0006\u0010\u001a\u001a\u00020\u00022\u0006\u0010 \u001a\u00020\nH\u0007J\u001a\u0010!\u001a\u00020\u00052\u0006\u0010\u001a\u001a\u00020\u00022\b\u0010\"\u001a\u0004\u0018\u00010\u0011H\u0007J\u0018\u0010#\u001a\u00020\u00052\u0006\u0010\u001a\u001a\u00020\u00022\u0006\u0010$\u001a\u00020\u0013H\u0007J\u0018\u0010%\u001a\u00020\u00052\u0006\u0010\u001a\u001a\u00020\u00022\u0006\u0010&\u001a\u00020\u0013H\u0007J\u0018\u0010'\u001a\u00020\u00052\u0006\u0010\u001a\u001a\u00020\u00022\u0006\u0010(\u001a\u00020\u0013H\u0007J\u001a\u0010)\u001a\u00020\u00052\u0006\u0010\u001a\u001a\u00020\u00022\b\u0010*\u001a\u0004\u0018\u00010\u0011H\u0007J\u0018\u0010+\u001a\u00020\u00052\u0006\u0010\u001a\u001a\u00020\u00022\u0006\u0010,\u001a\u00020\nH\u0007J\u001a\u0010-\u001a\u00020\u00052\u0006\u0010\u001a\u001a\u00020\u00022\b\u0010.\u001a\u0004\u0018\u00010\u0011H\u0007J\u0018\u0010/\u001a\u00020\u00052\u0006\u0010\u001a\u001a\u00020\u00022\u0006\u00100\u001a\u000201H\u0007J\u001a\u00102\u001a\u00020\u00052\u0006\u0010\u001a\u001a\u00020\u00022\b\u00103\u001a\u0004\u0018\u00010\u0011H\u0007J\u0018\u00104\u001a\u00020\u00052\u0006\u0010\u001a\u001a\u00020\u00022\u0006\u00105\u001a\u00020\u0013H\u0007J\u0018\u00106\u001a\u00020\u00052\u0006\u0010\u001a\u001a\u00020\u00022\u0006\u00107\u001a\u00020\u0013H\u0007¨\u00069"}, d2 = {"Lcom/swmansion/rnscreens/ScreenStackHeaderConfigViewManager;", "Lcom/facebook/react/uimanager/ViewGroupManager;", "Lcom/swmansion/rnscreens/ScreenStackHeaderConfig;", "()V", "addView", "", "parent", "child", "Landroid/view/View;", "index", "", "createViewInstance", "reactContext", "Lcom/facebook/react/uimanager/ThemedReactContext;", "getChildAt", "getChildCount", "getName", "", "needsCustomLayoutForChildren", "", "onAfterUpdateTransaction", "onDropViewInstance", "view", "removeAllViews", "removeViewAt", "setBackButtonInCustomView", "config", "backButtonInCustomView", "setBackgroundColor", ViewProps.BACKGROUND_COLOR, "(Lcom/swmansion/rnscreens/ScreenStackHeaderConfig;Ljava/lang/Integer;)V", "setColor", ViewProps.COLOR, "setDirection", "direction", "setHidden", ViewProps.HIDDEN, "setHideBackButton", "hideBackButton", "setHideShadow", "hideShadow", "setTitle", "title", "setTitleColor", "titleColor", "setTitleFontFamily", "titleFontFamily", "setTitleFontSize", "titleFontSize", "", "setTitleFontWeight", "titleFontWeight", "setTopInsetEnabled", "topInsetEnabled", "setTranslucent", "translucent", "Companion", "react-native-screens_release"}, k = 1, mv = {1, 4, 0})
/* loaded from: classes.dex */
public final class ScreenStackHeaderConfigViewManager extends ViewGroupManager<ScreenStackHeaderConfig> {
    public static final Companion Companion = new Companion(null);
    public static final String REACT_CLASS = "RNSScreenStackHeaderConfig";

    @Override // com.facebook.react.uimanager.ViewManager, com.facebook.react.bridge.NativeModule
    public String getName() {
        return REACT_CLASS;
    }

    @Override // com.facebook.react.uimanager.ViewGroupManager, com.facebook.react.uimanager.IViewManagerWithChildren
    public boolean needsCustomLayoutForChildren() {
        return true;
    }

    @Override // com.facebook.react.uimanager.ViewManager
    public ScreenStackHeaderConfig createViewInstance(ThemedReactContext reactContext) {
        Intrinsics.checkNotNullParameter(reactContext, "reactContext");
        return new ScreenStackHeaderConfig(reactContext);
    }

    public void addView(ScreenStackHeaderConfig parent, View child, int i) {
        Intrinsics.checkNotNullParameter(parent, "parent");
        Intrinsics.checkNotNullParameter(child, "child");
        if (!(child instanceof ScreenStackHeaderSubview)) {
            throw new JSApplicationCausedNativeException("Config children should be of type RNSScreenStackHeaderSubview");
        }
        parent.addConfigSubview((ScreenStackHeaderSubview) child, i);
    }

    public void onDropViewInstance(@Nonnull ScreenStackHeaderConfig view) {
        Intrinsics.checkNotNullParameter(view, "view");
        view.destroy();
    }

    public void removeAllViews(ScreenStackHeaderConfig parent) {
        Intrinsics.checkNotNullParameter(parent, "parent");
        parent.removeAllConfigSubviews();
    }

    public void removeViewAt(ScreenStackHeaderConfig parent, int i) {
        Intrinsics.checkNotNullParameter(parent, "parent");
        parent.removeConfigSubview(i);
    }

    public int getChildCount(ScreenStackHeaderConfig parent) {
        Intrinsics.checkNotNullParameter(parent, "parent");
        return parent.getConfigSubviewsCount();
    }

    public View getChildAt(ScreenStackHeaderConfig parent, int i) {
        Intrinsics.checkNotNullParameter(parent, "parent");
        return parent.getConfigSubview(i);
    }

    public void onAfterUpdateTransaction(ScreenStackHeaderConfig parent) {
        Intrinsics.checkNotNullParameter(parent, "parent");
        super.onAfterUpdateTransaction((ScreenStackHeaderConfigViewManager) parent);
        parent.onUpdate();
    }

    @ReactProp(name = "title")
    public final void setTitle(ScreenStackHeaderConfig config, String str) {
        Intrinsics.checkNotNullParameter(config, "config");
        config.setTitle(str);
    }

    @ReactProp(name = "titleFontFamily")
    public final void setTitleFontFamily(ScreenStackHeaderConfig config, String str) {
        Intrinsics.checkNotNullParameter(config, "config");
        config.setTitleFontFamily(str);
    }

    @ReactProp(name = "titleFontSize")
    public final void setTitleFontSize(ScreenStackHeaderConfig config, float f) {
        Intrinsics.checkNotNullParameter(config, "config");
        config.setTitleFontSize(f);
    }

    @ReactProp(name = "titleFontWeight")
    public final void setTitleFontWeight(ScreenStackHeaderConfig config, String str) {
        Intrinsics.checkNotNullParameter(config, "config");
        config.setTitleFontWeight(str);
    }

    @ReactProp(customType = "Color", name = "titleColor")
    public final void setTitleColor(ScreenStackHeaderConfig config, int i) {
        Intrinsics.checkNotNullParameter(config, "config");
        config.setTitleColor(i);
    }

    @ReactProp(customType = "Color", name = ViewProps.BACKGROUND_COLOR)
    public final void setBackgroundColor(ScreenStackHeaderConfig config, Integer num) {
        Intrinsics.checkNotNullParameter(config, "config");
        config.setBackgroundColor(num);
    }

    @ReactProp(name = "hideShadow")
    public final void setHideShadow(ScreenStackHeaderConfig config, boolean z) {
        Intrinsics.checkNotNullParameter(config, "config");
        config.setHideShadow(z);
    }

    @ReactProp(name = "hideBackButton")
    public final void setHideBackButton(ScreenStackHeaderConfig config, boolean z) {
        Intrinsics.checkNotNullParameter(config, "config");
        config.setHideBackButton(z);
    }

    @ReactProp(name = "topInsetEnabled")
    public final void setTopInsetEnabled(ScreenStackHeaderConfig config, boolean z) {
        Intrinsics.checkNotNullParameter(config, "config");
        config.setTopInsetEnabled(z);
    }

    @ReactProp(customType = "Color", name = ViewProps.COLOR)
    public final void setColor(ScreenStackHeaderConfig config, int i) {
        Intrinsics.checkNotNullParameter(config, "config");
        config.setTintColor(i);
    }

    @ReactProp(name = ViewProps.HIDDEN)
    public final void setHidden(ScreenStackHeaderConfig config, boolean z) {
        Intrinsics.checkNotNullParameter(config, "config");
        config.setHidden(z);
    }

    @ReactProp(name = "translucent")
    public final void setTranslucent(ScreenStackHeaderConfig config, boolean z) {
        Intrinsics.checkNotNullParameter(config, "config");
        config.setTranslucent(z);
    }

    @ReactProp(name = "backButtonInCustomView")
    public final void setBackButtonInCustomView(ScreenStackHeaderConfig config, boolean z) {
        Intrinsics.checkNotNullParameter(config, "config");
        config.setBackButtonInCustomView(z);
    }

    @ReactProp(name = "direction")
    public final void setDirection(ScreenStackHeaderConfig config, String str) {
        Intrinsics.checkNotNullParameter(config, "config");
        config.setDirection(str);
    }

    /* compiled from: ScreenStackHeaderConfigViewManager.kt */
    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n\u0000¨\u0006\u0005"}, d2 = {"Lcom/swmansion/rnscreens/ScreenStackHeaderConfigViewManager$Companion;", "", "()V", "REACT_CLASS", "", "react-native-screens_release"}, k = 1, mv = {1, 4, 0})
    /* loaded from: classes.dex */
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }
    }
}
