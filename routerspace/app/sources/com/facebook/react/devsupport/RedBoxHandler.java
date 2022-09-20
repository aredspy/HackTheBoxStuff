package com.facebook.react.devsupport;

import android.content.Context;
import android.text.SpannedString;
import com.facebook.react.devsupport.interfaces.ErrorType;
import com.facebook.react.devsupport.interfaces.StackFrame;
/* loaded from: classes.dex */
public interface RedBoxHandler {

    /* loaded from: classes.dex */
    public interface ReportCompletedListener {
        void onReportError(SpannedString spannedString);

        void onReportSuccess(SpannedString spannedString);
    }

    void handleRedbox(String title, StackFrame[] stack, ErrorType errorType);

    boolean isReportEnabled();

    void reportRedbox(Context context, String title, StackFrame[] stack, String sourceUrl, ReportCompletedListener reportCompletedListener);
}
