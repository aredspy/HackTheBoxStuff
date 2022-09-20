package com.facebook.react.uimanager.layoutanimation;

import android.view.animation.Interpolator;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;
/* loaded from: classes.dex */
public class SimpleSpringInterpolator implements Interpolator {
    private static final float FACTOR = 0.5f;
    public static final String PARAM_SPRING_DAMPING = "springDamping";
    private final float mSpringDamping;

    public static float getSpringDamping(ReadableMap params) {
        return params.getType(PARAM_SPRING_DAMPING).equals(ReadableType.Number) ? (float) params.getDouble(PARAM_SPRING_DAMPING) : FACTOR;
    }

    public SimpleSpringInterpolator() {
        this.mSpringDamping = FACTOR;
    }

    public SimpleSpringInterpolator(float springDamping) {
        this.mSpringDamping = springDamping;
    }

    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float input) {
        double pow = Math.pow(2.0d, (-10.0f) * input);
        float f = this.mSpringDamping;
        return (float) ((pow * Math.sin((((input - (f / 4.0f)) * 3.141592653589793d) * 2.0d) / f)) + 1.0d);
    }
}
