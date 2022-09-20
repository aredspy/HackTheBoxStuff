package com.facebook.react.views.scroll;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.OverScroller;
import androidx.recyclerview.widget.ItemTouchHelper;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.uimanager.UIManagerHelper;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
/* loaded from: classes.dex */
public class ReactScrollViewHelper {
    public static final String AUTO = "auto";
    public static final long MOMENTUM_DELAY = 20;
    public static final String OVER_SCROLL_ALWAYS = "always";
    public static final String OVER_SCROLL_NEVER = "never";
    private static Set<ScrollListener> sScrollListeners = Collections.newSetFromMap(new WeakHashMap());
    private static int SMOOTH_SCROLL_DURATION = ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION;
    private static boolean mSmoothScrollDurationInitialized = false;

    /* loaded from: classes.dex */
    public interface ScrollListener {
        void onLayout(ViewGroup scrollView);

        void onScroll(ViewGroup scrollView, ScrollEventType scrollEventType, float xVelocity, float yVelocity);
    }

    public static void emitScrollEvent(ViewGroup scrollView, float xVelocity, float yVelocity) {
        emitScrollEvent(scrollView, ScrollEventType.SCROLL, xVelocity, yVelocity);
    }

    public static void emitScrollBeginDragEvent(ViewGroup scrollView) {
        emitScrollEvent(scrollView, ScrollEventType.BEGIN_DRAG);
    }

    public static void emitScrollEndDragEvent(ViewGroup scrollView, float xVelocity, float yVelocity) {
        emitScrollEvent(scrollView, ScrollEventType.END_DRAG, xVelocity, yVelocity);
    }

    public static void emitScrollMomentumBeginEvent(ViewGroup scrollView, int xVelocity, int yVelocity) {
        emitScrollEvent(scrollView, ScrollEventType.MOMENTUM_BEGIN, xVelocity, yVelocity);
    }

    public static void emitScrollMomentumEndEvent(ViewGroup scrollView) {
        emitScrollEvent(scrollView, ScrollEventType.MOMENTUM_END);
    }

    private static void emitScrollEvent(ViewGroup scrollView, ScrollEventType scrollEventType) {
        emitScrollEvent(scrollView, scrollEventType, 0.0f, 0.0f);
    }

    private static void emitScrollEvent(ViewGroup scrollView, ScrollEventType scrollEventType, float xVelocity, float yVelocity) {
        View childAt = scrollView.getChildAt(0);
        if (childAt == null) {
            return;
        }
        for (ScrollListener scrollListener : sScrollListeners) {
            scrollListener.onScroll(scrollView, scrollEventType, xVelocity, yVelocity);
        }
        ReactContext reactContext = (ReactContext) scrollView.getContext();
        UIManagerHelper.getEventDispatcherForReactTag(reactContext, scrollView.getId()).dispatchEvent(ScrollEvent.obtain(UIManagerHelper.getSurfaceId(reactContext), scrollView.getId(), scrollEventType, scrollView.getScrollX(), scrollView.getScrollY(), xVelocity, yVelocity, childAt.getWidth(), childAt.getHeight(), scrollView.getWidth(), scrollView.getHeight()));
    }

    public static void emitLayoutEvent(ViewGroup scrollView) {
        for (ScrollListener scrollListener : sScrollListeners) {
            scrollListener.onLayout(scrollView);
        }
    }

    public static int parseOverScrollMode(String jsOverScrollMode) {
        if (jsOverScrollMode == null || jsOverScrollMode.equals("auto")) {
            return 1;
        }
        if (jsOverScrollMode.equals(OVER_SCROLL_ALWAYS)) {
            return 0;
        }
        if (jsOverScrollMode.equals(OVER_SCROLL_NEVER)) {
            return 2;
        }
        throw new JSApplicationIllegalArgumentException("wrong overScrollMode: " + jsOverScrollMode);
    }

    public static int getDefaultScrollAnimationDuration(Context context) {
        if (!mSmoothScrollDurationInitialized) {
            mSmoothScrollDurationInitialized = true;
            try {
                SMOOTH_SCROLL_DURATION = new OverScrollerDurationGetter(context).getScrollAnimationDuration();
            } catch (Throwable unused) {
            }
        }
        return SMOOTH_SCROLL_DURATION;
    }

    /* loaded from: classes.dex */
    public static class OverScrollerDurationGetter extends OverScroller {
        private int mScrollAnimationDuration = ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION;

        OverScrollerDurationGetter(Context context) {
            super(context);
        }

        public int getScrollAnimationDuration() {
            super.startScroll(0, 0, 0, 0);
            return this.mScrollAnimationDuration;
        }

        @Override // android.widget.OverScroller
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            this.mScrollAnimationDuration = duration;
        }
    }

    public static void addScrollListener(ScrollListener listener) {
        sScrollListeners.add(listener);
    }

    public static void removeScrollListener(ScrollListener listener) {
        sScrollListeners.remove(listener);
    }
}
