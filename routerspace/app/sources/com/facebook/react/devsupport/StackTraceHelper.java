package com.facebook.react.devsupport;

import com.facebook.common.util.UriUtil;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.devsupport.interfaces.StackFrame;
import com.facebook.react.views.textinput.ReactEditTextInputConnectionWrapper;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes.dex */
public class StackTraceHelper {
    public static final String COLUMN_KEY = "column";
    public static final String LINE_NUMBER_KEY = "lineNumber";
    private static final Pattern STACK_FRAME_PATTERN1 = Pattern.compile("^(?:(.*?)@)?(.*?)\\:([0-9]+)\\:([0-9]+)$");
    private static final Pattern STACK_FRAME_PATTERN2 = Pattern.compile("\\s*(?:at)\\s*(.+?)\\s*[@(](.*):([0-9]+):([0-9]+)[)]$");

    /* loaded from: classes.dex */
    public static class StackFrameImpl implements StackFrame {
        private final int mColumn;
        private final String mFile;
        private final String mFileName;
        private final boolean mIsCollapsed;
        private final int mLine;
        private final String mMethod;

        private StackFrameImpl(String file, String method, int line, int column, boolean isCollapsed) {
            this.mFile = file;
            this.mMethod = method;
            this.mLine = line;
            this.mColumn = column;
            this.mFileName = file != null ? new File(file).getName() : "";
            this.mIsCollapsed = isCollapsed;
        }

        private StackFrameImpl(String file, String method, int line, int column) {
            this(file, method, line, column, false);
        }

        private StackFrameImpl(String file, String fileName, String method, int line, int column) {
            this.mFile = file;
            this.mFileName = fileName;
            this.mMethod = method;
            this.mLine = line;
            this.mColumn = column;
            this.mIsCollapsed = false;
        }

        @Override // com.facebook.react.devsupport.interfaces.StackFrame
        public String getFile() {
            return this.mFile;
        }

        @Override // com.facebook.react.devsupport.interfaces.StackFrame
        public String getMethod() {
            return this.mMethod;
        }

        @Override // com.facebook.react.devsupport.interfaces.StackFrame
        public int getLine() {
            return this.mLine;
        }

        @Override // com.facebook.react.devsupport.interfaces.StackFrame
        public int getColumn() {
            return this.mColumn;
        }

        @Override // com.facebook.react.devsupport.interfaces.StackFrame
        public String getFileName() {
            return this.mFileName;
        }

        @Override // com.facebook.react.devsupport.interfaces.StackFrame
        public boolean isCollapsed() {
            return this.mIsCollapsed;
        }

        @Override // com.facebook.react.devsupport.interfaces.StackFrame
        public JSONObject toJSON() {
            return new JSONObject(MapBuilder.of(UriUtil.LOCAL_FILE_SCHEME, getFile(), "methodName", getMethod(), StackTraceHelper.LINE_NUMBER_KEY, Integer.valueOf(getLine()), StackTraceHelper.COLUMN_KEY, Integer.valueOf(getColumn()), "collapse", Boolean.valueOf(isCollapsed())));
        }
    }

    public static StackFrame[] convertJsStackTrace(ReadableArray stack) {
        int size = stack != null ? stack.size() : 0;
        StackFrame[] stackFrameArr = new StackFrame[size];
        for (int i = 0; i < size; i++) {
            ReadableType type = stack.getType(i);
            if (type == ReadableType.Map) {
                ReadableMap map = stack.getMap(i);
                String string = map.getString("methodName");
                stackFrameArr[i] = new StackFrameImpl(map.getString(UriUtil.LOCAL_FILE_SCHEME), string, (!map.hasKey(LINE_NUMBER_KEY) || map.isNull(LINE_NUMBER_KEY)) ? -1 : map.getInt(LINE_NUMBER_KEY), (!map.hasKey(COLUMN_KEY) || map.isNull(COLUMN_KEY)) ? -1 : map.getInt(COLUMN_KEY), map.hasKey("collapse") && !map.isNull("collapse") && map.getBoolean("collapse"));
            } else if (type == ReadableType.String) {
                stackFrameArr[i] = new StackFrameImpl((String) null, stack.getString(i), -1, -1);
            }
        }
        return stackFrameArr;
    }

    public static StackFrame[] convertJsStackTrace(JSONArray stack) {
        int length = stack != null ? stack.length() : 0;
        StackFrame[] stackFrameArr = new StackFrame[length];
        for (int i = 0; i < length; i++) {
            try {
                JSONObject jSONObject = stack.getJSONObject(i);
                stackFrameArr[i] = new StackFrameImpl(jSONObject.getString(UriUtil.LOCAL_FILE_SCHEME), jSONObject.getString("methodName"), (!jSONObject.has(LINE_NUMBER_KEY) || jSONObject.isNull(LINE_NUMBER_KEY)) ? -1 : jSONObject.getInt(LINE_NUMBER_KEY), (!jSONObject.has(COLUMN_KEY) || jSONObject.isNull(COLUMN_KEY)) ? -1 : jSONObject.getInt(COLUMN_KEY), jSONObject.has("collapse") && !jSONObject.isNull("collapse") && jSONObject.getBoolean("collapse"));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        return stackFrameArr;
    }

    public static StackFrame[] convertJsStackTrace(String stack) {
        String[] split = stack.split(ReactEditTextInputConnectionWrapper.NEWLINE_RAW_VALUE);
        StackFrame[] stackFrameArr = new StackFrame[split.length];
        for (int i = 0; i < split.length; i++) {
            Matcher matcher = STACK_FRAME_PATTERN1.matcher(split[i]);
            Matcher matcher2 = STACK_FRAME_PATTERN2.matcher(split[i]);
            if (matcher2.find()) {
                matcher = matcher2;
            } else if (!matcher.find()) {
                stackFrameArr[i] = new StackFrameImpl((String) null, split[i], -1, -1);
            }
            stackFrameArr[i] = new StackFrameImpl(matcher.group(2), matcher.group(1) == null ? "(unknown)" : matcher.group(1), Integer.parseInt(matcher.group(3)), Integer.parseInt(matcher.group(4)));
        }
        return stackFrameArr;
    }

    public static StackFrame[] convertJavaStackTrace(Throwable exception) {
        StackTraceElement[] stackTrace = exception.getStackTrace();
        StackFrame[] stackFrameArr = new StackFrame[stackTrace.length];
        for (int i = 0; i < stackTrace.length; i++) {
            stackFrameArr[i] = new StackFrameImpl(stackTrace[i].getClassName(), stackTrace[i].getFileName(), stackTrace[i].getMethodName(), stackTrace[i].getLineNumber(), -1);
        }
        return stackFrameArr;
    }

    public static String formatFrameSource(StackFrame frame) {
        StringBuilder sb = new StringBuilder();
        sb.append(frame.getFileName());
        int line = frame.getLine();
        if (line > 0) {
            sb.append(":");
            sb.append(line);
            int column = frame.getColumn();
            if (column > 0) {
                sb.append(":");
                sb.append(column);
            }
        }
        return sb.toString();
    }

    public static String formatStackTrace(String title, StackFrame[] stack) {
        StringBuilder sb = new StringBuilder();
        sb.append(title);
        sb.append(ReactEditTextInputConnectionWrapper.NEWLINE_RAW_VALUE);
        for (StackFrame stackFrame : stack) {
            sb.append(stackFrame.getMethod());
            sb.append(ReactEditTextInputConnectionWrapper.NEWLINE_RAW_VALUE);
            sb.append("    ");
            sb.append(formatFrameSource(stackFrame));
            sb.append(ReactEditTextInputConnectionWrapper.NEWLINE_RAW_VALUE);
        }
        return sb.toString();
    }
}
