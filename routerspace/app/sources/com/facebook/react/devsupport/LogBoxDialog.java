package com.facebook.react.devsupport;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import com.facebook.react.R;
/* loaded from: classes.dex */
public class LogBoxDialog extends Dialog {
    public LogBoxDialog(Activity context, View reactRootView) {
        super(context, R.style.Theme_Catalyst_LogBox);
        requestWindowFeature(1);
        setContentView(reactRootView);
    }
}
