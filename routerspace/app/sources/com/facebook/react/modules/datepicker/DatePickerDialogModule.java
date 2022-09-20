package com.facebook.react.modules.datepicker;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.DatePicker;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import com.facebook.fbreact.specs.NativeDatePickerAndroidSpec;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.module.annotations.ReactModule;
@ReactModule(name = DatePickerDialogModule.FRAGMENT_TAG)
/* loaded from: classes.dex */
public class DatePickerDialogModule extends NativeDatePickerAndroidSpec {
    static final String ACTION_DATE_SET = "dateSetAction";
    static final String ACTION_DISMISSED = "dismissedAction";
    static final String ARG_DATE = "date";
    static final String ARG_MAXDATE = "maxDate";
    static final String ARG_MINDATE = "minDate";
    static final String ARG_MODE = "mode";
    private static final String ERROR_NO_ACTIVITY = "E_NO_ACTIVITY";
    public static final String FRAGMENT_TAG = "DatePickerAndroid";

    @Override // com.facebook.react.bridge.NativeModule
    public String getName() {
        return FRAGMENT_TAG;
    }

    public DatePickerDialogModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    /* loaded from: classes.dex */
    private class DatePickerDialogListener implements DatePickerDialog.OnDateSetListener, DialogInterface.OnDismissListener {
        private final Promise mPromise;
        private boolean mPromiseResolved = false;

        public DatePickerDialogListener(final Promise promise) {
            DatePickerDialogModule.this = this$0;
            this.mPromise = promise;
        }

        @Override // android.app.DatePickerDialog.OnDateSetListener
        public void onDateSet(DatePicker view, int year, int month, int day) {
            if (this.mPromiseResolved || !DatePickerDialogModule.this.getReactApplicationContext().hasActiveReactInstance()) {
                return;
            }
            WritableNativeMap writableNativeMap = new WritableNativeMap();
            writableNativeMap.putString("action", DatePickerDialogModule.ACTION_DATE_SET);
            writableNativeMap.putInt("year", year);
            writableNativeMap.putInt("month", month);
            writableNativeMap.putInt("day", day);
            this.mPromise.resolve(writableNativeMap);
            this.mPromiseResolved = true;
        }

        @Override // android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface dialog) {
            if (this.mPromiseResolved || !DatePickerDialogModule.this.getReactApplicationContext().hasActiveReactInstance()) {
                return;
            }
            WritableNativeMap writableNativeMap = new WritableNativeMap();
            writableNativeMap.putString("action", DatePickerDialogModule.ACTION_DISMISSED);
            this.mPromise.resolve(writableNativeMap);
            this.mPromiseResolved = true;
        }
    }

    @Override // com.facebook.fbreact.specs.NativeDatePickerAndroidSpec
    public void open(final ReadableMap options, final Promise promise) {
        Activity currentActivity = getCurrentActivity();
        if (currentActivity == null || !(currentActivity instanceof FragmentActivity)) {
            promise.reject(ERROR_NO_ACTIVITY, "Tried to open a DatePicker dialog while not attached to a FragmentActivity");
            return;
        }
        FragmentActivity fragmentActivity = (FragmentActivity) currentActivity;
        final FragmentManager supportFragmentManager = fragmentActivity.getSupportFragmentManager();
        DialogFragment dialogFragment = (DialogFragment) supportFragmentManager.findFragmentByTag(FRAGMENT_TAG);
        if (dialogFragment != null) {
            dialogFragment.dismiss();
        }
        fragmentActivity.runOnUiThread(new Runnable() { // from class: com.facebook.react.modules.datepicker.DatePickerDialogModule.1
            @Override // java.lang.Runnable
            public void run() {
                DatePickerDialogFragment datePickerDialogFragment = new DatePickerDialogFragment();
                ReadableMap readableMap = options;
                if (readableMap != null) {
                    datePickerDialogFragment.setArguments(DatePickerDialogModule.this.createFragmentArguments(readableMap));
                }
                DatePickerDialogListener datePickerDialogListener = new DatePickerDialogListener(promise);
                datePickerDialogFragment.setOnDismissListener(datePickerDialogListener);
                datePickerDialogFragment.setOnDateSetListener(datePickerDialogListener);
                datePickerDialogFragment.show(supportFragmentManager, DatePickerDialogModule.FRAGMENT_TAG);
            }
        });
    }

    public Bundle createFragmentArguments(ReadableMap options) {
        Bundle bundle = new Bundle();
        if (options.hasKey(ARG_DATE) && !options.isNull(ARG_DATE)) {
            bundle.putLong(ARG_DATE, (long) options.getDouble(ARG_DATE));
        }
        if (options.hasKey(ARG_MINDATE) && !options.isNull(ARG_MINDATE)) {
            bundle.putLong(ARG_MINDATE, (long) options.getDouble(ARG_MINDATE));
        }
        if (options.hasKey(ARG_MAXDATE) && !options.isNull(ARG_MAXDATE)) {
            bundle.putLong(ARG_MAXDATE, (long) options.getDouble(ARG_MAXDATE));
        }
        if (options.hasKey(ARG_MODE) && !options.isNull(ARG_MODE)) {
            bundle.putString(ARG_MODE, options.getString(ARG_MODE));
        }
        return bundle;
    }
}
