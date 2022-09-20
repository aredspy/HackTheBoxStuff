package com.facebook.react.modules.systeminfo;

import android.app.UiModeManager;
import android.os.Build;
import android.provider.Settings;
import androidx.core.os.EnvironmentCompat;
import com.facebook.fbreact.specs.NativePlatformConstantsAndroidSpec;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.turbomodule.core.interfaces.TurboModule;
import java.util.HashMap;
import java.util.Map;
@ReactModule(name = AndroidInfoModule.NAME)
/* loaded from: classes.dex */
public class AndroidInfoModule extends NativePlatformConstantsAndroidSpec implements TurboModule {
    private static final String IS_TESTING = "IS_TESTING";
    public static final String NAME = "PlatformConstants";

    @Override // com.facebook.react.bridge.NativeModule
    public String getName() {
        return NAME;
    }

    @Override // com.facebook.react.bridge.BaseJavaModule, com.facebook.react.bridge.NativeModule, com.facebook.react.turbomodule.core.interfaces.TurboModule
    public void invalidate() {
    }

    public AndroidInfoModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    private String uiMode() {
        int currentModeType = ((UiModeManager) getReactApplicationContext().getSystemService("uimode")).getCurrentModeType();
        return currentModeType != 1 ? currentModeType != 2 ? currentModeType != 3 ? currentModeType != 4 ? currentModeType != 6 ? EnvironmentCompat.MEDIA_UNKNOWN : "watch" : "tv" : "car" : "desk" : "normal";
    }

    @Override // com.facebook.fbreact.specs.NativePlatformConstantsAndroidSpec
    public Map<String, Object> getTypedExportedConstants() {
        HashMap hashMap = new HashMap();
        hashMap.put("Version", Integer.valueOf(Build.VERSION.SDK_INT));
        hashMap.put("Release", Build.VERSION.RELEASE);
        hashMap.put("Serial", Build.SERIAL);
        hashMap.put("Fingerprint", Build.FINGERPRINT);
        hashMap.put("Model", Build.MODEL);
        hashMap.put("Manufacturer", Build.MANUFACTURER);
        hashMap.put("Brand", Build.BRAND);
        hashMap.put("isTesting", Boolean.valueOf("true".equals(System.getProperty(IS_TESTING)) || isRunningScreenshotTest().booleanValue()));
        hashMap.put("reactNativeVersion", ReactNativeVersion.VERSION);
        hashMap.put("uiMode", uiMode());
        return hashMap;
    }

    @Override // com.facebook.fbreact.specs.NativePlatformConstantsAndroidSpec
    public String getAndroidID() {
        return Settings.Secure.getString(getReactApplicationContext().getContentResolver(), "android_id");
    }

    private Boolean isRunningScreenshotTest() {
        try {
            Class.forName("com.facebook.testing.react.screenshots.ReactAppScreenshotTestActivity");
            return true;
        } catch (ClassNotFoundException unused) {
            return false;
        }
    }
}
