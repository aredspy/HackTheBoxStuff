package com.facebook.react.util;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.views.textinput.ReactEditTextInputConnectionWrapper;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* loaded from: classes.dex */
public class JSStackTrace {
    private static final String COLUMN_KEY = "column";
    private static final Pattern FILE_ID_PATTERN = Pattern.compile("\\b((?:seg-\\d+(?:_\\d+)?|\\d+)\\.js)");
    private static final String FILE_KEY = "file";
    private static final String LINE_NUMBER_KEY = "lineNumber";
    private static final String METHOD_NAME_KEY = "methodName";

    public static String format(String message, ReadableArray stack) {
        StringBuilder sb = new StringBuilder(message);
        sb.append(", stack:\n");
        for (int i = 0; i < stack.size(); i++) {
            ReadableMap map = stack.getMap(i);
            sb.append(map.getString(METHOD_NAME_KEY));
            sb.append("@");
            sb.append(parseFileId(map));
            if (map.hasKey("lineNumber") && !map.isNull("lineNumber") && map.getType("lineNumber") == ReadableType.Number) {
                sb.append(map.getInt("lineNumber"));
            } else {
                sb.append(-1);
            }
            if (map.hasKey("column") && !map.isNull("column") && map.getType("column") == ReadableType.Number) {
                sb.append(":");
                sb.append(map.getInt("column"));
            }
            sb.append(ReactEditTextInputConnectionWrapper.NEWLINE_RAW_VALUE);
        }
        return sb.toString();
    }

    private static String parseFileId(ReadableMap frame) {
        String string;
        if (!frame.hasKey("file") || frame.isNull("file") || frame.getType("file") != ReadableType.String || (string = frame.getString("file")) == null) {
            return "";
        }
        Matcher matcher = FILE_ID_PATTERN.matcher(string);
        if (!matcher.find()) {
            return "";
        }
        return matcher.group(1) + ":";
    }
}
