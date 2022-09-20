package com.facebook.react.uimanager;

import android.content.Context;
import android.content.ContextWrapper;
import android.view.View;
import android.widget.EditText;
import androidx.core.view.ViewCompat;
import com.facebook.react.bridge.CatalystInstance;
import com.facebook.react.bridge.JSIModuleType;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactNoCrashSoftException;
import com.facebook.react.bridge.ReactSoftExceptionLogger;
import com.facebook.react.bridge.UIManager;
import com.facebook.react.uimanager.common.ViewUtil;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.react.uimanager.events.EventDispatcherProvider;
/* loaded from: classes.dex */
public class UIManagerHelper {
    public static final int PADDING_BOTTOM_INDEX = 3;
    public static final int PADDING_END_INDEX = 1;
    public static final int PADDING_START_INDEX = 0;
    public static final int PADDING_TOP_INDEX = 2;
    private static final String TAG = "com.facebook.react.uimanager.UIManagerHelper";

    public static UIManager getUIManagerForReactTag(ReactContext context, int reactTag) {
        return getUIManager(context, ViewUtil.getUIManagerType(reactTag));
    }

    public static UIManager getUIManager(ReactContext context, int uiManagerType) {
        return getUIManager(context, uiManagerType, true);
    }

    private static UIManager getUIManager(ReactContext context, int uiManagerType, boolean returnNullIfCatalystIsInactive) {
        if (context.isBridgeless()) {
            UIManager uIManager = (UIManager) context.getJSIModule(JSIModuleType.UIManager);
            if (uIManager != null) {
                return uIManager;
            }
            ReactSoftExceptionLogger.logSoftException(TAG, new ReactNoCrashSoftException("Cannot get UIManager because the instance hasn't been initialized yet."));
            return null;
        } else if (!context.hasCatalystInstance()) {
            ReactSoftExceptionLogger.logSoftException(TAG, new ReactNoCrashSoftException("Cannot get UIManager because the context doesn't contain a CatalystInstance."));
            return null;
        } else {
            if (!context.hasActiveReactInstance()) {
                ReactSoftExceptionLogger.logSoftException(TAG, new ReactNoCrashSoftException("Cannot get UIManager because the context doesn't contain an active CatalystInstance."));
                if (returnNullIfCatalystIsInactive) {
                    return null;
                }
            }
            CatalystInstance catalystInstance = context.getCatalystInstance();
            try {
                if (uiManagerType == 2) {
                    return (UIManager) catalystInstance.getJSIModule(JSIModuleType.UIManager);
                }
                return (UIManager) catalystInstance.getNativeModule(UIManagerModule.class);
            } catch (IllegalArgumentException unused) {
                String str = TAG;
                ReactSoftExceptionLogger.logSoftException(str, new ReactNoCrashSoftException("Cannot get UIManager for UIManagerType: " + uiManagerType));
                return (UIManager) catalystInstance.getNativeModule(UIManagerModule.class);
            }
        }
    }

    public static EventDispatcher getEventDispatcherForReactTag(ReactContext context, int reactTag) {
        EventDispatcher eventDispatcher = getEventDispatcher(context, ViewUtil.getUIManagerType(reactTag));
        if (eventDispatcher == null) {
            String str = TAG;
            ReactSoftExceptionLogger.logSoftException(str, new IllegalStateException("Cannot get EventDispatcher for reactTag " + reactTag));
        }
        return eventDispatcher;
    }

    public static EventDispatcher getEventDispatcher(ReactContext context, int uiManagerType) {
        if (context.isBridgeless()) {
            if (context instanceof ThemedReactContext) {
                context = ((ThemedReactContext) context).getReactApplicationContext();
            }
            return ((EventDispatcherProvider) context).getEventDispatcher();
        }
        UIManager uIManager = getUIManager(context, uiManagerType, false);
        if (uIManager == null) {
            String str = TAG;
            ReactSoftExceptionLogger.logSoftException(str, new ReactNoCrashSoftException("Unable to find UIManager for UIManagerType " + uiManagerType));
            return null;
        }
        EventDispatcher eventDispatcher = (EventDispatcher) uIManager.getEventDispatcher();
        if (eventDispatcher == null) {
            String str2 = TAG;
            ReactSoftExceptionLogger.logSoftException(str2, new IllegalStateException("Cannot get EventDispatcher for UIManagerType " + uiManagerType));
        }
        return eventDispatcher;
    }

    public static ReactContext getReactContext(View view) {
        Context context = view.getContext();
        if (!(context instanceof ReactContext) && (context instanceof ContextWrapper)) {
            context = ((ContextWrapper) context).getBaseContext();
        }
        return (ReactContext) context;
    }

    public static int getSurfaceId(View view) {
        int id = view.getId();
        if (ViewUtil.getUIManagerType(id) == 1) {
            return -1;
        }
        Context context = view.getContext();
        if (!(context instanceof ThemedReactContext) && (context instanceof ContextWrapper)) {
            context = ((ContextWrapper) context).getBaseContext();
        }
        int surfaceId = getSurfaceId(context);
        if (surfaceId == -1) {
            String str = TAG;
            ReactSoftExceptionLogger.logSoftException(str, new IllegalStateException("Fabric View [" + id + "] does not have SurfaceId associated with it"));
        }
        return surfaceId;
    }

    public static int getSurfaceId(Context context) {
        if (context instanceof ThemedReactContext) {
            return ((ThemedReactContext) context).getSurfaceId();
        }
        return -1;
    }

    public static float[] getDefaultTextInputPadding(ThemedReactContext context) {
        EditText editText = new EditText(context);
        return new float[]{PixelUtil.toDIPFromPixel(ViewCompat.getPaddingStart(editText)), PixelUtil.toDIPFromPixel(ViewCompat.getPaddingEnd(editText)), PixelUtil.toDIPFromPixel(editText.getPaddingTop()), PixelUtil.toDIPFromPixel(editText.getPaddingBottom())};
    }
}
