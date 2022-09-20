package com.swmansion.rnscreens;

import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewProps;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.views.view.ReactViewGroup;
import com.facebook.react.views.view.ReactViewManager;
import com.swmansion.rnscreens.ScreenStackHeaderSubview;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
/* compiled from: ScreenStackHeaderSubviewManager.kt */
@ReactModule(name = ScreenStackHeaderSubviewManager.REACT_CLASS)
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u0000 \u000e2\u00020\u0001:\u0001\u000eB\u0005¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016J\b\u0010\u0007\u001a\u00020\bH\u0016J\u0018\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\bH\u0007¨\u0006\u000f"}, d2 = {"Lcom/swmansion/rnscreens/ScreenStackHeaderSubviewManager;", "Lcom/facebook/react/views/view/ReactViewManager;", "()V", "createViewInstance", "Lcom/facebook/react/views/view/ReactViewGroup;", "context", "Lcom/facebook/react/uimanager/ThemedReactContext;", "getName", "", "setType", "", "view", "Lcom/swmansion/rnscreens/ScreenStackHeaderSubview;", "type", "Companion", "react-native-screens_release"}, k = 1, mv = {1, 4, 0})
/* loaded from: classes.dex */
public final class ScreenStackHeaderSubviewManager extends ReactViewManager {
    public static final Companion Companion = new Companion(null);
    public static final String REACT_CLASS = "RNSScreenStackHeaderSubview";

    @Override // com.facebook.react.views.view.ReactViewManager, com.facebook.react.uimanager.ViewManager, com.facebook.react.bridge.NativeModule
    public String getName() {
        return REACT_CLASS;
    }

    @Override // com.facebook.react.views.view.ReactViewManager, com.facebook.react.uimanager.ViewManager
    public ReactViewGroup createViewInstance(ThemedReactContext context) {
        Intrinsics.checkNotNullParameter(context, "context");
        return new ScreenStackHeaderSubview(context);
    }

    @ReactProp(name = "type")
    public final void setType(ScreenStackHeaderSubview view, String type) {
        ScreenStackHeaderSubview.Type type2;
        Intrinsics.checkNotNullParameter(view, "view");
        Intrinsics.checkNotNullParameter(type, "type");
        switch (type.hashCode()) {
            case -1364013995:
                if (type.equals("center")) {
                    type2 = ScreenStackHeaderSubview.Type.CENTER;
                    view.setType(type2);
                    return;
                }
                throw new JSApplicationIllegalArgumentException("Unknown type " + type);
            case 3015911:
                if (type.equals("back")) {
                    type2 = ScreenStackHeaderSubview.Type.BACK;
                    view.setType(type2);
                    return;
                }
                throw new JSApplicationIllegalArgumentException("Unknown type " + type);
            case 3317767:
                if (type.equals(ViewProps.LEFT)) {
                    type2 = ScreenStackHeaderSubview.Type.LEFT;
                    view.setType(type2);
                    return;
                }
                throw new JSApplicationIllegalArgumentException("Unknown type " + type);
            case 108511772:
                if (type.equals(ViewProps.RIGHT)) {
                    type2 = ScreenStackHeaderSubview.Type.RIGHT;
                    view.setType(type2);
                    return;
                }
                throw new JSApplicationIllegalArgumentException("Unknown type " + type);
            default:
                throw new JSApplicationIllegalArgumentException("Unknown type " + type);
        }
    }

    /* compiled from: ScreenStackHeaderSubviewManager.kt */
    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n\u0000¨\u0006\u0005"}, d2 = {"Lcom/swmansion/rnscreens/ScreenStackHeaderSubviewManager$Companion;", "", "()V", "REACT_CLASS", "", "react-native-screens_release"}, k = 1, mv = {1, 4, 0})
    /* loaded from: classes.dex */
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }
    }
}
