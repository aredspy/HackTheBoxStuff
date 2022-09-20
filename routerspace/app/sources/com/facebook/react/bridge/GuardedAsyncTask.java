package com.facebook.react.bridge;

import android.os.AsyncTask;
/* loaded from: classes.dex */
public abstract class GuardedAsyncTask<Params, Progress> extends AsyncTask<Params, Progress, Void> {
    private final NativeModuleCallExceptionHandler mExceptionHandler;

    protected abstract void doInBackgroundGuarded(Params... params);

    @Deprecated
    public GuardedAsyncTask(ReactContext reactContext) {
        this(reactContext.getExceptionHandler());
    }

    protected GuardedAsyncTask(NativeModuleCallExceptionHandler exceptionHandler) {
        this.mExceptionHandler = exceptionHandler;
    }

    @Override // android.os.AsyncTask
    public final Void doInBackground(Params... params) {
        try {
            doInBackgroundGuarded(params);
            return null;
        } catch (RuntimeException e) {
            this.mExceptionHandler.handleException(e);
            return null;
        }
    }
}
