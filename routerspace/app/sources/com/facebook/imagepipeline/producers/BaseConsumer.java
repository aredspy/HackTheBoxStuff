package com.facebook.imagepipeline.producers;

import com.facebook.common.logging.FLog;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public abstract class BaseConsumer<T> implements Consumer<T> {
    private boolean mIsFinished = false;

    public static boolean isLast(int status) {
        return (status & 1) == 1;
    }

    public static int simpleStatusForIsLast(boolean isLast) {
        return isLast ? 1 : 0;
    }

    public static boolean statusHasAnyFlag(int status, int flag) {
        return (status & flag) != 0;
    }

    public static boolean statusHasFlag(int status, int flag) {
        return (status & flag) == flag;
    }

    public static int turnOffStatusFlag(int status, int flag) {
        return status & (~flag);
    }

    public static int turnOnStatusFlag(int status, int flag) {
        return status | flag;
    }

    protected abstract void onCancellationImpl();

    protected abstract void onFailureImpl(Throwable t);

    protected abstract void onNewResultImpl(@Nullable T newResult, int status);

    protected void onProgressUpdateImpl(float progress) {
    }

    public static boolean isNotLast(int status) {
        return !isLast(status);
    }

    @Override // com.facebook.imagepipeline.producers.Consumer
    public synchronized void onNewResult(@Nullable T newResult, int status) {
        if (this.mIsFinished) {
            return;
        }
        this.mIsFinished = isLast(status);
        try {
            onNewResultImpl(newResult, status);
        } catch (Exception e) {
            onUnhandledException(e);
        }
    }

    @Override // com.facebook.imagepipeline.producers.Consumer
    public synchronized void onFailure(Throwable t) {
        if (this.mIsFinished) {
            return;
        }
        this.mIsFinished = true;
        try {
            onFailureImpl(t);
        } catch (Exception e) {
            onUnhandledException(e);
        }
    }

    @Override // com.facebook.imagepipeline.producers.Consumer
    public synchronized void onCancellation() {
        if (this.mIsFinished) {
            return;
        }
        this.mIsFinished = true;
        try {
            onCancellationImpl();
        } catch (Exception e) {
            onUnhandledException(e);
        }
    }

    @Override // com.facebook.imagepipeline.producers.Consumer
    public synchronized void onProgressUpdate(float progress) {
        if (this.mIsFinished) {
            return;
        }
        try {
            onProgressUpdateImpl(progress);
        } catch (Exception e) {
            onUnhandledException(e);
        }
    }

    protected void onUnhandledException(Exception e) {
        FLog.wtf(getClass(), "unhandled exception", e);
    }
}
