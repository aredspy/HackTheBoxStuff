package com.facebook.react.common;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import com.facebook.infer.annotation.Assertions;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public class ShakeDetector implements SensorEventListener {
    private static final float REQUIRED_FORCE = 13.042845f;
    private float mAccelerationX;
    private float mAccelerationY;
    private float mAccelerationZ;
    private long mLastShakeTimestamp;
    private long mLastTimestamp;
    private int mMinNumShakes;
    private int mNumShakes;
    private SensorManager mSensorManager;
    private final ShakeListener mShakeListener;
    private static final long MIN_TIME_BETWEEN_SAMPLES_NS = TimeUnit.NANOSECONDS.convert(20, TimeUnit.MILLISECONDS);
    private static final float SHAKING_WINDOW_NS = (float) TimeUnit.NANOSECONDS.convert(3, TimeUnit.SECONDS);

    /* loaded from: classes.dex */
    public interface ShakeListener {
        void onShake();
    }

    @Override // android.hardware.SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public ShakeDetector(ShakeListener listener) {
        this(listener, 1);
    }

    public ShakeDetector(ShakeListener listener, int minNumShakes) {
        this.mShakeListener = listener;
        this.mMinNumShakes = minNumShakes;
    }

    public void start(SensorManager manager) {
        Assertions.assertNotNull(manager);
        Sensor defaultSensor = manager.getDefaultSensor(1);
        if (defaultSensor != null) {
            this.mSensorManager = manager;
            this.mLastTimestamp = -1L;
            manager.registerListener(this, defaultSensor, 2);
            this.mLastShakeTimestamp = 0L;
            reset();
        }
    }

    public void stop() {
        SensorManager sensorManager = this.mSensorManager;
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
            this.mSensorManager = null;
        }
    }

    private void reset() {
        this.mNumShakes = 0;
        this.mAccelerationX = 0.0f;
        this.mAccelerationY = 0.0f;
        this.mAccelerationZ = 0.0f;
    }

    private boolean atLeastRequiredForce(float a) {
        return Math.abs(a) > REQUIRED_FORCE;
    }

    private void recordShake(long timestamp) {
        this.mLastShakeTimestamp = timestamp;
        this.mNumShakes++;
    }

    @Override // android.hardware.SensorEventListener
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.timestamp - this.mLastTimestamp < MIN_TIME_BETWEEN_SAMPLES_NS) {
            return;
        }
        float f = sensorEvent.values[0];
        float f2 = sensorEvent.values[1];
        float f3 = sensorEvent.values[2] - 9.80665f;
        this.mLastTimestamp = sensorEvent.timestamp;
        if (atLeastRequiredForce(f) && this.mAccelerationX * f <= 0.0f) {
            recordShake(sensorEvent.timestamp);
            this.mAccelerationX = f;
        } else if (atLeastRequiredForce(f2) && this.mAccelerationY * f2 <= 0.0f) {
            recordShake(sensorEvent.timestamp);
            this.mAccelerationY = f2;
        } else if (atLeastRequiredForce(f3) && this.mAccelerationZ * f3 <= 0.0f) {
            recordShake(sensorEvent.timestamp);
            this.mAccelerationZ = f3;
        }
        maybeDispatchShake(sensorEvent.timestamp);
    }

    private void maybeDispatchShake(long currentTimestamp) {
        if (this.mNumShakes >= this.mMinNumShakes * 8) {
            reset();
            this.mShakeListener.onShake();
        }
        if (((float) (currentTimestamp - this.mLastShakeTimestamp)) > SHAKING_WINDOW_NS) {
            reset();
        }
    }
}
