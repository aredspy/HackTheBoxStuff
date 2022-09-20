package com.facebook.react.devsupport;

import com.facebook.common.logging.FLog;
import com.facebook.react.common.ReactConstants;
import com.facebook.react.devsupport.interfaces.PackagerStatusCallback;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
/* loaded from: classes.dex */
public class PackagerStatusCheck {
    private static final int HTTP_CONNECT_TIMEOUT_MS = 5000;
    private static final String PACKAGER_OK_STATUS = "packager-status:running";
    private static final String PACKAGER_STATUS_URL_TEMPLATE = "http://%s/status";
    private final OkHttpClient mClient;

    public PackagerStatusCheck() {
        this.mClient = new OkHttpClient.Builder().connectTimeout(5000L, TimeUnit.MILLISECONDS).readTimeout(0L, TimeUnit.MILLISECONDS).writeTimeout(0L, TimeUnit.MILLISECONDS).build();
    }

    public PackagerStatusCheck(OkHttpClient client) {
        this.mClient = client;
    }

    public void run(String host, final PackagerStatusCallback callback) {
        this.mClient.newCall(new Request.Builder().url(createPackagerStatusURL(host)).build()).enqueue(new Callback() { // from class: com.facebook.react.devsupport.PackagerStatusCheck.1
            @Override // okhttp3.Callback
            public void onFailure(Call call, IOException e) {
                FLog.w(ReactConstants.TAG, "The packager does not seem to be running as we got an IOException requesting its status: " + e.getMessage());
                callback.onPackagerStatusFetched(false);
            }

            @Override // okhttp3.Callback
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    FLog.e(ReactConstants.TAG, "Got non-success http code from packager when requesting status: " + response.code());
                    callback.onPackagerStatusFetched(false);
                    return;
                }
                ResponseBody body = response.body();
                if (body == null) {
                    FLog.e(ReactConstants.TAG, "Got null body response from packager when requesting status");
                    callback.onPackagerStatusFetched(false);
                    return;
                }
                String string = body.string();
                if (!PackagerStatusCheck.PACKAGER_OK_STATUS.equals(string)) {
                    FLog.e(ReactConstants.TAG, "Got unexpected response from packager when requesting status: " + string);
                    callback.onPackagerStatusFetched(false);
                    return;
                }
                callback.onPackagerStatusFetched(true);
            }
        });
    }

    private static String createPackagerStatusURL(String host) {
        return String.format(Locale.US, PACKAGER_STATUS_URL_TEMPLATE, host);
    }
}
