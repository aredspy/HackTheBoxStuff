package com.facebook.callercontext;

import javax.annotation.Nullable;
/* loaded from: classes.dex */
public interface CallerContextVerifier {
    void verifyCallerContext(@Nullable Object callerContext, boolean isPrefetch);
}
