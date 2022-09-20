package com.facebook.react.uimanager.layoutanimation;

import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.BaseInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.IllegalViewOperationException;
import java.util.Map;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public abstract class AbstractLayoutAnimation {
    private static final Map<InterpolatorType, BaseInterpolator> INTERPOLATOR = MapBuilder.of(InterpolatorType.LINEAR, new LinearInterpolator(), InterpolatorType.EASE_IN, new AccelerateInterpolator(), InterpolatorType.EASE_OUT, new DecelerateInterpolator(), InterpolatorType.EASE_IN_EASE_OUT, new AccelerateDecelerateInterpolator());
    private static final boolean SLOWDOWN_ANIMATION_MODE = false;
    protected AnimatedPropertyType mAnimatedProperty;
    private int mDelayMs;
    protected int mDurationMs;
    private Interpolator mInterpolator;

    abstract Animation createAnimationImpl(View view, int x, int y, int width, int height);

    abstract boolean isValid();

    public void reset() {
        this.mAnimatedProperty = null;
        this.mDurationMs = 0;
        this.mDelayMs = 0;
        this.mInterpolator = null;
    }

    public void initializeFromConfig(ReadableMap data, int globalDuration) {
        this.mAnimatedProperty = data.hasKey("property") ? AnimatedPropertyType.fromString(data.getString("property")) : null;
        if (data.hasKey("duration")) {
            globalDuration = data.getInt("duration");
        }
        this.mDurationMs = globalDuration;
        this.mDelayMs = data.hasKey("delay") ? data.getInt("delay") : 0;
        if (!data.hasKey("type")) {
            throw new IllegalArgumentException("Missing interpolation type.");
        }
        this.mInterpolator = getInterpolator(InterpolatorType.fromString(data.getString("type")), data);
        if (isValid()) {
            return;
        }
        throw new IllegalViewOperationException("Invalid layout animation : " + data);
    }

    public final Animation createAnimation(View view, int x, int y, int width, int height) {
        if (!isValid()) {
            return null;
        }
        Animation createAnimationImpl = createAnimationImpl(view, x, y, width, height);
        if (createAnimationImpl != null) {
            createAnimationImpl.setDuration(this.mDurationMs * 1);
            createAnimationImpl.setStartOffset(this.mDelayMs * 1);
            createAnimationImpl.setInterpolator(this.mInterpolator);
        }
        return createAnimationImpl;
    }

    private static Interpolator getInterpolator(InterpolatorType type, ReadableMap params) {
        BaseInterpolator baseInterpolator;
        if (type.equals(InterpolatorType.SPRING)) {
            baseInterpolator = new SimpleSpringInterpolator(SimpleSpringInterpolator.getSpringDamping(params));
        } else {
            baseInterpolator = INTERPOLATOR.get(type);
        }
        if (baseInterpolator != null) {
            return baseInterpolator;
        }
        throw new IllegalArgumentException("Missing interpolator for type : " + type);
    }
}
