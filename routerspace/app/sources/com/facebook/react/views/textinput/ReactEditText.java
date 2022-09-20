package com.facebook.react.views.textinput;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.text.method.QwertyKeyListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactSoftExceptionLogger;
import com.facebook.react.uimanager.FabricViewStateManager;
import com.facebook.react.uimanager.ReactAccessibilityDelegate;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.react.views.text.CustomLetterSpacingSpan;
import com.facebook.react.views.text.CustomLineHeightSpan;
import com.facebook.react.views.text.CustomStyleSpan;
import com.facebook.react.views.text.ReactAbsoluteSizeSpan;
import com.facebook.react.views.text.ReactSpan;
import com.facebook.react.views.text.ReactTextUpdate;
import com.facebook.react.views.text.ReactTypefaceUtils;
import com.facebook.react.views.text.TextAttributes;
import com.facebook.react.views.text.TextInlineImageSpan;
import com.facebook.react.views.text.TextLayoutManager;
import com.facebook.react.views.view.ReactViewBackgroundManager;
import java.util.ArrayList;
import java.util.Iterator;
/* loaded from: classes.dex */
public class ReactEditText extends AppCompatEditText implements FabricViewStateManager.HasFabricViewStateManager {
    public static final boolean DEBUG_MODE = false;
    private static final int UNSET = -1;
    private static final KeyListener sKeyListener = QwertyKeyListener.getInstanceForFullKeyboard();
    protected boolean mContainsImages;
    private ContentSizeWatcher mContentSizeWatcher;
    private EventDispatcher mEventDispatcher;
    private final InputMethodManager mInputMethodManager;
    private String mReturnKeyType;
    private SelectionWatcher mSelectionWatcher;
    private final String TAG = ReactEditText.class.getSimpleName();
    protected boolean mIsSettingTextFromCacheUpdate = false;
    private boolean mDetectScrollMovement = false;
    private boolean mOnKeyPress = false;
    private boolean mTypefaceDirty = false;
    private String mFontFamily = null;
    private int mFontWeight = -1;
    private int mFontStyle = -1;
    private boolean mAutoFocus = false;
    private boolean mDidAttachToWindow = false;
    private final FabricViewStateManager mFabricViewStateManager = new FabricViewStateManager();
    protected boolean mDisableTextDiffing = false;
    protected boolean mIsSettingTextFromState = false;
    private ReactViewBackgroundManager mReactBackgroundManager = new ReactViewBackgroundManager(this);
    private int mDefaultGravityHorizontal = getGravity() & GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK;
    private int mDefaultGravityVertical = getGravity() & 112;
    protected int mNativeEventCount = 0;
    protected boolean mIsSettingTextFromJS = false;
    private Boolean mBlurOnSubmit = null;
    private boolean mDisableFullscreen = false;
    private ArrayList<TextWatcher> mListeners = null;
    private TextWatcherDelegator mTextWatcherDelegator = null;
    private int mStagedInputType = getInputType();
    private final InternalKeyListener mKeyListener = new InternalKeyListener();
    private ScrollWatcher mScrollWatcher = null;
    private TextAttributes mTextAttributes = new TextAttributes();

    @Override // android.view.View
    public boolean isLayoutRequested() {
        return false;
    }

    public ReactEditText(Context context) {
        super(context);
        setFocusableInTouchMode(false);
        this.mInputMethodManager = (InputMethodManager) Assertions.assertNotNull(context.getSystemService("input_method"));
        applyTextAttributes();
        if (Build.VERSION.SDK_INT >= 26 && Build.VERSION.SDK_INT <= 27) {
            setLayerType(1, null);
        }
        ViewCompat.setAccessibilityDelegate(this, new ReactAccessibilityDelegate() { // from class: com.facebook.react.views.textinput.ReactEditText.1
            @Override // com.facebook.react.uimanager.ReactAccessibilityDelegate, androidx.core.view.AccessibilityDelegateCompat
            public boolean performAccessibilityAction(View host, int action, Bundle args) {
                if (action == 16) {
                    int length = ReactEditText.this.getText().length();
                    if (length > 0) {
                        ReactEditText.this.setSelection(length);
                    }
                    return ReactEditText.this.requestFocusInternal();
                }
                return super.performAccessibilityAction(host, action, args);
            }
        });
    }

    protected void finalize() {
        TextLayoutManager.deleteCachedSpannableForTag(getId());
    }

    @Override // android.widget.TextView, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        onContentSizeChange();
    }

    @Override // android.widget.TextView, android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (action == 0) {
            this.mDetectScrollMovement = true;
            getParent().requestDisallowInterceptTouchEvent(true);
        } else if (action == 2 && this.mDetectScrollMovement) {
            if (!canScrollVertically(-1) && !canScrollVertically(1) && !canScrollHorizontally(-1) && !canScrollHorizontally(1)) {
                getParent().requestDisallowInterceptTouchEvent(false);
            }
            this.mDetectScrollMovement = false;
        }
        return super.onTouchEvent(ev);
    }

    @Override // android.widget.TextView, android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == 66 && !isMultiline()) {
            hideSoftKeyboard();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override // android.widget.TextView, android.view.View
    protected void onScrollChanged(int horiz, int vert, int oldHoriz, int oldVert) {
        super.onScrollChanged(horiz, vert, oldHoriz, oldVert);
        ScrollWatcher scrollWatcher = this.mScrollWatcher;
        if (scrollWatcher != null) {
            scrollWatcher.onScrollChanged(horiz, vert, oldHoriz, oldVert);
        }
    }

    @Override // androidx.appcompat.widget.AppCompatEditText, android.widget.TextView, android.view.View
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        ReactContext reactContext = UIManagerHelper.getReactContext(this);
        InputConnection onCreateInputConnection = super.onCreateInputConnection(outAttrs);
        if (onCreateInputConnection != null && this.mOnKeyPress) {
            onCreateInputConnection = new ReactEditTextInputConnectionWrapper(onCreateInputConnection, reactContext, this, this.mEventDispatcher);
        }
        if (isMultiline() && getBlurOnSubmit()) {
            outAttrs.imeOptions &= -1073741825;
        }
        return onCreateInputConnection;
    }

    @Override // android.view.View
    public void clearFocus() {
        setFocusableInTouchMode(false);
        super.clearFocus();
        hideSoftKeyboard();
    }

    @Override // android.view.View
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        return isFocused();
    }

    public boolean requestFocusInternal() {
        setFocusableInTouchMode(true);
        boolean requestFocus = super.requestFocus(130, null);
        if (getShowSoftInputOnFocus()) {
            showSoftKeyboard();
        }
        return requestFocus;
    }

    @Override // android.widget.TextView
    public void addTextChangedListener(TextWatcher watcher) {
        if (this.mListeners == null) {
            this.mListeners = new ArrayList<>();
            super.addTextChangedListener(getTextWatcherDelegator());
        }
        this.mListeners.add(watcher);
    }

    @Override // android.widget.TextView
    public void removeTextChangedListener(TextWatcher watcher) {
        ArrayList<TextWatcher> arrayList = this.mListeners;
        if (arrayList != null) {
            arrayList.remove(watcher);
            if (!this.mListeners.isEmpty()) {
                return;
            }
            this.mListeners = null;
            super.removeTextChangedListener(getTextWatcherDelegator());
        }
    }

    public void setContentSizeWatcher(ContentSizeWatcher contentSizeWatcher) {
        this.mContentSizeWatcher = contentSizeWatcher;
    }

    public void setScrollWatcher(ScrollWatcher scrollWatcher) {
        this.mScrollWatcher = scrollWatcher;
    }

    public void maybeSetSelection(int eventCounter, int start, int end) {
        if (!canUpdateWithEventCount(eventCounter) || start == -1 || end == -1) {
            return;
        }
        setSelection(clampToTextLength(start), clampToTextLength(end));
    }

    private int clampToTextLength(int value) {
        return Math.max(0, Math.min(value, getText() == null ? 0 : getText().length()));
    }

    @Override // android.widget.EditText
    public void setSelection(int start, int end) {
        super.setSelection(start, end);
    }

    @Override // android.widget.TextView
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        if (this.mIsSettingTextFromCacheUpdate || this.mSelectionWatcher == null || !hasFocus()) {
            return;
        }
        this.mSelectionWatcher.onSelectionChanged(selStart, selEnd);
    }

    @Override // android.widget.TextView, android.view.View
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        SelectionWatcher selectionWatcher;
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (!focused || (selectionWatcher = this.mSelectionWatcher) == null) {
            return;
        }
        selectionWatcher.onSelectionChanged(getSelectionStart(), getSelectionEnd());
    }

    public void setSelectionWatcher(SelectionWatcher selectionWatcher) {
        this.mSelectionWatcher = selectionWatcher;
    }

    public void setBlurOnSubmit(Boolean blurOnSubmit) {
        this.mBlurOnSubmit = blurOnSubmit;
    }

    public void setOnKeyPress(boolean onKeyPress) {
        this.mOnKeyPress = onKeyPress;
    }

    public boolean getBlurOnSubmit() {
        Boolean bool = this.mBlurOnSubmit;
        if (bool == null) {
            return !isMultiline();
        }
        return bool.booleanValue();
    }

    public void setDisableFullscreenUI(boolean disableFullscreenUI) {
        this.mDisableFullscreen = disableFullscreenUI;
        updateImeOptions();
    }

    public boolean getDisableFullscreenUI() {
        return this.mDisableFullscreen;
    }

    public void setReturnKeyType(String returnKeyType) {
        this.mReturnKeyType = returnKeyType;
        updateImeOptions();
    }

    public String getReturnKeyType() {
        return this.mReturnKeyType;
    }

    public int getStagedInputType() {
        return this.mStagedInputType;
    }

    public void setStagedInputType(int stagedInputType) {
        this.mStagedInputType = stagedInputType;
    }

    public void commitStagedInputType() {
        if (getInputType() != this.mStagedInputType) {
            int selectionStart = getSelectionStart();
            int selectionEnd = getSelectionEnd();
            setInputType(this.mStagedInputType);
            setSelection(selectionStart, selectionEnd);
        }
    }

    @Override // android.widget.TextView
    public void setInputType(int type) {
        Typeface typeface = super.getTypeface();
        super.setInputType(type);
        this.mStagedInputType = type;
        super.setTypeface(typeface);
        if (isMultiline()) {
            setSingleLine(false);
        }
        this.mKeyListener.setInputType(type);
        setKeyListener(this.mKeyListener);
    }

    public void setFontFamily(String fontFamily) {
        this.mFontFamily = fontFamily;
        this.mTypefaceDirty = true;
    }

    public void setFontWeight(String fontWeightString) {
        int parseFontWeight = ReactTypefaceUtils.parseFontWeight(fontWeightString);
        if (parseFontWeight != this.mFontWeight) {
            this.mFontWeight = parseFontWeight;
            this.mTypefaceDirty = true;
        }
    }

    public void setFontStyle(String fontStyleString) {
        int parseFontStyle = ReactTypefaceUtils.parseFontStyle(fontStyleString);
        if (parseFontStyle != this.mFontStyle) {
            this.mFontStyle = parseFontStyle;
            this.mTypefaceDirty = true;
        }
    }

    public void maybeUpdateTypeface() {
        if (!this.mTypefaceDirty) {
            return;
        }
        this.mTypefaceDirty = false;
        setTypeface(ReactTypefaceUtils.applyStyles(getTypeface(), this.mFontStyle, this.mFontWeight, this.mFontFamily, getContext().getAssets()));
    }

    public void requestFocusFromJS() {
        requestFocusInternal();
    }

    public void clearFocusFromJS() {
        clearFocus();
    }

    public int incrementAndGetEventCounter() {
        int i = this.mNativeEventCount + 1;
        this.mNativeEventCount = i;
        return i;
    }

    public void maybeSetTextFromJS(ReactTextUpdate reactTextUpdate) {
        this.mIsSettingTextFromJS = true;
        maybeSetText(reactTextUpdate);
        this.mIsSettingTextFromJS = false;
    }

    public void maybeSetTextFromState(ReactTextUpdate reactTextUpdate) {
        this.mIsSettingTextFromState = true;
        maybeSetText(reactTextUpdate);
        this.mIsSettingTextFromState = false;
    }

    public boolean canUpdateWithEventCount(int eventCounter) {
        return eventCounter >= this.mNativeEventCount;
    }

    public void maybeSetText(ReactTextUpdate reactTextUpdate) {
        if ((!isSecureText() || !TextUtils.equals(getText(), reactTextUpdate.getText())) && canUpdateWithEventCount(reactTextUpdate.getJsEventCounter())) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(reactTextUpdate.getText());
            manageSpans(spannableStringBuilder, reactTextUpdate.mContainsMultipleFragments);
            this.mContainsImages = reactTextUpdate.containsImages();
            this.mDisableTextDiffing = true;
            if (reactTextUpdate.getText().length() == 0) {
                setText((CharSequence) null);
            } else {
                getText().replace(0, length(), spannableStringBuilder);
            }
            this.mDisableTextDiffing = false;
            if (Build.VERSION.SDK_INT >= 23 && getBreakStrategy() != reactTextUpdate.getTextBreakStrategy()) {
                setBreakStrategy(reactTextUpdate.getTextBreakStrategy());
            }
            updateCachedSpannable(false);
        }
    }

    private void manageSpans(SpannableStringBuilder spannableStringBuilder, boolean skipAddSpansForMeasurements) {
        Object[] spans;
        for (Object obj : getText().getSpans(0, length(), Object.class)) {
            int spanFlags = getText().getSpanFlags(obj);
            boolean z = (spanFlags & 33) == 33;
            if (obj instanceof ReactSpan) {
                getText().removeSpan(obj);
            }
            if (z) {
                int spanStart = getText().getSpanStart(obj);
                int spanEnd = getText().getSpanEnd(obj);
                getText().removeSpan(obj);
                if (sameTextForSpan(getText(), spannableStringBuilder, spanStart, spanEnd)) {
                    spannableStringBuilder.setSpan(obj, spanStart, spanEnd, spanFlags);
                }
            }
        }
        if (!skipAddSpansForMeasurements) {
            addSpansForMeasurement(getText());
        }
    }

    private static boolean sameTextForSpan(final Editable oldText, final SpannableStringBuilder newText, final int start, final int end) {
        if (start > newText.length() || end > newText.length()) {
            return false;
        }
        while (start < end) {
            if (oldText.charAt(start) != newText.charAt(start)) {
                return false;
            }
            start++;
        }
        return true;
    }

    private void addSpansForMeasurement(Spannable spannable) {
        Object[] spans;
        if (!this.mFabricViewStateManager.hasStateWrapper()) {
            return;
        }
        boolean z = this.mDisableTextDiffing;
        this.mDisableTextDiffing = true;
        int length = spannable.length();
        int i = 0;
        for (Object obj : spannable.getSpans(0, length(), Object.class)) {
            int spanFlags = spannable.getSpanFlags(obj);
            if (((spanFlags & 18) == 18 || (spanFlags & 17) == 17) && (obj instanceof ReactSpan) && spannable.getSpanStart(obj) == 0 && spannable.getSpanEnd(obj) == length) {
                spannable.removeSpan(obj);
            }
        }
        ArrayList<TextLayoutManager.SetSpanOperation> arrayList = new ArrayList();
        if (!Float.isNaN(this.mTextAttributes.getLetterSpacing())) {
            arrayList.add(new TextLayoutManager.SetSpanOperation(0, length, new CustomLetterSpacingSpan(this.mTextAttributes.getLetterSpacing())));
        }
        arrayList.add(new TextLayoutManager.SetSpanOperation(0, length, new ReactAbsoluteSizeSpan(this.mTextAttributes.getEffectiveFontSize())));
        if (this.mFontStyle != -1 || this.mFontWeight != -1 || this.mFontFamily != null) {
            arrayList.add(new TextLayoutManager.SetSpanOperation(0, length, new CustomStyleSpan(this.mFontStyle, this.mFontWeight, null, this.mFontFamily, UIManagerHelper.getReactContext(this).getAssets())));
        }
        if (!Float.isNaN(this.mTextAttributes.getEffectiveLineHeight())) {
            arrayList.add(new TextLayoutManager.SetSpanOperation(0, length, new CustomLineHeightSpan(this.mTextAttributes.getEffectiveLineHeight())));
        }
        for (TextLayoutManager.SetSpanOperation setSpanOperation : arrayList) {
            setSpanOperation.execute(spannable, i);
            i++;
        }
        this.mDisableTextDiffing = z;
    }

    protected boolean showSoftKeyboard() {
        return this.mInputMethodManager.showSoftInput(this, 0);
    }

    protected void hideSoftKeyboard() {
        this.mInputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
    }

    private TextWatcherDelegator getTextWatcherDelegator() {
        if (this.mTextWatcherDelegator == null) {
            this.mTextWatcherDelegator = new TextWatcherDelegator();
        }
        return this.mTextWatcherDelegator;
    }

    public boolean isMultiline() {
        return (getInputType() & 131072) != 0;
    }

    private boolean isSecureText() {
        return (getInputType() & 144) != 0;
    }

    public void onContentSizeChange() {
        ContentSizeWatcher contentSizeWatcher = this.mContentSizeWatcher;
        if (contentSizeWatcher != null) {
            contentSizeWatcher.onLayout();
        }
        setIntrinsicContentSize();
    }

    private void setIntrinsicContentSize() {
        ReactContext reactContext = UIManagerHelper.getReactContext(this);
        if (this.mFabricViewStateManager.hasStateWrapper() || reactContext.isBridgeless()) {
            return;
        }
        ReactTextInputLocalData reactTextInputLocalData = new ReactTextInputLocalData(this);
        UIManagerModule uIManagerModule = (UIManagerModule) reactContext.getNativeModule(UIManagerModule.class);
        if (uIManagerModule == null) {
            return;
        }
        uIManagerModule.setViewLocalData(getId(), reactTextInputLocalData);
    }

    public void setGravityHorizontal(int gravityHorizontal) {
        if (gravityHorizontal == 0) {
            gravityHorizontal = this.mDefaultGravityHorizontal;
        }
        setGravity(gravityHorizontal | (getGravity() & (-8) & (-8388616)));
    }

    public void setGravityVertical(int gravityVertical) {
        if (gravityVertical == 0) {
            gravityVertical = this.mDefaultGravityVertical;
        }
        setGravity(gravityVertical | (getGravity() & (-113)));
    }

    /* JADX WARN: Removed duplicated region for block: B:36:0x0075  */
    /* JADX WARN: Removed duplicated region for block: B:37:0x007c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void updateImeOptions() {
        /*
            r9 = this;
            java.lang.String r0 = r9.mReturnKeyType
            r1 = 5
            r2 = 4
            r3 = 3
            r4 = 2
            r5 = 1
            r6 = 6
            if (r0 == 0) goto L70
            r0.hashCode()
            r7 = -1
            int r8 = r0.hashCode()
            switch(r8) {
                case -1273775369: goto L58;
                case -906336856: goto L4d;
                case 3304: goto L42;
                case 3089282: goto L37;
                case 3377907: goto L2c;
                case 3387192: goto L21;
                case 3526536: goto L16;
                default: goto L15;
            }
        L15:
            goto L62
        L16:
            java.lang.String r8 = "send"
            boolean r0 = r0.equals(r8)
            if (r0 != 0) goto L1f
            goto L62
        L1f:
            r7 = 6
            goto L62
        L21:
            java.lang.String r8 = "none"
            boolean r0 = r0.equals(r8)
            if (r0 != 0) goto L2a
            goto L62
        L2a:
            r7 = 5
            goto L62
        L2c:
            java.lang.String r8 = "next"
            boolean r0 = r0.equals(r8)
            if (r0 != 0) goto L35
            goto L62
        L35:
            r7 = 4
            goto L62
        L37:
            java.lang.String r8 = "done"
            boolean r0 = r0.equals(r8)
            if (r0 != 0) goto L40
            goto L62
        L40:
            r7 = 3
            goto L62
        L42:
            java.lang.String r8 = "go"
            boolean r0 = r0.equals(r8)
            if (r0 != 0) goto L4b
            goto L62
        L4b:
            r7 = 2
            goto L62
        L4d:
            java.lang.String r8 = "search"
            boolean r0 = r0.equals(r8)
            if (r0 != 0) goto L56
            goto L62
        L56:
            r7 = 1
            goto L62
        L58:
            java.lang.String r8 = "previous"
            boolean r0 = r0.equals(r8)
            if (r0 != 0) goto L61
            goto L62
        L61:
            r7 = 0
        L62:
            switch(r7) {
                case 0: goto L6e;
                case 1: goto L6c;
                case 2: goto L6a;
                case 3: goto L70;
                case 4: goto L71;
                case 5: goto L68;
                case 6: goto L66;
                default: goto L65;
            }
        L65:
            goto L70
        L66:
            r1 = 4
            goto L71
        L68:
            r1 = 1
            goto L71
        L6a:
            r1 = 2
            goto L71
        L6c:
            r1 = 3
            goto L71
        L6e:
            r1 = 7
            goto L71
        L70:
            r1 = 6
        L71:
            boolean r0 = r9.mDisableFullscreen
            if (r0 == 0) goto L7c
            r0 = 33554432(0x2000000, float:9.403955E-38)
            r0 = r0 | r1
            r9.setImeOptions(r0)
            goto L7f
        L7c:
            r9.setImeOptions(r1)
        L7f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.facebook.react.views.textinput.ReactEditText.updateImeOptions():void");
    }

    @Override // android.widget.TextView, android.view.View
    protected boolean verifyDrawable(Drawable drawable) {
        if (this.mContainsImages) {
            Editable text = getText();
            for (TextInlineImageSpan textInlineImageSpan : (TextInlineImageSpan[]) text.getSpans(0, text.length(), TextInlineImageSpan.class)) {
                if (textInlineImageSpan.getDrawable() == drawable) {
                    return true;
                }
            }
        }
        return super.verifyDrawable(drawable);
    }

    @Override // android.widget.TextView, android.view.View, android.graphics.drawable.Drawable.Callback
    public void invalidateDrawable(Drawable drawable) {
        if (this.mContainsImages) {
            Editable text = getText();
            for (TextInlineImageSpan textInlineImageSpan : (TextInlineImageSpan[]) text.getSpans(0, text.length(), TextInlineImageSpan.class)) {
                if (textInlineImageSpan.getDrawable() == drawable) {
                    invalidate();
                }
            }
        }
        super.invalidateDrawable(drawable);
    }

    @Override // android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mContainsImages) {
            Editable text = getText();
            for (TextInlineImageSpan textInlineImageSpan : (TextInlineImageSpan[]) text.getSpans(0, text.length(), TextInlineImageSpan.class)) {
                textInlineImageSpan.onDetachedFromWindow();
            }
        }
    }

    @Override // android.view.View
    public void onStartTemporaryDetach() {
        super.onStartTemporaryDetach();
        if (this.mContainsImages) {
            Editable text = getText();
            for (TextInlineImageSpan textInlineImageSpan : (TextInlineImageSpan[]) text.getSpans(0, text.length(), TextInlineImageSpan.class)) {
                textInlineImageSpan.onStartTemporaryDetach();
            }
        }
    }

    @Override // android.widget.TextView, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        super.setTextIsSelectable(true);
        if (this.mContainsImages) {
            Editable text = getText();
            for (TextInlineImageSpan textInlineImageSpan : (TextInlineImageSpan[]) text.getSpans(0, text.length(), TextInlineImageSpan.class)) {
                textInlineImageSpan.onAttachedToWindow();
            }
        }
        if (this.mAutoFocus && !this.mDidAttachToWindow) {
            requestFocusInternal();
        }
        this.mDidAttachToWindow = true;
    }

    @Override // android.view.View
    public void onFinishTemporaryDetach() {
        super.onFinishTemporaryDetach();
        if (this.mContainsImages) {
            Editable text = getText();
            for (TextInlineImageSpan textInlineImageSpan : (TextInlineImageSpan[]) text.getSpans(0, text.length(), TextInlineImageSpan.class)) {
                textInlineImageSpan.onFinishTemporaryDetach();
            }
        }
    }

    @Override // android.view.View
    public void setBackgroundColor(int color) {
        this.mReactBackgroundManager.setBackgroundColor(color);
    }

    public void setBorderWidth(int position, float width) {
        this.mReactBackgroundManager.setBorderWidth(position, width);
    }

    public void setBorderColor(int position, float color, float alpha) {
        this.mReactBackgroundManager.setBorderColor(position, color, alpha);
    }

    public void setBorderRadius(float borderRadius) {
        this.mReactBackgroundManager.setBorderRadius(borderRadius);
    }

    public void setBorderRadius(float borderRadius, int position) {
        this.mReactBackgroundManager.setBorderRadius(borderRadius, position);
    }

    public void setBorderStyle(String style) {
        this.mReactBackgroundManager.setBorderStyle(style);
    }

    public void setLetterSpacingPt(float letterSpacingPt) {
        this.mTextAttributes.setLetterSpacing(letterSpacingPt);
        applyTextAttributes();
    }

    public void setAllowFontScaling(boolean allowFontScaling) {
        if (this.mTextAttributes.getAllowFontScaling() != allowFontScaling) {
            this.mTextAttributes.setAllowFontScaling(allowFontScaling);
            applyTextAttributes();
        }
    }

    public void setFontSize(float fontSize) {
        this.mTextAttributes.setFontSize(fontSize);
        applyTextAttributes();
    }

    public void setMaxFontSizeMultiplier(float maxFontSizeMultiplier) {
        if (maxFontSizeMultiplier != this.mTextAttributes.getMaxFontSizeMultiplier()) {
            this.mTextAttributes.setMaxFontSizeMultiplier(maxFontSizeMultiplier);
            applyTextAttributes();
        }
    }

    public void setAutoFocus(boolean autoFocus) {
        this.mAutoFocus = autoFocus;
    }

    protected void applyTextAttributes() {
        setTextSize(0, this.mTextAttributes.getEffectiveFontSize());
        float effectiveLetterSpacing = this.mTextAttributes.getEffectiveLetterSpacing();
        if (!Float.isNaN(effectiveLetterSpacing)) {
            setLetterSpacing(effectiveLetterSpacing);
        }
    }

    @Override // com.facebook.react.uimanager.FabricViewStateManager.HasFabricViewStateManager
    public FabricViewStateManager getFabricViewStateManager() {
        return this.mFabricViewStateManager;
    }

    public void updateCachedSpannable(boolean resetStyles) {
        if (this.mFabricViewStateManager.hasStateWrapper() && getId() != -1) {
            boolean z = true;
            if (resetStyles) {
                this.mIsSettingTextFromCacheUpdate = true;
                addSpansForMeasurement(getText());
                this.mIsSettingTextFromCacheUpdate = false;
            }
            Editable text = getText();
            if (text == null || text.length() <= 0) {
                z = false;
            }
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            if (z) {
                try {
                    spannableStringBuilder.append(text.subSequence(0, text.length()));
                } catch (IndexOutOfBoundsException e) {
                    ReactSoftExceptionLogger.logSoftException(this.TAG, e);
                }
            }
            if (!z) {
                if (getHint() != null && getHint().length() > 0) {
                    spannableStringBuilder.append(getHint());
                } else {
                    spannableStringBuilder.append((CharSequence) "I");
                }
                addSpansForMeasurement(spannableStringBuilder);
            }
            TextLayoutManager.setCachedSpannabledForTag(getId(), spannableStringBuilder);
        }
    }

    public void setEventDispatcher(EventDispatcher eventDispatcher) {
        this.mEventDispatcher = eventDispatcher;
    }

    /* loaded from: classes.dex */
    public class TextWatcherDelegator implements TextWatcher {
        private TextWatcherDelegator() {
            ReactEditText.this = this$0;
        }

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (ReactEditText.this.mIsSettingTextFromCacheUpdate || ReactEditText.this.mIsSettingTextFromJS || ReactEditText.this.mListeners == null) {
                return;
            }
            Iterator it = ReactEditText.this.mListeners.iterator();
            while (it.hasNext()) {
                ((TextWatcher) it.next()).beforeTextChanged(s, start, count, after);
            }
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!ReactEditText.this.mIsSettingTextFromCacheUpdate) {
                if (!ReactEditText.this.mIsSettingTextFromJS && ReactEditText.this.mListeners != null) {
                    Iterator it = ReactEditText.this.mListeners.iterator();
                    while (it.hasNext()) {
                        ((TextWatcher) it.next()).onTextChanged(s, start, before, count);
                    }
                }
                ReactEditText reactEditText = ReactEditText.this;
                reactEditText.updateCachedSpannable(!reactEditText.mIsSettingTextFromJS && !ReactEditText.this.mIsSettingTextFromState && start == 0 && before == 0);
            }
            ReactEditText.this.onContentSizeChange();
        }

        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable s) {
            if (ReactEditText.this.mIsSettingTextFromCacheUpdate || ReactEditText.this.mIsSettingTextFromJS || ReactEditText.this.mListeners == null) {
                return;
            }
            Iterator it = ReactEditText.this.mListeners.iterator();
            while (it.hasNext()) {
                ((TextWatcher) it.next()).afterTextChanged(s);
            }
        }
    }

    /* loaded from: classes.dex */
    public static class InternalKeyListener implements KeyListener {
        private int mInputType = 0;

        public void setInputType(int inputType) {
            this.mInputType = inputType;
        }

        @Override // android.text.method.KeyListener
        public int getInputType() {
            return this.mInputType;
        }

        @Override // android.text.method.KeyListener
        public boolean onKeyDown(View view, Editable text, int keyCode, KeyEvent event) {
            return ReactEditText.sKeyListener.onKeyDown(view, text, keyCode, event);
        }

        @Override // android.text.method.KeyListener
        public boolean onKeyUp(View view, Editable text, int keyCode, KeyEvent event) {
            return ReactEditText.sKeyListener.onKeyUp(view, text, keyCode, event);
        }

        @Override // android.text.method.KeyListener
        public boolean onKeyOther(View view, Editable text, KeyEvent event) {
            return ReactEditText.sKeyListener.onKeyOther(view, text, event);
        }

        @Override // android.text.method.KeyListener
        public void clearMetaKeyState(View view, Editable content, int states) {
            ReactEditText.sKeyListener.clearMetaKeyState(view, content, states);
        }
    }
}
