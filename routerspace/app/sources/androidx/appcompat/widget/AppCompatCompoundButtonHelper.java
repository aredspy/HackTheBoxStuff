package androidx.appcompat.widget;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.CompoundButton;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.widget.CompoundButtonCompat;
/* loaded from: classes.dex */
class AppCompatCompoundButtonHelper {
    private ColorStateList mButtonTintList = null;
    private PorterDuff.Mode mButtonTintMode = null;
    private boolean mHasButtonTint = false;
    private boolean mHasButtonTintMode = false;
    private boolean mSkipNextApply;
    private final CompoundButton mView;

    /* loaded from: classes.dex */
    interface DirectSetButtonDrawableInterface {
        void setButtonDrawable(Drawable drawable);
    }

    public AppCompatCompoundButtonHelper(CompoundButton compoundButton) {
        this.mView = compoundButton;
    }

    /* JADX WARN: Removed duplicated region for block: B:18:0x0054 A[Catch: all -> 0x007c, TryCatch #1 {all -> 0x007c, blocks: (B:3:0x000d, B:5:0x0015, B:7:0x001d, B:11:0x002f, B:13:0x0037, B:15:0x003f, B:16:0x004c, B:18:0x0054, B:19:0x005f, B:21:0x0067), top: B:30:0x000d }] */
    /* JADX WARN: Removed duplicated region for block: B:21:0x0067 A[Catch: all -> 0x007c, TRY_LEAVE, TryCatch #1 {all -> 0x007c, blocks: (B:3:0x000d, B:5:0x0015, B:7:0x001d, B:11:0x002f, B:13:0x0037, B:15:0x003f, B:16:0x004c, B:18:0x0054, B:19:0x005f, B:21:0x0067), top: B:30:0x000d }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void loadFromAttributes(android.util.AttributeSet r4, int r5) {
        /*
            r3 = this;
            android.widget.CompoundButton r0 = r3.mView
            android.content.Context r0 = r0.getContext()
            int[] r1 = androidx.appcompat.R.styleable.CompoundButton
            r2 = 0
            android.content.res.TypedArray r4 = r0.obtainStyledAttributes(r4, r1, r5, r2)
            int r5 = androidx.appcompat.R.styleable.CompoundButton_buttonCompat     // Catch: java.lang.Throwable -> L7c
            boolean r5 = r4.hasValue(r5)     // Catch: java.lang.Throwable -> L7c
            if (r5 == 0) goto L2c
            int r5 = androidx.appcompat.R.styleable.CompoundButton_buttonCompat     // Catch: java.lang.Throwable -> L7c
            int r5 = r4.getResourceId(r5, r2)     // Catch: java.lang.Throwable -> L7c
            if (r5 == 0) goto L2c
            android.widget.CompoundButton r0 = r3.mView     // Catch: android.content.res.Resources.NotFoundException -> L2c java.lang.Throwable -> L7c
            android.content.Context r1 = r0.getContext()     // Catch: android.content.res.Resources.NotFoundException -> L2c java.lang.Throwable -> L7c
            android.graphics.drawable.Drawable r5 = androidx.appcompat.content.res.AppCompatResources.getDrawable(r1, r5)     // Catch: android.content.res.Resources.NotFoundException -> L2c java.lang.Throwable -> L7c
            r0.setButtonDrawable(r5)     // Catch: android.content.res.Resources.NotFoundException -> L2c java.lang.Throwable -> L7c
            r5 = 1
            goto L2d
        L2c:
            r5 = 0
        L2d:
            if (r5 != 0) goto L4c
            int r5 = androidx.appcompat.R.styleable.CompoundButton_android_button     // Catch: java.lang.Throwable -> L7c
            boolean r5 = r4.hasValue(r5)     // Catch: java.lang.Throwable -> L7c
            if (r5 == 0) goto L4c
            int r5 = androidx.appcompat.R.styleable.CompoundButton_android_button     // Catch: java.lang.Throwable -> L7c
            int r5 = r4.getResourceId(r5, r2)     // Catch: java.lang.Throwable -> L7c
            if (r5 == 0) goto L4c
            android.widget.CompoundButton r0 = r3.mView     // Catch: java.lang.Throwable -> L7c
            android.content.Context r1 = r0.getContext()     // Catch: java.lang.Throwable -> L7c
            android.graphics.drawable.Drawable r5 = androidx.appcompat.content.res.AppCompatResources.getDrawable(r1, r5)     // Catch: java.lang.Throwable -> L7c
            r0.setButtonDrawable(r5)     // Catch: java.lang.Throwable -> L7c
        L4c:
            int r5 = androidx.appcompat.R.styleable.CompoundButton_buttonTint     // Catch: java.lang.Throwable -> L7c
            boolean r5 = r4.hasValue(r5)     // Catch: java.lang.Throwable -> L7c
            if (r5 == 0) goto L5f
            android.widget.CompoundButton r5 = r3.mView     // Catch: java.lang.Throwable -> L7c
            int r0 = androidx.appcompat.R.styleable.CompoundButton_buttonTint     // Catch: java.lang.Throwable -> L7c
            android.content.res.ColorStateList r0 = r4.getColorStateList(r0)     // Catch: java.lang.Throwable -> L7c
            androidx.core.widget.CompoundButtonCompat.setButtonTintList(r5, r0)     // Catch: java.lang.Throwable -> L7c
        L5f:
            int r5 = androidx.appcompat.R.styleable.CompoundButton_buttonTintMode     // Catch: java.lang.Throwable -> L7c
            boolean r5 = r4.hasValue(r5)     // Catch: java.lang.Throwable -> L7c
            if (r5 == 0) goto L78
            android.widget.CompoundButton r5 = r3.mView     // Catch: java.lang.Throwable -> L7c
            int r0 = androidx.appcompat.R.styleable.CompoundButton_buttonTintMode     // Catch: java.lang.Throwable -> L7c
            r1 = -1
            int r0 = r4.getInt(r0, r1)     // Catch: java.lang.Throwable -> L7c
            r1 = 0
            android.graphics.PorterDuff$Mode r0 = androidx.appcompat.widget.DrawableUtils.parseTintMode(r0, r1)     // Catch: java.lang.Throwable -> L7c
            androidx.core.widget.CompoundButtonCompat.setButtonTintMode(r5, r0)     // Catch: java.lang.Throwable -> L7c
        L78:
            r4.recycle()
            return
        L7c:
            r5 = move-exception
            r4.recycle()
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.appcompat.widget.AppCompatCompoundButtonHelper.loadFromAttributes(android.util.AttributeSet, int):void");
    }

    public void setSupportButtonTintList(ColorStateList colorStateList) {
        this.mButtonTintList = colorStateList;
        this.mHasButtonTint = true;
        applyButtonTint();
    }

    public ColorStateList getSupportButtonTintList() {
        return this.mButtonTintList;
    }

    public void setSupportButtonTintMode(PorterDuff.Mode mode) {
        this.mButtonTintMode = mode;
        this.mHasButtonTintMode = true;
        applyButtonTint();
    }

    public PorterDuff.Mode getSupportButtonTintMode() {
        return this.mButtonTintMode;
    }

    public void onSetButtonDrawable() {
        if (this.mSkipNextApply) {
            this.mSkipNextApply = false;
            return;
        }
        this.mSkipNextApply = true;
        applyButtonTint();
    }

    void applyButtonTint() {
        Drawable buttonDrawable = CompoundButtonCompat.getButtonDrawable(this.mView);
        if (buttonDrawable != null) {
            if (!this.mHasButtonTint && !this.mHasButtonTintMode) {
                return;
            }
            Drawable mutate = DrawableCompat.wrap(buttonDrawable).mutate();
            if (this.mHasButtonTint) {
                DrawableCompat.setTintList(mutate, this.mButtonTintList);
            }
            if (this.mHasButtonTintMode) {
                DrawableCompat.setTintMode(mutate, this.mButtonTintMode);
            }
            if (mutate.isStateful()) {
                mutate.setState(this.mView.getDrawableState());
            }
            this.mView.setButtonDrawable(mutate);
        }
    }

    public int getCompoundPaddingLeft(int i) {
        Drawable buttonDrawable;
        return (Build.VERSION.SDK_INT >= 17 || (buttonDrawable = CompoundButtonCompat.getButtonDrawable(this.mView)) == null) ? i : i + buttonDrawable.getIntrinsicWidth();
    }
}
