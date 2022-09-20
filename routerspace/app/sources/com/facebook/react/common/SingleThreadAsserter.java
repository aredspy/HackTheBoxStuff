package com.facebook.react.common;

import com.facebook.infer.annotation.Assertions;
/* loaded from: classes.dex */
public class SingleThreadAsserter {
    private Thread mThread = null;

    public void assertNow() {
        Thread currentThread = Thread.currentThread();
        if (this.mThread == null) {
            this.mThread = currentThread;
        }
        Assertions.assertCondition(this.mThread == currentThread);
    }
}
