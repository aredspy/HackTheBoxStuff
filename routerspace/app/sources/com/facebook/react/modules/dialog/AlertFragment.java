package com.facebook.react.modules.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import com.facebook.react.modules.dialog.DialogModule;
/* loaded from: classes.dex */
public class AlertFragment extends DialogFragment implements DialogInterface.OnClickListener {
    static final String ARG_BUTTON_NEGATIVE = "button_negative";
    static final String ARG_BUTTON_NEUTRAL = "button_neutral";
    static final String ARG_BUTTON_POSITIVE = "button_positive";
    static final String ARG_ITEMS = "items";
    static final String ARG_MESSAGE = "message";
    static final String ARG_TITLE = "title";
    private final DialogModule.AlertFragmentListener mListener;

    public AlertFragment() {
        this.mListener = null;
    }

    public AlertFragment(DialogModule.AlertFragmentListener listener, Bundle arguments) {
        this.mListener = listener;
        setArguments(arguments);
    }

    public static Dialog createDialog(Context activityContext, Bundle arguments, DialogInterface.OnClickListener fragment) {
        AlertDialog.Builder title = new AlertDialog.Builder(activityContext).setTitle(arguments.getString(ARG_TITLE));
        if (arguments.containsKey(ARG_BUTTON_POSITIVE)) {
            title.setPositiveButton(arguments.getString(ARG_BUTTON_POSITIVE), fragment);
        }
        if (arguments.containsKey(ARG_BUTTON_NEGATIVE)) {
            title.setNegativeButton(arguments.getString(ARG_BUTTON_NEGATIVE), fragment);
        }
        if (arguments.containsKey(ARG_BUTTON_NEUTRAL)) {
            title.setNeutralButton(arguments.getString(ARG_BUTTON_NEUTRAL), fragment);
        }
        if (arguments.containsKey(ARG_MESSAGE)) {
            title.setMessage(arguments.getString(ARG_MESSAGE));
        }
        if (arguments.containsKey(ARG_ITEMS)) {
            title.setItems(arguments.getCharSequenceArray(ARG_ITEMS), fragment);
        }
        return title.create();
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return createDialog(getActivity(), getArguments(), this);
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialog, int which) {
        DialogModule.AlertFragmentListener alertFragmentListener = this.mListener;
        if (alertFragmentListener != null) {
            alertFragmentListener.onClick(dialog, which);
        }
    }

    @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        DialogModule.AlertFragmentListener alertFragmentListener = this.mListener;
        if (alertFragmentListener != null) {
            alertFragmentListener.onDismiss(dialog);
        }
    }
}
