package com.facebook.common.references;

import android.graphics.Bitmap;
import com.facebook.common.internal.Objects;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.logging.FLog;
import java.util.IdentityHashMap;
import java.util.Map;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class SharedReference<T> {
    private static final Map<Object, Integer> sLiveObjects = new IdentityHashMap();
    private int mRefCount = 1;
    private final ResourceReleaser<T> mResourceReleaser;
    @Nullable
    private T mValue;

    public SharedReference(T value, ResourceReleaser<T> resourceReleaser) {
        this.mValue = (T) Preconditions.checkNotNull(value);
        this.mResourceReleaser = (ResourceReleaser) Preconditions.checkNotNull(resourceReleaser);
        addLiveReference(value);
    }

    private static void addLiveReference(Object value) {
        if (!CloseableReference.useGc() || (!(value instanceof Bitmap) && !(value instanceof HasBitmap))) {
            Map<Object, Integer> map = sLiveObjects;
            synchronized (map) {
                Integer num = map.get(value);
                if (num == null) {
                    map.put(value, 1);
                } else {
                    map.put(value, Integer.valueOf(num.intValue() + 1));
                }
            }
        }
    }

    private static void removeLiveReference(Object value) {
        Map<Object, Integer> map = sLiveObjects;
        synchronized (map) {
            Integer num = map.get(value);
            if (num == null) {
                FLog.wtf("SharedReference", "No entry in sLiveObjects for value of type %s", value.getClass());
            } else if (num.intValue() == 1) {
                map.remove(value);
            } else {
                map.put(value, Integer.valueOf(num.intValue() - 1));
            }
        }
    }

    @Nullable
    public synchronized T get() {
        return this.mValue;
    }

    public synchronized boolean isValid() {
        return this.mRefCount > 0;
    }

    public static boolean isValid(@Nullable SharedReference<?> ref) {
        return ref != null && ref.isValid();
    }

    public synchronized void addReference() {
        ensureValid();
        this.mRefCount++;
    }

    public synchronized boolean addReferenceIfValid() {
        if (isValid()) {
            addReference();
            return true;
        }
        return false;
    }

    public synchronized boolean deleteReferenceIfValid() {
        if (isValid()) {
            deleteReference();
            return true;
        }
        return false;
    }

    public void deleteReference() {
        T t;
        if (decreaseRefCount() == 0) {
            synchronized (this) {
                t = this.mValue;
                this.mValue = null;
            }
            if (t == null) {
                return;
            }
            this.mResourceReleaser.release(t);
            removeLiveReference(t);
        }
    }

    private synchronized int decreaseRefCount() {
        int i;
        ensureValid();
        Preconditions.checkArgument(Boolean.valueOf(this.mRefCount > 0));
        i = this.mRefCount - 1;
        this.mRefCount = i;
        return i;
    }

    private void ensureValid() {
        if (isValid(this)) {
            return;
        }
        throw new NullReferenceException();
    }

    public synchronized int getRefCountTestOnly() {
        return this.mRefCount;
    }

    /* loaded from: classes.dex */
    public static class NullReferenceException extends RuntimeException {
        public NullReferenceException() {
            super("Null shared reference");
        }
    }

    public static String reportData() {
        return Objects.toStringHelper("SharedReference").add("live_objects_count", sLiveObjects.size()).toString();
    }
}
