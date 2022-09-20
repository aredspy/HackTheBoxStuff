package com.swmansion.rnscreens;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Transformation;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.ViewProps;
import com.google.android.material.appbar.AppBarLayout;
import com.swmansion.rnscreens.Screen;
import com.swmansion.rnscreens.ScreenStackFragment;
import java.util.Objects;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
/* compiled from: ScreenStackFragment.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000X\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\u0018\u00002\u00020\u0001:\u0002()B\u000f\b\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004B\u0007\b\u0016¢\u0006\u0002\u0010\u0005J\u0006\u0010\r\u001a\u00020\tJ\u0006\u0010\u000e\u001a\u00020\u000fJ\b\u0010\u0010\u001a\u00020\u000fH\u0002J\b\u0010\u0011\u001a\u00020\u000fH\u0016J\"\u0010\u0012\u001a\u0004\u0018\u00010\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\t2\u0006\u0010\u0017\u001a\u00020\u0015H\u0016J&\u0010\u0018\u001a\u0004\u0018\u00010\u00192\u0006\u0010\u001a\u001a\u00020\u001b2\b\u0010\u001c\u001a\u0004\u0018\u00010\u001d2\b\u0010\u001e\u001a\u0004\u0018\u00010\u001fH\u0016J\b\u0010 \u001a\u00020\u000fH\u0016J\u0006\u0010!\u001a\u00020\u000fJ\u000e\u0010\"\u001a\u00020\u000f2\u0006\u0010#\u001a\u00020\fJ\u000e\u0010$\u001a\u00020\u000f2\u0006\u0010%\u001a\u00020\tJ\u000e\u0010&\u001a\u00020\u000f2\u0006\u0010'\u001a\u00020\tR\u0010\u0010\u0006\u001a\u0004\u0018\u00010\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u000b\u001a\u0004\u0018\u00010\fX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006*"}, d2 = {"Lcom/swmansion/rnscreens/ScreenStackFragment;", "Lcom/swmansion/rnscreens/ScreenFragment;", "screenView", "Lcom/swmansion/rnscreens/Screen;", "(Lcom/swmansion/rnscreens/Screen;)V", "()V", "mAppBarLayout", "Lcom/google/android/material/appbar/AppBarLayout;", "mIsTranslucent", "", "mShadowHidden", "mToolbar", "Landroidx/appcompat/widget/Toolbar;", "canNavigateBack", "dismiss", "", "notifyViewAppearTransitionEnd", "onContainerUpdate", "onCreateAnimation", "Landroid/view/animation/Animation;", "transit", "", "enter", "nextAnim", "onCreateView", "Landroid/view/View;", "inflater", "Landroid/view/LayoutInflater;", "container", "Landroid/view/ViewGroup;", "savedInstanceState", "Landroid/os/Bundle;", "onViewAnimationEnd", "removeToolbar", "setToolbar", "toolbar", "setToolbarShadowHidden", ViewProps.HIDDEN, "setToolbarTranslucent", "translucent", "NotifyingCoordinatorLayout", "ScreensAnimation", "react-native-screens_release"}, k = 1, mv = {1, 4, 0})
/* loaded from: classes.dex */
public final class ScreenStackFragment extends ScreenFragment {
    private AppBarLayout mAppBarLayout;
    private boolean mIsTranslucent;
    private boolean mShadowHidden;
    private Toolbar mToolbar;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public ScreenStackFragment(Screen screenView) {
        super(screenView);
        Intrinsics.checkNotNullParameter(screenView, "screenView");
    }

    public ScreenStackFragment() {
        throw new IllegalStateException("ScreenStack fragments should never be restored. Follow instructions from https://github.com/software-mansion/react-native-screens/issues/17#issuecomment-424704067 to properly configure your main activity.");
    }

    public final void removeToolbar() {
        Toolbar toolbar;
        AppBarLayout appBarLayout = this.mAppBarLayout;
        if (appBarLayout != null && (toolbar = this.mToolbar) != null && toolbar.getParent() == appBarLayout) {
            appBarLayout.removeView(toolbar);
        }
        this.mToolbar = null;
    }

    public final void setToolbar(Toolbar toolbar) {
        Intrinsics.checkNotNullParameter(toolbar, "toolbar");
        AppBarLayout appBarLayout = this.mAppBarLayout;
        if (appBarLayout != null) {
            appBarLayout.addView(toolbar);
        }
        AppBarLayout.LayoutParams layoutParams = new AppBarLayout.LayoutParams(-1, -2);
        layoutParams.setScrollFlags(0);
        toolbar.setLayoutParams(layoutParams);
        this.mToolbar = toolbar;
    }

    public final void setToolbarShadowHidden(boolean z) {
        if (this.mShadowHidden != z) {
            AppBarLayout appBarLayout = this.mAppBarLayout;
            if (appBarLayout != null) {
                appBarLayout.setTargetElevation(z ? 0.0f : PixelUtil.toPixelFromDIP(4.0f));
            }
            this.mShadowHidden = z;
        }
    }

    public final void setToolbarTranslucent(boolean z) {
        if (this.mIsTranslucent != z) {
            ViewGroup.LayoutParams layoutParams = getScreen().getLayoutParams();
            Objects.requireNonNull(layoutParams, "null cannot be cast to non-null type androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams");
            ((CoordinatorLayout.LayoutParams) layoutParams).setBehavior(z ? null : new AppBarLayout.ScrollingViewBehavior());
            this.mIsTranslucent = z;
        }
    }

    @Override // com.swmansion.rnscreens.ScreenFragment
    public void onContainerUpdate() {
        ScreenStackHeaderConfig headerConfig = getScreen().getHeaderConfig();
        if (headerConfig != null) {
            headerConfig.onUpdate();
        }
    }

    @Override // com.swmansion.rnscreens.ScreenFragment
    public void onViewAnimationEnd() {
        super.onViewAnimationEnd();
        notifyViewAppearTransitionEnd();
    }

    @Override // androidx.fragment.app.Fragment
    public Animation onCreateAnimation(int i, boolean z, int i2) {
        if (i == 0 && !isHidden() && getScreen().getStackAnimation() == Screen.StackAnimation.NONE) {
            if (z) {
                UiThreadUtil.runOnUiThread(new Runnable() { // from class: com.swmansion.rnscreens.ScreenStackFragment$onCreateAnimation$1
                    @Override // java.lang.Runnable
                    public final void run() {
                        ScreenStackFragment.this.dispatchOnWillAppear();
                        ScreenStackFragment.this.dispatchOnAppear();
                    }
                });
                return null;
            }
            dispatchOnWillDisappear();
            dispatchOnDisappear();
            notifyViewAppearTransitionEnd();
            return null;
        }
        return null;
    }

    private final void notifyViewAppearTransitionEnd() {
        View view = getView();
        ViewParent parent = view != null ? view.getParent() : null;
        if (parent instanceof ScreenStack) {
            ((ScreenStack) parent).onViewAppearTransitionEnd();
        }
    }

    @Override // com.swmansion.rnscreens.ScreenFragment, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {
        NotifyingCoordinatorLayout notifyingCoordinatorLayout;
        AppBarLayout appBarLayout;
        AppBarLayout appBarLayout2;
        Intrinsics.checkNotNullParameter(inflater, "inflater");
        Context it = getContext();
        AppBarLayout appBarLayout3 = null;
        if (it != null) {
            Intrinsics.checkNotNullExpressionValue(it, "it");
            notifyingCoordinatorLayout = new NotifyingCoordinatorLayout(it, this);
        } else {
            notifyingCoordinatorLayout = null;
        }
        CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams(-1, -1);
        layoutParams.setBehavior(this.mIsTranslucent ? null : new AppBarLayout.ScrollingViewBehavior());
        getScreen().setLayoutParams(layoutParams);
        if (notifyingCoordinatorLayout != null) {
            notifyingCoordinatorLayout.addView(ScreenFragment.Companion.recycleView(getScreen()));
        }
        Context context = getContext();
        if (context != null) {
            appBarLayout3 = new AppBarLayout(context);
        }
        this.mAppBarLayout = appBarLayout3;
        if (appBarLayout3 != null) {
            appBarLayout3.setBackgroundColor(0);
        }
        AppBarLayout appBarLayout4 = this.mAppBarLayout;
        if (appBarLayout4 != null) {
            appBarLayout4.setLayoutParams(new AppBarLayout.LayoutParams(-1, -2));
        }
        if (notifyingCoordinatorLayout != null) {
            notifyingCoordinatorLayout.addView(this.mAppBarLayout);
        }
        if (this.mShadowHidden && (appBarLayout2 = this.mAppBarLayout) != null) {
            appBarLayout2.setTargetElevation(0.0f);
        }
        Toolbar toolbar = this.mToolbar;
        if (toolbar != null && (appBarLayout = this.mAppBarLayout) != null) {
            appBarLayout.addView(ScreenFragment.Companion.recycleView(toolbar));
        }
        return notifyingCoordinatorLayout;
    }

    public final boolean canNavigateBack() {
        ScreenContainer<?> container = getScreen().getContainer();
        if (!(container instanceof ScreenStack)) {
            throw new IllegalStateException("ScreenStackFragment added into a non-stack container".toString());
        }
        if (!Intrinsics.areEqual(((ScreenStack) container).getRootScreen(), getScreen())) {
            return true;
        }
        Fragment parentFragment = getParentFragment();
        if (!(parentFragment instanceof ScreenStackFragment)) {
            return false;
        }
        return ((ScreenStackFragment) parentFragment).canNavigateBack();
    }

    public final void dismiss() {
        ScreenContainer<?> container = getScreen().getContainer();
        if (!(container instanceof ScreenStack)) {
            throw new IllegalStateException("ScreenStackFragment added into a non-stack container".toString());
        }
        ((ScreenStack) container).dismiss(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: ScreenStackFragment.kt */
    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0002\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\u0010\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0016R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\r"}, d2 = {"Lcom/swmansion/rnscreens/ScreenStackFragment$NotifyingCoordinatorLayout;", "Landroidx/coordinatorlayout/widget/CoordinatorLayout;", "context", "Landroid/content/Context;", "mFragment", "Lcom/swmansion/rnscreens/ScreenFragment;", "(Landroid/content/Context;Lcom/swmansion/rnscreens/ScreenFragment;)V", "mAnimationListener", "Landroid/view/animation/Animation$AnimationListener;", "startAnimation", "", "animation", "Landroid/view/animation/Animation;", "react-native-screens_release"}, k = 1, mv = {1, 4, 0})
    /* loaded from: classes.dex */
    public static final class NotifyingCoordinatorLayout extends CoordinatorLayout {
        private final Animation.AnimationListener mAnimationListener = new Animation.AnimationListener() { // from class: com.swmansion.rnscreens.ScreenStackFragment$NotifyingCoordinatorLayout$mAnimationListener$1
            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationRepeat(Animation animation) {
                Intrinsics.checkNotNullParameter(animation, "animation");
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationStart(Animation animation) {
                ScreenFragment screenFragment;
                Intrinsics.checkNotNullParameter(animation, "animation");
                screenFragment = ScreenStackFragment.NotifyingCoordinatorLayout.this.mFragment;
                screenFragment.onViewAnimationStart();
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationEnd(Animation animation) {
                ScreenFragment screenFragment;
                Intrinsics.checkNotNullParameter(animation, "animation");
                screenFragment = ScreenStackFragment.NotifyingCoordinatorLayout.this.mFragment;
                screenFragment.onViewAnimationEnd();
            }
        };
        private final ScreenFragment mFragment;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public NotifyingCoordinatorLayout(Context context, ScreenFragment mFragment) {
            super(context);
            Intrinsics.checkNotNullParameter(context, "context");
            Intrinsics.checkNotNullParameter(mFragment, "mFragment");
            this.mFragment = mFragment;
        }

        @Override // android.view.View
        public void startAnimation(Animation animation) {
            Intrinsics.checkNotNullParameter(animation, "animation");
            ScreensAnimation screensAnimation = new ScreensAnimation(this.mFragment);
            screensAnimation.setDuration(animation.getDuration());
            if ((animation instanceof AnimationSet) && !this.mFragment.isRemoving()) {
                ((AnimationSet) animation).addAnimation(screensAnimation);
                animation.setAnimationListener(this.mAnimationListener);
                super.startAnimation(animation);
                return;
            }
            AnimationSet animationSet = new AnimationSet(true);
            animationSet.addAnimation(animation);
            animationSet.addAnimation(screensAnimation);
            animationSet.setAnimationListener(this.mAnimationListener);
            super.startAnimation(animationSet);
        }
    }

    /* compiled from: ScreenStackFragment.kt */
    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0002\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\u0018\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0014R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000b"}, d2 = {"Lcom/swmansion/rnscreens/ScreenStackFragment$ScreensAnimation;", "Landroid/view/animation/Animation;", "mFragment", "Lcom/swmansion/rnscreens/ScreenFragment;", "(Lcom/swmansion/rnscreens/ScreenFragment;)V", "applyTransformation", "", "interpolatedTime", "", "t", "Landroid/view/animation/Transformation;", "react-native-screens_release"}, k = 1, mv = {1, 4, 0})
    /* loaded from: classes.dex */
    private static final class ScreensAnimation extends Animation {
        private final ScreenFragment mFragment;

        public ScreensAnimation(ScreenFragment mFragment) {
            Intrinsics.checkNotNullParameter(mFragment, "mFragment");
            this.mFragment = mFragment;
        }

        @Override // android.view.animation.Animation
        protected void applyTransformation(float f, Transformation t) {
            Intrinsics.checkNotNullParameter(t, "t");
            super.applyTransformation(f, t);
            ScreenFragment screenFragment = this.mFragment;
            screenFragment.dispatchTransitionProgress(f, !screenFragment.isResumed());
        }
    }
}
