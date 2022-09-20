package com.facebook.react.devsupport;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.view.ViewCompat;
import com.facebook.common.logging.FLog;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.R;
import com.facebook.react.bridge.DefaultNativeModuleCallExceptionHandler;
import com.facebook.react.bridge.JSBundleLoader;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactMarker;
import com.facebook.react.bridge.ReactMarkerConstants;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.common.DebugServerException;
import com.facebook.react.common.ReactConstants;
import com.facebook.react.common.ShakeDetector;
import com.facebook.react.devsupport.BundleDownloader;
import com.facebook.react.devsupport.DevInternalSettings;
import com.facebook.react.devsupport.DevServerHelper;
import com.facebook.react.devsupport.InspectorPackagerConnection;
import com.facebook.react.devsupport.JSCHeapCapture;
import com.facebook.react.devsupport.interfaces.BundleLoadCallback;
import com.facebook.react.devsupport.interfaces.DevBundleDownloadListener;
import com.facebook.react.devsupport.interfaces.DevOptionHandler;
import com.facebook.react.devsupport.interfaces.DevSupportManager;
import com.facebook.react.devsupport.interfaces.ErrorCustomizer;
import com.facebook.react.devsupport.interfaces.ErrorType;
import com.facebook.react.devsupport.interfaces.PackagerStatusCallback;
import com.facebook.react.devsupport.interfaces.StackFrame;
import com.facebook.react.modules.core.RCTNativeAppEventEmitter;
import com.facebook.react.packagerconnection.RequestHandler;
import com.facebook.react.packagerconnection.Responder;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
/* loaded from: classes.dex */
public abstract class DevSupportManagerBase implements DevSupportManager {
    public static final String EMOJI_FACE_WITH_NO_GOOD_GESTURE = " 🙅";
    public static final String EMOJI_HUNDRED_POINTS_SYMBOL = " 💯";
    private static final String EXOPACKAGE_LOCATION_FORMAT = "/data/local/tmp/exopackage/%s//secondary-dex";
    private static final String FLIPPER_DEBUGGER_URL = "flipper://null/Hermesdebuggerrn?device=React%20Native";
    private static final String FLIPPER_DEVTOOLS_URL = "flipper://null/React?device=React%20Native";
    private static final int JAVA_ERROR_COOKIE = -1;
    private static final int JSEXCEPTION_ERROR_COOKIE = -1;
    private static final String RELOAD_APP_ACTION_SUFFIX = ".RELOAD_APP_ACTION";
    private Activity currentActivity;
    private final Context mApplicationContext;
    private DevBundleDownloadListener mBundleDownloadListener;
    private ReactContext mCurrentContext;
    private Map<String, RequestHandler> mCustomPackagerCommandHandlers;
    private DebugOverlayController mDebugOverlayController;
    private final DevLoadingViewController mDevLoadingViewController;
    private AlertDialog mDevOptionsDialog;
    private final DevServerHelper mDevServerHelper;
    private DevInternalSettings mDevSettings;
    private List<ErrorCustomizer> mErrorCustomizers;
    private final String mJSAppBundleName;
    private final File mJSBundleDownloadedFile;
    private final File mJSSplitBundlesDir;
    private StackFrame[] mLastErrorStack;
    private String mLastErrorTitle;
    private ErrorType mLastErrorType;
    private DevSupportManager.PackagerLocationCustomizer mPackagerLocationCustomizer;
    private final ReactInstanceDevHelper mReactInstanceDevHelper;
    private RedBoxDialog mRedBoxDialog;
    private RedBoxHandler mRedBoxHandler;
    private final ShakeDetector mShakeDetector;
    private final LinkedHashMap<String, DevOptionHandler> mCustomDevOptions = new LinkedHashMap<>();
    private boolean mDevLoadingViewVisible = false;
    private int mPendingJSSplitBundleRequests = 0;
    private boolean mIsReceiverRegistered = false;
    private boolean mIsShakeDetectorStarted = false;
    private boolean mIsDevSupportEnabled = false;
    private int mLastErrorCookie = 0;
    private InspectorPackagerConnection.BundleStatus mBundleStatus = new InspectorPackagerConnection.BundleStatus();
    private final BroadcastReceiver mReloadAppBroadcastReceiver = new BroadcastReceiver() { // from class: com.facebook.react.devsupport.DevSupportManagerBase.4
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (DevSupportManagerBase.getReloadAppAction(context).equals(intent.getAction())) {
                if (intent.getBooleanExtra(DevServerHelper.RELOAD_APP_EXTRA_JS_PROXY, false)) {
                    DevSupportManagerBase.this.mDevSettings.setRemoteJSDebugEnabled(true);
                    DevSupportManagerBase.this.mDevServerHelper.launchJSDevtools();
                } else {
                    DevSupportManagerBase.this.mDevSettings.setRemoteJSDebugEnabled(false);
                }
                DevSupportManagerBase.this.handleReloadJS();
            }
        }
    };
    private final DefaultNativeModuleCallExceptionHandler mDefaultNativeModuleCallExceptionHandler = new DefaultNativeModuleCallExceptionHandler();

    /* loaded from: classes.dex */
    public interface CallbackWithBundleLoader {
        void onError(String url, Throwable cause);

        void onSuccess(JSBundleLoader bundleLoader);
    }

    protected abstract String getUniqueTag();

    public DevSupportManagerBase(Context applicationContext, ReactInstanceDevHelper reactInstanceDevHelper, String packagerPathForJSBundleName, boolean enableOnCreate, RedBoxHandler redBoxHandler, DevBundleDownloadListener devBundleDownloadListener, int minNumShakes, Map<String, RequestHandler> customPackagerCommandHandlers) {
        this.mReactInstanceDevHelper = reactInstanceDevHelper;
        this.mApplicationContext = applicationContext;
        this.mJSAppBundleName = packagerPathForJSBundleName;
        this.mDevSettings = new DevInternalSettings(applicationContext, new DevInternalSettings.Listener() { // from class: com.facebook.react.devsupport.DevSupportManagerBase.1
            @Override // com.facebook.react.devsupport.DevInternalSettings.Listener
            public void onInternalSettingsChanged() {
                DevSupportManagerBase.this.reloadSettings();
            }
        });
        this.mDevServerHelper = new DevServerHelper(this.mDevSettings, applicationContext.getPackageName(), new InspectorPackagerConnection.BundleStatusProvider() { // from class: com.facebook.react.devsupport.DevSupportManagerBase.2
            @Override // com.facebook.react.devsupport.InspectorPackagerConnection.BundleStatusProvider
            public InspectorPackagerConnection.BundleStatus getBundleStatus() {
                return DevSupportManagerBase.this.mBundleStatus;
            }
        });
        this.mBundleDownloadListener = devBundleDownloadListener;
        this.mShakeDetector = new ShakeDetector(new ShakeDetector.ShakeListener() { // from class: com.facebook.react.devsupport.DevSupportManagerBase.3
            @Override // com.facebook.react.common.ShakeDetector.ShakeListener
            public void onShake() {
                DevSupportManagerBase.this.showDevOptionsDialog();
            }
        }, minNumShakes);
        this.mCustomPackagerCommandHandlers = customPackagerCommandHandlers;
        String uniqueTag = getUniqueTag();
        this.mJSBundleDownloadedFile = new File(applicationContext.getFilesDir(), uniqueTag + "ReactNativeDevBundle.js");
        this.mJSSplitBundlesDir = applicationContext.getDir(uniqueTag.toLowerCase() + "_dev_js_split_bundles", 0);
        setDevSupportEnabled(enableOnCreate);
        this.mRedBoxHandler = redBoxHandler;
        this.mDevLoadingViewController = new DevLoadingViewController(reactInstanceDevHelper);
    }

    @Override // com.facebook.react.bridge.NativeModuleCallExceptionHandler
    public void handleException(Exception e) {
        if (this.mIsDevSupportEnabled) {
            logJSException(e);
        } else {
            this.mDefaultNativeModuleCallExceptionHandler.handleException(e);
        }
    }

    private void logJSException(Exception e) {
        StringBuilder sb = new StringBuilder(e.getMessage() == null ? "Exception in native call from JS" : e.getMessage());
        for (Throwable cause = e.getCause(); cause != null; cause = cause.getCause()) {
            sb.append("\n\n");
            sb.append(cause.getMessage());
        }
        if (e instanceof JSException) {
            FLog.e(ReactConstants.TAG, "Exception in native call from JS", e);
            String stack = ((JSException) e).getStack();
            sb.append("\n\n");
            sb.append(stack);
            showNewError(sb.toString(), new StackFrame[0], -1, ErrorType.JS);
            return;
        }
        showNewJavaError(sb.toString(), e);
    }

    @Override // com.facebook.react.devsupport.interfaces.DevSupportManager
    public void showNewJavaError(String message, Throwable e) {
        FLog.e(ReactConstants.TAG, "Exception in native call", e);
        showNewError(message, StackTraceHelper.convertJavaStackTrace(e), -1, ErrorType.NATIVE);
    }

    @Override // com.facebook.react.devsupport.interfaces.DevSupportManager
    public void addCustomDevOption(String optionName, DevOptionHandler optionHandler) {
        this.mCustomDevOptions.put(optionName, optionHandler);
    }

    @Override // com.facebook.react.devsupport.interfaces.DevSupportManager
    public void showNewJSError(String message, ReadableArray details, int errorCookie) {
        showNewError(message, StackTraceHelper.convertJsStackTrace(details), errorCookie, ErrorType.JS);
    }

    @Override // com.facebook.react.devsupport.interfaces.DevSupportManager
    public void registerErrorCustomizer(ErrorCustomizer errorCustomizer) {
        if (this.mErrorCustomizers == null) {
            this.mErrorCustomizers = new ArrayList();
        }
        this.mErrorCustomizers.add(errorCustomizer);
    }

    public Pair<String, StackFrame[]> processErrorCustomizers(Pair<String, StackFrame[]> errorInfo) {
        List<ErrorCustomizer> list = this.mErrorCustomizers;
        if (list == null) {
            return errorInfo;
        }
        for (ErrorCustomizer errorCustomizer : list) {
            Pair<String, StackFrame[]> customizeErrorInfo = errorCustomizer.customizeErrorInfo(errorInfo);
            if (customizeErrorInfo != null) {
                errorInfo = customizeErrorInfo;
            }
        }
        return errorInfo;
    }

    @Override // com.facebook.react.devsupport.interfaces.DevSupportManager
    public void updateJSError(final String message, final ReadableArray details, final int errorCookie) {
        UiThreadUtil.runOnUiThread(new Runnable() { // from class: com.facebook.react.devsupport.DevSupportManagerBase.5
            @Override // java.lang.Runnable
            public void run() {
                if (DevSupportManagerBase.this.mRedBoxDialog == null || !DevSupportManagerBase.this.mRedBoxDialog.isShowing() || errorCookie != DevSupportManagerBase.this.mLastErrorCookie) {
                    return;
                }
                StackFrame[] convertJsStackTrace = StackTraceHelper.convertJsStackTrace(details);
                Pair processErrorCustomizers = DevSupportManagerBase.this.processErrorCustomizers(Pair.create(message, convertJsStackTrace));
                DevSupportManagerBase.this.mRedBoxDialog.setExceptionDetails((String) processErrorCustomizers.first, (StackFrame[]) processErrorCustomizers.second);
                DevSupportManagerBase.this.updateLastErrorInfo(message, convertJsStackTrace, errorCookie, ErrorType.JS);
                if (DevSupportManagerBase.this.mRedBoxHandler != null) {
                    DevSupportManagerBase.this.mRedBoxHandler.handleRedbox(message, convertJsStackTrace, ErrorType.JS);
                    DevSupportManagerBase.this.mRedBoxDialog.resetReporting();
                }
                DevSupportManagerBase.this.mRedBoxDialog.show();
            }
        });
    }

    @Override // com.facebook.react.devsupport.interfaces.DevSupportManager
    public void hideRedboxDialog() {
        RedBoxDialog redBoxDialog = this.mRedBoxDialog;
        if (redBoxDialog != null) {
            redBoxDialog.dismiss();
            this.mRedBoxDialog = null;
        }
    }

    @Override // com.facebook.react.devsupport.interfaces.DevSupportManager
    public View createRootView(String appKey) {
        return this.mReactInstanceDevHelper.createRootView(appKey);
    }

    @Override // com.facebook.react.devsupport.interfaces.DevSupportManager
    public void destroyRootView(View rootView) {
        this.mReactInstanceDevHelper.destroyRootView(rootView);
    }

    private void hideDevOptionsDialog() {
        AlertDialog alertDialog = this.mDevOptionsDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.mDevOptionsDialog = null;
        }
    }

    private void showNewError(final String message, final StackFrame[] stack, final int errorCookie, final ErrorType errorType) {
        UiThreadUtil.runOnUiThread(new Runnable() { // from class: com.facebook.react.devsupport.DevSupportManagerBase.6
            @Override // java.lang.Runnable
            public void run() {
                Activity currentActivity = DevSupportManagerBase.this.mReactInstanceDevHelper.getCurrentActivity();
                if (DevSupportManagerBase.this.mRedBoxDialog == null || currentActivity != DevSupportManagerBase.this.currentActivity) {
                    if (currentActivity != null && !currentActivity.isFinishing()) {
                        DevSupportManagerBase.this.currentActivity = currentActivity;
                        DevSupportManagerBase devSupportManagerBase = DevSupportManagerBase.this;
                        Activity activity = DevSupportManagerBase.this.currentActivity;
                        DevSupportManagerBase devSupportManagerBase2 = DevSupportManagerBase.this;
                        devSupportManagerBase.mRedBoxDialog = new RedBoxDialog(activity, devSupportManagerBase2, devSupportManagerBase2.mRedBoxHandler);
                    } else {
                        FLog.e(ReactConstants.TAG, "Unable to launch redbox because react activity is not available, here is the error that redbox would've displayed: " + message);
                        return;
                    }
                }
                if (DevSupportManagerBase.this.mRedBoxDialog.isShowing()) {
                    return;
                }
                Pair processErrorCustomizers = DevSupportManagerBase.this.processErrorCustomizers(Pair.create(message, stack));
                DevSupportManagerBase.this.mRedBoxDialog.setExceptionDetails((String) processErrorCustomizers.first, (StackFrame[]) processErrorCustomizers.second);
                DevSupportManagerBase.this.updateLastErrorInfo(message, stack, errorCookie, errorType);
                if (DevSupportManagerBase.this.mRedBoxHandler != null && errorType == ErrorType.NATIVE) {
                    DevSupportManagerBase.this.mRedBoxHandler.handleRedbox(message, stack, ErrorType.NATIVE);
                }
                DevSupportManagerBase.this.mRedBoxDialog.resetReporting();
                DevSupportManagerBase.this.mRedBoxDialog.show();
            }
        });
    }

    @Override // com.facebook.react.devsupport.interfaces.DevSupportManager
    public void showDevOptionsDialog() {
        String str;
        String str2;
        String str3;
        if (this.mDevOptionsDialog != null || !this.mIsDevSupportEnabled || ActivityManager.isUserAMonkey()) {
            return;
        }
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        linkedHashMap.put(this.mApplicationContext.getString(R.string.catalyst_reload), new DevOptionHandler() { // from class: com.facebook.react.devsupport.DevSupportManagerBase.7
            @Override // com.facebook.react.devsupport.interfaces.DevOptionHandler
            public void onOptionSelected() {
                if (!DevSupportManagerBase.this.mDevSettings.isJSDevModeEnabled() && DevSupportManagerBase.this.mDevSettings.isHotModuleReplacementEnabled()) {
                    Toast.makeText(DevSupportManagerBase.this.mApplicationContext, DevSupportManagerBase.this.mApplicationContext.getString(R.string.catalyst_hot_reloading_auto_disable), 1).show();
                    DevSupportManagerBase.this.mDevSettings.setHotModuleReplacementEnabled(false);
                }
                DevSupportManagerBase.this.handleReloadJS();
            }
        });
        if (this.mDevSettings.isDeviceDebugEnabled()) {
            if (this.mDevSettings.isRemoteJSDebugEnabled()) {
                this.mDevSettings.setRemoteJSDebugEnabled(false);
                handleReloadJS();
            }
            linkedHashMap.put(this.mApplicationContext.getString(R.string.catalyst_debug_open), new DevOptionHandler() { // from class: com.facebook.react.devsupport.DevSupportManagerBase.8
                @Override // com.facebook.react.devsupport.interfaces.DevOptionHandler
                public void onOptionSelected() {
                    DevSupportManagerBase.this.mDevServerHelper.openUrl(DevSupportManagerBase.this.mCurrentContext, DevSupportManagerBase.FLIPPER_DEBUGGER_URL, DevSupportManagerBase.this.mApplicationContext.getString(R.string.catalyst_open_flipper_error));
                }
            });
            linkedHashMap.put(this.mApplicationContext.getString(R.string.catalyst_devtools_open), new DevOptionHandler() { // from class: com.facebook.react.devsupport.DevSupportManagerBase.9
                @Override // com.facebook.react.devsupport.interfaces.DevOptionHandler
                public void onOptionSelected() {
                    DevSupportManagerBase.this.mDevServerHelper.openUrl(DevSupportManagerBase.this.mCurrentContext, DevSupportManagerBase.FLIPPER_DEVTOOLS_URL, DevSupportManagerBase.this.mApplicationContext.getString(R.string.catalyst_open_flipper_error));
                }
            });
        }
        linkedHashMap.put(this.mApplicationContext.getString(R.string.catalyst_change_bundle_location), new DevOptionHandler() { // from class: com.facebook.react.devsupport.DevSupportManagerBase.10
            @Override // com.facebook.react.devsupport.interfaces.DevOptionHandler
            public void onOptionSelected() {
                Activity currentActivity = DevSupportManagerBase.this.mReactInstanceDevHelper.getCurrentActivity();
                if (currentActivity == null || currentActivity.isFinishing()) {
                    FLog.e(ReactConstants.TAG, "Unable to launch change bundle location because react activity is not available");
                    return;
                }
                final EditText editText = new EditText(currentActivity);
                editText.setHint("localhost:8081");
                new AlertDialog.Builder(currentActivity).setTitle(DevSupportManagerBase.this.mApplicationContext.getString(R.string.catalyst_change_bundle_location)).setView(editText).setPositiveButton(17039370, new DialogInterface.OnClickListener() { // from class: com.facebook.react.devsupport.DevSupportManagerBase.10.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        DevSupportManagerBase.this.mDevSettings.getPackagerConnectionSettings().setDebugServerHost(editText.getText().toString());
                        DevSupportManagerBase.this.handleReloadJS();
                    }
                }).create().show();
            }
        });
        if (this.mDevSettings.isElementInspectorEnabled()) {
            str = this.mApplicationContext.getString(R.string.catalyst_inspector_stop);
        } else {
            str = this.mApplicationContext.getString(R.string.catalyst_inspector);
        }
        linkedHashMap.put(str, new DevOptionHandler() { // from class: com.facebook.react.devsupport.DevSupportManagerBase.11
            @Override // com.facebook.react.devsupport.interfaces.DevOptionHandler
            public void onOptionSelected() {
                DevSupportManagerBase.this.mDevSettings.setElementInspectorEnabled(!DevSupportManagerBase.this.mDevSettings.isElementInspectorEnabled());
                DevSupportManagerBase.this.mReactInstanceDevHelper.toggleElementInspector();
            }
        });
        if (this.mDevSettings.isHotModuleReplacementEnabled()) {
            str2 = this.mApplicationContext.getString(R.string.catalyst_hot_reloading_stop);
        } else {
            str2 = this.mApplicationContext.getString(R.string.catalyst_hot_reloading);
        }
        linkedHashMap.put(str2, new DevOptionHandler() { // from class: com.facebook.react.devsupport.DevSupportManagerBase.12
            @Override // com.facebook.react.devsupport.interfaces.DevOptionHandler
            public void onOptionSelected() {
                boolean z = !DevSupportManagerBase.this.mDevSettings.isHotModuleReplacementEnabled();
                DevSupportManagerBase.this.mDevSettings.setHotModuleReplacementEnabled(z);
                if (DevSupportManagerBase.this.mCurrentContext != null) {
                    if (z) {
                        ((HMRClient) DevSupportManagerBase.this.mCurrentContext.getJSModule(HMRClient.class)).enable();
                    } else {
                        ((HMRClient) DevSupportManagerBase.this.mCurrentContext.getJSModule(HMRClient.class)).disable();
                    }
                }
                if (!z || DevSupportManagerBase.this.mDevSettings.isJSDevModeEnabled()) {
                    return;
                }
                Toast.makeText(DevSupportManagerBase.this.mApplicationContext, DevSupportManagerBase.this.mApplicationContext.getString(R.string.catalyst_hot_reloading_auto_enable), 1).show();
                DevSupportManagerBase.this.mDevSettings.setJSDevModeEnabled(true);
                DevSupportManagerBase.this.handleReloadJS();
            }
        });
        if (this.mDevSettings.isFpsDebugEnabled()) {
            str3 = this.mApplicationContext.getString(R.string.catalyst_perf_monitor_stop);
        } else {
            str3 = this.mApplicationContext.getString(R.string.catalyst_perf_monitor);
        }
        linkedHashMap.put(str3, new DevOptionHandler() { // from class: com.facebook.react.devsupport.DevSupportManagerBase.13
            @Override // com.facebook.react.devsupport.interfaces.DevOptionHandler
            public void onOptionSelected() {
                if (!DevSupportManagerBase.this.mDevSettings.isFpsDebugEnabled()) {
                    Activity currentActivity = DevSupportManagerBase.this.mReactInstanceDevHelper.getCurrentActivity();
                    if (currentActivity == null) {
                        FLog.e(ReactConstants.TAG, "Unable to get reference to react activity");
                    } else {
                        DebugOverlayController.requestPermission(currentActivity);
                    }
                }
                DevSupportManagerBase.this.mDevSettings.setFpsDebugEnabled(!DevSupportManagerBase.this.mDevSettings.isFpsDebugEnabled());
            }
        });
        linkedHashMap.put(this.mApplicationContext.getString(R.string.catalyst_settings), new DevOptionHandler() { // from class: com.facebook.react.devsupport.DevSupportManagerBase.14
            @Override // com.facebook.react.devsupport.interfaces.DevOptionHandler
            public void onOptionSelected() {
                Intent intent = new Intent(DevSupportManagerBase.this.mApplicationContext, DevSettingsActivity.class);
                intent.setFlags(268435456);
                DevSupportManagerBase.this.mApplicationContext.startActivity(intent);
            }
        });
        if (this.mCustomDevOptions.size() > 0) {
            linkedHashMap.putAll(this.mCustomDevOptions);
        }
        final DevOptionHandler[] devOptionHandlerArr = (DevOptionHandler[]) linkedHashMap.values().toArray(new DevOptionHandler[0]);
        Activity currentActivity = this.mReactInstanceDevHelper.getCurrentActivity();
        if (currentActivity == null || currentActivity.isFinishing()) {
            FLog.e(ReactConstants.TAG, "Unable to launch dev options menu because react activity isn't available");
            return;
        }
        TextView textView = new TextView(getApplicationContext());
        textView.setText("React Native DevMenu (" + getUniqueTag() + ")");
        textView.setPadding(0, 50, 0, 0);
        textView.setGravity(17);
        textView.setTextColor(ViewCompat.MEASURED_STATE_MASK);
        textView.setTextSize(17.0f);
        textView.setTypeface(textView.getTypeface(), 1);
        AlertDialog create = new AlertDialog.Builder(currentActivity).setCustomTitle(textView).setItems((CharSequence[]) linkedHashMap.keySet().toArray(new String[0]), new DialogInterface.OnClickListener() { // from class: com.facebook.react.devsupport.DevSupportManagerBase.16
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                devOptionHandlerArr[which].onOptionSelected();
                DevSupportManagerBase.this.mDevOptionsDialog = null;
            }
        }).setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.facebook.react.devsupport.DevSupportManagerBase.15
            @Override // android.content.DialogInterface.OnCancelListener
            public void onCancel(DialogInterface dialog) {
                DevSupportManagerBase.this.mDevOptionsDialog = null;
            }
        }).create();
        this.mDevOptionsDialog = create;
        create.show();
        ReactContext reactContext = this.mCurrentContext;
        if (reactContext == null) {
            return;
        }
        ((RCTNativeAppEventEmitter) reactContext.getJSModule(RCTNativeAppEventEmitter.class)).emit("RCTDevMenuShown", null);
    }

    @Override // com.facebook.react.devsupport.interfaces.DevSupportManager
    public void setDevSupportEnabled(boolean isDevSupportEnabled) {
        this.mIsDevSupportEnabled = isDevSupportEnabled;
        reloadSettings();
    }

    @Override // com.facebook.react.devsupport.interfaces.DevSupportManager
    public boolean getDevSupportEnabled() {
        return this.mIsDevSupportEnabled;
    }

    @Override // com.facebook.react.devsupport.interfaces.DevSupportManager
    public DevInternalSettings getDevSettings() {
        return this.mDevSettings;
    }

    @Override // com.facebook.react.devsupport.interfaces.DevSupportManager
    public void onNewReactContextCreated(ReactContext reactContext) {
        resetCurrentContext(reactContext);
    }

    @Override // com.facebook.react.devsupport.interfaces.DevSupportManager
    public void onReactInstanceDestroyed(ReactContext reactContext) {
        if (reactContext == this.mCurrentContext) {
            resetCurrentContext(null);
        }
    }

    @Override // com.facebook.react.devsupport.interfaces.DevSupportManager
    public String getSourceMapUrl() {
        String str = this.mJSAppBundleName;
        return str == null ? "" : this.mDevServerHelper.getSourceMapUrl((String) Assertions.assertNotNull(str));
    }

    @Override // com.facebook.react.devsupport.interfaces.DevSupportManager
    public String getSourceUrl() {
        String str = this.mJSAppBundleName;
        return str == null ? "" : this.mDevServerHelper.getSourceUrl((String) Assertions.assertNotNull(str));
    }

    @Override // com.facebook.react.devsupport.interfaces.DevSupportManager
    public String getJSBundleURLForRemoteDebugging() {
        return this.mDevServerHelper.getJSBundleURLForRemoteDebugging((String) Assertions.assertNotNull(this.mJSAppBundleName));
    }

    @Override // com.facebook.react.devsupport.interfaces.DevSupportManager
    public String getDownloadedJSBundleFile() {
        return this.mJSBundleDownloadedFile.getAbsolutePath();
    }

    @Override // com.facebook.react.devsupport.interfaces.DevSupportManager
    public boolean hasUpToDateJSBundleInCache() {
        if (this.mIsDevSupportEnabled && this.mJSBundleDownloadedFile.exists()) {
            try {
                String packageName = this.mApplicationContext.getPackageName();
                if (this.mJSBundleDownloadedFile.lastModified() > this.mApplicationContext.getPackageManager().getPackageInfo(packageName, 0).lastUpdateTime) {
                    File file = new File(String.format(Locale.US, EXOPACKAGE_LOCATION_FORMAT, packageName));
                    if (!file.exists()) {
                        return true;
                    }
                    return this.mJSBundleDownloadedFile.lastModified() > file.lastModified();
                }
            } catch (PackageManager.NameNotFoundException unused) {
                FLog.e(ReactConstants.TAG, "DevSupport is unable to get current app info");
            }
        }
        return false;
    }

    private void resetCurrentContext(ReactContext reactContext) {
        if (this.mCurrentContext == reactContext) {
            return;
        }
        this.mCurrentContext = reactContext;
        DebugOverlayController debugOverlayController = this.mDebugOverlayController;
        if (debugOverlayController != null) {
            debugOverlayController.setFpsDebugViewVisible(false);
        }
        if (reactContext != null) {
            this.mDebugOverlayController = new DebugOverlayController(reactContext);
        }
        if (this.mCurrentContext != null) {
            try {
                URL url = new URL(getSourceUrl());
                ((HMRClient) this.mCurrentContext.getJSModule(HMRClient.class)).setup("android", url.getPath().substring(1), url.getHost(), url.getPort(), this.mDevSettings.isHotModuleReplacementEnabled());
            } catch (MalformedURLException e) {
                showNewJavaError(e.getMessage(), e);
            }
        }
        reloadSettings();
    }

    @Override // com.facebook.react.devsupport.interfaces.DevSupportManager
    public void reloadSettings() {
        if (UiThreadUtil.isOnUiThread()) {
            reload();
        } else {
            UiThreadUtil.runOnUiThread(new Runnable() { // from class: com.facebook.react.devsupport.DevSupportManagerBase.17
                @Override // java.lang.Runnable
                public void run() {
                    DevSupportManagerBase.this.reload();
                }
            });
        }
    }

    public ReactContext getCurrentContext() {
        return this.mCurrentContext;
    }

    public String getJSAppBundleName() {
        return this.mJSAppBundleName;
    }

    public Context getApplicationContext() {
        return this.mApplicationContext;
    }

    public DevServerHelper getDevServerHelper() {
        return this.mDevServerHelper;
    }

    public ReactInstanceDevHelper getReactInstanceDevHelper() {
        return this.mReactInstanceDevHelper;
    }

    private void showDevLoadingViewForUrl(String bundleUrl) {
        this.mDevLoadingViewController.showForUrl(bundleUrl);
        this.mDevLoadingViewVisible = true;
    }

    public void showDevLoadingViewForRemoteJSEnabled() {
        this.mDevLoadingViewController.showForRemoteJSEnabled();
        this.mDevLoadingViewVisible = true;
    }

    public void hideDevLoadingView() {
        this.mDevLoadingViewController.hide();
        this.mDevLoadingViewVisible = false;
    }

    public void fetchSplitBundleAndCreateBundleLoader(String bundlePath, final CallbackWithBundleLoader callback) {
        String devServerSplitBundleURL = this.mDevServerHelper.getDevServerSplitBundleURL(bundlePath);
        File file = this.mJSSplitBundlesDir;
        UiThreadUtil.runOnUiThread(new AnonymousClass18(devServerSplitBundleURL, new File(file, bundlePath.replaceAll("/", "_") + ".jsbundle"), callback));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.facebook.react.devsupport.DevSupportManagerBase$18 */
    /* loaded from: classes.dex */
    public class AnonymousClass18 implements Runnable {
        final /* synthetic */ File val$bundleFile;
        final /* synthetic */ String val$bundleUrl;
        final /* synthetic */ CallbackWithBundleLoader val$callback;

        AnonymousClass18(final String val$callback, final File val$bundleFile, final CallbackWithBundleLoader val$bundleUrl) {
            DevSupportManagerBase.this = this$0;
            this.val$bundleUrl = val$callback;
            this.val$bundleFile = val$bundleFile;
            this.val$callback = val$bundleUrl;
        }

        @Override // java.lang.Runnable
        public void run() {
            DevSupportManagerBase.this.showSplitBundleDevLoadingView(this.val$bundleUrl);
            DevSupportManagerBase.this.mDevServerHelper.downloadBundleFromURL(new DevBundleDownloadListener() { // from class: com.facebook.react.devsupport.DevSupportManagerBase.18.1
                @Override // com.facebook.react.devsupport.interfaces.DevBundleDownloadListener
                public void onSuccess() {
                    UiThreadUtil.runOnUiThread(new Runnable() { // from class: com.facebook.react.devsupport.DevSupportManagerBase.18.1.1
                        @Override // java.lang.Runnable
                        public void run() {
                            DevSupportManagerBase.this.hideSplitBundleDevLoadingView();
                        }
                    });
                    ReactContext reactContext = DevSupportManagerBase.this.mCurrentContext;
                    if (reactContext == null || !reactContext.hasActiveReactInstance()) {
                        return;
                    }
                    AnonymousClass18.this.val$callback.onSuccess(JSBundleLoader.createCachedSplitBundleFromNetworkLoader(AnonymousClass18.this.val$bundleUrl, AnonymousClass18.this.val$bundleFile.getAbsolutePath()));
                }

                @Override // com.facebook.react.devsupport.interfaces.DevBundleDownloadListener
                public void onProgress(String status, Integer done, Integer total) {
                    DevSupportManagerBase.this.mDevLoadingViewController.updateProgress(status, done, total);
                }

                @Override // com.facebook.react.devsupport.interfaces.DevBundleDownloadListener
                public void onFailure(Exception cause) {
                    UiThreadUtil.runOnUiThread(new Runnable() { // from class: com.facebook.react.devsupport.DevSupportManagerBase.18.1.2
                        @Override // java.lang.Runnable
                        public void run() {
                            DevSupportManagerBase.this.hideSplitBundleDevLoadingView();
                        }
                    });
                    AnonymousClass18.this.val$callback.onError(AnonymousClass18.this.val$bundleUrl, cause);
                }
            }, this.val$bundleFile, this.val$bundleUrl, null);
        }
    }

    public void showSplitBundleDevLoadingView(String bundleUrl) {
        showDevLoadingViewForUrl(bundleUrl);
        this.mPendingJSSplitBundleRequests++;
    }

    public void hideSplitBundleDevLoadingView() {
        int i = this.mPendingJSSplitBundleRequests - 1;
        this.mPendingJSSplitBundleRequests = i;
        if (i == 0) {
            hideDevLoadingView();
        }
    }

    @Override // com.facebook.react.devsupport.interfaces.DevSupportManager
    public void isPackagerRunning(final PackagerStatusCallback callback) {
        Runnable runnable = new Runnable() { // from class: com.facebook.react.devsupport.DevSupportManagerBase.19
            @Override // java.lang.Runnable
            public void run() {
                DevSupportManagerBase.this.mDevServerHelper.isPackagerRunning(callback);
            }
        };
        DevSupportManager.PackagerLocationCustomizer packagerLocationCustomizer = this.mPackagerLocationCustomizer;
        if (packagerLocationCustomizer != null) {
            packagerLocationCustomizer.run(runnable);
        } else {
            runnable.run();
        }
    }

    @Override // com.facebook.react.devsupport.interfaces.DevSupportManager
    public File downloadBundleResourceFromUrlSync(final String resourceURL, final File outputFile) {
        return this.mDevServerHelper.downloadBundleResourceFromUrlSync(resourceURL, outputFile);
    }

    @Override // com.facebook.react.devsupport.interfaces.DevSupportManager
    public String getLastErrorTitle() {
        return this.mLastErrorTitle;
    }

    @Override // com.facebook.react.devsupport.interfaces.DevSupportManager
    public StackFrame[] getLastErrorStack() {
        return this.mLastErrorStack;
    }

    @Override // com.facebook.react.devsupport.interfaces.DevSupportManager
    public ErrorType getLastErrorType() {
        return this.mLastErrorType;
    }

    public void handleCaptureHeap(final Responder responder) {
        JSCHeapCapture jSCHeapCapture;
        ReactContext reactContext = this.mCurrentContext;
        if (reactContext == null || (jSCHeapCapture = (JSCHeapCapture) reactContext.getNativeModule(JSCHeapCapture.class)) == null) {
            return;
        }
        jSCHeapCapture.captureHeap(this.mApplicationContext.getCacheDir().getPath(), new JSCHeapCapture.CaptureCallback() { // from class: com.facebook.react.devsupport.DevSupportManagerBase.20
            @Override // com.facebook.react.devsupport.JSCHeapCapture.CaptureCallback
            public void onSuccess(File capture) {
                responder.respond(capture.toString());
            }

            @Override // com.facebook.react.devsupport.JSCHeapCapture.CaptureCallback
            public void onFailure(JSCHeapCapture.CaptureException error) {
                responder.error(error.toString());
            }
        });
    }

    public void updateLastErrorInfo(final String message, final StackFrame[] stack, final int errorCookie, final ErrorType errorType) {
        this.mLastErrorTitle = message;
        this.mLastErrorStack = stack;
        this.mLastErrorCookie = errorCookie;
        this.mLastErrorType = errorType;
    }

    @Override // com.facebook.react.devsupport.interfaces.DevSupportManager
    public void reloadJSFromServer(final String bundleURL) {
        reloadJSFromServer(bundleURL, new BundleLoadCallback() { // from class: com.facebook.react.devsupport.DevSupportManagerBase.21
            @Override // com.facebook.react.devsupport.interfaces.BundleLoadCallback
            public void onSuccess() {
                UiThreadUtil.runOnUiThread(new Runnable() { // from class: com.facebook.react.devsupport.DevSupportManagerBase.21.1
                    @Override // java.lang.Runnable
                    public void run() {
                        DevSupportManagerBase.this.mReactInstanceDevHelper.onJSBundleLoadedFromServer();
                    }
                });
            }
        });
    }

    @Override // com.facebook.react.devsupport.interfaces.DevSupportManager
    public void reloadJSFromServer(final String bundleURL, final BundleLoadCallback callback) {
        ReactMarker.logMarker(ReactMarkerConstants.DOWNLOAD_START);
        showDevLoadingViewForUrl(bundleURL);
        final BundleDownloader.BundleInfo bundleInfo = new BundleDownloader.BundleInfo();
        this.mDevServerHelper.downloadBundleFromURL(new DevBundleDownloadListener() { // from class: com.facebook.react.devsupport.DevSupportManagerBase.22
            @Override // com.facebook.react.devsupport.interfaces.DevBundleDownloadListener
            public void onSuccess() {
                DevSupportManagerBase.this.hideDevLoadingView();
                synchronized (DevSupportManagerBase.this) {
                    DevSupportManagerBase.this.mBundleStatus.isLastDownloadSucess = true;
                    DevSupportManagerBase.this.mBundleStatus.updateTimestamp = System.currentTimeMillis();
                }
                if (DevSupportManagerBase.this.mBundleDownloadListener != null) {
                    DevSupportManagerBase.this.mBundleDownloadListener.onSuccess();
                }
                ReactMarker.logMarker(ReactMarkerConstants.DOWNLOAD_END, bundleInfo.toJSONString());
                callback.onSuccess();
            }

            @Override // com.facebook.react.devsupport.interfaces.DevBundleDownloadListener
            public void onProgress(final String status, final Integer done, final Integer total) {
                DevSupportManagerBase.this.mDevLoadingViewController.updateProgress(status, done, total);
                if (DevSupportManagerBase.this.mBundleDownloadListener != null) {
                    DevSupportManagerBase.this.mBundleDownloadListener.onProgress(status, done, total);
                }
            }

            @Override // com.facebook.react.devsupport.interfaces.DevBundleDownloadListener
            public void onFailure(final Exception cause) {
                DevSupportManagerBase.this.hideDevLoadingView();
                synchronized (DevSupportManagerBase.this) {
                    DevSupportManagerBase.this.mBundleStatus.isLastDownloadSucess = false;
                }
                if (DevSupportManagerBase.this.mBundleDownloadListener != null) {
                    DevSupportManagerBase.this.mBundleDownloadListener.onFailure(cause);
                }
                FLog.e(ReactConstants.TAG, "Unable to download JS bundle", cause);
                DevSupportManagerBase.this.reportBundleLoadingFailure(cause);
            }
        }, this.mJSBundleDownloadedFile, bundleURL, bundleInfo);
    }

    public void reportBundleLoadingFailure(final Exception cause) {
        UiThreadUtil.runOnUiThread(new Runnable() { // from class: com.facebook.react.devsupport.DevSupportManagerBase.23
            @Override // java.lang.Runnable
            public void run() {
                Exception exc = cause;
                if (exc instanceof DebugServerException) {
                    DevSupportManagerBase.this.showNewJavaError(((DebugServerException) exc).getMessage(), cause);
                    return;
                }
                DevSupportManagerBase devSupportManagerBase = DevSupportManagerBase.this;
                devSupportManagerBase.showNewJavaError(devSupportManagerBase.mApplicationContext.getString(R.string.catalyst_reload_error), cause);
            }
        });
    }

    @Override // com.facebook.react.devsupport.interfaces.DevSupportManager
    public void startInspector() {
        if (this.mIsDevSupportEnabled) {
            this.mDevServerHelper.openInspectorConnection();
        }
    }

    @Override // com.facebook.react.devsupport.interfaces.DevSupportManager
    public void stopInspector() {
        this.mDevServerHelper.closeInspectorConnection();
    }

    @Override // com.facebook.react.devsupport.interfaces.DevSupportManager
    public void setHotModuleReplacementEnabled(final boolean isHotModuleReplacementEnabled) {
        if (!this.mIsDevSupportEnabled) {
            return;
        }
        UiThreadUtil.runOnUiThread(new Runnable() { // from class: com.facebook.react.devsupport.DevSupportManagerBase.24
            @Override // java.lang.Runnable
            public void run() {
                DevSupportManagerBase.this.mDevSettings.setHotModuleReplacementEnabled(isHotModuleReplacementEnabled);
                DevSupportManagerBase.this.handleReloadJS();
            }
        });
    }

    @Override // com.facebook.react.devsupport.interfaces.DevSupportManager
    public void setRemoteJSDebugEnabled(final boolean isRemoteJSDebugEnabled) {
        if (!this.mIsDevSupportEnabled) {
            return;
        }
        UiThreadUtil.runOnUiThread(new Runnable() { // from class: com.facebook.react.devsupport.DevSupportManagerBase.25
            @Override // java.lang.Runnable
            public void run() {
                DevSupportManagerBase.this.mDevSettings.setRemoteJSDebugEnabled(isRemoteJSDebugEnabled);
                DevSupportManagerBase.this.handleReloadJS();
            }
        });
    }

    @Override // com.facebook.react.devsupport.interfaces.DevSupportManager
    public void setFpsDebugEnabled(final boolean isFpsDebugEnabled) {
        if (!this.mIsDevSupportEnabled) {
            return;
        }
        UiThreadUtil.runOnUiThread(new Runnable() { // from class: com.facebook.react.devsupport.DevSupportManagerBase.26
            @Override // java.lang.Runnable
            public void run() {
                DevSupportManagerBase.this.mDevSettings.setFpsDebugEnabled(isFpsDebugEnabled);
            }
        });
    }

    @Override // com.facebook.react.devsupport.interfaces.DevSupportManager
    public void toggleElementInspector() {
        if (!this.mIsDevSupportEnabled) {
            return;
        }
        UiThreadUtil.runOnUiThread(new Runnable() { // from class: com.facebook.react.devsupport.DevSupportManagerBase.27
            @Override // java.lang.Runnable
            public void run() {
                DevSupportManagerBase.this.mDevSettings.setElementInspectorEnabled(!DevSupportManagerBase.this.mDevSettings.isElementInspectorEnabled());
                DevSupportManagerBase.this.mReactInstanceDevHelper.toggleElementInspector();
            }
        });
    }

    public void reload() {
        UiThreadUtil.assertOnUiThread();
        if (this.mIsDevSupportEnabled) {
            DebugOverlayController debugOverlayController = this.mDebugOverlayController;
            if (debugOverlayController != null) {
                debugOverlayController.setFpsDebugViewVisible(this.mDevSettings.isFpsDebugEnabled());
            }
            if (!this.mIsShakeDetectorStarted) {
                this.mShakeDetector.start((SensorManager) this.mApplicationContext.getSystemService("sensor"));
                this.mIsShakeDetectorStarted = true;
            }
            if (!this.mIsReceiverRegistered) {
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(getReloadAppAction(this.mApplicationContext));
                this.mApplicationContext.registerReceiver(this.mReloadAppBroadcastReceiver, intentFilter);
                this.mIsReceiverRegistered = true;
            }
            if (this.mDevLoadingViewVisible) {
                this.mDevLoadingViewController.showMessage("Reloading...");
            }
            this.mDevServerHelper.openPackagerConnection(getClass().getSimpleName(), new DevServerHelper.PackagerCommandListener() { // from class: com.facebook.react.devsupport.DevSupportManagerBase.28
                @Override // com.facebook.react.devsupport.DevServerHelper.PackagerCommandListener
                public void onPackagerConnected() {
                }

                @Override // com.facebook.react.devsupport.DevServerHelper.PackagerCommandListener
                public void onPackagerDisconnected() {
                }

                @Override // com.facebook.react.devsupport.DevServerHelper.PackagerCommandListener
                public void onPackagerReloadCommand() {
                    DevSupportManagerBase.this.mDevServerHelper.disableDebugger();
                    UiThreadUtil.runOnUiThread(new Runnable() { // from class: com.facebook.react.devsupport.DevSupportManagerBase.28.1
                        @Override // java.lang.Runnable
                        public void run() {
                            DevSupportManagerBase.this.handleReloadJS();
                        }
                    });
                }

                @Override // com.facebook.react.devsupport.DevServerHelper.PackagerCommandListener
                public void onPackagerDevMenuCommand() {
                    UiThreadUtil.runOnUiThread(new Runnable() { // from class: com.facebook.react.devsupport.DevSupportManagerBase.28.2
                        @Override // java.lang.Runnable
                        public void run() {
                            DevSupportManagerBase.this.showDevOptionsDialog();
                        }
                    });
                }

                @Override // com.facebook.react.devsupport.DevServerHelper.PackagerCommandListener
                public void onCaptureHeapCommand(final Responder responder) {
                    UiThreadUtil.runOnUiThread(new Runnable() { // from class: com.facebook.react.devsupport.DevSupportManagerBase.28.3
                        @Override // java.lang.Runnable
                        public void run() {
                            DevSupportManagerBase.this.handleCaptureHeap(responder);
                        }
                    });
                }

                @Override // com.facebook.react.devsupport.DevServerHelper.PackagerCommandListener
                public Map<String, RequestHandler> customCommandHandlers() {
                    return DevSupportManagerBase.this.mCustomPackagerCommandHandlers;
                }
            });
            return;
        }
        DebugOverlayController debugOverlayController2 = this.mDebugOverlayController;
        if (debugOverlayController2 != null) {
            debugOverlayController2.setFpsDebugViewVisible(false);
        }
        if (this.mIsShakeDetectorStarted) {
            this.mShakeDetector.stop();
            this.mIsShakeDetectorStarted = false;
        }
        if (this.mIsReceiverRegistered) {
            this.mApplicationContext.unregisterReceiver(this.mReloadAppBroadcastReceiver);
            this.mIsReceiverRegistered = false;
        }
        hideRedboxDialog();
        hideDevOptionsDialog();
        this.mDevLoadingViewController.hide();
        this.mDevServerHelper.closePackagerConnection();
    }

    public static String getReloadAppAction(Context context) {
        return context.getPackageName() + RELOAD_APP_ACTION_SUFFIX;
    }

    @Override // com.facebook.react.devsupport.interfaces.DevSupportManager
    public void setPackagerLocationCustomizer(DevSupportManager.PackagerLocationCustomizer packagerLocationCustomizer) {
        this.mPackagerLocationCustomizer = packagerLocationCustomizer;
    }
}
