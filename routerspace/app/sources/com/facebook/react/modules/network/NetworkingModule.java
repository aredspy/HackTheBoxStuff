package com.facebook.react.modules.network;

import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import com.facebook.common.logging.FLog;
import com.facebook.fbreact.specs.NativeNetworkingAndroidSpec;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.GuardedAsyncTask;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.StandardCharsets;
import com.facebook.react.common.network.OkHttpCallUtil;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.CookieJar;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.ByteString;
import okio.GzipSource;
import okio.Okio;
@ReactModule(name = NetworkingModule.NAME)
/* loaded from: classes.dex */
public final class NetworkingModule extends NativeNetworkingAndroidSpec {
    private static final int CHUNK_TIMEOUT_NS = 100000000;
    private static final String CONTENT_ENCODING_HEADER_NAME = "content-encoding";
    private static final String CONTENT_TYPE_HEADER_NAME = "content-type";
    private static final int MAX_CHUNK_SIZE_BETWEEN_FLUSHES = 8192;
    public static final String NAME = "Networking";
    private static final String REQUEST_BODY_KEY_BASE64 = "base64";
    private static final String REQUEST_BODY_KEY_FORMDATA = "formData";
    private static final String REQUEST_BODY_KEY_STRING = "string";
    private static final String REQUEST_BODY_KEY_URI = "uri";
    private static final String TAG = "NetworkingModule";
    private static final String USER_AGENT_HEADER_NAME = "user-agent";
    private static CustomClientBuilder customClientBuilder;
    private final OkHttpClient mClient;
    private final ForwardingCookieHandler mCookieHandler;
    private final CookieJarContainer mCookieJarContainer;
    private final String mDefaultUserAgent;
    private final List<RequestBodyHandler> mRequestBodyHandlers;
    private final Set<Integer> mRequestIds;
    private final List<ResponseHandler> mResponseHandlers;
    private boolean mShuttingDown;
    private final List<UriHandler> mUriHandlers;

    /* loaded from: classes.dex */
    public interface CustomClientBuilder {
        void apply(OkHttpClient.Builder builder);
    }

    /* loaded from: classes.dex */
    public interface RequestBodyHandler {
        boolean supports(ReadableMap map);

        RequestBody toRequestBody(ReadableMap map, String contentType);
    }

    /* loaded from: classes.dex */
    public interface ResponseHandler {
        boolean supports(String responseType);

        WritableMap toResponseData(ResponseBody body) throws IOException;
    }

    /* loaded from: classes.dex */
    public interface UriHandler {
        WritableMap fetch(Uri uri) throws IOException;

        boolean supports(Uri uri, String responseType);
    }

    public static boolean shouldDispatch(long now, long last) {
        return last + 100000000 < now;
    }

    @Override // com.facebook.fbreact.specs.NativeNetworkingAndroidSpec
    public void addListener(String eventName) {
    }

    @Override // com.facebook.react.bridge.NativeModule
    public String getName() {
        return NAME;
    }

    @Override // com.facebook.fbreact.specs.NativeNetworkingAndroidSpec
    public void removeListeners(double count) {
    }

    NetworkingModule(ReactApplicationContext reactContext, String defaultUserAgent, OkHttpClient client, List<NetworkInterceptorCreator> networkInterceptorCreators) {
        super(reactContext);
        this.mRequestBodyHandlers = new ArrayList();
        this.mUriHandlers = new ArrayList();
        this.mResponseHandlers = new ArrayList();
        if (networkInterceptorCreators != null) {
            OkHttpClient.Builder newBuilder = client.newBuilder();
            for (NetworkInterceptorCreator networkInterceptorCreator : networkInterceptorCreators) {
                newBuilder.addNetworkInterceptor(networkInterceptorCreator.create());
            }
            client = newBuilder.build();
        }
        this.mClient = client;
        this.mCookieHandler = new ForwardingCookieHandler(reactContext);
        this.mCookieJarContainer = (CookieJarContainer) client.cookieJar();
        this.mShuttingDown = false;
        this.mDefaultUserAgent = defaultUserAgent;
        this.mRequestIds = new HashSet();
    }

    NetworkingModule(ReactApplicationContext context, String defaultUserAgent, OkHttpClient client) {
        this(context, defaultUserAgent, client, null);
    }

    public NetworkingModule(final ReactApplicationContext context) {
        this(context, null, OkHttpClientProvider.createClient(context), null);
    }

    public NetworkingModule(ReactApplicationContext context, List<NetworkInterceptorCreator> networkInterceptorCreators) {
        this(context, null, OkHttpClientProvider.createClient(context), networkInterceptorCreators);
    }

    public NetworkingModule(ReactApplicationContext context, String defaultUserAgent) {
        this(context, defaultUserAgent, OkHttpClientProvider.createClient(context), null);
    }

    public static void setCustomClientBuilder(CustomClientBuilder ccb) {
        customClientBuilder = ccb;
    }

    private static void applyCustomBuilder(OkHttpClient.Builder builder) {
        CustomClientBuilder customClientBuilder2 = customClientBuilder;
        if (customClientBuilder2 != null) {
            customClientBuilder2.apply(builder);
        }
    }

    @Override // com.facebook.react.bridge.BaseJavaModule, com.facebook.react.bridge.NativeModule, com.facebook.react.turbomodule.core.interfaces.TurboModule
    public void initialize() {
        this.mCookieJarContainer.setCookieJar(new JavaNetCookieJar(this.mCookieHandler));
    }

    @Override // com.facebook.react.bridge.BaseJavaModule, com.facebook.react.bridge.NativeModule, com.facebook.react.turbomodule.core.interfaces.TurboModule
    public void invalidate() {
        this.mShuttingDown = true;
        cancelAllRequests();
        this.mCookieHandler.destroy();
        this.mCookieJarContainer.removeCookieJar();
        this.mRequestBodyHandlers.clear();
        this.mResponseHandlers.clear();
        this.mUriHandlers.clear();
    }

    public void addUriHandler(UriHandler handler) {
        this.mUriHandlers.add(handler);
    }

    public void addRequestBodyHandler(RequestBodyHandler handler) {
        this.mRequestBodyHandlers.add(handler);
    }

    public void addResponseHandler(ResponseHandler handler) {
        this.mResponseHandlers.add(handler);
    }

    public void removeUriHandler(UriHandler handler) {
        this.mUriHandlers.remove(handler);
    }

    public void removeRequestBodyHandler(RequestBodyHandler handler) {
        this.mRequestBodyHandlers.remove(handler);
    }

    public void removeResponseHandler(ResponseHandler handler) {
        this.mResponseHandlers.remove(handler);
    }

    @Override // com.facebook.fbreact.specs.NativeNetworkingAndroidSpec
    public void sendRequest(String method, String url, double requestIdAsDouble, ReadableArray headers, ReadableMap data, String responseType, boolean useIncrementalUpdates, double timeoutAsDouble, boolean withCredentials) {
        int i = (int) requestIdAsDouble;
        try {
            sendRequestInternal(method, url, i, headers, data, responseType, useIncrementalUpdates, (int) timeoutAsDouble, withCredentials);
        } catch (Throwable th) {
            FLog.e(TAG, "Failed to send url request: " + url, th);
            DeviceEventManagerModule.RCTDeviceEventEmitter eventEmitter = getEventEmitter("sendRequest error");
            if (eventEmitter == null) {
                return;
            }
            ResponseUtil.onRequestError(eventEmitter, i, th.getMessage(), th);
        }
    }

    public void sendRequestInternal(String method, String url, final int requestId, ReadableArray headers, ReadableMap data, final String responseType, final boolean useIncrementalUpdates, int timeout, boolean withCredentials) {
        RequestBodyHandler requestBodyHandler;
        RequestBody requestBody;
        final DeviceEventManagerModule.RCTDeviceEventEmitter eventEmitter = getEventEmitter("sendRequestInternal");
        try {
            Uri parse = Uri.parse(url);
            for (UriHandler uriHandler : this.mUriHandlers) {
                if (uriHandler.supports(parse, responseType)) {
                    ResponseUtil.onDataReceived(eventEmitter, requestId, uriHandler.fetch(parse));
                    ResponseUtil.onRequestSuccess(eventEmitter, requestId);
                    return;
                }
            }
            try {
                Request.Builder url2 = new Request.Builder().url(url);
                if (requestId != 0) {
                    url2.tag(Integer.valueOf(requestId));
                }
                OkHttpClient.Builder newBuilder = this.mClient.newBuilder();
                applyCustomBuilder(newBuilder);
                if (!withCredentials) {
                    newBuilder.cookieJar(CookieJar.NO_COOKIES);
                }
                if (useIncrementalUpdates) {
                    newBuilder.addNetworkInterceptor(new Interceptor() { // from class: com.facebook.react.modules.network.NetworkingModule.1
                        @Override // okhttp3.Interceptor
                        public Response intercept(Interceptor.Chain chain) throws IOException {
                            Response proceed = chain.proceed(chain.request());
                            return proceed.newBuilder().body(new ProgressResponseBody(proceed.body(), new ProgressListener() { // from class: com.facebook.react.modules.network.NetworkingModule.1.1
                                long last = System.nanoTime();

                                @Override // com.facebook.react.modules.network.ProgressListener
                                public void onProgress(long bytesWritten, long contentLength, boolean done) {
                                    long nanoTime = System.nanoTime();
                                    if ((done || NetworkingModule.shouldDispatch(nanoTime, this.last)) && !responseType.equals("text")) {
                                        ResponseUtil.onDataReceivedProgress(eventEmitter, requestId, bytesWritten, contentLength);
                                        this.last = nanoTime;
                                    }
                                }
                            })).build();
                        }
                    });
                }
                if (timeout != this.mClient.connectTimeoutMillis()) {
                    newBuilder.connectTimeout(timeout, TimeUnit.MILLISECONDS);
                }
                OkHttpClient build = newBuilder.build();
                Headers extractHeaders = extractHeaders(headers, data);
                if (extractHeaders == null) {
                    ResponseUtil.onRequestError(eventEmitter, requestId, "Unrecognized headers format", null);
                    return;
                }
                String str = extractHeaders.get(CONTENT_TYPE_HEADER_NAME);
                String str2 = extractHeaders.get(CONTENT_ENCODING_HEADER_NAME);
                url2.headers(extractHeaders);
                if (data != null) {
                    Iterator<RequestBodyHandler> it = this.mRequestBodyHandlers.iterator();
                    while (it.hasNext()) {
                        requestBodyHandler = it.next();
                        if (requestBodyHandler.supports(data)) {
                            break;
                        }
                    }
                }
                requestBodyHandler = null;
                if (data == null || method.toLowerCase().equals("get") || method.toLowerCase().equals("head")) {
                    requestBody = RequestBodyUtil.getEmptyBody(method);
                } else if (requestBodyHandler != null) {
                    requestBody = requestBodyHandler.toRequestBody(data, str);
                } else if (data.hasKey(REQUEST_BODY_KEY_STRING)) {
                    if (str == null) {
                        ResponseUtil.onRequestError(eventEmitter, requestId, "Payload is set but no content-type header specified", null);
                        return;
                    }
                    String string = data.getString(REQUEST_BODY_KEY_STRING);
                    MediaType parse2 = MediaType.parse(str);
                    if (RequestBodyUtil.isGzipEncoding(str2)) {
                        requestBody = RequestBodyUtil.createGzip(parse2, string);
                        if (requestBody == null) {
                            ResponseUtil.onRequestError(eventEmitter, requestId, "Failed to gzip request body", null);
                            return;
                        }
                    } else {
                        Charset charset = StandardCharsets.UTF_8;
                        if (parse2 != null) {
                            charset = parse2.charset(charset);
                        }
                        requestBody = RequestBody.create(parse2, string.getBytes(charset));
                    }
                } else if (data.hasKey(REQUEST_BODY_KEY_BASE64)) {
                    if (str == null) {
                        ResponseUtil.onRequestError(eventEmitter, requestId, "Payload is set but no content-type header specified", null);
                        return;
                    }
                    requestBody = RequestBody.create(MediaType.parse(str), ByteString.decodeBase64(data.getString(REQUEST_BODY_KEY_BASE64)));
                } else if (data.hasKey(REQUEST_BODY_KEY_URI)) {
                    if (str == null) {
                        ResponseUtil.onRequestError(eventEmitter, requestId, "Payload is set but no content-type header specified", null);
                        return;
                    }
                    String string2 = data.getString(REQUEST_BODY_KEY_URI);
                    InputStream fileInputStream = RequestBodyUtil.getFileInputStream(getReactApplicationContext(), string2);
                    if (fileInputStream == null) {
                        ResponseUtil.onRequestError(eventEmitter, requestId, "Could not retrieve file for uri " + string2, null);
                        return;
                    }
                    requestBody = RequestBodyUtil.create(MediaType.parse(str), fileInputStream);
                } else if (data.hasKey(REQUEST_BODY_KEY_FORMDATA)) {
                    if (str == null) {
                        str = "multipart/form-data";
                    }
                    MultipartBody.Builder constructMultipartBody = constructMultipartBody(data.getArray(REQUEST_BODY_KEY_FORMDATA), str, requestId);
                    if (constructMultipartBody == null) {
                        return;
                    }
                    requestBody = constructMultipartBody.build();
                } else {
                    requestBody = RequestBodyUtil.getEmptyBody(method);
                }
                url2.method(method, wrapRequestBodyWithProgressEmitter(requestBody, eventEmitter, requestId));
                addRequest(requestId);
                build.newCall(url2.build()).enqueue(new Callback() { // from class: com.facebook.react.modules.network.NetworkingModule.2
                    @Override // okhttp3.Callback
                    public void onFailure(Call call, IOException e) {
                        String str3;
                        if (NetworkingModule.this.mShuttingDown) {
                            return;
                        }
                        NetworkingModule.this.removeRequest(requestId);
                        if (e.getMessage() != null) {
                            str3 = e.getMessage();
                        } else {
                            str3 = "Error while executing request: " + e.getClass().getSimpleName();
                        }
                        ResponseUtil.onRequestError(eventEmitter, requestId, str3, e);
                    }

                    @Override // okhttp3.Callback
                    public void onResponse(Call call, Response response) throws IOException {
                        if (NetworkingModule.this.mShuttingDown) {
                            return;
                        }
                        NetworkingModule.this.removeRequest(requestId);
                        ResponseUtil.onResponseReceived(eventEmitter, requestId, response.code(), NetworkingModule.translateHeaders(response.headers()), response.request().url().toString());
                        try {
                            ResponseBody body = response.body();
                            if ("gzip".equalsIgnoreCase(response.header("Content-Encoding")) && body != null) {
                                GzipSource gzipSource = new GzipSource(body.source());
                                String header = response.header("Content-Type");
                                body = ResponseBody.create(header != null ? MediaType.parse(header) : null, -1L, Okio.buffer(gzipSource));
                            }
                            for (ResponseHandler responseHandler : NetworkingModule.this.mResponseHandlers) {
                                if (responseHandler.supports(responseType)) {
                                    ResponseUtil.onDataReceived(eventEmitter, requestId, responseHandler.toResponseData(body));
                                    ResponseUtil.onRequestSuccess(eventEmitter, requestId);
                                    return;
                                }
                            }
                            if (useIncrementalUpdates && responseType.equals("text")) {
                                NetworkingModule.this.readWithProgress(eventEmitter, requestId, body);
                                ResponseUtil.onRequestSuccess(eventEmitter, requestId);
                                return;
                            }
                            String str3 = "";
                            if (responseType.equals("text")) {
                                try {
                                    str3 = body.string();
                                } catch (IOException e) {
                                    if (!response.request().method().equalsIgnoreCase("HEAD")) {
                                        ResponseUtil.onRequestError(eventEmitter, requestId, e.getMessage(), e);
                                    }
                                }
                            } else if (responseType.equals(NetworkingModule.REQUEST_BODY_KEY_BASE64)) {
                                str3 = Base64.encodeToString(body.bytes(), 2);
                            }
                            ResponseUtil.onDataReceived(eventEmitter, requestId, str3);
                            ResponseUtil.onRequestSuccess(eventEmitter, requestId);
                        } catch (IOException e2) {
                            ResponseUtil.onRequestError(eventEmitter, requestId, e2.getMessage(), e2);
                        }
                    }
                });
            } catch (Exception e) {
                ResponseUtil.onRequestError(eventEmitter, requestId, e.getMessage(), null);
            }
        } catch (IOException e2) {
            ResponseUtil.onRequestError(eventEmitter, requestId, e2.getMessage(), e2);
        }
    }

    private RequestBody wrapRequestBodyWithProgressEmitter(final RequestBody requestBody, final DeviceEventManagerModule.RCTDeviceEventEmitter eventEmitter, final int requestId) {
        if (requestBody == null) {
            return null;
        }
        return RequestBodyUtil.createProgressRequest(requestBody, new ProgressListener() { // from class: com.facebook.react.modules.network.NetworkingModule.3
            long last = System.nanoTime();

            @Override // com.facebook.react.modules.network.ProgressListener
            public void onProgress(long bytesWritten, long contentLength, boolean done) {
                long nanoTime = System.nanoTime();
                if (done || NetworkingModule.shouldDispatch(nanoTime, this.last)) {
                    ResponseUtil.onDataSend(eventEmitter, requestId, bytesWritten, contentLength);
                    this.last = nanoTime;
                }
            }
        });
    }

    public void readWithProgress(DeviceEventManagerModule.RCTDeviceEventEmitter eventEmitter, int requestId, ResponseBody responseBody) throws IOException {
        long j;
        long j2 = -1;
        try {
            ProgressResponseBody progressResponseBody = (ProgressResponseBody) responseBody;
            j = progressResponseBody.totalBytesRead();
            try {
                j2 = progressResponseBody.contentLength();
            } catch (ClassCastException unused) {
            }
        } catch (ClassCastException unused2) {
            j = -1;
        }
        ProgressiveStringDecoder progressiveStringDecoder = new ProgressiveStringDecoder(responseBody.contentType() == null ? StandardCharsets.UTF_8 : responseBody.contentType().charset(StandardCharsets.UTF_8));
        InputStream byteStream = responseBody.byteStream();
        try {
            byte[] bArr = new byte[8192];
            while (true) {
                int read = byteStream.read(bArr);
                if (read == -1) {
                    return;
                }
                ResponseUtil.onIncrementalDataReceived(eventEmitter, requestId, progressiveStringDecoder.decodeNext(bArr, read), j, j2);
            }
        } finally {
            byteStream.close();
        }
    }

    private synchronized void addRequest(int requestId) {
        this.mRequestIds.add(Integer.valueOf(requestId));
    }

    public synchronized void removeRequest(int requestId) {
        this.mRequestIds.remove(Integer.valueOf(requestId));
    }

    private synchronized void cancelAllRequests() {
        for (Integer num : this.mRequestIds) {
            cancelRequest(num.intValue());
        }
        this.mRequestIds.clear();
    }

    public static WritableMap translateHeaders(Headers headers) {
        Bundle bundle = new Bundle();
        for (int i = 0; i < headers.size(); i++) {
            String name = headers.name(i);
            if (bundle.containsKey(name)) {
                bundle.putString(name, bundle.getString(name) + ", " + headers.value(i));
            } else {
                bundle.putString(name, headers.value(i));
            }
        }
        return Arguments.fromBundle(bundle);
    }

    @Override // com.facebook.fbreact.specs.NativeNetworkingAndroidSpec
    public void abortRequest(double requestIdAsDouble) {
        int i = (int) requestIdAsDouble;
        cancelRequest(i);
        removeRequest(i);
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [com.facebook.react.modules.network.NetworkingModule$4] */
    private void cancelRequest(final int requestId) {
        new GuardedAsyncTask<Void, Void>(getReactApplicationContext()) { // from class: com.facebook.react.modules.network.NetworkingModule.4
            public void doInBackgroundGuarded(Void... params) {
                OkHttpCallUtil.cancelTag(NetworkingModule.this.mClient, Integer.valueOf(requestId));
            }
        }.execute(new Void[0]);
    }

    @Override // com.facebook.fbreact.specs.NativeNetworkingAndroidSpec
    @ReactMethod
    public void clearCookies(com.facebook.react.bridge.Callback callback) {
        this.mCookieHandler.clearCookies(callback);
    }

    private MultipartBody.Builder constructMultipartBody(ReadableArray body, String contentType, int requestId) {
        MediaType mediaType;
        DeviceEventManagerModule.RCTDeviceEventEmitter eventEmitter = getEventEmitter("constructMultipartBody");
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MediaType.parse(contentType));
        int size = body.size();
        for (int i = 0; i < size; i++) {
            ReadableMap map = body.getMap(i);
            Headers extractHeaders = extractHeaders(map.getArray("headers"), null);
            if (extractHeaders == null) {
                ResponseUtil.onRequestError(eventEmitter, requestId, "Missing or invalid header format for FormData part.", null);
                return null;
            }
            String str = extractHeaders.get(CONTENT_TYPE_HEADER_NAME);
            if (str != null) {
                mediaType = MediaType.parse(str);
                extractHeaders = extractHeaders.newBuilder().removeAll(CONTENT_TYPE_HEADER_NAME).build();
            } else {
                mediaType = null;
            }
            if (map.hasKey(REQUEST_BODY_KEY_STRING)) {
                builder.addPart(extractHeaders, RequestBody.create(mediaType, map.getString(REQUEST_BODY_KEY_STRING)));
            } else if (!map.hasKey(REQUEST_BODY_KEY_URI)) {
                ResponseUtil.onRequestError(eventEmitter, requestId, "Unrecognized FormData part.", null);
            } else if (mediaType == null) {
                ResponseUtil.onRequestError(eventEmitter, requestId, "Binary FormData part needs a content-type header.", null);
                return null;
            } else {
                String string = map.getString(REQUEST_BODY_KEY_URI);
                InputStream fileInputStream = RequestBodyUtil.getFileInputStream(getReactApplicationContext(), string);
                if (fileInputStream == null) {
                    ResponseUtil.onRequestError(eventEmitter, requestId, "Could not retrieve file for uri " + string, null);
                    return null;
                }
                builder.addPart(extractHeaders, RequestBodyUtil.create(mediaType, fileInputStream));
            }
        }
        return builder;
    }

    private Headers extractHeaders(ReadableArray headersArray, ReadableMap requestData) {
        String str;
        if (headersArray == null) {
            return null;
        }
        Headers.Builder builder = new Headers.Builder();
        int size = headersArray.size();
        boolean z = false;
        for (int i = 0; i < size; i++) {
            ReadableArray array = headersArray.getArray(i);
            if (array != null && array.size() == 2) {
                String stripHeaderName = HeaderUtil.stripHeaderName(array.getString(0));
                String stripHeaderValue = HeaderUtil.stripHeaderValue(array.getString(1));
                if (stripHeaderName != null && stripHeaderValue != null) {
                    builder.add(stripHeaderName, stripHeaderValue);
                }
            }
            return null;
        }
        if (builder.get(USER_AGENT_HEADER_NAME) == null && (str = this.mDefaultUserAgent) != null) {
            builder.add(USER_AGENT_HEADER_NAME, str);
        }
        if (requestData != null && requestData.hasKey(REQUEST_BODY_KEY_STRING)) {
            z = true;
        }
        if (!z) {
            builder.removeAll(CONTENT_ENCODING_HEADER_NAME);
        }
        return builder.build();
    }

    private DeviceEventManagerModule.RCTDeviceEventEmitter getEventEmitter(String reason) {
        if (getReactApplicationContextIfActiveOrWarn() != null) {
            return (DeviceEventManagerModule.RCTDeviceEventEmitter) getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class);
        }
        return null;
    }
}
