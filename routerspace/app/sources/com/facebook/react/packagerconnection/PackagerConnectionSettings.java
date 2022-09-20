package com.facebook.react.packagerconnection;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import com.facebook.common.logging.FLog;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.modules.systeminfo.AndroidInfoHelpers;
/* loaded from: classes.dex */
public class PackagerConnectionSettings {
    private static final String PREFS_DEBUG_SERVER_HOST_KEY = "debug_http_host";
    private static final String TAG = "PackagerConnectionSettings";
    private final Context mAppContext;
    private final String mPackageName;
    private final SharedPreferences mPreferences;

    public PackagerConnectionSettings(Context applicationContext) {
        this.mPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        this.mPackageName = applicationContext.getPackageName();
        this.mAppContext = applicationContext;
    }

    public String getDebugServerHost() {
        String string = this.mPreferences.getString(PREFS_DEBUG_SERVER_HOST_KEY, null);
        if (!TextUtils.isEmpty(string)) {
            return (String) Assertions.assertNotNull(string);
        }
        String serverHost = AndroidInfoHelpers.getServerHost(this.mAppContext);
        if (serverHost.equals(AndroidInfoHelpers.DEVICE_LOCALHOST)) {
            String str = TAG;
            FLog.w(str, "You seem to be running on device. Run '" + AndroidInfoHelpers.getAdbReverseTcpCommand(this.mAppContext) + "' to forward the debug server's port to the device.");
        }
        return serverHost;
    }

    public void setDebugServerHost(String host) {
        this.mPreferences.edit().putString(PREFS_DEBUG_SERVER_HOST_KEY, host).apply();
    }

    public String getInspectorServerHost() {
        return AndroidInfoHelpers.getInspectorProxyHost(this.mAppContext);
    }

    public String getPackageName() {
        return this.mPackageName;
    }
}
