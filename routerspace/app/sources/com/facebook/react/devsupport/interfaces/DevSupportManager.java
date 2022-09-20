package com.facebook.react.devsupport.interfaces;

import android.view.View;
import com.facebook.react.bridge.NativeModuleCallExceptionHandler;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.modules.debug.interfaces.DeveloperSettings;
import java.io.File;
/* loaded from: classes.dex */
public interface DevSupportManager extends NativeModuleCallExceptionHandler {

    /* loaded from: classes.dex */
    public interface PackagerLocationCustomizer {
        void run(Runnable callback);
    }

    void addCustomDevOption(String optionName, DevOptionHandler optionHandler);

    View createRootView(String appKey);

    void destroyRootView(View rootView);

    File downloadBundleResourceFromUrlSync(final String resourceURL, final File outputFile);

    DeveloperSettings getDevSettings();

    boolean getDevSupportEnabled();

    String getDownloadedJSBundleFile();

    String getJSBundleURLForRemoteDebugging();

    StackFrame[] getLastErrorStack();

    String getLastErrorTitle();

    ErrorType getLastErrorType();

    String getSourceMapUrl();

    String getSourceUrl();

    void handleReloadJS();

    boolean hasUpToDateJSBundleInCache();

    void hideRedboxDialog();

    void isPackagerRunning(PackagerStatusCallback callback);

    void loadSplitBundleFromServer(String bundlePath, DevSplitBundleCallback callback);

    void onNewReactContextCreated(ReactContext reactContext);

    void onReactInstanceDestroyed(ReactContext reactContext);

    void registerErrorCustomizer(ErrorCustomizer errorCustomizer);

    void reloadJSFromServer(final String bundleURL);

    void reloadJSFromServer(final String bundleURL, final BundleLoadCallback callback);

    void reloadSettings();

    void setDevSupportEnabled(boolean isDevSupportEnabled);

    void setFpsDebugEnabled(final boolean isFpsDebugEnabled);

    void setHotModuleReplacementEnabled(final boolean isHotModuleReplacementEnabled);

    void setPackagerLocationCustomizer(PackagerLocationCustomizer packagerLocationCustomizer);

    void setRemoteJSDebugEnabled(final boolean isRemoteJSDebugEnabled);

    void showDevOptionsDialog();

    void showNewJSError(String message, ReadableArray details, int errorCookie);

    void showNewJavaError(String message, Throwable e);

    void startInspector();

    void stopInspector();

    void toggleElementInspector();

    void updateJSError(final String message, final ReadableArray details, final int errorCookie);
}
