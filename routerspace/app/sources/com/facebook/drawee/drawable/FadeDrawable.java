package com.facebook.drawee.drawable;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import com.facebook.common.internal.Preconditions;
import java.util.Arrays;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class FadeDrawable extends ArrayDrawable {
    public static final int TRANSITION_NONE = 2;
    public static final int TRANSITION_RUNNING = 1;
    public static final int TRANSITION_STARTING = 0;
    private final int mActualImageLayer;
    int mAlpha;
    int[] mAlphas;
    private final int mDefaultLayerAlpha;
    private final boolean mDefaultLayerIsOn;
    int mDurationMs;
    private boolean mIsFadingActualImage;
    boolean[] mIsLayerOn;
    private final Drawable[] mLayers;
    private boolean mMutateDrawables;
    @Nullable
    private OnFadeListener mOnFadeListener;
    private boolean mOnFadeListenerShowImmediately;
    int mPreventInvalidateCount;
    int[] mStartAlphas;
    long mStartTimeMs;
    int mTransitionState;

    /* loaded from: classes.dex */
    public interface OnFadeListener {
        void onFadeFinished();

        void onFadeStarted();

        void onShownImmediately();
    }

    public FadeDrawable(Drawable[] layers) {
        this(layers, false, -1);
    }

    public FadeDrawable(Drawable[] layers, boolean allLayersVisible, int actualImageLayer) {
        super(layers);
        boolean z = true;
        this.mMutateDrawables = true;
        int i = 0;
        Preconditions.checkState(layers.length < 1 ? false : z, "At least one layer required!");
        this.mLayers = layers;
        this.mStartAlphas = new int[layers.length];
        this.mAlphas = new int[layers.length];
        this.mAlpha = 255;
        this.mIsLayerOn = new boolean[layers.length];
        this.mPreventInvalidateCount = 0;
        this.mDefaultLayerIsOn = allLayersVisible;
        this.mDefaultLayerAlpha = allLayersVisible ? 255 : i;
        this.mActualImageLayer = actualImageLayer;
        resetInternal();
    }

    @Override // android.graphics.drawable.Drawable
    public void invalidateSelf() {
        if (this.mPreventInvalidateCount == 0) {
            super.invalidateSelf();
        }
    }

    public void beginBatchMode() {
        this.mPreventInvalidateCount++;
    }

    public void endBatchMode() {
        this.mPreventInvalidateCount--;
        invalidateSelf();
    }

    public void setTransitionDuration(int durationMs) {
        this.mDurationMs = durationMs;
        if (this.mTransitionState == 1) {
            this.mTransitionState = 0;
        }
    }

    public int getTransitionDuration() {
        return this.mDurationMs;
    }

    private void resetInternal() {
        this.mTransitionState = 2;
        Arrays.fill(this.mStartAlphas, this.mDefaultLayerAlpha);
        this.mStartAlphas[0] = 255;
        Arrays.fill(this.mAlphas, this.mDefaultLayerAlpha);
        this.mAlphas[0] = 255;
        Arrays.fill(this.mIsLayerOn, this.mDefaultLayerIsOn);
        this.mIsLayerOn[0] = true;
    }

    public void reset() {
        resetInternal();
        invalidateSelf();
    }

    public void fadeInLayer(int index) {
        this.mTransitionState = 0;
        this.mIsLayerOn[index] = true;
        invalidateSelf();
    }

    public void fadeOutLayer(int index) {
        this.mTransitionState = 0;
        this.mIsLayerOn[index] = false;
        invalidateSelf();
    }

    public void fadeInAllLayers() {
        this.mTransitionState = 0;
        Arrays.fill(this.mIsLayerOn, true);
        invalidateSelf();
    }

    public void fadeOutAllLayers() {
        this.mTransitionState = 0;
        Arrays.fill(this.mIsLayerOn, false);
        invalidateSelf();
    }

    public void fadeToLayer(int index) {
        this.mTransitionState = 0;
        Arrays.fill(this.mIsLayerOn, false);
        this.mIsLayerOn[index] = true;
        invalidateSelf();
    }

    public void fadeUpToLayer(int index) {
        this.mTransitionState = 0;
        int i = index + 1;
        Arrays.fill(this.mIsLayerOn, 0, i, true);
        Arrays.fill(this.mIsLayerOn, i, this.mLayers.length, false);
        invalidateSelf();
    }

    public void showLayerImmediately(int index) {
        this.mIsLayerOn[index] = true;
        this.mAlphas[index] = 255;
        if (index == this.mActualImageLayer) {
            this.mOnFadeListenerShowImmediately = true;
        }
        invalidateSelf();
    }

    public void hideLayerImmediately(int index) {
        this.mIsLayerOn[index] = false;
        this.mAlphas[index] = 0;
        invalidateSelf();
    }

    public void finishTransitionImmediately() {
        this.mTransitionState = 2;
        for (int i = 0; i < this.mLayers.length; i++) {
            this.mAlphas[i] = this.mIsLayerOn[i] ? 255 : 0;
        }
        invalidateSelf();
    }

    private boolean updateAlphas(float ratio) {
        boolean z = true;
        for (int i = 0; i < this.mLayers.length; i++) {
            boolean[] zArr = this.mIsLayerOn;
            int i2 = zArr[i] ? 1 : -1;
            int[] iArr = this.mAlphas;
            iArr[i] = (int) (this.mStartAlphas[i] + (i2 * 255 * ratio));
            if (iArr[i] < 0) {
                iArr[i] = 0;
            }
            if (iArr[i] > 255) {
                iArr[i] = 255;
            }
            if (zArr[i] && iArr[i] < 255) {
                z = false;
            }
            if (!zArr[i] && iArr[i] > 0) {
                z = false;
            }
        }
        return z;
    }

    /* JADX WARN: Removed duplicated region for block: B:24:0x0056 A[LOOP:0: B:22:0x0051->B:24:0x0056, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:26:0x0074  */
    /* JADX WARN: Removed duplicated region for block: B:27:0x007b  */
    /* JADX WARN: Removed duplicated region for block: B:29:0x0072 A[EDGE_INSN: B:29:0x0072->B:25:0x0072 ?: BREAK  , SYNTHETIC] */
    @Override // com.facebook.drawee.drawable.ArrayDrawable, android.graphics.drawable.Drawable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void draw(android.graphics.Canvas r9) {
        /*
            r8 = this;
            int r0 = r8.mTransitionState
            r1 = 2
            r2 = 0
            r3 = 1
            if (r0 == 0) goto L2b
            if (r0 == r3) goto La
            goto L51
        La:
            int r0 = r8.mDurationMs
            if (r0 <= 0) goto L10
            r0 = 1
            goto L11
        L10:
            r0 = 0
        L11:
            com.facebook.common.internal.Preconditions.checkState(r0)
            long r4 = r8.getCurrentTimeMs()
            long r6 = r8.mStartTimeMs
            long r4 = r4 - r6
            float r0 = (float) r4
            int r4 = r8.mDurationMs
            float r4 = (float) r4
            float r0 = r0 / r4
            boolean r0 = r8.updateAlphas(r0)
            if (r0 == 0) goto L27
            goto L28
        L27:
            r1 = 1
        L28:
            r8.mTransitionState = r1
            goto L50
        L2b:
            int[] r0 = r8.mAlphas
            int[] r4 = r8.mStartAlphas
            android.graphics.drawable.Drawable[] r5 = r8.mLayers
            int r5 = r5.length
            java.lang.System.arraycopy(r0, r2, r4, r2, r5)
            long r4 = r8.getCurrentTimeMs()
            r8.mStartTimeMs = r4
            int r0 = r8.mDurationMs
            if (r0 != 0) goto L42
            r0 = 1065353216(0x3f800000, float:1.0)
            goto L43
        L42:
            r0 = 0
        L43:
            boolean r0 = r8.updateAlphas(r0)
            r8.maybeOnFadeStarted()
            if (r0 == 0) goto L4d
            goto L4e
        L4d:
            r1 = 1
        L4e:
            r8.mTransitionState = r1
        L50:
            r3 = r0
        L51:
            android.graphics.drawable.Drawable[] r0 = r8.mLayers
            int r1 = r0.length
            if (r2 >= r1) goto L72
            r0 = r0[r2]
            int[] r1 = r8.mAlphas
            r1 = r1[r2]
            int r4 = r8.mAlpha
            int r1 = r1 * r4
            double r4 = (double) r1
            r6 = 4643176031446892544(0x406fe00000000000, double:255.0)
            double r4 = r4 / r6
            double r4 = java.lang.Math.ceil(r4)
            int r1 = (int) r4
            r8.drawDrawableWithAlpha(r9, r0, r1)
            int r2 = r2 + 1
            goto L51
        L72:
            if (r3 == 0) goto L7b
            r8.maybeOnFadeFinished()
            r8.maybeOnImageShownImmediately()
            goto L7e
        L7b:
            r8.invalidateSelf()
        L7e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.facebook.drawee.drawable.FadeDrawable.draw(android.graphics.Canvas):void");
    }

    private void maybeOnImageShownImmediately() {
        if (this.mOnFadeListenerShowImmediately && this.mTransitionState == 2 && this.mIsLayerOn[this.mActualImageLayer]) {
            OnFadeListener onFadeListener = this.mOnFadeListener;
            if (onFadeListener != null) {
                onFadeListener.onShownImmediately();
            }
            this.mOnFadeListenerShowImmediately = false;
        }
    }

    private void drawDrawableWithAlpha(Canvas canvas, Drawable drawable, int alpha) {
        if (drawable == null || alpha <= 0) {
            return;
        }
        this.mPreventInvalidateCount++;
        if (this.mMutateDrawables) {
            drawable.mutate();
        }
        drawable.setAlpha(alpha);
        this.mPreventInvalidateCount--;
        drawable.draw(canvas);
    }

    @Override // com.facebook.drawee.drawable.ArrayDrawable, android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        if (this.mAlpha != alpha) {
            this.mAlpha = alpha;
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getAlpha() {
        return this.mAlpha;
    }

    protected long getCurrentTimeMs() {
        return SystemClock.uptimeMillis();
    }

    public int getTransitionState() {
        return this.mTransitionState;
    }

    public boolean isLayerOn(int index) {
        return this.mIsLayerOn[index];
    }

    public boolean isDefaultLayerIsOn() {
        return this.mDefaultLayerIsOn;
    }

    public void setOnFadeListener(@Nullable OnFadeListener onFadeListener) {
        this.mOnFadeListener = onFadeListener;
    }

    public void setMutateDrawables(boolean mutateDrawables) {
        this.mMutateDrawables = mutateDrawables;
    }

    private void maybeOnFadeStarted() {
        int i;
        if (!this.mIsFadingActualImage && (i = this.mActualImageLayer) >= 0) {
            boolean[] zArr = this.mIsLayerOn;
            if (i >= zArr.length || !zArr[i]) {
                return;
            }
            this.mIsFadingActualImage = true;
            OnFadeListener onFadeListener = this.mOnFadeListener;
            if (onFadeListener == null) {
                return;
            }
            onFadeListener.onFadeStarted();
        }
    }

    private void maybeOnFadeFinished() {
        if (!this.mIsFadingActualImage) {
            return;
        }
        this.mIsFadingActualImage = false;
        OnFadeListener onFadeListener = this.mOnFadeListener;
        if (onFadeListener == null) {
            return;
        }
        onFadeListener.onFadeFinished();
    }
}
