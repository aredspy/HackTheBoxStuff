package com.facebook.react.packagerconnection;
/* loaded from: classes.dex */
public interface RequestHandler {
    void onNotification(Object params);

    void onRequest(Object params, Responder responder);
}
