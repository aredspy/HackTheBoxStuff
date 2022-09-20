package com.facebook.react.common;

import android.net.Uri;
import android.text.TextUtils;
import com.facebook.common.logging.FLog;
import com.facebook.react.devsupport.StackTraceHelper;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes.dex */
public class DebugServerException extends RuntimeException {
    private static final String GENERIC_ERROR_MESSAGE = "\n\nTry the following to fix the issue:\n• Ensure that Metro is running\n• Ensure that your device/emulator is connected to your machine and has USB debugging enabled - run 'adb devices' to see a list of connected devices\n• Ensure Airplane Mode is disabled\n• If you're on a physical device connected to the same machine, run 'adb reverse tcp:<PORT> tcp:<PORT>' to forward requests from your device\n• If your device is on the same Wi-Fi network, set 'Debug server host & port for device' in 'Dev settings' to your machine's IP address and the port of the local dev server - e.g. 10.0.1.1:<PORT>\n\n";
    private final String mOriginalMessage;

    public static DebugServerException makeGeneric(String url, String reason, Throwable t) {
        return makeGeneric(url, reason, "", t);
    }

    public static DebugServerException makeGeneric(String url, String reason, String extra, Throwable t) {
        String replace = GENERIC_ERROR_MESSAGE.replace("<PORT>", String.valueOf(Uri.parse(url).getPort()));
        return new DebugServerException(reason + replace + extra, t);
    }

    private DebugServerException(String description, String fileName, int lineNumber, int column) {
        super(description + "\n  at " + fileName + ":" + lineNumber + ":" + column);
        this.mOriginalMessage = description;
    }

    public DebugServerException(String description) {
        super(description);
        this.mOriginalMessage = description;
    }

    public DebugServerException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
        this.mOriginalMessage = detailMessage;
    }

    public String getOriginalMessage() {
        return this.mOriginalMessage;
    }

    public static DebugServerException parse(String url, String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            return new DebugServerException(jSONObject.getString("message"), shortenFileName(jSONObject.getString("filename")), jSONObject.getInt(StackTraceHelper.LINE_NUMBER_KEY), jSONObject.getInt(StackTraceHelper.COLUMN_KEY));
        } catch (JSONException e) {
            FLog.w(ReactConstants.TAG, "Could not parse DebugServerException from: " + str, e);
            return null;
        }
    }

    private static String shortenFileName(String fullFileName) {
        String[] split = fullFileName.split("/");
        return split[split.length - 1];
    }
}
