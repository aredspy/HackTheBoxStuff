package androidx.viewpager2.widget;

import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import androidx.recyclerview.widget.RecyclerView;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class FakeDrag {
    private int mActualDraggedDistance;
    private long mFakeDragBeginTime;
    private int mMaximumVelocity;
    private final RecyclerView mRecyclerView;
    private float mRequestedDragDistance;
    private final ScrollEventAdapter mScrollEventAdapter;
    private VelocityTracker mVelocityTracker;
    private final ViewPager2 mViewPager;

    public FakeDrag(ViewPager2 viewPager2, ScrollEventAdapter scrollEventAdapter, RecyclerView recyclerView) {
        this.mViewPager = viewPager2;
        this.mScrollEventAdapter = scrollEventAdapter;
        this.mRecyclerView = recyclerView;
    }

    public boolean isFakeDragging() {
        return this.mScrollEventAdapter.isFakeDragging();
    }

    public boolean beginFakeDrag() {
        if (this.mScrollEventAdapter.isDragging()) {
            return false;
        }
        this.mActualDraggedDistance = 0;
        this.mRequestedDragDistance = 0;
        this.mFakeDragBeginTime = SystemClock.uptimeMillis();
        beginFakeVelocityTracker();
        this.mScrollEventAdapter.notifyBeginFakeDrag();
        if (!this.mScrollEventAdapter.isIdle()) {
            this.mRecyclerView.stopScroll();
        }
        addFakeMotionEvent(this.mFakeDragBeginTime, 0, 0.0f, 0.0f);
        return true;
    }

    public boolean fakeDragBy(float f) {
        int i = 0;
        if (!this.mScrollEventAdapter.isFakeDragging()) {
            return false;
        }
        float f2 = this.mRequestedDragDistance - f;
        this.mRequestedDragDistance = f2;
        int round = Math.round(f2 - this.mActualDraggedDistance);
        this.mActualDraggedDistance += round;
        long uptimeMillis = SystemClock.uptimeMillis();
        boolean z = this.mViewPager.getOrientation() == 0;
        int i2 = z ? round : 0;
        if (!z) {
            i = round;
        }
        float f3 = z ? this.mRequestedDragDistance : 0.0f;
        float f4 = z ? 0.0f : this.mRequestedDragDistance;
        this.mRecyclerView.scrollBy(i2, i);
        addFakeMotionEvent(uptimeMillis, 2, f3, f4);
        return true;
    }

    public boolean endFakeDrag() {
        if (!this.mScrollEventAdapter.isFakeDragging()) {
            return false;
        }
        this.mScrollEventAdapter.notifyEndFakeDrag();
        VelocityTracker velocityTracker = this.mVelocityTracker;
        velocityTracker.computeCurrentVelocity(1000, this.mMaximumVelocity);
        if (this.mRecyclerView.fling((int) velocityTracker.getXVelocity(), (int) velocityTracker.getYVelocity())) {
            return true;
        }
        this.mViewPager.snapToPage();
        return true;
    }

    private void beginFakeVelocityTracker() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
            this.mMaximumVelocity = ViewConfiguration.get(this.mViewPager.getContext()).getScaledMaximumFlingVelocity();
            return;
        }
        velocityTracker.clear();
    }

    private void addFakeMotionEvent(long j, int i, float f, float f2) {
        MotionEvent obtain = MotionEvent.obtain(this.mFakeDragBeginTime, j, i, f, f2, 0);
        this.mVelocityTracker.addMovement(obtain);
        obtain.recycle();
    }
}
