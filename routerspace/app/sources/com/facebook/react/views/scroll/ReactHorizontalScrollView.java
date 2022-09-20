package com.facebook.react.views.scroll;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.HorizontalScrollView;
import android.widget.OverScroller;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import com.facebook.common.logging.FLog;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.common.ReactConstants;
import com.facebook.react.modules.i18nmanager.I18nUtil;
import com.facebook.react.uimanager.FabricViewStateManager;
import com.facebook.react.uimanager.MeasureSpecAssertions;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.ReactClippingViewGroup;
import com.facebook.react.uimanager.ReactClippingViewGroupHelper;
import com.facebook.react.uimanager.ReactOverflowView;
import com.facebook.react.uimanager.ViewProps;
import com.facebook.react.uimanager.events.NativeGestureUtil;
import com.facebook.react.views.view.ReactViewBackgroundManager;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
public class ReactHorizontalScrollView extends HorizontalScrollView implements ReactClippingViewGroup, FabricViewStateManager.HasFabricViewStateManager, ReactOverflowView {
    private static final String CONTENT_OFFSET_LEFT = "contentOffsetLeft";
    private static final String CONTENT_OFFSET_TOP = "contentOffsetTop";
    private static boolean DEBUG_MODE = false;
    private static int NO_SCROLL_POSITION = Integer.MIN_VALUE;
    private static final String SCROLL_AWAY_PADDING_TOP = "scrollAwayPaddingTop";
    private static String TAG = "ReactHorizontalScrollView";
    private static final int UNSET_CONTENT_OFFSET = -1;
    private static Field sScrollerField = null;
    private static boolean sTriedToGetScrollerField = false;
    private boolean mActivelyScrolling;
    private Rect mClippingRect;
    private float mDecelerationRate;
    private boolean mDisableIntervalMomentum;
    private boolean mDragging;
    private Drawable mEndBackground;
    private int mEndFillColor;
    private final FabricViewStateManager mFabricViewStateManager;
    private int mFinalAnimatedPositionScrollX;
    private int mFinalAnimatedPositionScrollY;
    private FpsListener mFpsListener;
    private int mLastStateUpdateScrollX;
    private int mLastStateUpdateScrollY;
    private int mLayoutDirection;
    private final OnScrollDispatchHelper mOnScrollDispatchHelper;
    private String mOverflow;
    private boolean mPagedArrowScrolling;
    private boolean mPagingEnabled;
    private Runnable mPostTouchRunnable;
    private ReactViewBackgroundManager mReactBackgroundManager;
    private final Rect mRect;
    private boolean mRemoveClippedSubviews;
    private ValueAnimator mScrollAnimator;
    private boolean mScrollEnabled;
    private String mScrollPerfTag;
    private int mScrollXAfterMeasure;
    private final OverScroller mScroller;
    private boolean mSendMomentumEvents;
    private int mSnapInterval;
    private List<Integer> mSnapOffsets;
    private boolean mSnapToEnd;
    private boolean mSnapToStart;
    private final Rect mTempRect;
    private final VelocityHelper mVelocityHelper;
    private int pendingContentOffsetX;
    private int pendingContentOffsetY;

    public ReactHorizontalScrollView(Context context) {
        this(context, null);
    }

    public ReactHorizontalScrollView(Context context, FpsListener fpsListener) {
        super(context);
        this.mScrollXAfterMeasure = NO_SCROLL_POSITION;
        this.mOnScrollDispatchHelper = new OnScrollDispatchHelper();
        this.mVelocityHelper = new VelocityHelper();
        this.mRect = new Rect();
        this.mOverflow = ViewProps.HIDDEN;
        this.mPagingEnabled = false;
        this.mScrollEnabled = true;
        this.mFpsListener = null;
        this.mEndFillColor = 0;
        this.mDisableIntervalMomentum = false;
        this.mSnapInterval = 0;
        this.mDecelerationRate = 0.985f;
        this.mSnapToStart = true;
        this.mSnapToEnd = true;
        this.mPagedArrowScrolling = false;
        this.pendingContentOffsetX = -1;
        this.pendingContentOffsetY = -1;
        this.mFabricViewStateManager = new FabricViewStateManager();
        this.mFinalAnimatedPositionScrollX = 0;
        this.mFinalAnimatedPositionScrollY = 0;
        this.mLastStateUpdateScrollX = -1;
        this.mLastStateUpdateScrollY = -1;
        this.mTempRect = new Rect();
        this.mReactBackgroundManager = new ReactViewBackgroundManager(this);
        this.mFpsListener = fpsListener;
        ViewCompat.setAccessibilityDelegate(this, new AccessibilityDelegateCompat() { // from class: com.facebook.react.views.scroll.ReactHorizontalScrollView.1
            @Override // androidx.core.view.AccessibilityDelegateCompat
            public void onInitializeAccessibilityEvent(View host, AccessibilityEvent event) {
                super.onInitializeAccessibilityEvent(host, event);
                event.setScrollable(ReactHorizontalScrollView.this.mScrollEnabled);
            }

            @Override // androidx.core.view.AccessibilityDelegateCompat
            public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfoCompat info) {
                super.onInitializeAccessibilityNodeInfo(host, info);
                info.setScrollable(ReactHorizontalScrollView.this.mScrollEnabled);
            }
        });
        this.mScroller = getOverScrollerFromParent();
        this.mLayoutDirection = I18nUtil.getInstance().isRTL(context) ? 1 : 0;
    }

    private OverScroller getOverScrollerFromParent() {
        if (!sTriedToGetScrollerField) {
            sTriedToGetScrollerField = true;
            try {
                Field declaredField = HorizontalScrollView.class.getDeclaredField("mScroller");
                sScrollerField = declaredField;
                declaredField.setAccessible(true);
            } catch (NoSuchFieldException unused) {
                FLog.w(TAG, "Failed to get mScroller field for HorizontalScrollView! This app will exhibit the bounce-back scrolling bug :(");
            }
        }
        Field field = sScrollerField;
        OverScroller overScroller = null;
        if (field != null) {
            try {
                Object obj = field.get(this);
                if (obj instanceof OverScroller) {
                    overScroller = (OverScroller) obj;
                } else {
                    FLog.w(TAG, "Failed to cast mScroller field in HorizontalScrollView (probably due to OEM changes to AOSP)! This app will exhibit the bounce-back scrolling bug :(");
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to get mScroller from HorizontalScrollView!", e);
            }
        }
        return overScroller;
    }

    public void setScrollPerfTag(String scrollPerfTag) {
        this.mScrollPerfTag = scrollPerfTag;
    }

    @Override // com.facebook.react.uimanager.ReactClippingViewGroup
    public void setRemoveClippedSubviews(boolean removeClippedSubviews) {
        if (removeClippedSubviews && this.mClippingRect == null) {
            this.mClippingRect = new Rect();
        }
        this.mRemoveClippedSubviews = removeClippedSubviews;
        updateClippingRect();
    }

    @Override // com.facebook.react.uimanager.ReactClippingViewGroup
    public boolean getRemoveClippedSubviews() {
        return this.mRemoveClippedSubviews;
    }

    public void setDisableIntervalMomentum(boolean disableIntervalMomentum) {
        this.mDisableIntervalMomentum = disableIntervalMomentum;
    }

    public void setSendMomentumEvents(boolean sendMomentumEvents) {
        this.mSendMomentumEvents = sendMomentumEvents;
    }

    public void setScrollEnabled(boolean scrollEnabled) {
        this.mScrollEnabled = scrollEnabled;
    }

    public void setPagingEnabled(boolean pagingEnabled) {
        this.mPagingEnabled = pagingEnabled;
    }

    public void setDecelerationRate(float decelerationRate) {
        this.mDecelerationRate = decelerationRate;
        OverScroller overScroller = this.mScroller;
        if (overScroller != null) {
            overScroller.setFriction(1.0f - decelerationRate);
        }
    }

    public void setSnapInterval(int snapInterval) {
        this.mSnapInterval = snapInterval;
    }

    public void setSnapOffsets(List<Integer> snapOffsets) {
        this.mSnapOffsets = snapOffsets;
    }

    public void setSnapToStart(boolean snapToStart) {
        this.mSnapToStart = snapToStart;
    }

    public void setSnapToEnd(boolean snapToEnd) {
        this.mSnapToEnd = snapToEnd;
    }

    public void flashScrollIndicators() {
        awakenScrollBars();
    }

    public void setOverflow(String overflow) {
        this.mOverflow = overflow;
        invalidate();
    }

    @Override // com.facebook.react.uimanager.ReactOverflowView
    public String getOverflow() {
        return this.mOverflow;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (DEBUG_MODE) {
            FLog.i(TAG, "onDraw[%d]", Integer.valueOf(getId()));
        }
        getDrawingRect(this.mRect);
        String str = this.mOverflow;
        str.hashCode();
        if (!str.equals(ViewProps.VISIBLE)) {
            canvas.clipRect(this.mRect);
        }
        super.onDraw(canvas);
    }

    @Override // android.widget.HorizontalScrollView, android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        OverScroller overScroller;
        MeasureSpecAssertions.assertExplicitMeasureSpec(widthMeasureSpec, heightMeasureSpec);
        int size = View.MeasureSpec.getSize(widthMeasureSpec);
        int size2 = View.MeasureSpec.getSize(heightMeasureSpec);
        if (DEBUG_MODE) {
            FLog.i(TAG, "onMeasure[%d] measured width: %d measured height: %d", Integer.valueOf(getId()), Integer.valueOf(size), Integer.valueOf(size2));
        }
        boolean z = getMeasuredHeight() != size2;
        setMeasuredDimension(size, size2);
        if (!z || (overScroller = this.mScroller) == null) {
            return;
        }
        this.mScrollXAfterMeasure = overScroller.getCurrX();
    }

    @Override // android.widget.HorizontalScrollView, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        OverScroller overScroller;
        if (DEBUG_MODE) {
            FLog.i(TAG, "onLayout[%d] l %d t %d r %d b %d", Integer.valueOf(getId()), Integer.valueOf(l), Integer.valueOf(t), Integer.valueOf(r), Integer.valueOf(b));
        }
        int i = this.mScrollXAfterMeasure;
        if (i != NO_SCROLL_POSITION && (overScroller = this.mScroller) != null && i != overScroller.getFinalX() && !this.mScroller.isFinished()) {
            if (DEBUG_MODE) {
                FLog.i(TAG, "onLayout[%d] scroll hack enabled: reset to previous scrollX position of %d", Integer.valueOf(getId()), Integer.valueOf(this.mScrollXAfterMeasure));
            }
            OverScroller overScroller2 = this.mScroller;
            overScroller2.startScroll(this.mScrollXAfterMeasure, overScroller2.getFinalY(), 0, 0);
            this.mScroller.forceFinished(true);
            this.mScrollXAfterMeasure = NO_SCROLL_POSITION;
        }
        int i2 = this.pendingContentOffsetX;
        if (i2 == -1) {
            i2 = getScrollX();
        }
        int i3 = this.pendingContentOffsetY;
        if (i3 == -1) {
            i3 = getScrollY();
        }
        scrollTo(i2, i3);
        ReactScrollViewHelper.emitLayoutEvent(this);
    }

    @Override // android.widget.HorizontalScrollView, android.view.ViewGroup, android.view.ViewParent
    public void requestChildFocus(View child, View focused) {
        if (focused != null && !this.mPagingEnabled) {
            scrollToChild(focused);
        }
        super.requestChildFocus(child, focused);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        if (this.mPagingEnabled && !this.mPagedArrowScrolling) {
            ArrayList arrayList = new ArrayList();
            super.addFocusables(arrayList, direction, focusableMode);
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                View view = (View) it.next();
                if (isScrolledInView(view) || isPartiallyScrolledInView(view) || view.isFocused()) {
                    views.add(view);
                }
            }
            return;
        }
        super.addFocusables(views, direction, focusableMode);
    }

    private int getScrollDelta(View descendent) {
        descendent.getDrawingRect(this.mTempRect);
        offsetDescendantRectToMyCoords(descendent, this.mTempRect);
        return computeScrollDeltaToGetChildRectOnScreen(this.mTempRect);
    }

    private boolean isScrolledInView(View descendent) {
        return getScrollDelta(descendent) == 0;
    }

    private boolean isPartiallyScrolledInView(View descendent) {
        int scrollDelta = getScrollDelta(descendent);
        descendent.getDrawingRect(this.mTempRect);
        return scrollDelta != 0 && Math.abs(scrollDelta) < this.mTempRect.width();
    }

    private boolean isMostlyScrolledInView(View descendent) {
        int scrollDelta = getScrollDelta(descendent);
        descendent.getDrawingRect(this.mTempRect);
        return scrollDelta != 0 && Math.abs(scrollDelta) < this.mTempRect.width() / 2;
    }

    private void scrollToChild(View child) {
        int scrollDelta = getScrollDelta(child);
        if (scrollDelta != 0) {
            scrollBy(scrollDelta, 0);
        }
    }

    @Override // android.view.View
    protected void onScrollChanged(int x, int y, int oldX, int oldY) {
        if (DEBUG_MODE) {
            FLog.i(TAG, "onScrollChanged[%d] x %d y %d oldx %d oldy %d", Integer.valueOf(getId()), Integer.valueOf(x), Integer.valueOf(y), Integer.valueOf(oldX), Integer.valueOf(oldY));
        }
        super.onScrollChanged(x, y, oldX, oldY);
        this.mActivelyScrolling = true;
        if (this.mOnScrollDispatchHelper.onScrollChanged(x, y)) {
            if (this.mRemoveClippedSubviews) {
                updateClippingRect();
            }
            updateStateOnScroll();
            ReactScrollViewHelper.emitScrollEvent(this, this.mOnScrollDispatchHelper.getXFlingVelocity(), this.mOnScrollDispatchHelper.getYFlingVelocity());
        }
    }

    @Override // android.widget.HorizontalScrollView, android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!this.mScrollEnabled) {
            return false;
        }
        try {
            if (super.onInterceptTouchEvent(ev)) {
                NativeGestureUtil.notifyNativeGestureStarted(this, ev);
                ReactScrollViewHelper.emitScrollBeginDragEvent(this);
                this.mDragging = true;
                enableFpsListener();
                return true;
            }
        } catch (IllegalArgumentException e) {
            FLog.w(ReactConstants.TAG, "Error intercepting touch event.", e);
        }
        return false;
    }

    @Override // android.widget.HorizontalScrollView
    public boolean pageScroll(int direction) {
        boolean pageScroll = super.pageScroll(direction);
        if (this.mPagingEnabled && pageScroll) {
            handlePostTouchScrolling(0, 0);
        }
        return pageScroll;
    }

    @Override // android.widget.HorizontalScrollView
    public boolean arrowScroll(int direction) {
        if (this.mPagingEnabled) {
            boolean z = true;
            this.mPagedArrowScrolling = true;
            if (getChildCount() > 0) {
                View findNextFocus = FocusFinder.getInstance().findNextFocus(this, findFocus(), direction);
                View contentView = getContentView();
                if (contentView != null && findNextFocus != null && findNextFocus.getParent() == contentView) {
                    if (!isScrolledInView(findNextFocus) && !isMostlyScrolledInView(findNextFocus)) {
                        smoothScrollToNextPage(direction);
                    }
                    findNextFocus.requestFocus();
                } else {
                    smoothScrollToNextPage(direction);
                }
            } else {
                z = false;
            }
            this.mPagedArrowScrolling = false;
            return z;
        }
        return super.arrowScroll(direction);
    }

    @Override // android.widget.HorizontalScrollView, android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        if (!this.mScrollEnabled) {
            return false;
        }
        this.mVelocityHelper.calculateVelocity(ev);
        if ((ev.getAction() & 255) == 1 && this.mDragging) {
            updateStateOnScroll();
            float xVelocity = this.mVelocityHelper.getXVelocity();
            float yVelocity = this.mVelocityHelper.getYVelocity();
            ReactScrollViewHelper.emitScrollEndDragEvent(this, xVelocity, yVelocity);
            this.mDragging = false;
            handlePostTouchScrolling(Math.round(xVelocity), Math.round(yVelocity));
        }
        return super.onTouchEvent(ev);
    }

    @Override // android.widget.HorizontalScrollView
    public boolean executeKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (this.mScrollEnabled || !(keyCode == 21 || keyCode == 22)) {
            return super.executeKeyEvent(event);
        }
        return false;
    }

    @Override // android.widget.HorizontalScrollView
    public void fling(int velocityX) {
        if (DEBUG_MODE) {
            FLog.i(TAG, "fling[%d] velocityX %d", Integer.valueOf(getId()), Integer.valueOf(velocityX));
        }
        int abs = (int) (Math.abs(velocityX) * Math.signum(this.mOnScrollDispatchHelper.getXFlingVelocity()));
        if (this.mPagingEnabled) {
            flingAndSnap(abs);
        } else if (this.mScroller != null) {
            this.mScroller.fling(getScrollX(), getScrollY(), abs, 0, 0, Integer.MAX_VALUE, 0, 0, ((getWidth() - ViewCompat.getPaddingStart(this)) - ViewCompat.getPaddingEnd(this)) / 2, 0);
            ViewCompat.postInvalidateOnAnimation(this);
        } else {
            super.fling(abs);
        }
        handlePostTouchScrolling(abs, 0);
    }

    @Override // android.widget.HorizontalScrollView, android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (this.mRemoveClippedSubviews) {
            updateClippingRect();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mRemoveClippedSubviews) {
            updateClippingRect();
        }
    }

    @Override // com.facebook.react.uimanager.ReactClippingViewGroup
    public void updateClippingRect() {
        if (!this.mRemoveClippedSubviews) {
            return;
        }
        Assertions.assertNotNull(this.mClippingRect);
        ReactClippingViewGroupHelper.calculateClippingRect(this, this.mClippingRect);
        View contentView = getContentView();
        if (!(contentView instanceof ReactClippingViewGroup)) {
            return;
        }
        ((ReactClippingViewGroup) contentView).updateClippingRect();
    }

    @Override // com.facebook.react.uimanager.ReactClippingViewGroup
    public void getClippingRect(Rect outClippingRect) {
        outClippingRect.set((Rect) Assertions.assertNotNull(this.mClippingRect));
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public boolean getChildVisibleRect(View child, Rect r, Point offset) {
        return super.getChildVisibleRect(child, r, offset);
    }

    private int getSnapInterval() {
        int i = this.mSnapInterval;
        return i != 0 ? i : getWidth();
    }

    private View getContentView() {
        return getChildAt(0);
    }

    public void setEndFillColor(int color) {
        if (color != this.mEndFillColor) {
            this.mEndFillColor = color;
            this.mEndBackground = new ColorDrawable(this.mEndFillColor);
        }
    }

    @Override // android.widget.HorizontalScrollView, android.view.View
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        int computeHorizontalScrollRange;
        if (DEBUG_MODE) {
            FLog.i(TAG, "onOverScrolled[%d] scrollX %d scrollY %d clampedX %b clampedY %b", Integer.valueOf(getId()), Integer.valueOf(scrollX), Integer.valueOf(scrollY), Boolean.valueOf(clampedX), Boolean.valueOf(clampedY));
        }
        OverScroller overScroller = this.mScroller;
        if (overScroller != null && !overScroller.isFinished() && this.mScroller.getCurrX() != this.mScroller.getFinalX() && scrollX >= (computeHorizontalScrollRange = computeHorizontalScrollRange() - getWidth())) {
            this.mScroller.abortAnimation();
            scrollX = computeHorizontalScrollRange;
        }
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }

    private void enableFpsListener() {
        if (isScrollPerfLoggingEnabled()) {
            Assertions.assertNotNull(this.mFpsListener);
            Assertions.assertNotNull(this.mScrollPerfTag);
            this.mFpsListener.enable(this.mScrollPerfTag);
        }
    }

    public void disableFpsListener() {
        if (isScrollPerfLoggingEnabled()) {
            Assertions.assertNotNull(this.mFpsListener);
            Assertions.assertNotNull(this.mScrollPerfTag);
            this.mFpsListener.disable(this.mScrollPerfTag);
        }
    }

    private boolean isScrollPerfLoggingEnabled() {
        String str;
        return (this.mFpsListener == null || (str = this.mScrollPerfTag) == null || str.isEmpty()) ? false : true;
    }

    @Override // android.widget.HorizontalScrollView, android.view.View
    public void draw(Canvas canvas) {
        if (this.mEndFillColor != 0) {
            View contentView = getContentView();
            if (this.mEndBackground != null && contentView != null && contentView.getRight() < getWidth()) {
                this.mEndBackground.setBounds(contentView.getRight(), 0, getWidth(), getHeight());
                this.mEndBackground.draw(canvas);
            }
        }
        super.draw(canvas);
    }

    private void handlePostTouchScrolling(int velocityX, int velocityY) {
        if (DEBUG_MODE) {
            FLog.i(TAG, "handlePostTouchScrolling[%d] velocityX %d velocityY %d", Integer.valueOf(getId()), Integer.valueOf(velocityX), Integer.valueOf(velocityY));
        }
        if (this.mPostTouchRunnable != null) {
            return;
        }
        if (this.mSendMomentumEvents) {
            ReactScrollViewHelper.emitScrollMomentumBeginEvent(this, velocityX, velocityY);
        }
        this.mActivelyScrolling = false;
        Runnable runnable = new Runnable() { // from class: com.facebook.react.views.scroll.ReactHorizontalScrollView.2
            private boolean mSnappingToPage = false;
            private boolean mRunning = true;
            private int mStableFrames = 0;

            @Override // java.lang.Runnable
            public void run() {
                if (ReactHorizontalScrollView.this.mActivelyScrolling) {
                    ReactHorizontalScrollView.this.mActivelyScrolling = false;
                    this.mStableFrames = 0;
                    this.mRunning = true;
                } else {
                    ReactHorizontalScrollView.this.updateStateOnScroll();
                    int i = this.mStableFrames + 1;
                    this.mStableFrames = i;
                    this.mRunning = i < 3;
                    if (!ReactHorizontalScrollView.this.mPagingEnabled || this.mSnappingToPage) {
                        if (ReactHorizontalScrollView.this.mSendMomentumEvents) {
                            ReactScrollViewHelper.emitScrollMomentumEndEvent(ReactHorizontalScrollView.this);
                        }
                        ReactHorizontalScrollView.this.disableFpsListener();
                    } else {
                        this.mSnappingToPage = true;
                        ReactHorizontalScrollView.this.flingAndSnap(0);
                        ViewCompat.postOnAnimationDelayed(ReactHorizontalScrollView.this, this, 20L);
                    }
                }
                if (!this.mRunning) {
                    ReactHorizontalScrollView.this.mPostTouchRunnable = null;
                } else {
                    ViewCompat.postOnAnimationDelayed(ReactHorizontalScrollView.this, this, 20L);
                }
            }
        };
        this.mPostTouchRunnable = runnable;
        ViewCompat.postOnAnimationDelayed(this, runnable, 20L);
    }

    private int getPostAnimationScrollX() {
        ValueAnimator valueAnimator = this.mScrollAnimator;
        return (valueAnimator == null || !valueAnimator.isRunning()) ? getScrollX() : this.mFinalAnimatedPositionScrollX;
    }

    private int getPostAnimationScrollY() {
        ValueAnimator valueAnimator = this.mScrollAnimator;
        return (valueAnimator == null || !valueAnimator.isRunning()) ? getScrollY() : this.mFinalAnimatedPositionScrollY;
    }

    private int predictFinalScrollPosition(int velocityX) {
        OverScroller overScroller = new OverScroller(getContext());
        overScroller.setFriction(1.0f - this.mDecelerationRate);
        overScroller.fling(getPostAnimationScrollX(), getPostAnimationScrollY(), velocityX, 0, 0, Math.max(0, computeHorizontalScrollRange() - getWidth()), 0, 0, ((getWidth() - ViewCompat.getPaddingStart(this)) - ViewCompat.getPaddingEnd(this)) / 2, 0);
        return overScroller.getFinalX();
    }

    private void smoothScrollAndSnap(int velocity) {
        if (DEBUG_MODE) {
            FLog.i(TAG, "smoothScrollAndSnap[%d] velocity %d", Integer.valueOf(getId()), Integer.valueOf(velocity));
        }
        double snapInterval = getSnapInterval();
        double postAnimationScrollX = getPostAnimationScrollX();
        double d = postAnimationScrollX / snapInterval;
        int floor = (int) Math.floor(d);
        int ceil = (int) Math.ceil(d);
        int round = (int) Math.round(d);
        int round2 = (int) Math.round(predictFinalScrollPosition(velocity) / snapInterval);
        if (velocity > 0 && ceil == floor) {
            ceil++;
        } else if (velocity < 0 && floor == ceil) {
            floor--;
        }
        if (velocity > 0 && round < ceil && round2 > floor) {
            round = ceil;
        } else if (velocity < 0 && round > floor && round2 < ceil) {
            round = floor;
        }
        double d2 = round * snapInterval;
        if (d2 != postAnimationScrollX) {
            this.mActivelyScrolling = true;
            reactSmoothScrollTo((int) d2, getScrollY());
        }
    }

    public void flingAndSnap(int velocityX) {
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        if (DEBUG_MODE) {
            FLog.i(TAG, "smoothScrollAndSnap[%d] velocityX %d", Integer.valueOf(getId()), Integer.valueOf(velocityX));
        }
        if (getChildCount() <= 0) {
            return;
        }
        if (this.mSnapInterval == 0 && this.mSnapOffsets == null) {
            smoothScrollAndSnap(velocityX);
            return;
        }
        int max = Math.max(0, computeHorizontalScrollRange() - getWidth());
        int predictFinalScrollPosition = predictFinalScrollPosition(velocityX);
        if (this.mDisableIntervalMomentum) {
            predictFinalScrollPosition = getScrollX();
        }
        int width = (getWidth() - ViewCompat.getPaddingStart(this)) - ViewCompat.getPaddingEnd(this);
        if (this.mLayoutDirection == 1) {
            predictFinalScrollPosition = max - predictFinalScrollPosition;
            i = -velocityX;
        } else {
            i = velocityX;
        }
        List<Integer> list = this.mSnapOffsets;
        if (list != null && !list.isEmpty()) {
            i5 = this.mSnapOffsets.get(0).intValue();
            List<Integer> list2 = this.mSnapOffsets;
            i4 = list2.get(list2.size() - 1).intValue();
            i2 = max;
            i3 = 0;
            for (int i6 = 0; i6 < this.mSnapOffsets.size(); i6++) {
                int intValue = this.mSnapOffsets.get(i6).intValue();
                if (intValue <= predictFinalScrollPosition && predictFinalScrollPosition - intValue < predictFinalScrollPosition - i3) {
                    i3 = intValue;
                }
                if (intValue >= predictFinalScrollPosition && intValue - predictFinalScrollPosition < i2 - predictFinalScrollPosition) {
                    i2 = intValue;
                }
            }
        } else {
            double snapInterval = getSnapInterval();
            double d = predictFinalScrollPosition / snapInterval;
            i4 = max;
            i3 = (int) (Math.floor(d) * snapInterval);
            i2 = Math.min((int) (Math.ceil(d) * snapInterval), max);
            i5 = 0;
        }
        int i7 = predictFinalScrollPosition - i3;
        int i8 = i2 - predictFinalScrollPosition;
        int i9 = i7 < i8 ? i3 : i2;
        int scrollX = getScrollX();
        if (this.mLayoutDirection == 1) {
            scrollX = max - scrollX;
        }
        if (this.mSnapToEnd || predictFinalScrollPosition < i4) {
            if (this.mSnapToStart || predictFinalScrollPosition > i5) {
                if (i > 0) {
                    i += (int) (i8 * 10.0d);
                    predictFinalScrollPosition = i2;
                } else if (i < 0) {
                    i -= (int) (i7 * 10.0d);
                    predictFinalScrollPosition = i3;
                } else {
                    predictFinalScrollPosition = i9;
                }
            } else if (scrollX > i5) {
                predictFinalScrollPosition = i5;
            }
        } else if (scrollX < i4) {
            predictFinalScrollPosition = i4;
        }
        int min = Math.min(Math.max(0, predictFinalScrollPosition), max);
        if (this.mLayoutDirection == 1) {
            min = max - min;
            i = -i;
        }
        int i10 = min;
        OverScroller overScroller = this.mScroller;
        if (overScroller != null) {
            this.mActivelyScrolling = true;
            overScroller.fling(getScrollX(), getScrollY(), i != 0 ? i : i10 - getScrollX(), 0, i10, i10, 0, 0, (i10 == 0 || i10 == max) ? width / 2 : 0, 0);
            postInvalidateOnAnimation();
            return;
        }
        reactSmoothScrollTo(i10, getScrollY());
    }

    private void smoothScrollToNextPage(int direction) {
        if (DEBUG_MODE) {
            FLog.i(TAG, "smoothScrollToNextPage[%d] direction %d", Integer.valueOf(getId()), Integer.valueOf(direction));
        }
        int width = getWidth();
        int scrollX = getScrollX();
        int i = scrollX / width;
        if (scrollX % width != 0) {
            i++;
        }
        int i2 = direction == 17 ? i - 1 : i + 1;
        if (i2 < 0) {
            i2 = 0;
        }
        reactSmoothScrollTo(i2 * width, getScrollY());
        handlePostTouchScrolling(0, 0);
    }

    @Override // android.view.View
    public void setBackgroundColor(int color) {
        this.mReactBackgroundManager.setBackgroundColor(color);
    }

    public void setBorderWidth(int position, float width) {
        this.mReactBackgroundManager.setBorderWidth(position, width);
    }

    public void setBorderColor(int position, float color, float alpha) {
        this.mReactBackgroundManager.setBorderColor(position, color, alpha);
    }

    public void setBorderRadius(float borderRadius) {
        this.mReactBackgroundManager.setBorderRadius(borderRadius);
    }

    public void setBorderRadius(float borderRadius, int position) {
        this.mReactBackgroundManager.setBorderRadius(borderRadius, position);
    }

    public void setBorderStyle(String style) {
        this.mReactBackgroundManager.setBorderStyle(style);
    }

    public void reactSmoothScrollTo(int x, int y) {
        if (DEBUG_MODE) {
            FLog.i(TAG, "reactSmoothScrollTo[%d] x %d y %d", Integer.valueOf(getId()), Integer.valueOf(x), Integer.valueOf(y));
        }
        ValueAnimator valueAnimator = this.mScrollAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.mFinalAnimatedPositionScrollX = x;
        this.mFinalAnimatedPositionScrollY = y;
        ValueAnimator ofPropertyValuesHolder = ObjectAnimator.ofPropertyValuesHolder(PropertyValuesHolder.ofInt("scrollX", getScrollX(), x), PropertyValuesHolder.ofInt("scrollY", getScrollY(), y));
        this.mScrollAnimator = ofPropertyValuesHolder;
        ofPropertyValuesHolder.setDuration(ReactScrollViewHelper.getDefaultScrollAnimationDuration(getContext()));
        this.mScrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.facebook.react.views.scroll.ReactHorizontalScrollView.3
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator2) {
                ReactHorizontalScrollView.this.scrollTo(((Integer) valueAnimator2.getAnimatedValue("scrollX")).intValue(), ((Integer) valueAnimator2.getAnimatedValue("scrollY")).intValue());
            }
        });
        this.mScrollAnimator.addListener(new Animator.AnimatorListener() { // from class: com.facebook.react.views.scroll.ReactHorizontalScrollView.4
            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationRepeat(Animator animator) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                ReactHorizontalScrollView.this.mFinalAnimatedPositionScrollX = -1;
                ReactHorizontalScrollView.this.mFinalAnimatedPositionScrollY = -1;
                ReactHorizontalScrollView.this.mScrollAnimator = null;
                ReactHorizontalScrollView.this.updateStateOnScroll();
            }
        });
        this.mScrollAnimator.start();
        updateStateOnScroll(x, y);
        setPendingContentOffsets(x, y);
    }

    @Override // android.widget.HorizontalScrollView, android.view.View
    public void scrollTo(int x, int y) {
        if (DEBUG_MODE) {
            FLog.i(TAG, "scrollTo[%d] x %d y %d", Integer.valueOf(getId()), Integer.valueOf(x), Integer.valueOf(y));
        }
        super.scrollTo(x, y);
        updateStateOnScroll(x, y);
        setPendingContentOffsets(x, y);
    }

    private void setPendingContentOffsets(int x, int y) {
        if (DEBUG_MODE) {
            FLog.i(TAG, "setPendingContentOffsets[%d] x %d y %d", Integer.valueOf(getId()), Integer.valueOf(x), Integer.valueOf(y));
        }
        View contentView = getContentView();
        if (contentView != null && contentView.getWidth() != 0 && contentView.getHeight() != 0) {
            this.pendingContentOffsetX = -1;
            this.pendingContentOffsetY = -1;
            return;
        }
        this.pendingContentOffsetX = x;
        this.pendingContentOffsetY = y;
    }

    private void updateStateOnScroll(final int scrollX, final int scrollY) {
        final int i;
        if (DEBUG_MODE) {
            FLog.i(TAG, "updateStateOnScroll[%d] scrollX %d scrollY %d", Integer.valueOf(getId()), Integer.valueOf(scrollX), Integer.valueOf(scrollY));
        }
        if (scrollX == this.mLastStateUpdateScrollX && scrollY == this.mLastStateUpdateScrollY) {
            return;
        }
        this.mLastStateUpdateScrollX = scrollX;
        this.mLastStateUpdateScrollY = scrollY;
        if (this.mLayoutDirection == 1) {
            View contentView = getContentView();
            i = -(((contentView != null ? contentView.getWidth() : 0) - scrollX) - getWidth());
        } else {
            i = scrollX;
        }
        if (DEBUG_MODE) {
            FLog.i(TAG, "updateStateOnScroll[%d] scrollX %d scrollY %d fabricScrollX", Integer.valueOf(getId()), Integer.valueOf(scrollX), Integer.valueOf(scrollY), Integer.valueOf(i));
        }
        this.mFabricViewStateManager.setState(new FabricViewStateManager.StateUpdateCallback() { // from class: com.facebook.react.views.scroll.ReactHorizontalScrollView.5
            @Override // com.facebook.react.uimanager.FabricViewStateManager.StateUpdateCallback
            public WritableMap getStateUpdate() {
                WritableNativeMap writableNativeMap = new WritableNativeMap();
                writableNativeMap.putDouble(ReactHorizontalScrollView.CONTENT_OFFSET_LEFT, PixelUtil.toDIPFromPixel(i));
                writableNativeMap.putDouble(ReactHorizontalScrollView.CONTENT_OFFSET_TOP, PixelUtil.toDIPFromPixel(scrollY));
                writableNativeMap.putDouble(ReactHorizontalScrollView.SCROLL_AWAY_PADDING_TOP, 0.0d);
                return writableNativeMap;
            }
        });
    }

    public void updateStateOnScroll() {
        updateStateOnScroll(getScrollX(), getScrollY());
    }

    @Override // com.facebook.react.uimanager.FabricViewStateManager.HasFabricViewStateManager
    public FabricViewStateManager getFabricViewStateManager() {
        return this.mFabricViewStateManager;
    }
}
