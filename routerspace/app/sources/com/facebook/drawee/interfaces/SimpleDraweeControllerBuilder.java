package com.facebook.drawee.interfaces;

import android.net.Uri;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public interface SimpleDraweeControllerBuilder {
    DraweeController build();

    SimpleDraweeControllerBuilder setCallerContext(Object callerContext);

    SimpleDraweeControllerBuilder setOldController(@Nullable DraweeController oldController);

    SimpleDraweeControllerBuilder setUri(Uri uri);

    SimpleDraweeControllerBuilder setUri(@Nullable String uriString);
}
