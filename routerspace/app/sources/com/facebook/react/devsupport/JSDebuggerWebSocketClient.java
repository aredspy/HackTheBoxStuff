package com.facebook.react.devsupport;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.JsonWriter;
import com.facebook.common.logging.FLog;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.common.JavascriptException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
/* loaded from: classes.dex */
public class JSDebuggerWebSocketClient extends WebSocketListener {
    private static final String TAG = "JSDebuggerWebSocketClient";
    private JSDebuggerCallback mConnectCallback;
    private OkHttpClient mHttpClient;
    private WebSocket mWebSocket;
    private final AtomicInteger mRequestID = new AtomicInteger();
    private final ConcurrentHashMap<Integer, JSDebuggerCallback> mCallbacks = new ConcurrentHashMap<>();

    /* loaded from: classes.dex */
    public interface JSDebuggerCallback {
        void onFailure(Throwable cause);

        void onSuccess(String response);
    }

    public void connect(String url, JSDebuggerCallback callback) {
        if (this.mHttpClient != null) {
            throw new IllegalStateException("JSDebuggerWebSocketClient is already initialized.");
        }
        this.mConnectCallback = callback;
        this.mHttpClient = new OkHttpClient.Builder().connectTimeout(10L, TimeUnit.SECONDS).writeTimeout(10L, TimeUnit.SECONDS).readTimeout(0L, TimeUnit.MINUTES).build();
        this.mHttpClient.newWebSocket(new Request.Builder().url(url).build(), this);
    }

    public void prepareJSRuntime(JSDebuggerCallback callback) {
        int andIncrement = this.mRequestID.getAndIncrement();
        this.mCallbacks.put(Integer.valueOf(andIncrement), callback);
        try {
            StringWriter stringWriter = new StringWriter();
            new JsonWriter(stringWriter).beginObject().name("id").value(andIncrement).name("method").value("prepareJSRuntime").endObject().close();
            sendMessage(andIncrement, stringWriter.toString());
        } catch (IOException e) {
            triggerRequestFailure(andIncrement, e);
        }
    }

    public void loadBundle(String sourceURL, HashMap<String, String> injectedObjects, JSDebuggerCallback callback) {
        int andIncrement = this.mRequestID.getAndIncrement();
        this.mCallbacks.put(Integer.valueOf(andIncrement), callback);
        try {
            StringWriter stringWriter = new StringWriter();
            JsonWriter beginObject = new JsonWriter(stringWriter).beginObject().name("id").value(andIncrement).name("method").value("executeApplicationScript").name("url").value(sourceURL).name("inject").beginObject();
            for (String str : injectedObjects.keySet()) {
                beginObject.name(str).value(injectedObjects.get(str));
            }
            beginObject.endObject().endObject().close();
            sendMessage(andIncrement, stringWriter.toString());
        } catch (IOException e) {
            triggerRequestFailure(andIncrement, e);
        }
    }

    public void executeJSCall(String methodName, String jsonArgsArray, JSDebuggerCallback callback) {
        int andIncrement = this.mRequestID.getAndIncrement();
        this.mCallbacks.put(Integer.valueOf(andIncrement), callback);
        try {
            StringWriter stringWriter = new StringWriter();
            JsonWriter jsonWriter = new JsonWriter(stringWriter);
            jsonWriter.beginObject().name("id").value(andIncrement).name("method").value(methodName);
            stringWriter.append((CharSequence) ",\"arguments\":").append((CharSequence) jsonArgsArray);
            jsonWriter.endObject().close();
            sendMessage(andIncrement, stringWriter.toString());
        } catch (IOException e) {
            triggerRequestFailure(andIncrement, e);
        }
    }

    public void closeQuietly() {
        WebSocket webSocket = this.mWebSocket;
        if (webSocket != null) {
            try {
                webSocket.close(1000, "End of session");
            } catch (Exception unused) {
            }
            this.mWebSocket = null;
        }
    }

    private void sendMessage(int requestID, String message) {
        WebSocket webSocket = this.mWebSocket;
        if (webSocket == null) {
            triggerRequestFailure(requestID, new IllegalStateException("WebSocket connection no longer valid"));
            return;
        }
        try {
            webSocket.send(message);
        } catch (Exception e) {
            triggerRequestFailure(requestID, e);
        }
    }

    private void triggerRequestFailure(int requestID, Throwable cause) {
        JSDebuggerCallback jSDebuggerCallback = this.mCallbacks.get(Integer.valueOf(requestID));
        if (jSDebuggerCallback != null) {
            this.mCallbacks.remove(Integer.valueOf(requestID));
            jSDebuggerCallback.onFailure(cause);
        }
    }

    private void triggerRequestSuccess(int requestID, String response) {
        JSDebuggerCallback jSDebuggerCallback = this.mCallbacks.get(Integer.valueOf(requestID));
        if (jSDebuggerCallback != null) {
            this.mCallbacks.remove(Integer.valueOf(requestID));
            jSDebuggerCallback.onSuccess(response);
        }
    }

    @Override // okhttp3.WebSocketListener
    public void onMessage(WebSocket webSocket, String text) {
        Integer num = null;
        try {
            JsonReader jsonReader = new JsonReader(new StringReader(text));
            jsonReader.beginObject();
            String str = null;
            while (jsonReader.hasNext()) {
                String nextName = jsonReader.nextName();
                if (JsonToken.NULL == jsonReader.peek()) {
                    jsonReader.skipValue();
                } else if ("replyID".equals(nextName)) {
                    num = Integer.valueOf(jsonReader.nextInt());
                } else if ("result".equals(nextName)) {
                    str = jsonReader.nextString();
                } else if ("error".equals(nextName)) {
                    String nextString = jsonReader.nextString();
                    abort(nextString, new JavascriptException(nextString));
                }
            }
            if (num == null) {
                return;
            }
            triggerRequestSuccess(num.intValue(), str);
        } catch (IOException e) {
            if (num != null) {
                triggerRequestFailure(num.intValue(), e);
            } else {
                abort("Parsing response message from websocket failed", e);
            }
        }
    }

    @Override // okhttp3.WebSocketListener
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        abort("Websocket exception", t);
    }

    @Override // okhttp3.WebSocketListener
    public void onOpen(WebSocket webSocket, Response response) {
        this.mWebSocket = webSocket;
        ((JSDebuggerCallback) Assertions.assertNotNull(this.mConnectCallback)).onSuccess(null);
        this.mConnectCallback = null;
    }

    @Override // okhttp3.WebSocketListener
    public void onClosed(WebSocket webSocket, int code, String reason) {
        this.mWebSocket = null;
    }

    private void abort(String message, Throwable cause) {
        FLog.e(TAG, "Error occurred, shutting down websocket connection: " + message, cause);
        closeQuietly();
        JSDebuggerCallback jSDebuggerCallback = this.mConnectCallback;
        if (jSDebuggerCallback != null) {
            jSDebuggerCallback.onFailure(cause);
            this.mConnectCallback = null;
        }
        for (JSDebuggerCallback jSDebuggerCallback2 : this.mCallbacks.values()) {
            jSDebuggerCallback2.onFailure(cause);
        }
        this.mCallbacks.clear();
    }
}
