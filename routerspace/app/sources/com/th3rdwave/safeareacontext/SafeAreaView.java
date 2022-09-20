package com.th3rdwave.safeareacontext;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.views.view.ReactViewGroup;
import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicBoolean;
/* loaded from: classes.dex */
public class SafeAreaView extends ReactViewGroup implements ViewTreeObserver.OnPreDrawListener {
    private static final long MAX_WAIT_TIME_NANO = 500000000;
    private EnumSet<SafeAreaViewEdges> mEdges;
    private EdgeInsets mInsets;
    private SafeAreaViewMode mMode = SafeAreaViewMode.PADDING;
    private View mProviderView;

    public SafeAreaView(Context context) {
        super(context);
    }

    private static ReactContext getReactContext(View view) {
        Context context = view.getContext();
        if (!(context instanceof ReactContext) && (context instanceof ContextWrapper)) {
            context = ((ContextWrapper) context).getBaseContext();
        }
        return (ReactContext) context;
    }

    private void updateInsets() {
        if (this.mInsets != null) {
            EnumSet<SafeAreaViewEdges> enumSet = this.mEdges;
            if (enumSet == null) {
                enumSet = EnumSet.allOf(SafeAreaViewEdges.class);
            }
            SafeAreaViewLocalData safeAreaViewLocalData = new SafeAreaViewLocalData(this.mInsets, this.mMode, enumSet);
            UIManagerModule uIManagerModule = (UIManagerModule) getReactContext(this).getNativeModule(UIManagerModule.class);
            if (uIManagerModule == null) {
                return;
            }
            uIManagerModule.setViewLocalData(getId(), safeAreaViewLocalData);
            waitForReactLayout();
        }
    }

    private void waitForReactLayout() {
        final AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        long nanoTime = System.nanoTime();
        getReactContext(this).runOnNativeModulesQueueThread(new Runnable() { // from class: com.th3rdwave.safeareacontext.SafeAreaView.1
            @Override // java.lang.Runnable
            public void run() {
                synchronized (atomicBoolean) {
                    if (atomicBoolean.compareAndSet(false, true)) {
                        atomicBoolean.notify();
                    }
                }
            }
        });
        synchronized (atomicBoolean) {
            long j = 0;
            while (!atomicBoolean.get() && j < MAX_WAIT_TIME_NANO) {
                try {
                    atomicBoolean.wait(500L);
                } catch (InterruptedException unused) {
                    atomicBoolean.set(true);
                }
                j += System.nanoTime() - nanoTime;
            }
            if (j >= MAX_WAIT_TIME_NANO) {
                Log.w("SafeAreaView", "Timed out waiting for layout.");
            }
        }
    }

    public void setMode(SafeAreaViewMode mode) {
        this.mMode = mode;
        updateInsets();
    }

    public void setEdges(EnumSet<SafeAreaViewEdges> edges) {
        this.mEdges = edges;
        updateInsets();
    }

    private boolean maybeUpdateInsets() {
        EdgeInsets safeAreaInsets;
        EdgeInsets edgeInsets;
        View view = this.mProviderView;
        if (view == null || (safeAreaInsets = SafeAreaUtils.getSafeAreaInsets(view)) == null || ((edgeInsets = this.mInsets) != null && edgeInsets.equalsToEdgeInsets(safeAreaInsets))) {
            return false;
        }
        this.mInsets = safeAreaInsets;
        updateInsets();
        return true;
    }

    private View findProvider() {
        for (ViewParent parent = getParent(); parent != null; parent = parent.getParent()) {
            if (parent instanceof SafeAreaProvider) {
                return (View) parent;
            }
        }
        return this;
    }

    @Override // com.facebook.react.views.view.ReactViewGroup, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        View findProvider = findProvider();
        this.mProviderView = findProvider;
        findProvider.getViewTreeObserver().addOnPreDrawListener(this);
        maybeUpdateInsets();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        View view = this.mProviderView;
        if (view != null) {
            view.getViewTreeObserver().removeOnPreDrawListener(this);
        }
        this.mProviderView = null;
    }

    @Override // android.view.ViewTreeObserver.OnPreDrawListener
    public boolean onPreDraw() {
        boolean maybeUpdateInsets = maybeUpdateInsets();
        if (maybeUpdateInsets) {
            requestLayout();
        }
        return !maybeUpdateInsets;
    }
}
