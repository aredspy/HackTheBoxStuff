package com.facebook.react.bridge.queue;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
/* loaded from: classes.dex */
public interface MessageQueueThread {
    void assertIsOnThread();

    void assertIsOnThread(String message);

    <T> Future<T> callOnQueue(final Callable<T> callable);

    MessageQueueThreadPerfStats getPerfStats();

    boolean isOnThread();

    void quitSynchronous();

    void resetPerfStats();

    void runOnQueue(Runnable runnable);
}
