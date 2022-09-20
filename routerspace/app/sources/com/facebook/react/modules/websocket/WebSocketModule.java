package com.facebook.react.modules.websocket;

import com.facebook.common.logging.FLog;
import com.facebook.common.util.UriUtil;
import com.facebook.fbreact.specs.NativeWebSocketModuleSpec;
import com.facebook.imagepipeline.producers.ProducerContext;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.ReactConstants;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.modules.network.ForwardingCookieHandler;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
@ReactModule(hasConstants = false, name = "WebSocketModule")
/* loaded from: classes.dex */
public final class WebSocketModule extends NativeWebSocketModuleSpec {
    public static final String NAME = "WebSocketModule";
    public static final String TAG = "WebSocketModule";
    private ForwardingCookieHandler mCookieHandler;
    private final Map<Integer, WebSocket> mWebSocketConnections = new ConcurrentHashMap();
    private final Map<Integer, ContentHandler> mContentHandlers = new ConcurrentHashMap();

    /* loaded from: classes.dex */
    public interface ContentHandler {
        void onMessage(String text, WritableMap params);

        void onMessage(ByteString byteString, WritableMap params);
    }

    @Override // com.facebook.fbreact.specs.NativeWebSocketModuleSpec
    public void addListener(String eventName) {
    }

    @Override // com.facebook.react.bridge.NativeModule
    public String getName() {
        return "WebSocketModule";
    }

    @Override // com.facebook.fbreact.specs.NativeWebSocketModuleSpec
    public void removeListeners(double count) {
    }

    public WebSocketModule(ReactApplicationContext context) {
        super(context);
        this.mCookieHandler = new ForwardingCookieHandler(context);
    }

    public void sendEvent(String eventName, WritableMap params) {
        ReactApplicationContext reactApplicationContextIfActiveOrWarn = getReactApplicationContextIfActiveOrWarn();
        if (reactApplicationContextIfActiveOrWarn != null) {
            ((DeviceEventManagerModule.RCTDeviceEventEmitter) reactApplicationContextIfActiveOrWarn.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)).emit(eventName, params);
        }
    }

    public void setContentHandler(final int id, final ContentHandler contentHandler) {
        if (contentHandler != null) {
            this.mContentHandlers.put(Integer.valueOf(id), contentHandler);
        } else {
            this.mContentHandlers.remove(Integer.valueOf(id));
        }
    }

    @Override // com.facebook.fbreact.specs.NativeWebSocketModuleSpec
    public void connect(final String url, final ReadableArray protocols, final ReadableMap options, final double socketID) {
        boolean z;
        final int i = (int) socketID;
        OkHttpClient build = new OkHttpClient.Builder().connectTimeout(10L, TimeUnit.SECONDS).writeTimeout(10L, TimeUnit.SECONDS).readTimeout(0L, TimeUnit.MINUTES).build();
        Request.Builder url2 = new Request.Builder().tag(Integer.valueOf(i)).url(url);
        String cookie = getCookie(url);
        if (cookie != null) {
            url2.addHeader("Cookie", cookie);
        }
        if (options == null || !options.hasKey("headers") || !options.getType("headers").equals(ReadableType.Map)) {
            z = false;
        } else {
            ReadableMap map = options.getMap("headers");
            ReadableMapKeySetIterator keySetIterator = map.keySetIterator();
            z = false;
            while (keySetIterator.hasNextKey()) {
                String nextKey = keySetIterator.nextKey();
                if (ReadableType.String.equals(map.getType(nextKey))) {
                    if (nextKey.equalsIgnoreCase(ProducerContext.ExtraKeys.ORIGIN)) {
                        z = true;
                    }
                    url2.addHeader(nextKey, map.getString(nextKey));
                } else {
                    FLog.w(ReactConstants.TAG, "Ignoring: requested " + nextKey + ", value not a string");
                }
            }
        }
        if (!z) {
            url2.addHeader(ProducerContext.ExtraKeys.ORIGIN, getDefaultOrigin(url));
        }
        if (protocols != null && protocols.size() > 0) {
            StringBuilder sb = new StringBuilder("");
            for (int i2 = 0; i2 < protocols.size(); i2++) {
                String trim = protocols.getString(i2).trim();
                if (!trim.isEmpty() && !trim.contains(",")) {
                    sb.append(trim);
                    sb.append(",");
                }
            }
            if (sb.length() > 0) {
                sb.replace(sb.length() - 1, sb.length(), "");
                url2.addHeader("Sec-WebSocket-Protocol", sb.toString());
            }
        }
        build.newWebSocket(url2.build(), new WebSocketListener() { // from class: com.facebook.react.modules.websocket.WebSocketModule.1
            @Override // okhttp3.WebSocketListener
            public void onOpen(WebSocket webSocket, Response response) {
                WebSocketModule.this.mWebSocketConnections.put(Integer.valueOf(i), webSocket);
                WritableMap createMap = Arguments.createMap();
                createMap.putInt("id", i);
                createMap.putString("protocol", response.header("Sec-WebSocket-Protocol", ""));
                WebSocketModule.this.sendEvent("websocketOpen", createMap);
            }

            @Override // okhttp3.WebSocketListener
            public void onClosing(WebSocket websocket, int code, String reason) {
                websocket.close(code, reason);
            }

            @Override // okhttp3.WebSocketListener
            public void onClosed(WebSocket webSocket, int code, String reason) {
                WritableMap createMap = Arguments.createMap();
                createMap.putInt("id", i);
                createMap.putInt("code", code);
                createMap.putString("reason", reason);
                WebSocketModule.this.sendEvent("websocketClosed", createMap);
            }

            @Override // okhttp3.WebSocketListener
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                WebSocketModule.this.notifyWebSocketFailed(i, t.getMessage());
            }

            @Override // okhttp3.WebSocketListener
            public void onMessage(WebSocket webSocket, String text) {
                WritableMap createMap = Arguments.createMap();
                createMap.putInt("id", i);
                createMap.putString("type", "text");
                ContentHandler contentHandler = (ContentHandler) WebSocketModule.this.mContentHandlers.get(Integer.valueOf(i));
                if (contentHandler != null) {
                    contentHandler.onMessage(text, createMap);
                } else {
                    createMap.putString(UriUtil.DATA_SCHEME, text);
                }
                WebSocketModule.this.sendEvent("websocketMessage", createMap);
            }

            @Override // okhttp3.WebSocketListener
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                WritableMap createMap = Arguments.createMap();
                createMap.putInt("id", i);
                createMap.putString("type", "binary");
                ContentHandler contentHandler = (ContentHandler) WebSocketModule.this.mContentHandlers.get(Integer.valueOf(i));
                if (contentHandler != null) {
                    contentHandler.onMessage(bytes, createMap);
                } else {
                    createMap.putString(UriUtil.DATA_SCHEME, bytes.base64());
                }
                WebSocketModule.this.sendEvent("websocketMessage", createMap);
            }
        });
        build.dispatcher().executorService().shutdown();
    }

    @Override // com.facebook.fbreact.specs.NativeWebSocketModuleSpec
    public void close(double code, String reason, double socketID) {
        int i = (int) socketID;
        WebSocket webSocket = this.mWebSocketConnections.get(Integer.valueOf(i));
        if (webSocket == null) {
            return;
        }
        try {
            webSocket.close((int) code, reason);
            this.mWebSocketConnections.remove(Integer.valueOf(i));
            this.mContentHandlers.remove(Integer.valueOf(i));
        } catch (Exception e) {
            FLog.e(ReactConstants.TAG, "Could not close WebSocket connection for id " + i, e);
        }
    }

    @Override // com.facebook.fbreact.specs.NativeWebSocketModuleSpec
    public void send(String message, double socketID) {
        int i = (int) socketID;
        WebSocket webSocket = this.mWebSocketConnections.get(Integer.valueOf(i));
        if (webSocket == null) {
            WritableMap createMap = Arguments.createMap();
            createMap.putInt("id", i);
            createMap.putString("message", "client is null");
            sendEvent("websocketFailed", createMap);
            WritableMap createMap2 = Arguments.createMap();
            createMap2.putInt("id", i);
            createMap2.putInt("code", 0);
            createMap2.putString("reason", "client is null");
            sendEvent("websocketClosed", createMap2);
            this.mWebSocketConnections.remove(Integer.valueOf(i));
            this.mContentHandlers.remove(Integer.valueOf(i));
            return;
        }
        try {
            webSocket.send(message);
        } catch (Exception e) {
            notifyWebSocketFailed(i, e.getMessage());
        }
    }

    @Override // com.facebook.fbreact.specs.NativeWebSocketModuleSpec
    public void sendBinary(String base64String, double socketID) {
        int i = (int) socketID;
        WebSocket webSocket = this.mWebSocketConnections.get(Integer.valueOf(i));
        if (webSocket == null) {
            WritableMap createMap = Arguments.createMap();
            createMap.putInt("id", i);
            createMap.putString("message", "client is null");
            sendEvent("websocketFailed", createMap);
            WritableMap createMap2 = Arguments.createMap();
            createMap2.putInt("id", i);
            createMap2.putInt("code", 0);
            createMap2.putString("reason", "client is null");
            sendEvent("websocketClosed", createMap2);
            this.mWebSocketConnections.remove(Integer.valueOf(i));
            this.mContentHandlers.remove(Integer.valueOf(i));
            return;
        }
        try {
            webSocket.send(ByteString.decodeBase64(base64String));
        } catch (Exception e) {
            notifyWebSocketFailed(i, e.getMessage());
        }
    }

    public void sendBinary(ByteString byteString, int id) {
        WebSocket webSocket = this.mWebSocketConnections.get(Integer.valueOf(id));
        if (webSocket == null) {
            WritableMap createMap = Arguments.createMap();
            createMap.putInt("id", id);
            createMap.putString("message", "client is null");
            sendEvent("websocketFailed", createMap);
            WritableMap createMap2 = Arguments.createMap();
            createMap2.putInt("id", id);
            createMap2.putInt("code", 0);
            createMap2.putString("reason", "client is null");
            sendEvent("websocketClosed", createMap2);
            this.mWebSocketConnections.remove(Integer.valueOf(id));
            this.mContentHandlers.remove(Integer.valueOf(id));
            return;
        }
        try {
            webSocket.send(byteString);
        } catch (Exception e) {
            notifyWebSocketFailed(id, e.getMessage());
        }
    }

    @Override // com.facebook.fbreact.specs.NativeWebSocketModuleSpec
    public void ping(double socketID) {
        int i = (int) socketID;
        WebSocket webSocket = this.mWebSocketConnections.get(Integer.valueOf(i));
        if (webSocket == null) {
            WritableMap createMap = Arguments.createMap();
            createMap.putInt("id", i);
            createMap.putString("message", "client is null");
            sendEvent("websocketFailed", createMap);
            WritableMap createMap2 = Arguments.createMap();
            createMap2.putInt("id", i);
            createMap2.putInt("code", 0);
            createMap2.putString("reason", "client is null");
            sendEvent("websocketClosed", createMap2);
            this.mWebSocketConnections.remove(Integer.valueOf(i));
            this.mContentHandlers.remove(Integer.valueOf(i));
            return;
        }
        try {
            webSocket.send(ByteString.EMPTY);
        } catch (Exception e) {
            notifyWebSocketFailed(i, e.getMessage());
        }
    }

    public void notifyWebSocketFailed(int id, String message) {
        WritableMap createMap = Arguments.createMap();
        createMap.putInt("id", id);
        createMap.putString("message", message);
        sendEvent("websocketFailed", createMap);
    }

    /* JADX WARN: Removed duplicated region for block: B:25:0x0053  */
    /* JADX WARN: Removed duplicated region for block: B:31:0x007e A[Catch: URISyntaxException -> 0x00bf, TryCatch #0 {URISyntaxException -> 0x00bf, blocks: (B:2:0x0000, B:11:0x002c, B:14:0x0034, B:17:0x003c, B:20:0x0046, B:29:0x005a, B:30:0x006e, B:31:0x007e, B:32:0x008d, B:34:0x0093, B:35:0x00ae), top: B:39:0x0000 }] */
    /* JADX WARN: Removed duplicated region for block: B:34:0x0093 A[Catch: URISyntaxException -> 0x00bf, TryCatch #0 {URISyntaxException -> 0x00bf, blocks: (B:2:0x0000, B:11:0x002c, B:14:0x0034, B:17:0x003c, B:20:0x0046, B:29:0x005a, B:30:0x006e, B:31:0x007e, B:32:0x008d, B:34:0x0093, B:35:0x00ae), top: B:39:0x0000 }] */
    /* JADX WARN: Removed duplicated region for block: B:35:0x00ae A[Catch: URISyntaxException -> 0x00bf, TRY_LEAVE, TryCatch #0 {URISyntaxException -> 0x00bf, blocks: (B:2:0x0000, B:11:0x002c, B:14:0x0034, B:17:0x003c, B:20:0x0046, B:29:0x005a, B:30:0x006e, B:31:0x007e, B:32:0x008d, B:34:0x0093, B:35:0x00ae), top: B:39:0x0000 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static java.lang.String getDefaultOrigin(java.lang.String r12) {
        /*
            java.lang.String r0 = ""
            java.net.URI r1 = new java.net.URI     // Catch: java.net.URISyntaxException -> Lbf
            r1.<init>(r12)     // Catch: java.net.URISyntaxException -> Lbf
            java.lang.String r2 = r1.getScheme()     // Catch: java.net.URISyntaxException -> Lbf
            int r3 = r2.hashCode()     // Catch: java.net.URISyntaxException -> Lbf
            r4 = 3804(0xedc, float:5.33E-42)
            java.lang.String r5 = "https"
            java.lang.String r6 = "http"
            r7 = -1
            r8 = 0
            r9 = 3
            r10 = 2
            r11 = 1
            if (r3 == r4) goto L46
            r4 = 118039(0x1cd17, float:1.65408E-40)
            if (r3 == r4) goto L3c
            r4 = 3213448(0x310888, float:4.503E-39)
            if (r3 == r4) goto L34
            r4 = 99617003(0x5f008eb, float:2.2572767E-35)
            if (r3 == r4) goto L2c
            goto L50
        L2c:
            boolean r2 = r2.equals(r5)     // Catch: java.net.URISyntaxException -> Lbf
            if (r2 == 0) goto L50
            r2 = 3
            goto L51
        L34:
            boolean r2 = r2.equals(r6)     // Catch: java.net.URISyntaxException -> Lbf
            if (r2 == 0) goto L50
            r2 = 2
            goto L51
        L3c:
            java.lang.String r3 = "wss"
            boolean r2 = r2.equals(r3)     // Catch: java.net.URISyntaxException -> Lbf
            if (r2 == 0) goto L50
            r2 = 0
            goto L51
        L46:
            java.lang.String r3 = "ws"
            boolean r2 = r2.equals(r3)     // Catch: java.net.URISyntaxException -> Lbf
            if (r2 == 0) goto L50
            r2 = 1
            goto L51
        L50:
            r2 = -1
        L51:
            if (r2 == 0) goto L7e
            if (r2 == r11) goto L6e
            if (r2 == r10) goto L5a
            if (r2 == r9) goto L5a
            goto L8d
        L5a:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch: java.net.URISyntaxException -> Lbf
            r2.<init>()     // Catch: java.net.URISyntaxException -> Lbf
            r2.append(r0)     // Catch: java.net.URISyntaxException -> Lbf
            java.lang.String r0 = r1.getScheme()     // Catch: java.net.URISyntaxException -> Lbf
            r2.append(r0)     // Catch: java.net.URISyntaxException -> Lbf
            java.lang.String r0 = r2.toString()     // Catch: java.net.URISyntaxException -> Lbf
            goto L8d
        L6e:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch: java.net.URISyntaxException -> Lbf
            r2.<init>()     // Catch: java.net.URISyntaxException -> Lbf
            r2.append(r0)     // Catch: java.net.URISyntaxException -> Lbf
            r2.append(r6)     // Catch: java.net.URISyntaxException -> Lbf
            java.lang.String r0 = r2.toString()     // Catch: java.net.URISyntaxException -> Lbf
            goto L8d
        L7e:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch: java.net.URISyntaxException -> Lbf
            r2.<init>()     // Catch: java.net.URISyntaxException -> Lbf
            r2.append(r0)     // Catch: java.net.URISyntaxException -> Lbf
            r2.append(r5)     // Catch: java.net.URISyntaxException -> Lbf
            java.lang.String r0 = r2.toString()     // Catch: java.net.URISyntaxException -> Lbf
        L8d:
            int r2 = r1.getPort()     // Catch: java.net.URISyntaxException -> Lbf
            if (r2 == r7) goto Lae
            java.lang.String r2 = "%s://%s:%s"
            java.lang.Object[] r3 = new java.lang.Object[r9]     // Catch: java.net.URISyntaxException -> Lbf
            r3[r8] = r0     // Catch: java.net.URISyntaxException -> Lbf
            java.lang.String r0 = r1.getHost()     // Catch: java.net.URISyntaxException -> Lbf
            r3[r11] = r0     // Catch: java.net.URISyntaxException -> Lbf
            int r0 = r1.getPort()     // Catch: java.net.URISyntaxException -> Lbf
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch: java.net.URISyntaxException -> Lbf
            r3[r10] = r0     // Catch: java.net.URISyntaxException -> Lbf
            java.lang.String r12 = java.lang.String.format(r2, r3)     // Catch: java.net.URISyntaxException -> Lbf
            goto Lbe
        Lae:
            java.lang.String r2 = "%s://%s"
            java.lang.Object[] r3 = new java.lang.Object[r10]     // Catch: java.net.URISyntaxException -> Lbf
            r3[r8] = r0     // Catch: java.net.URISyntaxException -> Lbf
            java.lang.String r0 = r1.getHost()     // Catch: java.net.URISyntaxException -> Lbf
            r3[r11] = r0     // Catch: java.net.URISyntaxException -> Lbf
            java.lang.String r12 = java.lang.String.format(r2, r3)     // Catch: java.net.URISyntaxException -> Lbf
        Lbe:
            return r12
        Lbf:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Unable to set "
            r1.append(r2)
            r1.append(r12)
            java.lang.String r12 = " as default origin header"
            r1.append(r12)
            java.lang.String r12 = r1.toString()
            r0.<init>(r12)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.facebook.react.modules.websocket.WebSocketModule.getDefaultOrigin(java.lang.String):java.lang.String");
    }

    private String getCookie(String uri) {
        try {
            List<String> list = this.mCookieHandler.get(new URI(getDefaultOrigin(uri)), new HashMap()).get("Cookie");
            if (list != null && !list.isEmpty()) {
                return list.get(0);
            }
            return null;
        } catch (IOException | URISyntaxException unused) {
            throw new IllegalArgumentException("Unable to get cookie from " + uri);
        }
    }
}
