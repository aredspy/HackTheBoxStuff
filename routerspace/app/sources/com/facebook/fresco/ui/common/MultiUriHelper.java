package com.facebook.fresco.ui.common;

import android.net.Uri;
import com.facebook.common.internal.Fn;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public abstract class MultiUriHelper {
    @Nullable
    public static <T> Uri getMainUri(@Nullable T mainRequest, @Nullable T lowResRequest, @Nullable T[] firstAvailableRequest, Fn<T, Uri> requestToUri) {
        Uri apply;
        Uri apply2;
        if (mainRequest == null || (apply2 = requestToUri.apply(mainRequest)) == null) {
            if (firstAvailableRequest != null && firstAvailableRequest.length > 0 && firstAvailableRequest[0] != null && (apply = requestToUri.apply(firstAvailableRequest[0])) != null) {
                return apply;
            }
            if (lowResRequest == null) {
                return null;
            }
            return requestToUri.apply(lowResRequest);
        }
        return apply2;
    }
}
