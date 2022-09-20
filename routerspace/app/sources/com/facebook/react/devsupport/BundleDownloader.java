package com.facebook.react.devsupport;

import androidx.core.app.NotificationCompat;
import androidx.core.os.EnvironmentCompat;
import com.facebook.cache.disk.DefaultDiskStorage;
import com.facebook.common.logging.FLog;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.common.DebugServerException;
import com.facebook.react.common.ReactConstants;
import com.facebook.react.devsupport.MultipartStreamReader;
import com.facebook.react.devsupport.interfaces.DevBundleDownloadListener;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.ws.RealWebSocket;
import okio.Buffer;
import okio.BufferedSource;
import okio.Okio;
import okio.Sink;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes.dex */
public class BundleDownloader {
    private static final int FILES_CHANGED_COUNT_NOT_BUILT_BY_BUNDLER = -2;
    private static final String TAG = "BundleDownloader";
    private final OkHttpClient mClient;
    private Call mDownloadBundleFromURLCall;

    /* loaded from: classes.dex */
    public static class BundleInfo {
        private int mFilesChangedCount;
        private String mUrl;

        public static BundleInfo fromJSONString(String jsonStr) {
            if (jsonStr == null) {
                return null;
            }
            BundleInfo bundleInfo = new BundleInfo();
            try {
                JSONObject jSONObject = new JSONObject(jsonStr);
                bundleInfo.mUrl = jSONObject.getString("url");
                bundleInfo.mFilesChangedCount = jSONObject.getInt("filesChangedCount");
                return bundleInfo;
            } catch (JSONException e) {
                FLog.e(BundleDownloader.TAG, "Invalid bundle info: ", e);
                return null;
            }
        }

        public String toJSONString() {
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put("url", this.mUrl);
                jSONObject.put("filesChangedCount", this.mFilesChangedCount);
                return jSONObject.toString();
            } catch (JSONException e) {
                FLog.e(BundleDownloader.TAG, "Can't serialize bundle info: ", e);
                return null;
            }
        }

        public String getUrl() {
            String str = this.mUrl;
            return str != null ? str : EnvironmentCompat.MEDIA_UNKNOWN;
        }

        public int getFilesChangedCount() {
            return this.mFilesChangedCount;
        }
    }

    public BundleDownloader(OkHttpClient client) {
        this.mClient = client;
    }

    public void downloadBundleFromURL(final DevBundleDownloadListener callback, final File outputFile, final String bundleURL, final BundleInfo bundleInfo) {
        downloadBundleFromURL(callback, outputFile, bundleURL, bundleInfo, new Request.Builder());
    }

    public void downloadBundleFromURL(final DevBundleDownloadListener callback, final File outputFile, final String bundleURL, final BundleInfo bundleInfo, Request.Builder requestBuilder) {
        Call call = (Call) Assertions.assertNotNull(this.mClient.newCall(requestBuilder.url(bundleURL).addHeader("Accept", "multipart/mixed").build()));
        this.mDownloadBundleFromURLCall = call;
        call.enqueue(new Callback() { // from class: com.facebook.react.devsupport.BundleDownloader.1
            @Override // okhttp3.Callback
            public void onFailure(Call call2, IOException e) {
                if (BundleDownloader.this.mDownloadBundleFromURLCall == null || BundleDownloader.this.mDownloadBundleFromURLCall.isCanceled()) {
                    BundleDownloader.this.mDownloadBundleFromURLCall = null;
                    return;
                }
                BundleDownloader.this.mDownloadBundleFromURLCall = null;
                String httpUrl = call2.request().url().toString();
                DevBundleDownloadListener devBundleDownloadListener = callback;
                devBundleDownloadListener.onFailure(DebugServerException.makeGeneric(httpUrl, "Could not connect to development server.", "URL: " + httpUrl, e));
            }

            @Override // okhttp3.Callback
            public void onResponse(Call call2, final Response response) throws IOException {
                if (BundleDownloader.this.mDownloadBundleFromURLCall == null || BundleDownloader.this.mDownloadBundleFromURLCall.isCanceled()) {
                    BundleDownloader.this.mDownloadBundleFromURLCall = null;
                    return;
                }
                BundleDownloader.this.mDownloadBundleFromURLCall = null;
                String httpUrl = response.request().url().toString();
                Matcher matcher = Pattern.compile("multipart/mixed;.*boundary=\"([^\"]+)\"").matcher(response.header("content-type"));
                try {
                    if (matcher.find()) {
                        BundleDownloader.this.processMultipartResponse(httpUrl, response, matcher.group(1), outputFile, bundleInfo, callback);
                    } else {
                        BundleDownloader.this.processBundleResult(httpUrl, response.code(), response.headers(), Okio.buffer(response.body().source()), outputFile, bundleInfo, callback);
                    }
                    if (response == null) {
                        return;
                    }
                    response.close();
                } catch (Throwable th) {
                    try {
                        throw th;
                    } catch (Throwable th2) {
                        if (response != null) {
                            if (th != null) {
                                try {
                                    response.close();
                                } catch (Throwable th3) {
                                    th.addSuppressed(th3);
                                }
                            } else {
                                response.close();
                            }
                        }
                        throw th2;
                    }
                }
            }
        });
    }

    public void processMultipartResponse(final String url, final Response response, String boundary, final File outputFile, final BundleInfo bundleInfo, final DevBundleDownloadListener callback) throws IOException {
        if (!new MultipartStreamReader(response.body().source(), boundary).readAllParts(new MultipartStreamReader.ChunkListener() { // from class: com.facebook.react.devsupport.BundleDownloader.2
            @Override // com.facebook.react.devsupport.MultipartStreamReader.ChunkListener
            public void onChunkComplete(Map<String, String> headers, Buffer body, boolean isLastChunk) throws IOException {
                if (isLastChunk) {
                    int code = response.code();
                    if (headers.containsKey("X-Http-Status")) {
                        code = Integer.parseInt(headers.get("X-Http-Status"));
                    }
                    BundleDownloader.this.processBundleResult(url, code, Headers.of(headers), body, outputFile, bundleInfo, callback);
                } else if (!headers.containsKey("Content-Type") || !headers.get("Content-Type").equals("application/json")) {
                } else {
                    try {
                        JSONObject jSONObject = new JSONObject(body.readUtf8());
                        String string = jSONObject.has(NotificationCompat.CATEGORY_STATUS) ? jSONObject.getString(NotificationCompat.CATEGORY_STATUS) : "Bundling";
                        Integer num = null;
                        Integer valueOf = jSONObject.has("done") ? Integer.valueOf(jSONObject.getInt("done")) : null;
                        if (jSONObject.has("total")) {
                            num = Integer.valueOf(jSONObject.getInt("total"));
                        }
                        callback.onProgress(string, valueOf, num);
                    } catch (JSONException e) {
                        FLog.e(ReactConstants.TAG, "Error parsing progress JSON. " + e.toString());
                    }
                }
            }

            @Override // com.facebook.react.devsupport.MultipartStreamReader.ChunkListener
            public void onChunkProgress(Map<String, String> headers, long loaded, long total) {
                if ("application/javascript".equals(headers.get("Content-Type"))) {
                    callback.onProgress("Downloading", Integer.valueOf((int) (loaded / RealWebSocket.DEFAULT_MINIMUM_DEFLATE_SIZE)), Integer.valueOf((int) (total / RealWebSocket.DEFAULT_MINIMUM_DEFLATE_SIZE)));
                }
            }
        })) {
            callback.onFailure(new DebugServerException("Error while reading multipart response.\n\nResponse code: " + response.code() + "\n\nURL: " + url.toString() + "\n\n"));
        }
    }

    public void processBundleResult(String url, int statusCode, Headers headers, BufferedSource body, File outputFile, BundleInfo bundleInfo, DevBundleDownloadListener callback) throws IOException {
        if (statusCode != 200) {
            String readUtf8 = body.readUtf8();
            DebugServerException parse = DebugServerException.parse(url, readUtf8);
            if (parse != null) {
                callback.onFailure(parse);
                return;
            }
            callback.onFailure(new DebugServerException("The development server returned response error code: " + statusCode + "\n\nURL: " + url + "\n\nBody:\n" + readUtf8));
            return;
        }
        if (bundleInfo != null) {
            populateBundleInfo(url, headers, bundleInfo);
        }
        File file = new File(outputFile.getPath() + DefaultDiskStorage.FileType.TEMP);
        if (storePlainJSInFile(body, file) && !file.renameTo(outputFile)) {
            throw new IOException("Couldn't rename " + file + " to " + outputFile);
        }
        callback.onSuccess();
    }

    private static boolean storePlainJSInFile(BufferedSource body, File outputFile) throws IOException {
        Sink sink;
        Throwable th;
        try {
            sink = Okio.sink(outputFile);
        } catch (Throwable th2) {
            th = th2;
            sink = null;
        }
        try {
            body.readAll(sink);
            if (sink == null) {
                return true;
            }
            sink.close();
            return true;
        } catch (Throwable th3) {
            th = th3;
            if (sink != null) {
                sink.close();
            }
            throw th;
        }
    }

    private static void populateBundleInfo(String url, Headers headers, BundleInfo bundleInfo) {
        bundleInfo.mUrl = url;
        String str = headers.get("X-Metro-Files-Changed-Count");
        if (str != null) {
            try {
                bundleInfo.mFilesChangedCount = Integer.parseInt(str);
            } catch (NumberFormatException unused) {
                bundleInfo.mFilesChangedCount = -2;
            }
        }
    }
}
