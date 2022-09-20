package com.facebook.react.devsupport;

import android.content.Context;
import android.os.AsyncTask;
import com.facebook.common.logging.FLog;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.common.ReactConstants;
import com.facebook.react.devsupport.BundleDownloader;
import com.facebook.react.devsupport.InspectorPackagerConnection;
import com.facebook.react.devsupport.interfaces.DevBundleDownloadListener;
import com.facebook.react.devsupport.interfaces.PackagerStatusCallback;
import com.facebook.react.devsupport.interfaces.StackFrame;
import com.facebook.react.modules.systeminfo.AndroidInfoHelpers;
import com.facebook.react.packagerconnection.FileIoHandler;
import com.facebook.react.packagerconnection.JSPackagerClient;
import com.facebook.react.packagerconnection.NotificationOnlyHandler;
import com.facebook.react.packagerconnection.ReconnectingWebSocket;
import com.facebook.react.packagerconnection.RequestHandler;
import com.facebook.react.packagerconnection.RequestOnlyHandler;
import com.facebook.react.packagerconnection.Responder;
import com.facebook.react.util.RNLog;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Okio;
import okio.Sink;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes.dex */
public class DevServerHelper {
    private static final String DEBUGGER_MSG_DISABLE = "{ \"id\":1,\"method\":\"Debugger.disable\" }";
    private static final int HTTP_CONNECT_TIMEOUT_MS = 5000;
    public static final String RELOAD_APP_EXTRA_JS_PROXY = "jsproxy";
    private final BundleDownloader mBundleDownloader;
    private InspectorPackagerConnection.BundleStatusProvider mBundlerStatusProvider;
    private final OkHttpClient mClient;
    private InspectorPackagerConnection mInspectorPackagerConnection;
    private final String mPackageName;
    private JSPackagerClient mPackagerClient;
    private final PackagerStatusCheck mPackagerStatusCheck;
    private final DevInternalSettings mSettings;

    /* loaded from: classes.dex */
    public interface OnServerContentChangeListener {
        void onServerContentChanged();
    }

    /* loaded from: classes.dex */
    public interface PackagerCommandListener {
        Map<String, RequestHandler> customCommandHandlers();

        void onCaptureHeapCommand(final Responder responder);

        void onPackagerConnected();

        void onPackagerDevMenuCommand();

        void onPackagerDisconnected();

        void onPackagerReloadCommand();
    }

    /* loaded from: classes.dex */
    public interface PackagerCustomCommandProvider {
    }

    /* loaded from: classes.dex */
    public interface SymbolicationListener {
        void onSymbolicationComplete(Iterable<StackFrame> stackFrames);
    }

    /* loaded from: classes.dex */
    public enum BundleType {
        BUNDLE("bundle"),
        MAP("map");
        
        private final String mTypeID;

        BundleType(String typeID) {
            this.mTypeID = typeID;
        }

        public String typeID() {
            return this.mTypeID;
        }
    }

    public DevServerHelper(DevInternalSettings settings, String packageName, InspectorPackagerConnection.BundleStatusProvider bundleStatusProvider) {
        this.mSettings = settings;
        this.mBundlerStatusProvider = bundleStatusProvider;
        OkHttpClient build = new OkHttpClient.Builder().connectTimeout(5000L, TimeUnit.MILLISECONDS).readTimeout(0L, TimeUnit.MILLISECONDS).writeTimeout(0L, TimeUnit.MILLISECONDS).build();
        this.mClient = build;
        this.mBundleDownloader = new BundleDownloader(build);
        this.mPackagerStatusCheck = new PackagerStatusCheck(build);
        this.mPackageName = packageName;
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [com.facebook.react.devsupport.DevServerHelper$1] */
    public void openPackagerConnection(final String clientId, final PackagerCommandListener commandListener) {
        if (this.mPackagerClient != null) {
            FLog.w(ReactConstants.TAG, "Packager connection already open, nooping.");
        } else {
            new AsyncTask<Void, Void, Void>() { // from class: com.facebook.react.devsupport.DevServerHelper.1
                public Void doInBackground(Void... backgroundParams) {
                    HashMap hashMap = new HashMap();
                    hashMap.put("reload", new NotificationOnlyHandler() { // from class: com.facebook.react.devsupport.DevServerHelper.1.1
                        @Override // com.facebook.react.packagerconnection.NotificationOnlyHandler, com.facebook.react.packagerconnection.RequestHandler
                        public void onNotification(Object params) {
                            commandListener.onPackagerReloadCommand();
                        }
                    });
                    hashMap.put("devMenu", new NotificationOnlyHandler() { // from class: com.facebook.react.devsupport.DevServerHelper.1.2
                        @Override // com.facebook.react.packagerconnection.NotificationOnlyHandler, com.facebook.react.packagerconnection.RequestHandler
                        public void onNotification(Object params) {
                            commandListener.onPackagerDevMenuCommand();
                        }
                    });
                    hashMap.put("captureHeap", new RequestOnlyHandler() { // from class: com.facebook.react.devsupport.DevServerHelper.1.3
                        @Override // com.facebook.react.packagerconnection.RequestOnlyHandler, com.facebook.react.packagerconnection.RequestHandler
                        public void onRequest(Object params, Responder responder) {
                            commandListener.onCaptureHeapCommand(responder);
                        }
                    });
                    Map<String, RequestHandler> customCommandHandlers = commandListener.customCommandHandlers();
                    if (customCommandHandlers != null) {
                        hashMap.putAll(customCommandHandlers);
                    }
                    hashMap.putAll(new FileIoHandler().handlers());
                    ReconnectingWebSocket.ConnectionCallback connectionCallback = new ReconnectingWebSocket.ConnectionCallback() { // from class: com.facebook.react.devsupport.DevServerHelper.1.4
                        @Override // com.facebook.react.packagerconnection.ReconnectingWebSocket.ConnectionCallback
                        public void onConnected() {
                            commandListener.onPackagerConnected();
                        }

                        @Override // com.facebook.react.packagerconnection.ReconnectingWebSocket.ConnectionCallback
                        public void onDisconnected() {
                            commandListener.onPackagerDisconnected();
                        }
                    };
                    DevServerHelper.this.mPackagerClient = new JSPackagerClient(clientId, DevServerHelper.this.mSettings.getPackagerConnectionSettings(), hashMap, connectionCallback);
                    DevServerHelper.this.mPackagerClient.init();
                    return null;
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        }
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [com.facebook.react.devsupport.DevServerHelper$2] */
    public void closePackagerConnection() {
        new AsyncTask<Void, Void, Void>() { // from class: com.facebook.react.devsupport.DevServerHelper.2
            public Void doInBackground(Void... params) {
                if (DevServerHelper.this.mPackagerClient != null) {
                    DevServerHelper.this.mPackagerClient.close();
                    DevServerHelper.this.mPackagerClient = null;
                }
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [com.facebook.react.devsupport.DevServerHelper$3] */
    public void openInspectorConnection() {
        if (this.mInspectorPackagerConnection != null) {
            FLog.w(ReactConstants.TAG, "Inspector connection already open, nooping.");
        } else {
            new AsyncTask<Void, Void, Void>() { // from class: com.facebook.react.devsupport.DevServerHelper.3
                public Void doInBackground(Void... params) {
                    DevServerHelper devServerHelper = DevServerHelper.this;
                    devServerHelper.mInspectorPackagerConnection = new InspectorPackagerConnection(devServerHelper.getInspectorDeviceUrl(), DevServerHelper.this.mPackageName, DevServerHelper.this.mBundlerStatusProvider);
                    DevServerHelper.this.mInspectorPackagerConnection.connect();
                    return null;
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        }
    }

    public void disableDebugger() {
        InspectorPackagerConnection inspectorPackagerConnection = this.mInspectorPackagerConnection;
        if (inspectorPackagerConnection != null) {
            inspectorPackagerConnection.sendEventToAllConnections(DEBUGGER_MSG_DISABLE);
        }
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [com.facebook.react.devsupport.DevServerHelper$4] */
    public void closeInspectorConnection() {
        new AsyncTask<Void, Void, Void>() { // from class: com.facebook.react.devsupport.DevServerHelper.4
            public Void doInBackground(Void... params) {
                if (DevServerHelper.this.mInspectorPackagerConnection != null) {
                    DevServerHelper.this.mInspectorPackagerConnection.closeQuietly();
                    DevServerHelper.this.mInspectorPackagerConnection = null;
                }
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [com.facebook.react.devsupport.DevServerHelper$5] */
    public void openUrl(final ReactContext context, final String url, final String errorMessage) {
        new AsyncTask<Void, String, Boolean>() { // from class: com.facebook.react.devsupport.DevServerHelper.5
            public Boolean doInBackground(Void... ignore) {
                return Boolean.valueOf(doSync());
            }

            public boolean doSync() {
                try {
                    String openUrlEndpoint = DevServerHelper.this.getOpenUrlEndpoint(context);
                    new OkHttpClient().newCall(new Request.Builder().url(openUrlEndpoint).post(RequestBody.create(MediaType.parse("application/json"), new JSONObject().put("url", url).toString())).build()).execute();
                    return true;
                } catch (IOException | JSONException e) {
                    FLog.e(ReactConstants.TAG, "Failed to open URL" + url, e);
                    return false;
                }
            }

            public void onPostExecute(Boolean result) {
                if (!result.booleanValue()) {
                    RNLog.w(context, errorMessage);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    public void symbolicateStackTrace(Iterable<StackFrame> stackFrames, final SymbolicationListener listener) {
        try {
            String createSymbolicateURL = createSymbolicateURL(this.mSettings.getPackagerConnectionSettings().getDebugServerHost());
            JSONArray jSONArray = new JSONArray();
            for (StackFrame stackFrame : stackFrames) {
                jSONArray.put(stackFrame.toJSON());
            }
            ((Call) Assertions.assertNotNull(this.mClient.newCall(new Request.Builder().url(createSymbolicateURL).post(RequestBody.create(MediaType.parse("application/json"), new JSONObject().put("stack", jSONArray).toString())).build()))).enqueue(new Callback() { // from class: com.facebook.react.devsupport.DevServerHelper.6
                @Override // okhttp3.Callback
                public void onFailure(Call call, IOException e) {
                    FLog.w(ReactConstants.TAG, "Got IOException when attempting symbolicate stack trace: " + e.getMessage());
                    listener.onSymbolicationComplete(null);
                }

                @Override // okhttp3.Callback
                public void onResponse(Call call, final Response response) throws IOException {
                    try {
                        listener.onSymbolicationComplete(Arrays.asList(StackTraceHelper.convertJsStackTrace(new JSONObject(response.body().string()).getJSONArray("stack"))));
                    } catch (JSONException unused) {
                        listener.onSymbolicationComplete(null);
                    }
                }
            });
        } catch (JSONException e) {
            FLog.w(ReactConstants.TAG, "Got JSONException when attempting symbolicate stack trace: " + e.getMessage());
        }
    }

    public void openStackFrameCall(StackFrame stackFrame) {
        ((Call) Assertions.assertNotNull(this.mClient.newCall(new Request.Builder().url(createOpenStackFrameURL(this.mSettings.getPackagerConnectionSettings().getDebugServerHost())).post(RequestBody.create(MediaType.parse("application/json"), stackFrame.toJSON().toString())).build()))).enqueue(new Callback() { // from class: com.facebook.react.devsupport.DevServerHelper.7
            @Override // okhttp3.Callback
            public void onResponse(Call call, final Response response) throws IOException {
            }

            @Override // okhttp3.Callback
            public void onFailure(Call call, IOException e) {
                FLog.w(ReactConstants.TAG, "Got IOException when attempting to open stack frame: " + e.getMessage());
            }
        });
    }

    public String getWebsocketProxyURL() {
        return String.format(Locale.US, "ws://%s/debugger-proxy?role=client", this.mSettings.getPackagerConnectionSettings().getDebugServerHost());
    }

    public String getInspectorDeviceUrl() {
        return String.format(Locale.US, "http://%s/inspector/device?name=%s&app=%s", this.mSettings.getPackagerConnectionSettings().getInspectorServerHost(), AndroidInfoHelpers.getFriendlyDeviceName(), this.mPackageName);
    }

    public void downloadBundleFromURL(DevBundleDownloadListener callback, File outputFile, String bundleURL, BundleDownloader.BundleInfo bundleInfo) {
        this.mBundleDownloader.downloadBundleFromURL(callback, outputFile, bundleURL, bundleInfo);
    }

    public String getOpenUrlEndpoint(Context context) {
        return String.format(Locale.US, "http://%s/open-url", AndroidInfoHelpers.getServerHost(context));
    }

    public void downloadBundleFromURL(DevBundleDownloadListener callback, File outputFile, String bundleURL, BundleDownloader.BundleInfo bundleInfo, Request.Builder requestBuilder) {
        this.mBundleDownloader.downloadBundleFromURL(callback, outputFile, bundleURL, bundleInfo, requestBuilder);
    }

    private String getHostForJSProxy() {
        String str = (String) Assertions.assertNotNull(this.mSettings.getPackagerConnectionSettings().getDebugServerHost());
        int lastIndexOf = str.lastIndexOf(58);
        if (lastIndexOf > -1) {
            return AndroidInfoHelpers.DEVICE_LOCALHOST + str.substring(lastIndexOf);
        }
        return AndroidInfoHelpers.DEVICE_LOCALHOST;
    }

    private boolean getDevMode() {
        return this.mSettings.isJSDevModeEnabled();
    }

    private boolean getJSMinifyMode() {
        return this.mSettings.isJSMinifyEnabled();
    }

    private String createBundleURL(String mainModuleID, BundleType type, String host) {
        return createBundleURL(mainModuleID, type, host, false, true);
    }

    private String createSplitBundleURL(String mainModuleID, String host) {
        return createBundleURL(mainModuleID, BundleType.BUNDLE, host, true, false);
    }

    private String createBundleURL(String mainModuleID, BundleType type, String host, boolean modulesOnly, boolean runModule) {
        Locale locale = Locale.US;
        Object[] objArr = new Object[9];
        objArr[0] = host;
        objArr[1] = mainModuleID;
        objArr[2] = type.typeID();
        objArr[3] = Boolean.valueOf(getDevMode());
        objArr[4] = Boolean.valueOf(getJSMinifyMode());
        objArr[5] = this.mPackageName;
        String str = "true";
        objArr[6] = modulesOnly ? str : "false";
        if (!runModule) {
            str = "false";
        }
        objArr[7] = str;
        objArr[8] = "";
        return String.format(locale, "http://%s/%s.%s?platform=android&dev=%s&minify=%s&app=%s&modulesOnly=%s&runModule=%s%s", objArr);
    }

    private String createBundleURL(String mainModuleID, BundleType type) {
        return createBundleURL(mainModuleID, type, this.mSettings.getPackagerConnectionSettings().getDebugServerHost());
    }

    private static String createResourceURL(String host, String resourcePath) {
        return String.format(Locale.US, "http://%s/%s", host, resourcePath);
    }

    private static String createSymbolicateURL(String host) {
        return String.format(Locale.US, "http://%s/symbolicate", host);
    }

    private static String createOpenStackFrameURL(String host) {
        return String.format(Locale.US, "http://%s/open-stack-frame", host);
    }

    public String getDevServerBundleURL(final String jsModulePath) {
        return createBundleURL(jsModulePath, BundleType.BUNDLE, this.mSettings.getPackagerConnectionSettings().getDebugServerHost());
    }

    public String getDevServerSplitBundleURL(String jsModulePath) {
        return createSplitBundleURL(jsModulePath, this.mSettings.getPackagerConnectionSettings().getDebugServerHost());
    }

    public void isPackagerRunning(final PackagerStatusCallback callback) {
        String debugServerHost = this.mSettings.getPackagerConnectionSettings().getDebugServerHost();
        if (debugServerHost == null) {
            FLog.w(ReactConstants.TAG, "No packager host configured.");
            callback.onPackagerStatusFetched(false);
            return;
        }
        this.mPackagerStatusCheck.run(debugServerHost, callback);
    }

    private String createLaunchJSDevtoolsCommandUrl() {
        return String.format(Locale.US, "http://%s/launch-js-devtools", this.mSettings.getPackagerConnectionSettings().getDebugServerHost());
    }

    public void launchJSDevtools() {
        this.mClient.newCall(new Request.Builder().url(createLaunchJSDevtoolsCommandUrl()).build()).enqueue(new Callback() { // from class: com.facebook.react.devsupport.DevServerHelper.8
            @Override // okhttp3.Callback
            public void onFailure(Call call, IOException e) {
            }

            @Override // okhttp3.Callback
            public void onResponse(Call call, Response response) throws IOException {
            }
        });
    }

    public String getSourceMapUrl(String mainModuleName) {
        return createBundleURL(mainModuleName, BundleType.MAP);
    }

    public String getSourceUrl(String mainModuleName) {
        return createBundleURL(mainModuleName, BundleType.BUNDLE);
    }

    public String getJSBundleURLForRemoteDebugging(String mainModuleName) {
        return createBundleURL(mainModuleName, BundleType.BUNDLE, getHostForJSProxy());
    }

    public File downloadBundleResourceFromUrlSync(final String resourcePath, final File outputFile) {
        Throwable th;
        Sink sink;
        try {
            Response execute = this.mClient.newCall(new Request.Builder().url(createResourceURL(this.mSettings.getPackagerConnectionSettings().getDebugServerHost(), resourcePath)).build()).execute();
            if (!execute.isSuccessful()) {
                if (execute != null) {
                    execute.close();
                }
                return null;
            }
            try {
                sink = Okio.sink(outputFile);
                try {
                    Okio.buffer(execute.body().source()).readAll(sink);
                    if (sink != null) {
                        sink.close();
                    }
                    if (execute != null) {
                        execute.close();
                    }
                    return outputFile;
                } catch (Throwable th2) {
                    th = th2;
                    if (sink != null) {
                        sink.close();
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                sink = null;
            }
        } catch (Exception e) {
            FLog.e(ReactConstants.TAG, "Failed to fetch resource synchronously - resourcePath: \"%s\", outputFile: \"%s\"", resourcePath, outputFile.getAbsolutePath(), e);
            return null;
        }
    }
}
