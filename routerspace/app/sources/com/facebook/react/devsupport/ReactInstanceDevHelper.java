package com.facebook.react.devsupport;

import android.app.Activity;
import android.view.View;
import com.facebook.react.bridge.JavaJSExecutor;
import com.facebook.react.bridge.JavaScriptExecutorFactory;
/* loaded from: classes.dex */
public interface ReactInstanceDevHelper {
    View createRootView(String appKey);

    void destroyRootView(View rootView);

    Activity getCurrentActivity();

    JavaScriptExecutorFactory getJavaScriptExecutorFactory();

    void onJSBundleLoadedFromServer();

    void onReloadWithJSDebugger(JavaJSExecutor.Factory proxyExecutorFactory);

    void toggleElementInspector();
}
