package com.facebook.react.devsupport;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.SpannedString;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.facebook.common.logging.FLog;
import com.facebook.common.util.UriUtil;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.R;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.common.ReactConstants;
import com.facebook.react.devsupport.RedBoxHandler;
import com.facebook.react.devsupport.interfaces.DevSupportManager;
import com.facebook.react.devsupport.interfaces.StackFrame;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONObject;
/* loaded from: classes.dex */
public class RedBoxDialog extends Dialog implements AdapterView.OnItemClickListener {
    private final DevSupportManager mDevSupportManager;
    private Button mDismissButton;
    private View mLineSeparator;
    private ProgressBar mLoadingIndicator;
    private final RedBoxHandler mRedBoxHandler;
    private Button mReloadJsButton;
    private Button mReportButton;
    private TextView mReportTextView;
    private ListView mStackView;
    private boolean isReporting = false;
    private RedBoxHandler.ReportCompletedListener mReportCompletedListener = new RedBoxHandler.ReportCompletedListener() { // from class: com.facebook.react.devsupport.RedBoxDialog.1
        @Override // com.facebook.react.devsupport.RedBoxHandler.ReportCompletedListener
        public void onReportSuccess(final SpannedString spannedString) {
            RedBoxDialog.this.isReporting = false;
            ((Button) Assertions.assertNotNull(RedBoxDialog.this.mReportButton)).setEnabled(true);
            ((ProgressBar) Assertions.assertNotNull(RedBoxDialog.this.mLoadingIndicator)).setVisibility(8);
            ((TextView) Assertions.assertNotNull(RedBoxDialog.this.mReportTextView)).setText(spannedString);
        }

        @Override // com.facebook.react.devsupport.RedBoxHandler.ReportCompletedListener
        public void onReportError(final SpannedString spannedString) {
            RedBoxDialog.this.isReporting = false;
            ((Button) Assertions.assertNotNull(RedBoxDialog.this.mReportButton)).setEnabled(true);
            ((ProgressBar) Assertions.assertNotNull(RedBoxDialog.this.mLoadingIndicator)).setVisibility(8);
            ((TextView) Assertions.assertNotNull(RedBoxDialog.this.mReportTextView)).setText(spannedString);
        }
    };
    private View.OnClickListener mReportButtonOnClickListener = new View.OnClickListener() { // from class: com.facebook.react.devsupport.RedBoxDialog.2
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (RedBoxDialog.this.mRedBoxHandler == null || !RedBoxDialog.this.mRedBoxHandler.isReportEnabled() || RedBoxDialog.this.isReporting) {
                return;
            }
            RedBoxDialog.this.isReporting = true;
            ((TextView) Assertions.assertNotNull(RedBoxDialog.this.mReportTextView)).setText("Reporting...");
            ((TextView) Assertions.assertNotNull(RedBoxDialog.this.mReportTextView)).setVisibility(0);
            ((ProgressBar) Assertions.assertNotNull(RedBoxDialog.this.mLoadingIndicator)).setVisibility(0);
            ((View) Assertions.assertNotNull(RedBoxDialog.this.mLineSeparator)).setVisibility(0);
            ((Button) Assertions.assertNotNull(RedBoxDialog.this.mReportButton)).setEnabled(false);
            RedBoxDialog.this.mRedBoxHandler.reportRedbox(view.getContext(), (String) Assertions.assertNotNull(RedBoxDialog.this.mDevSupportManager.getLastErrorTitle()), (StackFrame[]) Assertions.assertNotNull(RedBoxDialog.this.mDevSupportManager.getLastErrorStack()), RedBoxDialog.this.mDevSupportManager.getSourceUrl(), (RedBoxHandler.ReportCompletedListener) Assertions.assertNotNull(RedBoxDialog.this.mReportCompletedListener));
        }
    };
    private final DoubleTapReloadRecognizer mDoubleTapReloadRecognizer = new DoubleTapReloadRecognizer();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class StackAdapter extends BaseAdapter {
        private static final int VIEW_TYPE_COUNT = 2;
        private static final int VIEW_TYPE_STACKFRAME = 1;
        private static final int VIEW_TYPE_TITLE = 0;
        private final StackFrame[] mStack;
        private final String mTitle;

        @Override // android.widget.BaseAdapter, android.widget.ListAdapter
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        @Override // android.widget.BaseAdapter, android.widget.Adapter
        public int getItemViewType(int position) {
            return position == 0 ? 0 : 1;
        }

        @Override // android.widget.BaseAdapter, android.widget.Adapter
        public int getViewTypeCount() {
            return 2;
        }

        @Override // android.widget.BaseAdapter, android.widget.ListAdapter
        public boolean isEnabled(int position) {
            return position > 0;
        }

        /* loaded from: classes.dex */
        private static class FrameViewHolder {
            private final TextView mFileView;
            private final TextView mMethodView;

            private FrameViewHolder(View v) {
                this.mMethodView = (TextView) v.findViewById(R.id.rn_frame_method);
                this.mFileView = (TextView) v.findViewById(R.id.rn_frame_file);
            }
        }

        public StackAdapter(String title, StackFrame[] stack) {
            this.mTitle = title;
            this.mStack = stack;
            Assertions.assertNotNull(title);
            Assertions.assertNotNull(stack);
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return this.mStack.length + 1;
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            return position == 0 ? this.mTitle : this.mStack[position - 1];
        }

        @Override // android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView;
            if (position == 0) {
                if (convertView != null) {
                    textView = (TextView) convertView;
                } else {
                    textView = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.redbox_item_title, parent, false);
                }
                String str = this.mTitle;
                if (str == null) {
                    str = "<unknown title>";
                }
                textView.setText(str.replaceAll("\\x1b\\[[0-9;]*m", ""));
                return textView;
            }
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.redbox_item_frame, parent, false);
                convertView.setTag(new FrameViewHolder(convertView));
            }
            StackFrame stackFrame = this.mStack[position - 1];
            FrameViewHolder frameViewHolder = (FrameViewHolder) convertView.getTag();
            frameViewHolder.mMethodView.setText(stackFrame.getMethod());
            frameViewHolder.mFileView.setText(StackTraceHelper.formatFrameSource(stackFrame));
            frameViewHolder.mMethodView.setTextColor(stackFrame.isCollapsed() ? -5592406 : -1);
            frameViewHolder.mFileView.setTextColor(stackFrame.isCollapsed() ? -8355712 : -5000269);
            return convertView;
        }
    }

    /* loaded from: classes.dex */
    private static class OpenStackFrameTask extends AsyncTask<StackFrame, Void, Void> {
        private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        private final DevSupportManager mDevSupportManager;

        private OpenStackFrameTask(DevSupportManager devSupportManager) {
            this.mDevSupportManager = devSupportManager;
        }

        public Void doInBackground(StackFrame... stackFrames) {
            try {
                String uri = Uri.parse(this.mDevSupportManager.getSourceUrl()).buildUpon().path("/open-stack-frame").query(null).build().toString();
                OkHttpClient okHttpClient = new OkHttpClient();
                for (StackFrame stackFrame : stackFrames) {
                    okHttpClient.newCall(new Request.Builder().url(uri).post(RequestBody.create(JSON, stackFrameToJson(stackFrame).toString())).build()).execute();
                }
            } catch (Exception e) {
                FLog.e(ReactConstants.TAG, "Could not open stack frame", e);
            }
            return null;
        }

        private static JSONObject stackFrameToJson(StackFrame frame) {
            return new JSONObject(MapBuilder.of(UriUtil.LOCAL_FILE_SCHEME, frame.getFile(), "methodName", frame.getMethod(), StackTraceHelper.LINE_NUMBER_KEY, Integer.valueOf(frame.getLine()), StackTraceHelper.COLUMN_KEY, Integer.valueOf(frame.getColumn())));
        }
    }

    public RedBoxDialog(Context context, DevSupportManager devSupportManager, RedBoxHandler redBoxHandler) {
        super(context, R.style.Theme_Catalyst_RedBox);
        requestWindowFeature(1);
        setContentView(R.layout.redbox_view);
        this.mDevSupportManager = devSupportManager;
        this.mRedBoxHandler = redBoxHandler;
        ListView listView = (ListView) findViewById(R.id.rn_redbox_stack);
        this.mStackView = listView;
        listView.setOnItemClickListener(this);
        Button button = (Button) findViewById(R.id.rn_redbox_reload_button);
        this.mReloadJsButton = button;
        button.setOnClickListener(new View.OnClickListener() { // from class: com.facebook.react.devsupport.RedBoxDialog.3
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                RedBoxDialog.this.mDevSupportManager.handleReloadJS();
            }
        });
        Button button2 = (Button) findViewById(R.id.rn_redbox_dismiss_button);
        this.mDismissButton = button2;
        button2.setOnClickListener(new View.OnClickListener() { // from class: com.facebook.react.devsupport.RedBoxDialog.4
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                RedBoxDialog.this.dismiss();
            }
        });
        if (redBoxHandler == null || !redBoxHandler.isReportEnabled()) {
            return;
        }
        this.mLoadingIndicator = (ProgressBar) findViewById(R.id.rn_redbox_loading_indicator);
        this.mLineSeparator = findViewById(R.id.rn_redbox_line_separator);
        TextView textView = (TextView) findViewById(R.id.rn_redbox_report_label);
        this.mReportTextView = textView;
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        this.mReportTextView.setHighlightColor(0);
        Button button3 = (Button) findViewById(R.id.rn_redbox_report_button);
        this.mReportButton = button3;
        button3.setOnClickListener(this.mReportButtonOnClickListener);
    }

    public void setExceptionDetails(String title, StackFrame[] stack) {
        this.mStackView.setAdapter((ListAdapter) new StackAdapter(title, stack));
    }

    public void resetReporting() {
        RedBoxHandler redBoxHandler = this.mRedBoxHandler;
        if (redBoxHandler == null || !redBoxHandler.isReportEnabled()) {
            return;
        }
        this.isReporting = false;
        ((TextView) Assertions.assertNotNull(this.mReportTextView)).setVisibility(8);
        ((ProgressBar) Assertions.assertNotNull(this.mLoadingIndicator)).setVisibility(8);
        ((View) Assertions.assertNotNull(this.mLineSeparator)).setVisibility(8);
        ((Button) Assertions.assertNotNull(this.mReportButton)).setVisibility(0);
        ((Button) Assertions.assertNotNull(this.mReportButton)).setEnabled(true);
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        new OpenStackFrameTask(this.mDevSupportManager).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (StackFrame) this.mStackView.getAdapter().getItem(position));
    }

    @Override // android.app.Dialog, android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == 82) {
            this.mDevSupportManager.showDevOptionsDialog();
            return true;
        }
        if (this.mDoubleTapReloadRecognizer.didDoubleTapR(keyCode, getCurrentFocus())) {
            this.mDevSupportManager.handleReloadJS();
        }
        return super.onKeyUp(keyCode, event);
    }
}
