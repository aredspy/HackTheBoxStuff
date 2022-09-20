package com.facebook.react.devsupport;

import android.os.Handler;
import android.view.View;
import android.widget.EditText;
/* loaded from: classes.dex */
public class DoubleTapReloadRecognizer {
    private static final long DOUBLE_TAP_DELAY = 200;
    private boolean mDoRefresh = false;

    public boolean didDoubleTapR(int keyCode, View view) {
        if (keyCode == 46 && !(view instanceof EditText)) {
            if (this.mDoRefresh) {
                this.mDoRefresh = false;
                return true;
            }
            this.mDoRefresh = true;
            new Handler().postDelayed(new Runnable() { // from class: com.facebook.react.devsupport.DoubleTapReloadRecognizer.1
                @Override // java.lang.Runnable
                public void run() {
                    DoubleTapReloadRecognizer.this.mDoRefresh = false;
                }
            }, DOUBLE_TAP_DELAY);
        }
        return false;
    }
}
