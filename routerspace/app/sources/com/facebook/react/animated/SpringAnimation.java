package com.facebook.react.animated;

import com.facebook.react.bridge.ReadableMap;
/* loaded from: classes.dex */
class SpringAnimation extends AnimationDriver {
    private static final double MAX_DELTA_TIME_SEC = 0.064d;
    private static final double SOLVER_TIMESTEP_SEC = 0.001d;
    private int mCurrentLoop;
    private final PhysicsState mCurrentState;
    private double mDisplacementFromRestThreshold;
    private double mEndValue;
    private double mInitialVelocity;
    private int mIterations;
    private long mLastTime;
    private double mOriginalValue;
    private boolean mOvershootClampingEnabled;
    private double mRestSpeedThreshold;
    private double mSpringDamping;
    private double mSpringMass;
    private boolean mSpringStarted;
    private double mSpringStiffness;
    private double mStartValue;
    private double mTimeAccumulator;

    /* loaded from: classes.dex */
    public static class PhysicsState {
        double position;
        double velocity;

        private PhysicsState() {
        }
    }

    public SpringAnimation(ReadableMap config) {
        PhysicsState physicsState = new PhysicsState();
        this.mCurrentState = physicsState;
        physicsState.velocity = config.getDouble("initialVelocity");
        resetConfig(config);
    }

    @Override // com.facebook.react.animated.AnimationDriver
    public void resetConfig(ReadableMap config) {
        this.mSpringStiffness = config.getDouble("stiffness");
        this.mSpringDamping = config.getDouble("damping");
        this.mSpringMass = config.getDouble("mass");
        this.mInitialVelocity = this.mCurrentState.velocity;
        this.mEndValue = config.getDouble("toValue");
        this.mRestSpeedThreshold = config.getDouble("restSpeedThreshold");
        this.mDisplacementFromRestThreshold = config.getDouble("restDisplacementThreshold");
        this.mOvershootClampingEnabled = config.getBoolean("overshootClamping");
        boolean z = true;
        int i = config.hasKey("iterations") ? config.getInt("iterations") : 1;
        this.mIterations = i;
        if (i != 0) {
            z = false;
        }
        this.mHasFinished = z;
        this.mCurrentLoop = 0;
        this.mTimeAccumulator = 0.0d;
        this.mSpringStarted = false;
    }

    @Override // com.facebook.react.animated.AnimationDriver
    public void runAnimationStep(long frameTimeNanos) {
        long j = frameTimeNanos / 1000000;
        if (!this.mSpringStarted) {
            if (this.mCurrentLoop == 0) {
                this.mOriginalValue = this.mAnimatedValue.mValue;
                this.mCurrentLoop = 1;
            }
            PhysicsState physicsState = this.mCurrentState;
            double d = this.mAnimatedValue.mValue;
            physicsState.position = d;
            this.mStartValue = d;
            this.mLastTime = j;
            this.mTimeAccumulator = 0.0d;
            this.mSpringStarted = true;
        }
        advance((j - this.mLastTime) / 1000.0d);
        this.mLastTime = j;
        this.mAnimatedValue.mValue = this.mCurrentState.position;
        if (isAtRest()) {
            int i = this.mIterations;
            if (i == -1 || this.mCurrentLoop < i) {
                this.mSpringStarted = false;
                this.mAnimatedValue.mValue = this.mOriginalValue;
                this.mCurrentLoop++;
                return;
            }
            this.mHasFinished = true;
        }
    }

    private double getDisplacementDistanceForState(PhysicsState state) {
        return Math.abs(this.mEndValue - state.position);
    }

    private boolean isAtRest() {
        return Math.abs(this.mCurrentState.velocity) <= this.mRestSpeedThreshold && (getDisplacementDistanceForState(this.mCurrentState) <= this.mDisplacementFromRestThreshold || this.mSpringStiffness == 0.0d);
    }

    private boolean isOvershooting() {
        return this.mSpringStiffness > 0.0d && ((this.mStartValue < this.mEndValue && this.mCurrentState.position > this.mEndValue) || (this.mStartValue > this.mEndValue && this.mCurrentState.position < this.mEndValue));
    }

    private void advance(double realDeltaTime) {
        double d;
        double d2;
        if (isAtRest()) {
            return;
        }
        double d3 = MAX_DELTA_TIME_SEC;
        if (realDeltaTime <= MAX_DELTA_TIME_SEC) {
            d3 = realDeltaTime;
        }
        this.mTimeAccumulator += d3;
        double d4 = this.mSpringDamping;
        double d5 = this.mSpringMass;
        double d6 = this.mSpringStiffness;
        double d7 = -this.mInitialVelocity;
        double sqrt = d4 / (Math.sqrt(d6 * d5) * 2.0d);
        double sqrt2 = Math.sqrt(d6 / d5);
        double sqrt3 = Math.sqrt(1.0d - (sqrt * sqrt)) * sqrt2;
        double d8 = this.mEndValue - this.mStartValue;
        double d9 = this.mTimeAccumulator;
        if (sqrt < 1.0d) {
            double exp = Math.exp((-sqrt) * sqrt2 * d9);
            double d10 = sqrt * sqrt2;
            double d11 = d7 + (d10 * d8);
            double d12 = d9 * sqrt3;
            d = this.mEndValue - ((((d11 / sqrt3) * Math.sin(d12)) + (Math.cos(d12) * d8)) * exp);
            d2 = ((d10 * exp) * (((Math.sin(d12) * d11) / sqrt3) + (Math.cos(d12) * d8))) - (((Math.cos(d12) * d11) - ((sqrt3 * d8) * Math.sin(d12))) * exp);
        } else {
            double exp2 = Math.exp((-sqrt2) * d9);
            double d13 = this.mEndValue - (((((sqrt2 * d8) + d7) * d9) + d8) * exp2);
            d2 = exp2 * ((d7 * ((d9 * sqrt2) - 1.0d)) + (d9 * d8 * sqrt2 * sqrt2));
            d = d13;
        }
        this.mCurrentState.position = d;
        this.mCurrentState.velocity = d2;
        if (!isAtRest() && (!this.mOvershootClampingEnabled || !isOvershooting())) {
            return;
        }
        if (this.mSpringStiffness > 0.0d) {
            double d14 = this.mEndValue;
            this.mStartValue = d14;
            this.mCurrentState.position = d14;
        } else {
            double d15 = this.mCurrentState.position;
            this.mEndValue = d15;
            this.mStartValue = d15;
        }
        this.mCurrentState.velocity = 0.0d;
    }
}
