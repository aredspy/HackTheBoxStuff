package com.facebook.react.views.textinput;

import android.os.Build;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.core.view.ViewCompat;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIViewOperationQueue;
import com.facebook.react.uimanager.ViewProps;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.views.text.ReactBaseTextShadowNode;
import com.facebook.react.views.text.ReactTextUpdate;
import com.facebook.react.views.text.ReactTextViewManagerCallback;
import com.facebook.react.views.view.MeasureUtil;
import com.facebook.yoga.YogaMeasureFunction;
import com.facebook.yoga.YogaMeasureMode;
import com.facebook.yoga.YogaMeasureOutput;
import com.facebook.yoga.YogaNode;
/* loaded from: classes.dex */
public class ReactTextInputShadowNode extends ReactBaseTextShadowNode implements YogaMeasureFunction {
    public static final String PROP_PLACEHOLDER = "placeholder";
    public static final String PROP_SELECTION = "selection";
    public static final String PROP_TEXT = "text";
    private EditText mInternalEditText;
    private ReactTextInputLocalData mLocalData;
    private int mMostRecentEventCount;
    private String mPlaceholder;
    private int mSelectionEnd;
    private int mSelectionStart;
    private String mText;

    @Override // com.facebook.react.uimanager.ReactShadowNodeImpl, com.facebook.react.uimanager.ReactShadowNode
    public boolean isVirtualAnchor() {
        return true;
    }

    @Override // com.facebook.react.uimanager.ReactShadowNodeImpl, com.facebook.react.uimanager.ReactShadowNode
    public boolean isYogaLeafNode() {
        return true;
    }

    public ReactTextInputShadowNode(ReactTextViewManagerCallback reactTextViewManagerCallback) {
        super(reactTextViewManagerCallback);
        this.mMostRecentEventCount = -1;
        this.mText = null;
        this.mPlaceholder = null;
        this.mSelectionStart = -1;
        this.mSelectionEnd = -1;
        this.mTextBreakStrategy = Build.VERSION.SDK_INT < 23 ? 0 : 1;
        initMeasureFunction();
    }

    public ReactTextInputShadowNode() {
        this(null);
    }

    private void initMeasureFunction() {
        setMeasureFunction(this);
    }

    @Override // com.facebook.react.uimanager.ReactShadowNodeImpl, com.facebook.react.uimanager.ReactShadowNode
    public void setThemedContext(ThemedReactContext themedContext) {
        super.setThemedContext(themedContext);
        EditText createInternalEditText = createInternalEditText();
        setDefaultPadding(4, ViewCompat.getPaddingStart(createInternalEditText));
        setDefaultPadding(1, createInternalEditText.getPaddingTop());
        setDefaultPadding(5, ViewCompat.getPaddingEnd(createInternalEditText));
        setDefaultPadding(3, createInternalEditText.getPaddingBottom());
        this.mInternalEditText = createInternalEditText;
        createInternalEditText.setPadding(0, 0, 0, 0);
        this.mInternalEditText.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
    }

    @Override // com.facebook.yoga.YogaMeasureFunction
    public long measure(YogaNode node, float width, YogaMeasureMode widthMode, float height, YogaMeasureMode heightMode) {
        EditText editText = (EditText) Assertions.assertNotNull(this.mInternalEditText);
        ReactTextInputLocalData reactTextInputLocalData = this.mLocalData;
        if (reactTextInputLocalData != null) {
            reactTextInputLocalData.apply(editText);
        } else {
            editText.setTextSize(0, this.mTextAttributes.getEffectiveFontSize());
            if (this.mNumberOfLines != -1) {
                editText.setLines(this.mNumberOfLines);
            }
            if (Build.VERSION.SDK_INT >= 23 && editText.getBreakStrategy() != this.mTextBreakStrategy) {
                editText.setBreakStrategy(this.mTextBreakStrategy);
            }
        }
        editText.setHint(getPlaceholder());
        editText.measure(MeasureUtil.getMeasureSpec(width, widthMode), MeasureUtil.getMeasureSpec(height, heightMode));
        return YogaMeasureOutput.make(editText.getMeasuredWidth(), editText.getMeasuredHeight());
    }

    @Override // com.facebook.react.uimanager.ReactShadowNodeImpl, com.facebook.react.uimanager.ReactShadowNode
    public void setLocalData(Object data) {
        Assertions.assertCondition(data instanceof ReactTextInputLocalData);
        this.mLocalData = (ReactTextInputLocalData) data;
        dirty();
    }

    @ReactProp(name = "mostRecentEventCount")
    public void setMostRecentEventCount(int mostRecentEventCount) {
        this.mMostRecentEventCount = mostRecentEventCount;
    }

    @ReactProp(name = "text")
    public void setText(String text) {
        this.mText = text;
        if (text != null) {
            if (this.mSelectionStart > text.length()) {
                this.mSelectionStart = text.length();
            }
            if (this.mSelectionEnd > text.length()) {
                this.mSelectionEnd = text.length();
            }
        } else {
            this.mSelectionStart = -1;
            this.mSelectionEnd = -1;
        }
        markUpdated();
    }

    public String getText() {
        return this.mText;
    }

    @ReactProp(name = PROP_PLACEHOLDER)
    public void setPlaceholder(String placeholder) {
        this.mPlaceholder = placeholder;
        markUpdated();
    }

    public String getPlaceholder() {
        return this.mPlaceholder;
    }

    @ReactProp(name = PROP_SELECTION)
    public void setSelection(ReadableMap selection) {
        this.mSelectionEnd = -1;
        this.mSelectionStart = -1;
        if (selection != null && selection.hasKey(ViewProps.START) && selection.hasKey(ViewProps.END)) {
            this.mSelectionStart = selection.getInt(ViewProps.START);
            this.mSelectionEnd = selection.getInt(ViewProps.END);
            markUpdated();
        }
    }

    @Override // com.facebook.react.views.text.ReactBaseTextShadowNode
    public void setTextBreakStrategy(String textBreakStrategy) {
        if (Build.VERSION.SDK_INT < 23) {
            return;
        }
        if (textBreakStrategy == null || "simple".equals(textBreakStrategy)) {
            this.mTextBreakStrategy = 0;
        } else if ("highQuality".equals(textBreakStrategy)) {
            this.mTextBreakStrategy = 1;
        } else if ("balanced".equals(textBreakStrategy)) {
            this.mTextBreakStrategy = 2;
        } else {
            throw new JSApplicationIllegalArgumentException("Invalid textBreakStrategy: " + textBreakStrategy);
        }
    }

    @Override // com.facebook.react.uimanager.ReactShadowNodeImpl, com.facebook.react.uimanager.ReactShadowNode
    public void onCollectExtraUpdates(UIViewOperationQueue uiViewOperationQueue) {
        super.onCollectExtraUpdates(uiViewOperationQueue);
        if (this.mMostRecentEventCount != -1) {
            uiViewOperationQueue.enqueueUpdateExtraData(getReactTag(), new ReactTextUpdate(spannedFromShadowNode(this, getText(), false, null), this.mMostRecentEventCount, this.mContainsImages, getPadding(0), getPadding(1), getPadding(2), getPadding(3), this.mTextAlign, this.mTextBreakStrategy, this.mJustificationMode, this.mSelectionStart, this.mSelectionEnd));
        }
    }

    @Override // com.facebook.react.uimanager.ReactShadowNodeImpl, com.facebook.react.uimanager.ReactShadowNode
    public void setPadding(int spacingType, float padding) {
        super.setPadding(spacingType, padding);
        markUpdated();
    }

    protected EditText createInternalEditText() {
        return new EditText(getThemedContext());
    }
}
