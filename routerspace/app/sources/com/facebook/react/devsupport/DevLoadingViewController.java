package com.facebook.react.devsupport;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.facebook.common.logging.FLog;
import com.facebook.react.R;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.common.ReactConstants;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
/* loaded from: classes.dex */
public class DevLoadingViewController {
    private static boolean sEnabled = true;
    private PopupWindow mDevLoadingPopup;
    private TextView mDevLoadingView;
    private final ReactInstanceDevHelper mReactInstanceManagerHelper;

    public static void setDevLoadingEnabled(boolean enabled) {
        sEnabled = enabled;
    }

    public DevLoadingViewController(ReactInstanceDevHelper reactInstanceManagerHelper) {
        this.mReactInstanceManagerHelper = reactInstanceManagerHelper;
    }

    public void showMessage(final String message) {
        if (!sEnabled) {
            return;
        }
        UiThreadUtil.runOnUiThread(new Runnable() { // from class: com.facebook.react.devsupport.DevLoadingViewController.1
            @Override // java.lang.Runnable
            public void run() {
                DevLoadingViewController.this.showInternal(message);
            }
        });
    }

    public void showForUrl(String url) {
        Context context = getContext();
        if (context == null) {
            return;
        }
        try {
            URL url2 = new URL(url);
            int i = R.string.catalyst_loading_from_url;
            showMessage(context.getString(i, url2.getHost() + ":" + url2.getPort()));
        } catch (MalformedURLException e) {
            FLog.e(ReactConstants.TAG, "Bundle url format is invalid. \n\n" + e.toString());
        }
    }

    public void showForRemoteJSEnabled() {
        Context context = getContext();
        if (context == null) {
            return;
        }
        showMessage(context.getString(R.string.catalyst_debug_connecting));
    }

    public void updateProgress(final String status, final Integer done, final Integer total) {
        if (!sEnabled) {
            return;
        }
        UiThreadUtil.runOnUiThread(new Runnable() { // from class: com.facebook.react.devsupport.DevLoadingViewController.2
            @Override // java.lang.Runnable
            public void run() {
                Integer num;
                StringBuilder sb = new StringBuilder();
                String str = status;
                if (str == null) {
                    str = "Loading";
                }
                sb.append(str);
                if (done != null && (num = total) != null && num.intValue() > 0) {
                    sb.append(String.format(Locale.getDefault(), " %.1f%%", Float.valueOf((done.intValue() / total.intValue()) * 100.0f)));
                }
                sb.append("…");
                if (DevLoadingViewController.this.mDevLoadingView != null) {
                    DevLoadingViewController.this.mDevLoadingView.setText(sb);
                }
            }
        });
    }

    public void hide() {
        if (!sEnabled) {
            return;
        }
        UiThreadUtil.runOnUiThread(new Runnable() { // from class: com.facebook.react.devsupport.DevLoadingViewController.3
            @Override // java.lang.Runnable
            public void run() {
                DevLoadingViewController.this.hideInternal();
            }
        });
    }

    public void showInternal(String message) {
        PopupWindow popupWindow = this.mDevLoadingPopup;
        if (popupWindow == null || !popupWindow.isShowing()) {
            Activity currentActivity = this.mReactInstanceManagerHelper.getCurrentActivity();
            if (currentActivity == null) {
                FLog.e(ReactConstants.TAG, "Unable to display loading message because react activity isn't available");
                return;
            }
            Rect rect = new Rect();
            currentActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
            int i = rect.top;
            TextView textView = (TextView) ((LayoutInflater) currentActivity.getSystemService("layout_inflater")).inflate(R.layout.dev_loading_view, (ViewGroup) null);
            this.mDevLoadingView = textView;
            textView.setText(message);
            PopupWindow popupWindow2 = new PopupWindow(this.mDevLoadingView, -1, -2);
            this.mDevLoadingPopup = popupWindow2;
            popupWindow2.setTouchable(false);
            this.mDevLoadingPopup.showAtLocation(currentActivity.getWindow().getDecorView(), 0, 0, i);
        }
    }

    public void hideInternal() {
        PopupWindow popupWindow = this.mDevLoadingPopup;
        if (popupWindow == null || !popupWindow.isShowing()) {
            return;
        }
        this.mDevLoadingPopup.dismiss();
        this.mDevLoadingPopup = null;
        this.mDevLoadingView = null;
    }

    private Context getContext() {
        return this.mReactInstanceManagerHelper.getCurrentActivity();
    }
}
