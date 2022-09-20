package com.facebook.react.views.textinput;

import android.view.KeyEvent;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.uimanager.events.EventDispatcher;
/* loaded from: classes.dex */
class ReactEditTextInputConnectionWrapper extends InputConnectionWrapper {
    public static final String BACKSPACE_KEY_VALUE = "Backspace";
    public static final String ENTER_KEY_VALUE = "Enter";
    public static final String NEWLINE_RAW_VALUE = "\n";
    private ReactEditText mEditText;
    private EventDispatcher mEventDispatcher;
    private boolean mIsBatchEdit;
    private String mKey = null;

    public ReactEditTextInputConnectionWrapper(InputConnection target, final ReactContext reactContext, final ReactEditText editText, EventDispatcher eventDispatcher) {
        super(target, false);
        this.mEventDispatcher = eventDispatcher;
        this.mEditText = editText;
    }

    @Override // android.view.inputmethod.InputConnectionWrapper, android.view.inputmethod.InputConnection
    public boolean beginBatchEdit() {
        this.mIsBatchEdit = true;
        return super.beginBatchEdit();
    }

    @Override // android.view.inputmethod.InputConnectionWrapper, android.view.inputmethod.InputConnection
    public boolean endBatchEdit() {
        this.mIsBatchEdit = false;
        String str = this.mKey;
        if (str != null) {
            dispatchKeyEvent(str);
            this.mKey = null;
        }
        return super.endBatchEdit();
    }

    @Override // android.view.inputmethod.InputConnectionWrapper, android.view.inputmethod.InputConnection
    public boolean setComposingText(CharSequence text, int newCursorPosition) {
        int selectionStart = this.mEditText.getSelectionStart();
        int selectionEnd = this.mEditText.getSelectionEnd();
        boolean composingText = super.setComposingText(text, newCursorPosition);
        int selectionStart2 = this.mEditText.getSelectionStart();
        boolean z = false;
        boolean z2 = selectionStart == selectionEnd;
        boolean z3 = selectionStart2 == selectionStart;
        if (selectionStart2 < selectionStart || selectionStart2 <= 0) {
            z = true;
        }
        dispatchKeyEventOrEnqueue((z || (!z2 && z3)) ? BACKSPACE_KEY_VALUE : String.valueOf(this.mEditText.getText().charAt(selectionStart2 - 1)));
        return composingText;
    }

    @Override // android.view.inputmethod.InputConnectionWrapper, android.view.inputmethod.InputConnection
    public boolean commitText(CharSequence text, int newCursorPosition) {
        String charSequence = text.toString();
        if (charSequence.length() <= 2) {
            if (charSequence.equals("")) {
                charSequence = BACKSPACE_KEY_VALUE;
            }
            dispatchKeyEventOrEnqueue(charSequence);
        }
        return super.commitText(text, newCursorPosition);
    }

    @Override // android.view.inputmethod.InputConnectionWrapper, android.view.inputmethod.InputConnection
    public boolean deleteSurroundingText(int beforeLength, int afterLength) {
        dispatchKeyEvent(BACKSPACE_KEY_VALUE);
        return super.deleteSurroundingText(beforeLength, afterLength);
    }

    @Override // android.view.inputmethod.InputConnectionWrapper, android.view.inputmethod.InputConnection
    public boolean sendKeyEvent(KeyEvent event) {
        if (event.getAction() == 0) {
            if (event.getKeyCode() == 67) {
                dispatchKeyEvent(BACKSPACE_KEY_VALUE);
            } else if (event.getKeyCode() == 66) {
                dispatchKeyEvent(ENTER_KEY_VALUE);
            }
        }
        return super.sendKeyEvent(event);
    }

    private void dispatchKeyEventOrEnqueue(String key) {
        if (this.mIsBatchEdit) {
            this.mKey = key;
        } else {
            dispatchKeyEvent(key);
        }
    }

    private void dispatchKeyEvent(String key) {
        if (key.equals(NEWLINE_RAW_VALUE)) {
            key = ENTER_KEY_VALUE;
        }
        this.mEventDispatcher.dispatchEvent(new ReactTextInputKeyPressEvent(this.mEditText.getId(), key));
    }
}
