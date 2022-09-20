package com.facebook.react.views.slider;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatSeekBar;
/* loaded from: classes.dex */
public class ReactSlider extends AppCompatSeekBar {
    private static int DEFAULT_TOTAL_STEPS = 128;
    private double mMinValue = 0.0d;
    private double mMaxValue = 0.0d;
    private double mValue = 0.0d;
    private double mStep = 0.0d;
    private double mStepCalculated = 0.0d;

    public ReactSlider(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        disableStateListAnimatorIfNeeded();
    }

    public void disableStateListAnimatorIfNeeded() {
        if (Build.VERSION.SDK_INT < 23 || Build.VERSION.SDK_INT >= 26) {
            return;
        }
        super.setStateListAnimator(null);
    }

    public void setMaxValue(double max) {
        this.mMaxValue = max;
        updateAll();
    }

    public void setMinValue(double min) {
        this.mMinValue = min;
        updateAll();
    }

    public void setValue(double value) {
        this.mValue = value;
        updateValue();
    }

    public void setStep(double step) {
        this.mStep = step;
        updateAll();
    }

    public double toRealProgress(int seekBarProgress) {
        if (seekBarProgress == getMax()) {
            return this.mMaxValue;
        }
        return (seekBarProgress * getStepValue()) + this.mMinValue;
    }

    private void updateAll() {
        if (this.mStep == 0.0d) {
            this.mStepCalculated = (this.mMaxValue - this.mMinValue) / DEFAULT_TOTAL_STEPS;
        }
        setMax(getTotalSteps());
        updateValue();
    }

    private void updateValue() {
        double d = this.mValue;
        double d2 = this.mMinValue;
        setProgress((int) Math.round(((d - d2) / (this.mMaxValue - d2)) * getTotalSteps()));
    }

    private int getTotalSteps() {
        return (int) Math.ceil((this.mMaxValue - this.mMinValue) / getStepValue());
    }

    private double getStepValue() {
        double d = this.mStep;
        return d > 0.0d ? d : this.mStepCalculated;
    }
}
