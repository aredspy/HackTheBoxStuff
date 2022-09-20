package com.facebook.react.util;

import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.common.ReactConstants;
import com.facebook.react.uimanager.ViewProps;
/* loaded from: classes.dex */
public class RNLog {
    public static final int ADVICE = 4;
    public static final int ERROR = 6;
    public static final int LOG = 2;
    public static final int MINIMUM_LEVEL_FOR_UI = 5;
    public static final int TRACE = 3;
    public static final int WARN = 5;

    private static String levelToString(int level) {
        String str = "log";
        if (level != 2 && level != 3) {
            str = "warn";
            if (level != 4 && level != 5) {
                return level != 6 ? ViewProps.NONE : "error";
            }
        }
        return str;
    }

    public static void l(String message) {
        FLog.i(ReactConstants.TAG, message);
    }

    public static void t(String message) {
        FLog.i(ReactConstants.TAG, message);
    }

    public static void a(String message) {
        FLog.w(ReactConstants.TAG, "(ADVICE)" + message);
    }

    public static void w(ReactContext context, String message) {
        logInternal(context, message, 5);
        FLog.w(ReactConstants.TAG, message);
    }

    public static void e(ReactContext context, String message) {
        logInternal(context, message, 6);
        FLog.e(ReactConstants.TAG, message);
    }

    public static void e(String message) {
        FLog.e(ReactConstants.TAG, message);
    }

    private static void logInternal(ReactContext context, String message, int level) {
        if (level < 5 || context == null || message == null) {
            return;
        }
        ((RCTLog) context.getJSModule(RCTLog.class)).logIfNoNativeHook(levelToString(level), message);
    }
}
