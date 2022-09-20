package com.facebook.react.views.switchview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import androidx.appcompat.widget.SwitchCompat;
/* loaded from: classes.dex */
class ReactSwitch extends SwitchCompat {
    private boolean mAllowChange = true;
    private Integer mTrackColorForFalse = null;
    private Integer mTrackColorForTrue = null;

    public ReactSwitch(Context context) {
        super(context);
    }

    @Override // androidx.appcompat.widget.SwitchCompat, android.widget.CompoundButton, android.widget.Checkable
    public void setChecked(boolean checked) {
        if (this.mAllowChange && isChecked() != checked) {
            this.mAllowChange = false;
            super.setChecked(checked);
            setTrackColor(checked);
            return;
        }
        super.setChecked(isChecked());
    }

    void setColor(Drawable drawable, Integer color) {
        if (color == null) {
            drawable.clearColorFilter();
        } else {
            drawable.setColorFilter(color.intValue(), PorterDuff.Mode.MULTIPLY);
        }
    }

    public void setTrackColor(Integer color) {
        setColor(super.getTrackDrawable(), color);
    }

    public void setThumbColor(Integer color) {
        setColor(super.getThumbDrawable(), color);
        if (Build.VERSION.SDK_INT >= 21) {
            ((RippleDrawable) super.getBackground()).setColor(new ColorStateList(new int[][]{new int[]{16842919}}, new int[]{color.intValue()}));
        }
    }

    public void setOn(boolean on) {
        if (isChecked() != on) {
            super.setChecked(on);
            setTrackColor(on);
        }
        this.mAllowChange = true;
    }

    public void setTrackColorForTrue(Integer color) {
        if (color == this.mTrackColorForTrue) {
            return;
        }
        this.mTrackColorForTrue = color;
        if (!isChecked()) {
            return;
        }
        setTrackColor(this.mTrackColorForTrue);
    }

    public void setTrackColorForFalse(Integer color) {
        if (color == this.mTrackColorForFalse) {
            return;
        }
        this.mTrackColorForFalse = color;
        if (isChecked()) {
            return;
        }
        setTrackColor(this.mTrackColorForFalse);
    }

    private void setTrackColor(boolean checked) {
        Integer num = this.mTrackColorForTrue;
        if (num == null && this.mTrackColorForFalse == null) {
            return;
        }
        if (!checked) {
            num = this.mTrackColorForFalse;
        }
        setTrackColor(num);
    }
}
