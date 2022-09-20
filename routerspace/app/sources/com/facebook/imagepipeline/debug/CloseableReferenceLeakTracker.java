package com.facebook.imagepipeline.debug;

import com.facebook.common.references.SharedReference;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public interface CloseableReferenceLeakTracker {

    /* loaded from: classes.dex */
    public interface Listener {
        void onCloseableReferenceLeak(SharedReference<Object> reference, @Nullable Throwable stacktrace);
    }

    boolean isSet();

    void setListener(@Nullable Listener listener);

    void trackCloseableReferenceLeak(SharedReference<Object> reference, @Nullable Throwable stacktrace);
}
