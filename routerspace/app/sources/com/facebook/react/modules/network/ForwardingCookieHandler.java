package com.facebook.react.modules.network;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.GuardedAsyncTask;
import com.facebook.react.bridge.ReactContext;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
public class ForwardingCookieHandler extends CookieHandler {
    private static final String COOKIE_HEADER = "Cookie";
    private static final String VERSION_ONE_HEADER = "Set-cookie2";
    private static final String VERSION_ZERO_HEADER = "Set-cookie";
    private final ReactContext mContext;
    private CookieManager mCookieManager;
    private final CookieSaver mCookieSaver = new CookieSaver();

    private static void possiblyWorkaroundSyncManager(Context context) {
    }

    public void destroy() {
    }

    public ForwardingCookieHandler(ReactContext context) {
        this.mContext = context;
    }

    @Override // java.net.CookieHandler
    public Map<String, List<String>> get(URI uri, Map<String, List<String>> headers) throws IOException {
        CookieManager cookieManager = getCookieManager();
        if (cookieManager == null) {
            return Collections.emptyMap();
        }
        String cookie = cookieManager.getCookie(uri.toString());
        if (TextUtils.isEmpty(cookie)) {
            return Collections.emptyMap();
        }
        return Collections.singletonMap(COOKIE_HEADER, Collections.singletonList(cookie));
    }

    @Override // java.net.CookieHandler
    public void put(URI uri, Map<String, List<String>> headers) throws IOException {
        String uri2 = uri.toString();
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            String key = entry.getKey();
            if (key != null && isCookieHeader(key)) {
                addCookies(uri2, entry.getValue());
            }
        }
    }

    public void clearCookies(final Callback callback) {
        clearCookiesAsync(callback);
    }

    private void clearCookiesAsync(final Callback callback) {
        CookieManager cookieManager = getCookieManager();
        if (cookieManager != null) {
            cookieManager.removeAllCookies(new ValueCallback<Boolean>() { // from class: com.facebook.react.modules.network.ForwardingCookieHandler.1
                public void onReceiveValue(Boolean value) {
                    ForwardingCookieHandler.this.mCookieSaver.onCookiesModified();
                    callback.invoke(value);
                }
            });
        }
    }

    public void addCookies(final String url, final List<String> cookies) {
        CookieManager cookieManager = getCookieManager();
        if (cookieManager == null) {
            return;
        }
        for (String str : cookies) {
            addCookieAsync(url, str);
        }
        cookieManager.flush();
        this.mCookieSaver.onCookiesModified();
    }

    private void addCookieAsync(String url, String cookie) {
        CookieManager cookieManager = getCookieManager();
        if (cookieManager != null) {
            cookieManager.setCookie(url, cookie, null);
        }
    }

    private static boolean isCookieHeader(String name) {
        return name.equalsIgnoreCase(VERSION_ZERO_HEADER) || name.equalsIgnoreCase(VERSION_ONE_HEADER);
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [com.facebook.react.modules.network.ForwardingCookieHandler$2] */
    public void runInBackground(final Runnable runnable) {
        new GuardedAsyncTask<Void, Void>(this.mContext) { // from class: com.facebook.react.modules.network.ForwardingCookieHandler.2
            public void doInBackgroundGuarded(Void... params) {
                runnable.run();
            }
        }.execute(new Void[0]);
    }

    public CookieManager getCookieManager() {
        if (this.mCookieManager == null) {
            possiblyWorkaroundSyncManager(this.mContext);
            try {
                this.mCookieManager = CookieManager.getInstance();
            } catch (IllegalArgumentException unused) {
                return null;
            } catch (Exception e) {
                if (e.getMessage() != null && e.getClass().getCanonicalName().equals("android.webkit.WebViewFactory.MissingWebViewPackageException")) {
                    return null;
                }
                throw e;
            }
        }
        return this.mCookieManager;
    }

    /* loaded from: classes.dex */
    public class CookieSaver {
        private static final int MSG_PERSIST_COOKIES = 1;
        private static final int TIMEOUT = 30000;
        private final Handler mHandler;

        public void onCookiesModified() {
        }

        public CookieSaver() {
            ForwardingCookieHandler.this = this$0;
            this.mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() { // from class: com.facebook.react.modules.network.ForwardingCookieHandler.CookieSaver.1
                @Override // android.os.Handler.Callback
                public boolean handleMessage(Message msg) {
                    if (msg.what == 1) {
                        CookieSaver.this.persistCookies();
                        return true;
                    }
                    return false;
                }
            });
        }

        public void persistCookies() {
            this.mHandler.removeMessages(1);
            ForwardingCookieHandler.this.runInBackground(new Runnable() { // from class: com.facebook.react.modules.network.ForwardingCookieHandler.CookieSaver.2
                @Override // java.lang.Runnable
                public void run() {
                    CookieSaver.this.flush();
                }
            });
        }

        public void flush() {
            CookieManager cookieManager = ForwardingCookieHandler.this.getCookieManager();
            if (cookieManager != null) {
                cookieManager.flush();
            }
        }
    }
}
