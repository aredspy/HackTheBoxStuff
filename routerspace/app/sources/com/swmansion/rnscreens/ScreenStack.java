package com.swmansion.rnscreens;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import androidx.fragment.app.FragmentTransaction;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.swmansion.rnscreens.Screen;
import com.swmansion.rnscreens.events.StackFinishTransitioningEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.TypeIntrinsics;
/* compiled from: ScreenStack.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000t\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0010#\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u000e\u0018\u0000 ?2\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0002?@B\u000f\u0012\b\u0010\u0003\u001a\u0004\u0018\u00010\u0004¢\u0006\u0002\u0010\u0005J\u0010\u0010!\u001a\u00020\u00022\u0006\u0010\"\u001a\u00020\u001cH\u0014J\u000e\u0010#\u001a\u00020$2\u0006\u0010%\u001a\u00020\u0002J\u0010\u0010&\u001a\u00020$2\u0006\u0010'\u001a\u00020(H\u0014J\b\u0010)\u001a\u00020$H\u0002J\b\u0010*\u001a\u00020$H\u0002J \u0010+\u001a\u00020\u000b2\u0006\u0010'\u001a\u00020(2\u0006\u0010,\u001a\u00020-2\u0006\u0010.\u001a\u00020/H\u0014J\u0010\u00100\u001a\u00020$2\u0006\u00101\u001a\u00020-H\u0016J\u0012\u00102\u001a\u00020\u000b2\b\u0010%\u001a\u0004\u0018\u000103H\u0016J\b\u00104\u001a\u00020$H\u0014J\f\u00105\u001a\u00060\bR\u00020\u0000H\u0002J\b\u00106\u001a\u00020$H\u0016J\u0006\u00107\u001a\u00020$J\u0014\u00108\u001a\u00020$2\n\u00109\u001a\u00060\bR\u00020\u0000H\u0002J\b\u0010:\u001a\u00020$H\u0016J\u0010\u0010;\u001a\u00020$2\u0006\u0010<\u001a\u00020\u0019H\u0016J\u0010\u0010=\u001a\u00020$2\u0006\u00101\u001a\u00020-H\u0016J\u0010\u0010>\u001a\u00020$2\u0006\u00101\u001a\u00020-H\u0016R\u0018\u0010\u0006\u001a\f\u0012\b\u0012\u00060\bR\u00020\u00000\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u0018\u0010\t\u001a\f\u0012\b\u0012\u00060\bR\u00020\u00000\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\n\u001a\u00020\u000bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\f\u0010\r\"\u0004\b\u000e\u0010\u000fR\u000e\u0010\u0010\u001a\u00020\u000bX\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00020\u0012X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u000bX\u0082\u000e¢\u0006\u0002\n\u0000R\u001e\u0010\u0014\u001a\u0012\u0012\u0004\u0012\u00020\u00020\u0015j\b\u0012\u0004\u0012\u00020\u0002`\u0016X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0017\u001a\u0004\u0018\u00010\u0002X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0019X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\u000bX\u0082\u000e¢\u0006\u0002\n\u0000R\u0011\u0010\u001b\u001a\u00020\u001c8F¢\u0006\u0006\u001a\u0004\b\u001d\u0010\u001eR\u0016\u0010\u001f\u001a\u0004\u0018\u00010\u001c8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b \u0010\u001e¨\u0006A"}, d2 = {"Lcom/swmansion/rnscreens/ScreenStack;", "Lcom/swmansion/rnscreens/ScreenContainer;", "Lcom/swmansion/rnscreens/ScreenStackFragment;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "drawingOpPool", "", "Lcom/swmansion/rnscreens/ScreenStack$DrawingOp;", "drawingOps", "goingForward", "", "getGoingForward", "()Z", "setGoingForward", "(Z)V", "isDetachingCurrentScreen", "mDismissed", "", "mRemovalTransitionStarted", "mStack", "Ljava/util/ArrayList;", "Lkotlin/collections/ArrayList;", "mTopScreen", "previousChildrenCount", "", "reverseLastTwoChildren", "rootScreen", "Lcom/swmansion/rnscreens/Screen;", "getRootScreen", "()Lcom/swmansion/rnscreens/Screen;", "topScreen", "getTopScreen", "adapt", "screen", "dismiss", "", "screenFragment", "dispatchDraw", "canvas", "Landroid/graphics/Canvas;", "dispatchOnFinishTransitioning", "drawAndRelease", "drawChild", "child", "Landroid/view/View;", "drawingTime", "", "endViewTransition", "view", "hasScreen", "Lcom/swmansion/rnscreens/ScreenFragment;", "notifyContainerUpdate", "obtainDrawingOp", "onUpdate", "onViewAppearTransitionEnd", "performDraw", "op", "removeAllScreens", "removeScreenAt", "index", "removeView", "startViewTransition", "Companion", "DrawingOp", "react-native-screens_release"}, k = 1, mv = {1, 4, 0})
/* loaded from: classes.dex */
public final class ScreenStack extends ScreenContainer<ScreenStackFragment> {
    public static final Companion Companion = new Companion(null);
    private boolean goingForward;
    private boolean isDetachingCurrentScreen;
    private boolean mRemovalTransitionStarted;
    private ScreenStackFragment mTopScreen;
    private int previousChildrenCount;
    private boolean reverseLastTwoChildren;
    private final ArrayList<ScreenStackFragment> mStack = new ArrayList<>();
    private final Set<ScreenStackFragment> mDismissed = new HashSet();
    private final List<DrawingOp> drawingOpPool = new ArrayList();
    private final List<DrawingOp> drawingOps = new ArrayList();

    @Metadata(bv = {1, 0, 3}, k = 3, mv = {1, 4, 0})
    /* loaded from: classes.dex */
    public final /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;
        public static final /* synthetic */ int[] $EnumSwitchMapping$1;

        static {
            int[] iArr = new int[Screen.StackAnimation.values().length];
            $EnumSwitchMapping$0 = iArr;
            iArr[Screen.StackAnimation.SLIDE_FROM_RIGHT.ordinal()] = 1;
            iArr[Screen.StackAnimation.SLIDE_FROM_LEFT.ordinal()] = 2;
            iArr[Screen.StackAnimation.SLIDE_FROM_BOTTOM.ordinal()] = 3;
            iArr[Screen.StackAnimation.FADE_FROM_BOTTOM.ordinal()] = 4;
            int[] iArr2 = new int[Screen.StackAnimation.values().length];
            $EnumSwitchMapping$1 = iArr2;
            iArr2[Screen.StackAnimation.SLIDE_FROM_RIGHT.ordinal()] = 1;
            iArr2[Screen.StackAnimation.SLIDE_FROM_LEFT.ordinal()] = 2;
            iArr2[Screen.StackAnimation.SLIDE_FROM_BOTTOM.ordinal()] = 3;
            iArr2[Screen.StackAnimation.FADE_FROM_BOTTOM.ordinal()] = 4;
        }
    }

    public ScreenStack(Context context) {
        super(context);
    }

    public final boolean getGoingForward() {
        return this.goingForward;
    }

    public final void setGoingForward(boolean z) {
        this.goingForward = z;
    }

    public final void dismiss(ScreenStackFragment screenFragment) {
        Intrinsics.checkNotNullParameter(screenFragment, "screenFragment");
        this.mDismissed.add(screenFragment);
        performUpdatesNow();
    }

    @Override // com.swmansion.rnscreens.ScreenContainer
    public Screen getTopScreen() {
        ScreenStackFragment screenStackFragment = this.mTopScreen;
        if (screenStackFragment != null) {
            return screenStackFragment.getScreen();
        }
        return null;
    }

    public final Screen getRootScreen() {
        int screenCount = getScreenCount();
        for (int i = 0; i < screenCount; i++) {
            Screen screenAt = getScreenAt(i);
            if (!CollectionsKt.contains(this.mDismissed, screenAt.getFragment())) {
                return screenAt;
            }
        }
        throw new IllegalStateException("Stack has no root screen set");
    }

    @Override // com.swmansion.rnscreens.ScreenContainer
    public ScreenStackFragment adapt(Screen screen) {
        Intrinsics.checkNotNullParameter(screen, "screen");
        return new ScreenStackFragment(screen);
    }

    @Override // android.view.ViewGroup
    public void startViewTransition(View view) {
        Intrinsics.checkNotNullParameter(view, "view");
        super.startViewTransition(view);
        this.mRemovalTransitionStarted = true;
    }

    @Override // android.view.ViewGroup
    public void endViewTransition(View view) {
        Intrinsics.checkNotNullParameter(view, "view");
        super.endViewTransition(view);
        if (this.mRemovalTransitionStarted) {
            this.mRemovalTransitionStarted = false;
            dispatchOnFinishTransitioning();
        }
    }

    public final void onViewAppearTransitionEnd() {
        if (!this.mRemovalTransitionStarted) {
            dispatchOnFinishTransitioning();
        }
    }

    private final void dispatchOnFinishTransitioning() {
        EventDispatcher eventDispatcher;
        Context context = getContext();
        Objects.requireNonNull(context, "null cannot be cast to non-null type com.facebook.react.bridge.ReactContext");
        UIManagerModule uIManagerModule = (UIManagerModule) ((ReactContext) context).getNativeModule(UIManagerModule.class);
        if (uIManagerModule == null || (eventDispatcher = uIManagerModule.getEventDispatcher()) == null) {
            return;
        }
        eventDispatcher.dispatchEvent(new StackFinishTransitioningEvent(getId()));
    }

    @Override // com.swmansion.rnscreens.ScreenContainer
    public void removeScreenAt(int i) {
        Screen screenAt = getScreenAt(i);
        Set<ScreenStackFragment> set = this.mDismissed;
        ScreenFragment fragment = screenAt.getFragment();
        Objects.requireNonNull(set, "null cannot be cast to non-null type kotlin.collections.MutableCollection<T>");
        TypeIntrinsics.asMutableCollection(set).remove(fragment);
        super.removeScreenAt(i);
    }

    @Override // com.swmansion.rnscreens.ScreenContainer
    public void removeAllScreens() {
        this.mDismissed.clear();
        super.removeAllScreens();
    }

    @Override // com.swmansion.rnscreens.ScreenContainer
    public boolean hasScreen(ScreenFragment screenFragment) {
        return super.hasScreen(screenFragment) && !CollectionsKt.contains(this.mDismissed, screenFragment);
    }

    @Override // com.swmansion.rnscreens.ScreenContainer
    public void onUpdate() {
        boolean z;
        ScreenStackFragment screenStackFragment;
        ScreenStackFragment screenStackFragment2;
        Screen screen;
        Screen.StackAnimation stackAnimation = null;
        final ScreenStackFragment screenStackFragment3 = null;
        this.isDetachingCurrentScreen = false;
        ScreenStackFragment screenStackFragment4 = screenStackFragment3;
        for (int size = this.mScreenFragments.size() - 1; size >= 0; size--) {
            Object obj = this.mScreenFragments.get(size);
            Intrinsics.checkNotNullExpressionValue(obj, "mScreenFragments[i]");
            ScreenStackFragment screenStackFragment5 = (ScreenStackFragment) obj;
            if (!this.mDismissed.contains(screenStackFragment5)) {
                if (screenStackFragment3 == null) {
                    screenStackFragment3 = screenStackFragment5;
                } else {
                    screenStackFragment4 = screenStackFragment5;
                }
                if (!Companion.isTransparent(screenStackFragment5)) {
                    break;
                }
            }
        }
        Screen.StackAnimation stackAnimation2 = null;
        boolean z2 = true;
        if (!CollectionsKt.contains(this.mStack, screenStackFragment3)) {
            ScreenStackFragment screenStackFragment6 = this.mTopScreen;
            if (screenStackFragment6 == null || screenStackFragment3 == null) {
                if (screenStackFragment6 == null && screenStackFragment3 != null) {
                    stackAnimation2 = Screen.StackAnimation.NONE;
                    if (screenStackFragment3.getScreen().getStackAnimation() != Screen.StackAnimation.NONE && !isNested()) {
                        this.goingForward = true;
                        screenStackFragment3.dispatchOnWillAppear();
                        screenStackFragment3.dispatchOnAppear();
                    }
                }
                z = true;
            } else {
                z = (screenStackFragment6 != null && this.mScreenFragments.contains(screenStackFragment6)) || screenStackFragment3.getScreen().getReplaceAnimation() != Screen.ReplaceAnimation.POP;
                stackAnimation2 = screenStackFragment3.getScreen().getStackAnimation();
            }
        } else {
            if (this.mTopScreen != null && (!Intrinsics.areEqual(screenStackFragment2, screenStackFragment3))) {
                ScreenStackFragment screenStackFragment7 = this.mTopScreen;
                if (screenStackFragment7 != null && (screen = screenStackFragment7.getScreen()) != null) {
                    stackAnimation = screen.getStackAnimation();
                }
                stackAnimation2 = stackAnimation;
                z = false;
            }
            z = true;
        }
        FragmentTransaction createTransaction = createTransaction();
        int i = FragmentTransaction.TRANSIT_FRAGMENT_OPEN;
        if (stackAnimation2 != null) {
            if (!z) {
                i = 8194;
                if (stackAnimation2 != null) {
                    int i2 = WhenMappings.$EnumSwitchMapping$1[stackAnimation2.ordinal()];
                    if (i2 == 1) {
                        Intrinsics.checkNotNullExpressionValue(createTransaction.setCustomAnimations(R.anim.rns_slide_in_from_left, R.anim.rns_slide_out_to_right), "it.setCustomAnimations(R…m.rns_slide_out_to_right)");
                    } else if (i2 == 2) {
                        Intrinsics.checkNotNullExpressionValue(createTransaction.setCustomAnimations(R.anim.rns_slide_in_from_right, R.anim.rns_slide_out_to_left), "it.setCustomAnimations(R…im.rns_slide_out_to_left)");
                    } else if (i2 == 3) {
                        Intrinsics.checkNotNullExpressionValue(createTransaction.setCustomAnimations(R.anim.rns_no_animation_medium, R.anim.rns_slide_out_to_bottom), "it.setCustomAnimations(\n…                        )");
                    } else if (i2 == 4) {
                        Intrinsics.checkNotNullExpressionValue(createTransaction.setCustomAnimations(R.anim.rns_no_animation_250, R.anim.rns_fade_to_bottom), "it.setCustomAnimations(R….anim.rns_fade_to_bottom)");
                    }
                }
            } else if (stackAnimation2 != null) {
                int i3 = WhenMappings.$EnumSwitchMapping$0[stackAnimation2.ordinal()];
                if (i3 == 1) {
                    Intrinsics.checkNotNullExpressionValue(createTransaction.setCustomAnimations(R.anim.rns_slide_in_from_right, R.anim.rns_slide_out_to_left), "it.setCustomAnimations(R…im.rns_slide_out_to_left)");
                } else if (i3 == 2) {
                    Intrinsics.checkNotNullExpressionValue(createTransaction.setCustomAnimations(R.anim.rns_slide_in_from_left, R.anim.rns_slide_out_to_right), "it.setCustomAnimations(R…m.rns_slide_out_to_right)");
                } else if (i3 == 3) {
                    Intrinsics.checkNotNullExpressionValue(createTransaction.setCustomAnimations(R.anim.rns_slide_in_from_bottom, R.anim.rns_no_animation_medium), "it.setCustomAnimations(\n…                        )");
                } else if (i3 == 4) {
                    Intrinsics.checkNotNullExpressionValue(createTransaction.setCustomAnimations(R.anim.rns_fade_from_bottom, R.anim.rns_no_animation_350), "it.setCustomAnimations(R…nim.rns_no_animation_350)");
                }
            }
        }
        if (stackAnimation2 == Screen.StackAnimation.NONE) {
            i = 0;
        }
        if (stackAnimation2 == Screen.StackAnimation.FADE) {
            i = FragmentTransaction.TRANSIT_FRAGMENT_FADE;
        }
        if (stackAnimation2 != null && Companion.isSystemAnimation(stackAnimation2)) {
            createTransaction.setTransition(i);
        }
        this.goingForward = z;
        if (z && screenStackFragment3 != null && Companion.needsDrawReordering(screenStackFragment3) && screenStackFragment4 == null) {
            this.isDetachingCurrentScreen = true;
        }
        Iterator<ScreenStackFragment> it = this.mStack.iterator();
        while (it.hasNext()) {
            ScreenStackFragment next = it.next();
            if (!this.mScreenFragments.contains(next) || this.mDismissed.contains(next)) {
                createTransaction.remove(next);
            }
        }
        Iterator it2 = this.mScreenFragments.iterator();
        while (it2.hasNext() && (screenStackFragment = (ScreenStackFragment) it2.next()) != screenStackFragment4) {
            if (screenStackFragment != screenStackFragment3 && !this.mDismissed.contains(screenStackFragment)) {
                createTransaction.remove(screenStackFragment);
            }
        }
        if (screenStackFragment4 != null && !screenStackFragment4.isAdded()) {
            Iterator it3 = this.mScreenFragments.iterator();
            while (it3.hasNext()) {
                ScreenStackFragment screenStackFragment8 = (ScreenStackFragment) it3.next();
                if (z2) {
                    if (screenStackFragment8 == screenStackFragment4) {
                        z2 = false;
                    }
                }
                createTransaction.add(getId(), screenStackFragment8).runOnCommit(new Runnable() { // from class: com.swmansion.rnscreens.ScreenStack$onUpdate$1$1
                    @Override // java.lang.Runnable
                    public final void run() {
                        Screen screen2;
                        ScreenStackFragment screenStackFragment9 = ScreenStackFragment.this;
                        if (screenStackFragment9 == null || (screen2 = screenStackFragment9.getScreen()) == null) {
                            return;
                        }
                        screen2.bringToFront();
                    }
                });
            }
        } else if (screenStackFragment3 != null && !screenStackFragment3.isAdded()) {
            createTransaction.add(getId(), screenStackFragment3);
        }
        this.mTopScreen = screenStackFragment3;
        this.mStack.clear();
        this.mStack.addAll(this.mScreenFragments);
        createTransaction.commitNowAllowingStateLoss();
    }

    @Override // com.swmansion.rnscreens.ScreenContainer
    protected void notifyContainerUpdate() {
        Iterator<ScreenStackFragment> it = this.mStack.iterator();
        while (it.hasNext()) {
            it.next().onContainerUpdate();
        }
    }

    @Override // com.swmansion.rnscreens.ScreenContainer, android.view.ViewGroup, android.view.ViewManager
    public void removeView(View view) {
        Intrinsics.checkNotNullParameter(view, "view");
        if (this.isDetachingCurrentScreen) {
            this.isDetachingCurrentScreen = false;
            this.reverseLastTwoChildren = true;
        }
        super.removeView(view);
    }

    private final void drawAndRelease() {
        int size = this.drawingOps.size();
        for (int i = 0; i < size; i++) {
            DrawingOp drawingOp = this.drawingOps.get(i);
            drawingOp.draw();
            this.drawingOpPool.add(drawingOp);
        }
        this.drawingOps.clear();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        Intrinsics.checkNotNullParameter(canvas, "canvas");
        super.dispatchDraw(canvas);
        if (this.drawingOps.size() < this.previousChildrenCount) {
            this.reverseLastTwoChildren = false;
        }
        this.previousChildrenCount = this.drawingOps.size();
        if (this.reverseLastTwoChildren && this.drawingOps.size() >= 2) {
            List<DrawingOp> list = this.drawingOps;
            Collections.swap(list, list.size() - 1, this.drawingOps.size() - 2);
        }
        drawAndRelease();
    }

    @Override // android.view.ViewGroup
    protected boolean drawChild(Canvas canvas, View child, long j) {
        Intrinsics.checkNotNullParameter(canvas, "canvas");
        Intrinsics.checkNotNullParameter(child, "child");
        this.drawingOps.add(obtainDrawingOp().set(canvas, child, j));
        return true;
    }

    public final void performDraw(DrawingOp drawingOp) {
        super.drawChild(drawingOp.getCanvas(), drawingOp.getChild(), drawingOp.getDrawingTime());
    }

    private final DrawingOp obtainDrawingOp() {
        if (this.drawingOpPool.isEmpty()) {
            return new DrawingOp();
        }
        List<DrawingOp> list = this.drawingOpPool;
        return list.remove(list.size() - 1);
    }

    /* compiled from: ScreenStack.kt */
    @Metadata(bv = {1, 0, 3}, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\t\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0082\u0004\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0006\u0010\u0015\u001a\u00020\u0016J)\u0010\u0017\u001a\u00060\u0000R\u00020\u00182\b\u0010\u0003\u001a\u0004\u0018\u00010\u00042\b\u0010\t\u001a\u0004\u0018\u00010\n2\u0006\u0010\u000f\u001a\u00020\u0010H\u0086\u0002R\u001c\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\bR\u001c\u0010\t\u001a\u0004\u0018\u00010\nX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR\u001a\u0010\u000f\u001a\u00020\u0010X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u0012\"\u0004\b\u0013\u0010\u0014¨\u0006\u0019"}, d2 = {"Lcom/swmansion/rnscreens/ScreenStack$DrawingOp;", "", "(Lcom/swmansion/rnscreens/ScreenStack;)V", "canvas", "Landroid/graphics/Canvas;", "getCanvas", "()Landroid/graphics/Canvas;", "setCanvas", "(Landroid/graphics/Canvas;)V", "child", "Landroid/view/View;", "getChild", "()Landroid/view/View;", "setChild", "(Landroid/view/View;)V", "drawingTime", "", "getDrawingTime", "()J", "setDrawingTime", "(J)V", "draw", "", "set", "Lcom/swmansion/rnscreens/ScreenStack;", "react-native-screens_release"}, k = 1, mv = {1, 4, 0})
    /* loaded from: classes.dex */
    public final class DrawingOp {
        private Canvas canvas;
        private View child;
        private long drawingTime;

        public DrawingOp() {
            ScreenStack.this = r1;
        }

        public final Canvas getCanvas() {
            return this.canvas;
        }

        public final void setCanvas(Canvas canvas) {
            this.canvas = canvas;
        }

        public final View getChild() {
            return this.child;
        }

        public final void setChild(View view) {
            this.child = view;
        }

        public final long getDrawingTime() {
            return this.drawingTime;
        }

        public final void setDrawingTime(long j) {
            this.drawingTime = j;
        }

        public final DrawingOp set(Canvas canvas, View view, long j) {
            this.canvas = canvas;
            this.child = view;
            this.drawingTime = j;
            return this;
        }

        public final void draw() {
            ScreenStack.this.performDraw(this);
            this.canvas = null;
            this.child = null;
            this.drawingTime = 0L;
        }
    }

    /* compiled from: ScreenStack.kt */
    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0002J\u0010\u0010\u0007\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\tH\u0002J\u0010\u0010\n\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\tH\u0002¨\u0006\u000b"}, d2 = {"Lcom/swmansion/rnscreens/ScreenStack$Companion;", "", "()V", "isSystemAnimation", "", "stackAnimation", "Lcom/swmansion/rnscreens/Screen$StackAnimation;", "isTransparent", "fragment", "Lcom/swmansion/rnscreens/ScreenStackFragment;", "needsDrawReordering", "react-native-screens_release"}, k = 1, mv = {1, 4, 0})
    /* loaded from: classes.dex */
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        public final boolean isSystemAnimation(Screen.StackAnimation stackAnimation) {
            return stackAnimation == Screen.StackAnimation.DEFAULT || stackAnimation == Screen.StackAnimation.FADE || stackAnimation == Screen.StackAnimation.NONE;
        }

        public final boolean isTransparent(ScreenStackFragment screenStackFragment) {
            return screenStackFragment.getScreen().getStackPresentation() == Screen.StackPresentation.TRANSPARENT_MODAL;
        }

        public final boolean needsDrawReordering(ScreenStackFragment screenStackFragment) {
            return screenStackFragment.getScreen().getStackAnimation() == Screen.StackAnimation.SLIDE_FROM_BOTTOM || screenStackFragment.getScreen().getStackAnimation() == Screen.StackAnimation.FADE_FROM_BOTTOM;
        }
    }
}
