package com.facebook.react.packagerconnection;

import com.facebook.common.logging.FLog;
/* loaded from: classes.dex */
public abstract class RequestOnlyHandler implements RequestHandler {
    private static final String TAG = JSPackagerClient.class.getSimpleName();

    @Override // com.facebook.react.packagerconnection.RequestHandler
    public abstract void onRequest(Object params, Responder responder);

    @Override // com.facebook.react.packagerconnection.RequestHandler
    public final void onNotification(Object params) {
        FLog.e(TAG, "Notification is not supported");
    }
}
