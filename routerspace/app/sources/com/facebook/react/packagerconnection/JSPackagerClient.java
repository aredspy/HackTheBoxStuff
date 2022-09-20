package com.facebook.react.packagerconnection;

import android.net.Uri;
import com.facebook.common.logging.FLog;
import com.facebook.react.modules.systeminfo.AndroidInfoHelpers;
import com.facebook.react.packagerconnection.ReconnectingWebSocket;
import java.util.Map;
import okio.ByteString;
import org.json.JSONObject;
/* loaded from: classes.dex */
public final class JSPackagerClient implements ReconnectingWebSocket.MessageCallback {
    private static final int PROTOCOL_VERSION = 2;
    private static final String TAG = "JSPackagerClient";
    private Map<String, RequestHandler> mRequestHandlers;
    private ReconnectingWebSocket mWebSocket;

    /* loaded from: classes.dex */
    public class ResponderImpl implements Responder {
        private Object mId;

        public ResponderImpl(Object id) {
            JSPackagerClient.this = this$0;
            this.mId = id;
        }

        @Override // com.facebook.react.packagerconnection.Responder
        public void respond(Object result) {
            try {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("version", 2);
                jSONObject.put("id", this.mId);
                jSONObject.put("result", result);
                JSPackagerClient.this.mWebSocket.sendMessage(jSONObject.toString());
            } catch (Exception e) {
                FLog.e(JSPackagerClient.TAG, "Responding failed", e);
            }
        }

        @Override // com.facebook.react.packagerconnection.Responder
        public void error(Object error) {
            try {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("version", 2);
                jSONObject.put("id", this.mId);
                jSONObject.put("error", error);
                JSPackagerClient.this.mWebSocket.sendMessage(jSONObject.toString());
            } catch (Exception e) {
                FLog.e(JSPackagerClient.TAG, "Responding with error failed", e);
            }
        }
    }

    public JSPackagerClient(String clientId, PackagerConnectionSettings settings, Map<String, RequestHandler> requestHandlers) {
        this(clientId, settings, requestHandlers, null);
    }

    public JSPackagerClient(String clientId, PackagerConnectionSettings settings, Map<String, RequestHandler> requestHandlers, ReconnectingWebSocket.ConnectionCallback connectionCallback) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("ws").encodedAuthority(settings.getDebugServerHost()).appendPath("message").appendQueryParameter("device", AndroidInfoHelpers.getFriendlyDeviceName()).appendQueryParameter("app", settings.getPackageName()).appendQueryParameter("clientid", clientId);
        this.mWebSocket = new ReconnectingWebSocket(builder.build().toString(), this, connectionCallback);
        this.mRequestHandlers = requestHandlers;
    }

    public void init() {
        this.mWebSocket.connect();
    }

    public void close() {
        this.mWebSocket.closeQuietly();
    }

    @Override // com.facebook.react.packagerconnection.ReconnectingWebSocket.MessageCallback
    public void onMessage(String text) {
        try {
            JSONObject jSONObject = new JSONObject(text);
            int optInt = jSONObject.optInt("version");
            String optString = jSONObject.optString("method");
            Object opt = jSONObject.opt("id");
            Object opt2 = jSONObject.opt("params");
            if (optInt != 2) {
                String str = TAG;
                FLog.e(str, "Message with incompatible or missing version of protocol received: " + optInt);
            } else if (optString == null) {
                abortOnMessage(opt, "No method provided");
            } else {
                RequestHandler requestHandler = this.mRequestHandlers.get(optString);
                if (requestHandler == null) {
                    abortOnMessage(opt, "No request handler for method: " + optString);
                } else if (opt == null) {
                    requestHandler.onNotification(opt2);
                } else {
                    requestHandler.onRequest(opt2, new ResponderImpl(opt));
                }
            }
        } catch (Exception e) {
            FLog.e(TAG, "Handling the message failed", e);
        }
    }

    @Override // com.facebook.react.packagerconnection.ReconnectingWebSocket.MessageCallback
    public void onMessage(ByteString bytes) {
        FLog.w(TAG, "Websocket received message with payload of unexpected type binary");
    }

    private void abortOnMessage(Object id, String reason) {
        if (id != null) {
            new ResponderImpl(id).error(reason);
        }
        String str = TAG;
        FLog.e(str, "Handling the message failed with reason: " + reason);
    }
}
