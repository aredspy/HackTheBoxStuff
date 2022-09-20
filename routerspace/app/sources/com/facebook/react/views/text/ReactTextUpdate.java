package com.facebook.react.views.text;

import android.text.Spannable;
/* loaded from: classes.dex */
public class ReactTextUpdate {
    private final boolean mContainsImages;
    public boolean mContainsMultipleFragments;
    private final int mJsEventCounter;
    private final int mJustificationMode;
    private final float mPaddingBottom;
    private final float mPaddingLeft;
    private final float mPaddingRight;
    private final float mPaddingTop;
    private final int mSelectionEnd;
    private final int mSelectionStart;
    private final Spannable mText;
    private final int mTextAlign;
    private final int mTextBreakStrategy;

    @Deprecated
    public ReactTextUpdate(Spannable text, int jsEventCounter, boolean containsImages, float paddingStart, float paddingTop, float paddingEnd, float paddingBottom, int textAlign) {
        this(text, jsEventCounter, containsImages, paddingStart, paddingTop, paddingEnd, paddingBottom, textAlign, 1, 0, -1, -1);
    }

    public ReactTextUpdate(Spannable text, int jsEventCounter, boolean containsImages, float paddingStart, float paddingTop, float paddingEnd, float paddingBottom, int textAlign, int textBreakStrategy, int justificationMode) {
        this(text, jsEventCounter, containsImages, paddingStart, paddingTop, paddingEnd, paddingBottom, textAlign, textBreakStrategy, justificationMode, -1, -1);
    }

    public ReactTextUpdate(Spannable text, int jsEventCounter, boolean containsImages, int textAlign, int textBreakStrategy, int justificationMode) {
        this(text, jsEventCounter, containsImages, -1.0f, -1.0f, -1.0f, -1.0f, textAlign, textBreakStrategy, justificationMode, -1, -1);
    }

    public ReactTextUpdate(Spannable text, int jsEventCounter, boolean containsImages, float paddingStart, float paddingTop, float paddingEnd, float paddingBottom, int textAlign, int textBreakStrategy, int justificationMode, int selectionStart, int selectionEnd) {
        this.mText = text;
        this.mJsEventCounter = jsEventCounter;
        this.mContainsImages = containsImages;
        this.mPaddingLeft = paddingStart;
        this.mPaddingTop = paddingTop;
        this.mPaddingRight = paddingEnd;
        this.mPaddingBottom = paddingBottom;
        this.mTextAlign = textAlign;
        this.mTextBreakStrategy = textBreakStrategy;
        this.mSelectionStart = selectionStart;
        this.mSelectionEnd = selectionEnd;
        this.mJustificationMode = justificationMode;
    }

    public static ReactTextUpdate buildReactTextUpdateFromState(Spannable text, int jsEventCounter, int textAlign, int textBreakStrategy, int justificationMode, boolean containsMultipleFragments) {
        ReactTextUpdate reactTextUpdate = new ReactTextUpdate(text, jsEventCounter, false, textAlign, textBreakStrategy, justificationMode);
        reactTextUpdate.mContainsMultipleFragments = containsMultipleFragments;
        return reactTextUpdate;
    }

    public Spannable getText() {
        return this.mText;
    }

    public int getJsEventCounter() {
        return this.mJsEventCounter;
    }

    public boolean containsImages() {
        return this.mContainsImages;
    }

    public float getPaddingLeft() {
        return this.mPaddingLeft;
    }

    public float getPaddingTop() {
        return this.mPaddingTop;
    }

    public float getPaddingRight() {
        return this.mPaddingRight;
    }

    public float getPaddingBottom() {
        return this.mPaddingBottom;
    }

    public int getTextAlign() {
        return this.mTextAlign;
    }

    public int getTextBreakStrategy() {
        return this.mTextBreakStrategy;
    }

    public int getJustificationMode() {
        return this.mJustificationMode;
    }

    public int getSelectionStart() {
        return this.mSelectionStart;
    }

    public int getSelectionEnd() {
        return this.mSelectionEnd;
    }
}
