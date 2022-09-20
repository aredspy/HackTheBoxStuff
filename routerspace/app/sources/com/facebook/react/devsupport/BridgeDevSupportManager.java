package com.facebook.react.devsupport;

import android.content.Context;
import android.widget.Toast;
import com.facebook.common.logging.FLog;
import com.facebook.debug.holder.PrinterHolder;
import com.facebook.debug.tags.ReactDebugOverlayTags;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.R;
import com.facebook.react.bridge.JSBundleLoader;
import com.facebook.react.bridge.JavaJSExecutor;
import com.facebook.react.bridge.JavaScriptExecutorFactory;
import com.facebook.react.bridge.ReactMarker;
import com.facebook.react.bridge.ReactMarkerConstants;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.common.ReactConstants;
import com.facebook.react.common.futures.SimpleSettableFuture;
import com.facebook.react.devsupport.DevSupportManagerBase;
import com.facebook.react.devsupport.WebsocketJavaScriptExecutor;
import com.facebook.react.devsupport.interfaces.DevBundleDownloadListener;
import com.facebook.react.devsupport.interfaces.DevOptionHandler;
import com.facebook.react.devsupport.interfaces.DevSplitBundleCallback;
import com.facebook.react.packagerconnection.RequestHandler;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
/* loaded from: classes.dex */
public final class BridgeDevSupportManager extends DevSupportManagerBase {
    private boolean mIsSamplingProfilerEnabled = false;

    @Override // com.facebook.react.devsupport.DevSupportManagerBase
    protected String getUniqueTag() {
        return "Bridge";
    }

    public BridgeDevSupportManager(Context applicationContext, ReactInstanceDevHelper reactInstanceManagerHelper, String packagerPathForJSBundleName, boolean enableOnCreate, RedBoxHandler redBoxHandler, DevBundleDownloadListener devBundleDownloadListener, int minNumShakes, Map<String, RequestHandler> customPackagerCommandHandlers) {
        super(applicationContext, reactInstanceManagerHelper, packagerPathForJSBundleName, enableOnCreate, redBoxHandler, devBundleDownloadListener, minNumShakes, customPackagerCommandHandlers);
        String str;
        String str2;
        if (getDevSettings().isStartSamplingProfilerOnInit()) {
            if (!this.mIsSamplingProfilerEnabled) {
                toggleJSSamplingProfiler();
            } else {
                Toast.makeText(applicationContext, "JS Sampling Profiler was already running, so did not start the sampling profiler", 1).show();
            }
        }
        if (this.mIsSamplingProfilerEnabled) {
            str = applicationContext.getString(R.string.catalyst_sample_profiler_disable);
        } else {
            str = applicationContext.getString(R.string.catalyst_sample_profiler_enable);
        }
        addCustomDevOption(str, new DevOptionHandler() { // from class: com.facebook.react.devsupport.BridgeDevSupportManager.1
            @Override // com.facebook.react.devsupport.interfaces.DevOptionHandler
            public void onOptionSelected() {
                BridgeDevSupportManager.this.toggleJSSamplingProfiler();
            }
        });
        if (!getDevSettings().isDeviceDebugEnabled()) {
            if (getDevSettings().isRemoteJSDebugEnabled()) {
                str2 = applicationContext.getString(R.string.catalyst_debug_stop);
            } else {
                str2 = applicationContext.getString(R.string.catalyst_debug);
            }
            addCustomDevOption(str2, new DevOptionHandler() { // from class: com.facebook.react.devsupport.BridgeDevSupportManager.2
                @Override // com.facebook.react.devsupport.interfaces.DevOptionHandler
                public void onOptionSelected() {
                    BridgeDevSupportManager.this.getDevSettings().setRemoteJSDebugEnabled(!BridgeDevSupportManager.this.getDevSettings().isRemoteJSDebugEnabled());
                    BridgeDevSupportManager.this.handleReloadJS();
                }
            });
        }
    }

    @Override // com.facebook.react.devsupport.interfaces.DevSupportManager
    public void loadSplitBundleFromServer(final String bundlePath, final DevSplitBundleCallback callback) {
        fetchSplitBundleAndCreateBundleLoader(bundlePath, new DevSupportManagerBase.CallbackWithBundleLoader() { // from class: com.facebook.react.devsupport.BridgeDevSupportManager.3
            @Override // com.facebook.react.devsupport.DevSupportManagerBase.CallbackWithBundleLoader
            public void onSuccess(JSBundleLoader bundleLoader) {
                bundleLoader.loadScript(BridgeDevSupportManager.this.getCurrentContext().getCatalystInstance());
                ((HMRClient) BridgeDevSupportManager.this.getCurrentContext().getJSModule(HMRClient.class)).registerBundle(BridgeDevSupportManager.this.getDevServerHelper().getDevServerSplitBundleURL(bundlePath));
                callback.onSuccess();
            }

            @Override // com.facebook.react.devsupport.DevSupportManagerBase.CallbackWithBundleLoader
            public void onError(String url, Throwable cause) {
                callback.onError(url, cause);
            }
        });
    }

    public WebsocketJavaScriptExecutor.JSExecutorConnectCallback getExecutorConnectCallback(final SimpleSettableFuture<Boolean> future) {
        return new WebsocketJavaScriptExecutor.JSExecutorConnectCallback() { // from class: com.facebook.react.devsupport.BridgeDevSupportManager.4
            @Override // com.facebook.react.devsupport.WebsocketJavaScriptExecutor.JSExecutorConnectCallback
            public void onSuccess() {
                future.set(true);
                BridgeDevSupportManager.this.hideDevLoadingView();
            }

            @Override // com.facebook.react.devsupport.WebsocketJavaScriptExecutor.JSExecutorConnectCallback
            public void onFailure(final Throwable cause) {
                BridgeDevSupportManager.this.hideDevLoadingView();
                FLog.e(ReactConstants.TAG, "Failed to connect to debugger!", cause);
                future.setException(new IOException(BridgeDevSupportManager.this.getApplicationContext().getString(R.string.catalyst_debug_error), cause));
            }
        };
    }

    private void reloadJSInProxyMode() {
        getDevServerHelper().launchJSDevtools();
        getReactInstanceDevHelper().onReloadWithJSDebugger(new JavaJSExecutor.Factory() { // from class: com.facebook.react.devsupport.BridgeDevSupportManager.5
            @Override // com.facebook.react.bridge.JavaJSExecutor.Factory
            public JavaJSExecutor create() throws Exception {
                Throwable e;
                WebsocketJavaScriptExecutor websocketJavaScriptExecutor = new WebsocketJavaScriptExecutor();
                SimpleSettableFuture simpleSettableFuture = new SimpleSettableFuture();
                websocketJavaScriptExecutor.connect(BridgeDevSupportManager.this.getDevServerHelper().getWebsocketProxyURL(), BridgeDevSupportManager.this.getExecutorConnectCallback(simpleSettableFuture));
                try {
                    simpleSettableFuture.get(90L, TimeUnit.SECONDS);
                    return websocketJavaScriptExecutor;
                } catch (InterruptedException e2) {
                    e = e2;
                    throw new RuntimeException(e);
                } catch (ExecutionException e3) {
                    throw ((Exception) e3.getCause());
                } catch (TimeoutException e4) {
                    e = e4;
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override // com.facebook.react.devsupport.interfaces.DevSupportManager
    public void handleReloadJS() {
        UiThreadUtil.assertOnUiThread();
        ReactMarker.logMarker(ReactMarkerConstants.RELOAD, getDevSettings().getPackagerConnectionSettings().getDebugServerHost());
        hideRedboxDialog();
        if (getDevSettings().isRemoteJSDebugEnabled()) {
            PrinterHolder.getPrinter().logMessage(ReactDebugOverlayTags.RN_CORE, "RNCore: load from Proxy");
            showDevLoadingViewForRemoteJSEnabled();
            reloadJSInProxyMode();
            return;
        }
        PrinterHolder.getPrinter().logMessage(ReactDebugOverlayTags.RN_CORE, "RNCore: load from Server");
        reloadJSFromServer(getDevServerHelper().getDevServerBundleURL((String) Assertions.assertNotNull(getJSAppBundleName())));
    }

    /* JADX WARN: Type inference failed for: r3v0, types: [int, boolean] */
    public void toggleJSSamplingProfiler() {
        JavaScriptExecutorFactory javaScriptExecutorFactory = getReactInstanceDevHelper().getJavaScriptExecutorFactory();
        boolean z = false;
        ?? r3 = 1;
        try {
            if (!this.mIsSamplingProfilerEnabled) {
                try {
                    try {
                        javaScriptExecutorFactory.startSamplingProfiler();
                        Toast.makeText(getApplicationContext(), "Starting Sampling Profiler", z ? 1 : 0).show();
                    } catch (UnsupportedOperationException unused) {
                        Context applicationContext = getApplicationContext();
                        Toast.makeText(applicationContext, javaScriptExecutorFactory.toString() + " does not support Sampling Profiler", (int) r3).show();
                    }
                    return;
                } finally {
                }
            }
            try {
                String path = File.createTempFile("sampling-profiler-trace", ".cpuprofile", getApplicationContext().getCacheDir()).getPath();
                javaScriptExecutorFactory.stopSamplingProfiler(path);
                Context applicationContext2 = getApplicationContext();
                Toast.makeText(applicationContext2, "Saved results from Profiler to " + path, (int) r3).show();
            } catch (IOException unused2) {
                FLog.e(ReactConstants.TAG, "Could not create temporary file for saving results from Sampling Profiler");
            } catch (UnsupportedOperationException unused3) {
                Context applicationContext3 = getApplicationContext();
                Toast.makeText(applicationContext3, javaScriptExecutorFactory.toString() + "does not support Sampling Profiler", r3 == true ? 1 : 0).show();
            }
        } finally {
        }
    }
}
