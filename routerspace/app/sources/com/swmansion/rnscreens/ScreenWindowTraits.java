package com.swmansion.rnscreens;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.ViewParent;
import android.view.Window;
import androidx.core.view.ViewCompat;
import com.facebook.react.bridge.GuardedRunnable;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.UiThreadUtil;
import com.swmansion.rnscreens.Screen;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.jvm.internal.Intrinsics;
/* compiled from: ScreenWindowTraits.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\f\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\r\u0010\t\u001a\u00020\nH\u0000¢\u0006\u0002\b\u000bJ\r\u0010\f\u001a\u00020\nH\u0000¢\u0006\u0002\b\rJ\u0018\u0010\u000e\u001a\u00020\u00072\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0002J\u001c\u0010\u0013\u001a\u0004\u0018\u00010\u00102\b\u0010\u000f\u001a\u0004\u0018\u00010\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0002J\u001a\u0010\u0014\u001a\u0004\u0018\u00010\u00102\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0002J\u001a\u0010\u0015\u001a\u0004\u0018\u00010\u00102\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0002J)\u0010\u0016\u001a\u00020\n2\u0006\u0010\u000f\u001a\u00020\u00102\b\u0010\u0017\u001a\u0004\u0018\u00010\u00182\b\u0010\u0019\u001a\u0004\u0018\u00010\u001aH\u0001¢\u0006\u0002\b\u001bJ\u001f\u0010\u001c\u001a\u00020\n2\u0006\u0010\u000f\u001a\u00020\u00102\b\u0010\u0017\u001a\u0004\u0018\u00010\u0018H\u0000¢\u0006\u0002\b\u001dJ\u001f\u0010\u001e\u001a\u00020\n2\u0006\u0010\u000f\u001a\u00020\u00102\b\u0010\u0017\u001a\u0004\u0018\u00010\u0018H\u0000¢\u0006\u0002\b\u001fJ)\u0010 \u001a\u00020\n2\u0006\u0010\u000f\u001a\u00020\u00102\b\u0010\u0017\u001a\u0004\u0018\u00010\u00182\b\u0010\u0019\u001a\u0004\u0018\u00010\u001aH\u0000¢\u0006\u0002\b!J)\u0010\"\u001a\u00020\n2\u0006\u0010\u000f\u001a\u00020\u00102\b\u0010\u0017\u001a\u0004\u0018\u00010\u00182\b\u0010\u0019\u001a\u0004\u0018\u00010\u001aH\u0000¢\u0006\u0002\b#J)\u0010$\u001a\u00020\n2\u0006\u0010\u000f\u001a\u00020\u00102\b\u0010\u0017\u001a\u0004\u0018\u00010\u00182\b\u0010\u0019\u001a\u0004\u0018\u00010\u001aH\u0000¢\u0006\u0002\b%R\u0012\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e¢\u0006\u0004\n\u0002\u0010\u0005R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006&"}, d2 = {"Lcom/swmansion/rnscreens/ScreenWindowTraits;", "", "()V", "mDefaultStatusBarColor", "", "Ljava/lang/Integer;", "mDidSetOrientation", "", "mDidSetStatusBarAppearance", "applyDidSetOrientation", "", "applyDidSetOrientation$react_native_screens_release", "applyDidSetStatusBarAppearance", "applyDidSetStatusBarAppearance$react_native_screens_release", "checkTraitForScreen", "screen", "Lcom/swmansion/rnscreens/Screen;", "trait", "Lcom/swmansion/rnscreens/Screen$WindowTraits;", "childScreenWithTraitSet", "findParentWithTraitSet", "findScreenForTrait", "setColor", "activity", "Landroid/app/Activity;", "context", "Lcom/facebook/react/bridge/ReactContext;", "setColor$react_native_screens_release", "setHidden", "setHidden$react_native_screens_release", "setOrientation", "setOrientation$react_native_screens_release", "setStyle", "setStyle$react_native_screens_release", "setTranslucent", "setTranslucent$react_native_screens_release", "trySetWindowTraits", "trySetWindowTraits$react_native_screens_release", "react-native-screens_release"}, k = 1, mv = {1, 4, 0})
/* loaded from: classes.dex */
public final class ScreenWindowTraits {
    public static final ScreenWindowTraits INSTANCE = new ScreenWindowTraits();
    private static Integer mDefaultStatusBarColor;
    private static boolean mDidSetOrientation;
    private static boolean mDidSetStatusBarAppearance;

    @Metadata(bv = {1, 0, 3}, k = 3, mv = {1, 4, 0})
    /* loaded from: classes.dex */
    public final /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[Screen.WindowTraits.values().length];
            $EnumSwitchMapping$0 = iArr;
            iArr[Screen.WindowTraits.ORIENTATION.ordinal()] = 1;
            iArr[Screen.WindowTraits.COLOR.ordinal()] = 2;
            iArr[Screen.WindowTraits.STYLE.ordinal()] = 3;
            iArr[Screen.WindowTraits.TRANSLUCENT.ordinal()] = 4;
            iArr[Screen.WindowTraits.HIDDEN.ordinal()] = 5;
            iArr[Screen.WindowTraits.ANIMATED.ordinal()] = 6;
        }
    }

    private ScreenWindowTraits() {
    }

    public final void applyDidSetOrientation$react_native_screens_release() {
        mDidSetOrientation = true;
    }

    public final void applyDidSetStatusBarAppearance$react_native_screens_release() {
        mDidSetStatusBarAppearance = true;
    }

    public final void setOrientation$react_native_screens_release(Screen screen, Activity activity) {
        Integer screenOrientation;
        Intrinsics.checkNotNullParameter(screen, "screen");
        if (activity == null) {
            return;
        }
        Screen findScreenForTrait = findScreenForTrait(screen, Screen.WindowTraits.ORIENTATION);
        activity.setRequestedOrientation((findScreenForTrait == null || (screenOrientation = findScreenForTrait.getScreenOrientation()) == null) ? -1 : screenOrientation.intValue());
    }

    public final void setColor$react_native_screens_release(Screen screen, Activity activity, ReactContext reactContext) {
        Integer num;
        Boolean isStatusBarAnimated;
        Intrinsics.checkNotNullParameter(screen, "screen");
        if (activity == null || reactContext == null || Build.VERSION.SDK_INT < 21) {
            return;
        }
        if (mDefaultStatusBarColor == null) {
            Window window = activity.getWindow();
            Intrinsics.checkNotNullExpressionValue(window, "activity.window");
            mDefaultStatusBarColor = Integer.valueOf(window.getStatusBarColor());
        }
        Screen findScreenForTrait = findScreenForTrait(screen, Screen.WindowTraits.COLOR);
        Screen findScreenForTrait2 = findScreenForTrait(screen, Screen.WindowTraits.ANIMATED);
        if (findScreenForTrait == null || (num = findScreenForTrait.getStatusBarColor()) == null) {
            num = mDefaultStatusBarColor;
        }
        UiThreadUtil.runOnUiThread(new ScreenWindowTraits$setColor$1(activity, num, (findScreenForTrait2 == null || (isStatusBarAnimated = findScreenForTrait2.isStatusBarAnimated()) == null) ? false : isStatusBarAnimated.booleanValue(), reactContext, reactContext));
    }

    public final void setStyle$react_native_screens_release(Screen screen, final Activity activity, ReactContext reactContext) {
        final String str;
        Intrinsics.checkNotNullParameter(screen, "screen");
        if (activity == null || reactContext == null) {
            return;
        }
        Screen findScreenForTrait = findScreenForTrait(screen, Screen.WindowTraits.STYLE);
        if (findScreenForTrait == null || (str = findScreenForTrait.getStatusBarStyle()) == null) {
            str = "light";
        }
        if (Build.VERSION.SDK_INT < 23) {
            return;
        }
        UiThreadUtil.runOnUiThread(new Runnable() { // from class: com.swmansion.rnscreens.ScreenWindowTraits$setStyle$1
            @Override // java.lang.Runnable
            public final void run() {
                Window window = activity.getWindow();
                Intrinsics.checkNotNullExpressionValue(window, "activity.window");
                View decorView = window.getDecorView();
                Intrinsics.checkNotNullExpressionValue(decorView, "activity.window.decorView");
                int systemUiVisibility = decorView.getSystemUiVisibility();
                decorView.setSystemUiVisibility(Intrinsics.areEqual("dark", str) ? systemUiVisibility | 8192 : systemUiVisibility & (-8193));
            }
        });
    }

    public final void setTranslucent$react_native_screens_release(Screen screen, final Activity activity, final ReactContext reactContext) {
        Boolean isStatusBarTranslucent;
        Intrinsics.checkNotNullParameter(screen, "screen");
        if (activity == null || reactContext == null) {
            return;
        }
        Screen findScreenForTrait = findScreenForTrait(screen, Screen.WindowTraits.TRANSLUCENT);
        final boolean booleanValue = (findScreenForTrait == null || (isStatusBarTranslucent = findScreenForTrait.isStatusBarTranslucent()) == null) ? false : isStatusBarTranslucent.booleanValue();
        UiThreadUtil.runOnUiThread(new GuardedRunnable(reactContext) { // from class: com.swmansion.rnscreens.ScreenWindowTraits$setTranslucent$1
            @Override // com.facebook.react.bridge.GuardedRunnable
            public void runGuarded() {
                Window window = activity.getWindow();
                Intrinsics.checkNotNullExpressionValue(window, "activity.window");
                View decorView = window.getDecorView();
                Intrinsics.checkNotNullExpressionValue(decorView, "activity.window.decorView");
                if (booleanValue) {
                    decorView.setOnApplyWindowInsetsListener(ScreenWindowTraits$setTranslucent$1$runGuarded$1.INSTANCE);
                } else {
                    decorView.setOnApplyWindowInsetsListener(null);
                }
                ViewCompat.requestApplyInsets(decorView);
            }
        });
    }

    public final void setHidden$react_native_screens_release(Screen screen, final Activity activity) {
        Boolean isStatusBarHidden;
        Intrinsics.checkNotNullParameter(screen, "screen");
        if (activity == null) {
            return;
        }
        Screen findScreenForTrait = findScreenForTrait(screen, Screen.WindowTraits.HIDDEN);
        final boolean booleanValue = (findScreenForTrait == null || (isStatusBarHidden = findScreenForTrait.isStatusBarHidden()) == null) ? false : isStatusBarHidden.booleanValue();
        UiThreadUtil.runOnUiThread(new Runnable() { // from class: com.swmansion.rnscreens.ScreenWindowTraits$setHidden$1
            @Override // java.lang.Runnable
            public final void run() {
                if (booleanValue) {
                    activity.getWindow().addFlags(1024);
                    activity.getWindow().clearFlags(2048);
                    return;
                }
                activity.getWindow().addFlags(2048);
                activity.getWindow().clearFlags(1024);
            }
        });
    }

    public final void trySetWindowTraits$react_native_screens_release(Screen screen, Activity activity, ReactContext reactContext) {
        Intrinsics.checkNotNullParameter(screen, "screen");
        if (mDidSetOrientation) {
            setOrientation$react_native_screens_release(screen, activity);
        }
        if (mDidSetStatusBarAppearance) {
            setColor$react_native_screens_release(screen, activity, reactContext);
            setStyle$react_native_screens_release(screen, activity, reactContext);
            setTranslucent$react_native_screens_release(screen, activity, reactContext);
            setHidden$react_native_screens_release(screen, activity);
        }
    }

    private final Screen findScreenForTrait(Screen screen, Screen.WindowTraits windowTraits) {
        Screen childScreenWithTraitSet = childScreenWithTraitSet(screen, windowTraits);
        return childScreenWithTraitSet != null ? childScreenWithTraitSet : checkTraitForScreen(screen, windowTraits) ? screen : findParentWithTraitSet(screen, windowTraits);
    }

    private final Screen findParentWithTraitSet(Screen screen, Screen.WindowTraits windowTraits) {
        for (ViewParent container = screen.getContainer(); container != null; container = container.getParent()) {
            if (container instanceof Screen) {
                Screen screen2 = (Screen) container;
                if (checkTraitForScreen(screen2, windowTraits)) {
                    return screen2;
                }
            }
        }
        return null;
    }

    private final Screen childScreenWithTraitSet(Screen screen, Screen.WindowTraits windowTraits) {
        ScreenFragment fragment;
        if (screen == null || (fragment = screen.getFragment()) == null) {
            return null;
        }
        for (ScreenContainer<?> screenContainer : fragment.getChildScreenContainers()) {
            Screen topScreen = screenContainer.getTopScreen();
            ScreenWindowTraits screenWindowTraits = INSTANCE;
            Screen childScreenWithTraitSet = screenWindowTraits.childScreenWithTraitSet(topScreen, windowTraits);
            if (childScreenWithTraitSet != null) {
                return childScreenWithTraitSet;
            }
            if (topScreen != null && screenWindowTraits.checkTraitForScreen(topScreen, windowTraits)) {
                return topScreen;
            }
        }
        return null;
    }

    private final boolean checkTraitForScreen(Screen screen, Screen.WindowTraits windowTraits) {
        switch (WhenMappings.$EnumSwitchMapping$0[windowTraits.ordinal()]) {
            case 1:
                if (screen.getScreenOrientation() != null) {
                    return true;
                }
                break;
            case 2:
                if (screen.getStatusBarColor() != null) {
                    return true;
                }
                break;
            case 3:
                if (screen.getStatusBarStyle() != null) {
                    return true;
                }
                break;
            case 4:
                if (screen.isStatusBarTranslucent() != null) {
                    return true;
                }
                break;
            case 5:
                if (screen.isStatusBarHidden() != null) {
                    return true;
                }
                break;
            case 6:
                if (screen.isStatusBarAnimated() != null) {
                    return true;
                }
                break;
            default:
                throw new NoWhenBranchMatchedException();
        }
        return false;
    }
}
