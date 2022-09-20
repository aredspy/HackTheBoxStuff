package com.facebook.drawee.components;

import android.os.Looper;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public abstract class DeferredReleaser {
    @Nullable
    private static DeferredReleaser sInstance;

    /* loaded from: classes.dex */
    public interface Releasable {
        void release();
    }

    public abstract void cancelDeferredRelease(Releasable releasable);

    public abstract void scheduleDeferredRelease(Releasable releasable);

    public static synchronized DeferredReleaser getInstance() {
        DeferredReleaser deferredReleaser;
        synchronized (DeferredReleaser.class) {
            if (sInstance == null) {
                sInstance = new DeferredReleaserConcurrentImpl();
            }
            deferredReleaser = sInstance;
        }
        return deferredReleaser;
    }

    public static boolean isOnUiThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }
}
