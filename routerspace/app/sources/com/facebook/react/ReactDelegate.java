package com.facebook.react;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.devsupport.DoubleTapReloadRecognizer;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
/* loaded from: classes.dex */
public class ReactDelegate {
    private final Activity mActivity;
    private DoubleTapReloadRecognizer mDoubleTapReloadRecognizer = new DoubleTapReloadRecognizer();
    private Bundle mLaunchOptions;
    private final String mMainComponentName;
    private ReactNativeHost mReactNativeHost;
    private ReactRootView mReactRootView;

    public ReactDelegate(Activity activity, ReactNativeHost reactNativeHost, String appKey, Bundle launchOptions) {
        this.mActivity = activity;
        this.mMainComponentName = appKey;
        this.mLaunchOptions = launchOptions;
        this.mReactNativeHost = reactNativeHost;
    }

    public void onHostResume() {
        if (getReactNativeHost().hasInstance()) {
            if (this.mActivity instanceof DefaultHardwareBackBtnHandler) {
                ReactInstanceManager reactInstanceManager = getReactNativeHost().getReactInstanceManager();
                Activity activity = this.mActivity;
                reactInstanceManager.onHostResume(activity, (DefaultHardwareBackBtnHandler) activity);
                return;
            }
            throw new ClassCastException("Host Activity does not implement DefaultHardwareBackBtnHandler");
        }
    }

    public void onHostPause() {
        if (getReactNativeHost().hasInstance()) {
            getReactNativeHost().getReactInstanceManager().onHostPause(this.mActivity);
        }
    }

    public void onHostDestroy() {
        ReactRootView reactRootView = this.mReactRootView;
        if (reactRootView != null) {
            reactRootView.unmountReactApplication();
            this.mReactRootView = null;
        }
        if (getReactNativeHost().hasInstance()) {
            getReactNativeHost().getReactInstanceManager().onHostDestroy(this.mActivity);
        }
    }

    public boolean onBackPressed() {
        if (getReactNativeHost().hasInstance()) {
            getReactNativeHost().getReactInstanceManager().onBackPressed();
            return true;
        }
        return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data, boolean shouldForwardToReactInstance) {
        if (!getReactNativeHost().hasInstance() || !shouldForwardToReactInstance) {
            return;
        }
        getReactNativeHost().getReactInstanceManager().onActivityResult(this.mActivity, requestCode, resultCode, data);
    }

    public void loadApp() {
        loadApp(this.mMainComponentName);
    }

    public void loadApp(String appKey) {
        if (this.mReactRootView != null) {
            throw new IllegalStateException("Cannot loadApp while app is already running.");
        }
        ReactRootView createRootView = createRootView();
        this.mReactRootView = createRootView;
        createRootView.startReactApplication(getReactNativeHost().getReactInstanceManager(), appKey, this.mLaunchOptions);
    }

    public ReactRootView getReactRootView() {
        return this.mReactRootView;
    }

    protected ReactRootView createRootView() {
        return new ReactRootView(this.mActivity);
    }

    public boolean shouldShowDevMenuOrReload(int keyCode, KeyEvent event) {
        if (!getReactNativeHost().hasInstance() || !getReactNativeHost().getUseDeveloperSupport()) {
            return false;
        }
        if (keyCode == 82) {
            getReactNativeHost().getReactInstanceManager().showDevOptionsDialog();
            return true;
        } else if (!((DoubleTapReloadRecognizer) Assertions.assertNotNull(this.mDoubleTapReloadRecognizer)).didDoubleTapR(keyCode, this.mActivity.getCurrentFocus())) {
            return false;
        } else {
            getReactNativeHost().getReactInstanceManager().getDevSupportManager().handleReloadJS();
            return true;
        }
    }

    private ReactNativeHost getReactNativeHost() {
        return this.mReactNativeHost;
    }

    public ReactInstanceManager getReactInstanceManager() {
        return getReactNativeHost().getReactInstanceManager();
    }
}
