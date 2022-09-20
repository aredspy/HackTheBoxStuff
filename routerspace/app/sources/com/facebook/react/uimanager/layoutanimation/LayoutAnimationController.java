package com.facebook.react.uimanager.layoutanimation;

import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.UiThreadUtil;
/* loaded from: classes.dex */
public class LayoutAnimationController {
    private static Handler sCompletionHandler;
    private Runnable mCompletionRunnable;
    private boolean mShouldAnimateLayout;
    private final AbstractLayoutAnimation mLayoutCreateAnimation = new LayoutCreateAnimation();
    private final AbstractLayoutAnimation mLayoutUpdateAnimation = new LayoutUpdateAnimation();
    private final AbstractLayoutAnimation mLayoutDeleteAnimation = new LayoutDeleteAnimation();
    private final SparseArray<LayoutHandlingAnimation> mLayoutHandlers = new SparseArray<>(0);
    private long mMaxAnimationDuration = -1;

    public void initializeFromConfig(final ReadableMap config, final Callback completionCallback) {
        if (config == null) {
            reset();
            return;
        }
        int i = 0;
        this.mShouldAnimateLayout = false;
        if (config.hasKey("duration")) {
            i = config.getInt("duration");
        }
        if (config.hasKey(LayoutAnimationType.toString(LayoutAnimationType.CREATE))) {
            this.mLayoutCreateAnimation.initializeFromConfig(config.getMap(LayoutAnimationType.toString(LayoutAnimationType.CREATE)), i);
            this.mShouldAnimateLayout = true;
        }
        if (config.hasKey(LayoutAnimationType.toString(LayoutAnimationType.UPDATE))) {
            this.mLayoutUpdateAnimation.initializeFromConfig(config.getMap(LayoutAnimationType.toString(LayoutAnimationType.UPDATE)), i);
            this.mShouldAnimateLayout = true;
        }
        if (config.hasKey(LayoutAnimationType.toString(LayoutAnimationType.DELETE))) {
            this.mLayoutDeleteAnimation.initializeFromConfig(config.getMap(LayoutAnimationType.toString(LayoutAnimationType.DELETE)), i);
            this.mShouldAnimateLayout = true;
        }
        if (!this.mShouldAnimateLayout || completionCallback == null) {
            return;
        }
        this.mCompletionRunnable = new Runnable() { // from class: com.facebook.react.uimanager.layoutanimation.LayoutAnimationController.1
            @Override // java.lang.Runnable
            public void run() {
                completionCallback.invoke(Boolean.TRUE);
            }
        };
    }

    public void reset() {
        this.mLayoutCreateAnimation.reset();
        this.mLayoutUpdateAnimation.reset();
        this.mLayoutDeleteAnimation.reset();
        this.mCompletionRunnable = null;
        this.mShouldAnimateLayout = false;
        this.mMaxAnimationDuration = -1L;
    }

    public boolean shouldAnimateLayout(View viewToAnimate) {
        if (viewToAnimate == null) {
            return false;
        }
        return (this.mShouldAnimateLayout && viewToAnimate.getParent() != null) || this.mLayoutHandlers.get(viewToAnimate.getId()) != null;
    }

    public void applyLayoutUpdate(View view, int x, int y, int width, int height) {
        UiThreadUtil.assertOnUiThread();
        final int id = view.getId();
        LayoutHandlingAnimation layoutHandlingAnimation = this.mLayoutHandlers.get(id);
        if (layoutHandlingAnimation != null) {
            layoutHandlingAnimation.onLayoutUpdate(x, y, width, height);
            return;
        }
        Animation createAnimation = ((view.getWidth() == 0 || view.getHeight() == 0) ? this.mLayoutCreateAnimation : this.mLayoutUpdateAnimation).createAnimation(view, x, y, width, height);
        if (createAnimation instanceof LayoutHandlingAnimation) {
            createAnimation.setAnimationListener(new Animation.AnimationListener() { // from class: com.facebook.react.uimanager.layoutanimation.LayoutAnimationController.2
                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationRepeat(Animation animation) {
                }

                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationStart(Animation animation) {
                    LayoutAnimationController.this.mLayoutHandlers.put(id, (LayoutHandlingAnimation) animation);
                }

                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationEnd(Animation animation) {
                    LayoutAnimationController.this.mLayoutHandlers.remove(id);
                }
            });
        } else {
            view.layout(x, y, width + x, height + y);
        }
        if (createAnimation == null) {
            return;
        }
        long duration = createAnimation.getDuration();
        if (duration > this.mMaxAnimationDuration) {
            this.mMaxAnimationDuration = duration;
            scheduleCompletionCallback(duration);
        }
        view.startAnimation(createAnimation);
    }

    public void deleteView(final View view, final LayoutAnimationListener listener) {
        UiThreadUtil.assertOnUiThread();
        Animation createAnimation = this.mLayoutDeleteAnimation.createAnimation(view, view.getLeft(), view.getTop(), view.getWidth(), view.getHeight());
        if (createAnimation != null) {
            disableUserInteractions(view);
            createAnimation.setAnimationListener(new Animation.AnimationListener() { // from class: com.facebook.react.uimanager.layoutanimation.LayoutAnimationController.3
                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationRepeat(Animation anim) {
                }

                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationStart(Animation anim) {
                }

                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationEnd(Animation anim) {
                    listener.onAnimationEnd();
                }
            });
            long duration = createAnimation.getDuration();
            if (duration > this.mMaxAnimationDuration) {
                scheduleCompletionCallback(duration);
                this.mMaxAnimationDuration = duration;
            }
            view.startAnimation(createAnimation);
            return;
        }
        listener.onAnimationEnd();
    }

    private void disableUserInteractions(View view) {
        view.setClickable(false);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                disableUserInteractions(viewGroup.getChildAt(i));
            }
        }
    }

    private void scheduleCompletionCallback(long delayMillis) {
        if (sCompletionHandler == null) {
            sCompletionHandler = new Handler(Looper.getMainLooper());
        }
        Runnable runnable = this.mCompletionRunnable;
        if (runnable != null) {
            sCompletionHandler.removeCallbacks(runnable);
            sCompletionHandler.postDelayed(this.mCompletionRunnable, delayMillis);
        }
    }
}
