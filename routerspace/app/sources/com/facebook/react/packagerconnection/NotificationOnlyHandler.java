package com.facebook.react.packagerconnection;

import com.facebook.common.logging.FLog;
/* loaded from: classes.dex */
public abstract class NotificationOnlyHandler implements RequestHandler {
    private static final String TAG = JSPackagerClient.class.getSimpleName();

    @Override // com.facebook.react.packagerconnection.RequestHandler
    public abstract void onNotification(Object params);

    @Override // com.facebook.react.packagerconnection.RequestHandler
    public final void onRequest(Object params, Responder responder) {
        responder.error("Request is not supported");
        FLog.e(TAG, "Request is not supported");
    }
}