package com.facebook.react.devsupport;

import android.content.Context;
import com.facebook.react.devsupport.interfaces.DevBundleDownloadListener;
import com.facebook.react.devsupport.interfaces.DevSupportManager;
import com.facebook.react.packagerconnection.RequestHandler;
import java.util.Map;
/* loaded from: classes.dex */
public class DevSupportManagerFactory {
    private static final String DEVSUPPORT_IMPL_CLASS = "BridgeDevSupportManager";
    private static final String DEVSUPPORT_IMPL_PACKAGE = "com.facebook.react.devsupport";

    public static DevSupportManager create(Context applicationContext, ReactInstanceDevHelper reactInstanceDevHelper, String packagerPathForJSBundleName, boolean enableOnCreate, int minNumShakes) {
        return create(applicationContext, reactInstanceDevHelper, packagerPathForJSBundleName, enableOnCreate, null, null, minNumShakes, null);
    }

    public static DevSupportManager create(Context applicationContext, ReactInstanceDevHelper reactInstanceManagerHelper, String packagerPathForJSBundleName, boolean enableOnCreate, RedBoxHandler redBoxHandler, DevBundleDownloadListener devBundleDownloadListener, int minNumShakes, Map<String, RequestHandler> customPackagerCommandHandlers) {
        if (!enableOnCreate) {
            return new DisabledDevSupportManager();
        }
        try {
            return (DevSupportManager) Class.forName(DEVSUPPORT_IMPL_PACKAGE + "." + DEVSUPPORT_IMPL_CLASS).getConstructor(Context.class, ReactInstanceDevHelper.class, String.class, Boolean.TYPE, RedBoxHandler.class, DevBundleDownloadListener.class, Integer.TYPE, Map.class).newInstance(applicationContext, reactInstanceManagerHelper, packagerPathForJSBundleName, true, redBoxHandler, devBundleDownloadListener, Integer.valueOf(minNumShakes), customPackagerCommandHandlers);
        } catch (Exception e) {
            throw new RuntimeException("Requested enabled DevSupportManager, but BridgeDevSupportManager class was not found or could not be created", e);
        }
    }
}
