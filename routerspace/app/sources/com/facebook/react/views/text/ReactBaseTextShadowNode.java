package com.facebook.react.views.text;

import android.graphics.Color;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.IllegalViewOperationException;
import com.facebook.react.uimanager.LayoutShadowNode;
import com.facebook.react.uimanager.NativeViewHierarchyOptimizer;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.ReactShadowNode;
import com.facebook.react.uimanager.ReactShadowNodeImpl;
import com.facebook.react.uimanager.ViewProps;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.yoga.YogaDirection;
import com.facebook.yoga.YogaUnit;
import com.facebook.yoga.YogaValue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
public abstract class ReactBaseTextShadowNode extends LayoutShadowNode {
    public static final int DEFAULT_TEXT_SHADOW_COLOR = 1426063360;
    private static final String INLINE_VIEW_PLACEHOLDER = "0";
    public static final String PROP_SHADOW_COLOR = "textShadowColor";
    public static final String PROP_SHADOW_OFFSET = "textShadowOffset";
    public static final String PROP_SHADOW_OFFSET_HEIGHT = "height";
    public static final String PROP_SHADOW_OFFSET_WIDTH = "width";
    public static final String PROP_SHADOW_RADIUS = "textShadowRadius";
    public static final String PROP_TEXT_TRANSFORM = "textTransform";
    public static final int UNSET = -1;
    protected boolean mAdjustsFontSizeToFit;
    protected int mBackgroundColor;
    protected int mColor;
    protected boolean mContainsImages;
    protected String mFontFamily;
    protected String mFontFeatureSettings;
    protected int mFontStyle;
    protected int mFontWeight;
    protected int mHyphenationFrequency;
    protected boolean mIncludeFontPadding;
    protected Map<Integer, ReactShadowNode> mInlineViews;
    protected boolean mIsBackgroundColorSet;
    protected boolean mIsColorSet;
    protected boolean mIsLineThroughTextDecorationSet;
    protected boolean mIsUnderlineTextDecorationSet;
    protected int mJustificationMode;
    protected float mMinimumFontScale;
    protected int mNumberOfLines;
    protected ReactTextViewManagerCallback mReactTextViewManagerCallback;
    protected int mTextAlign;
    protected TextAttributes mTextAttributes;
    protected int mTextBreakStrategy;
    protected int mTextShadowColor;
    protected float mTextShadowOffsetDx;
    protected float mTextShadowOffsetDy;
    protected float mTextShadowRadius;

    /* loaded from: classes.dex */
    public static class SetSpanOperation {
        protected int end;
        protected int start;
        protected ReactSpan what;

        SetSpanOperation(int start, int end, ReactSpan what) {
            this.start = start;
            this.end = end;
            this.what = what;
        }

        public void execute(SpannableStringBuilder sb, int priority) {
            int i = this.start;
            sb.setSpan(this.what, i, this.end, ((priority << 16) & 16711680) | ((i == 0 ? 18 : 34) & (-16711681)));
        }
    }

    private static void buildSpannedFromShadowNode(ReactBaseTextShadowNode textShadowNode, SpannableStringBuilder sb, List<SetSpanOperation> ops, TextAttributes parentTextAttributes, boolean supportsInlineViews, Map<Integer, ReactShadowNode> inlineViews, int start) {
        TextAttributes textAttributes;
        float f;
        float f2;
        if (parentTextAttributes != null) {
            textAttributes = parentTextAttributes.applyChild(textShadowNode.mTextAttributes);
        } else {
            textAttributes = textShadowNode.mTextAttributes;
        }
        TextAttributes textAttributes2 = textAttributes;
        int childCount = textShadowNode.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ReactShadowNodeImpl childAt = textShadowNode.getChildAt(i);
            if (childAt instanceof ReactRawTextShadowNode) {
                sb.append((CharSequence) TextTransform.apply(((ReactRawTextShadowNode) childAt).getText(), textAttributes2.getTextTransform()));
            } else if (childAt instanceof ReactBaseTextShadowNode) {
                buildSpannedFromShadowNode((ReactBaseTextShadowNode) childAt, sb, ops, textAttributes2, supportsInlineViews, inlineViews, sb.length());
            } else if (childAt instanceof ReactTextInlineImageShadowNode) {
                sb.append(INLINE_VIEW_PLACEHOLDER);
                ops.add(new SetSpanOperation(sb.length() - 1, sb.length(), ((ReactTextInlineImageShadowNode) childAt).buildInlineImageSpan()));
            } else if (supportsInlineViews) {
                int reactTag = childAt.getReactTag();
                YogaValue styleWidth = childAt.getStyleWidth();
                YogaValue styleHeight = childAt.getStyleHeight();
                if (styleWidth.unit != YogaUnit.POINT || styleHeight.unit != YogaUnit.POINT) {
                    childAt.calculateLayout();
                    f2 = childAt.getLayoutWidth();
                    f = childAt.getLayoutHeight();
                } else {
                    f2 = styleWidth.value;
                    f = styleHeight.value;
                }
                sb.append(INLINE_VIEW_PLACEHOLDER);
                ops.add(new SetSpanOperation(sb.length() - 1, sb.length(), new TextInlineViewPlaceholderSpan(reactTag, (int) f2, (int) f)));
                inlineViews.put(Integer.valueOf(reactTag), childAt);
                childAt.markUpdateSeen();
            } else {
                throw new IllegalViewOperationException("Unexpected view type nested under a <Text> or <TextInput> node: " + childAt.getClass());
            }
            childAt.markUpdateSeen();
        }
        int length = sb.length();
        if (length >= start) {
            if (textShadowNode.mIsColorSet) {
                ops.add(new SetSpanOperation(start, length, new ReactForegroundColorSpan(textShadowNode.mColor)));
            }
            if (textShadowNode.mIsBackgroundColorSet) {
                ops.add(new SetSpanOperation(start, length, new ReactBackgroundColorSpan(textShadowNode.mBackgroundColor)));
            }
            float effectiveLetterSpacing = textAttributes2.getEffectiveLetterSpacing();
            if (!Float.isNaN(effectiveLetterSpacing) && (parentTextAttributes == null || parentTextAttributes.getEffectiveLetterSpacing() != effectiveLetterSpacing)) {
                ops.add(new SetSpanOperation(start, length, new CustomLetterSpacingSpan(effectiveLetterSpacing)));
            }
            int effectiveFontSize = textAttributes2.getEffectiveFontSize();
            if (parentTextAttributes == null || parentTextAttributes.getEffectiveFontSize() != effectiveFontSize) {
                ops.add(new SetSpanOperation(start, length, new ReactAbsoluteSizeSpan(effectiveFontSize)));
            }
            if (textShadowNode.mFontStyle != -1 || textShadowNode.mFontWeight != -1 || textShadowNode.mFontFamily != null) {
                ops.add(new SetSpanOperation(start, length, new CustomStyleSpan(textShadowNode.mFontStyle, textShadowNode.mFontWeight, textShadowNode.mFontFeatureSettings, textShadowNode.mFontFamily, textShadowNode.getThemedContext().getAssets())));
            }
            if (textShadowNode.mIsUnderlineTextDecorationSet) {
                ops.add(new SetSpanOperation(start, length, new ReactUnderlineSpan()));
            }
            if (textShadowNode.mIsLineThroughTextDecorationSet) {
                ops.add(new SetSpanOperation(start, length, new ReactStrikethroughSpan()));
            }
            if ((textShadowNode.mTextShadowOffsetDx != 0.0f || textShadowNode.mTextShadowOffsetDy != 0.0f || textShadowNode.mTextShadowRadius != 0.0f) && Color.alpha(textShadowNode.mTextShadowColor) != 0) {
                ops.add(new SetSpanOperation(start, length, new ShadowStyleSpan(textShadowNode.mTextShadowOffsetDx, textShadowNode.mTextShadowOffsetDy, textShadowNode.mTextShadowRadius, textShadowNode.mTextShadowColor)));
            }
            float effectiveLineHeight = textAttributes2.getEffectiveLineHeight();
            if (!Float.isNaN(effectiveLineHeight) && (parentTextAttributes == null || parentTextAttributes.getEffectiveLineHeight() != effectiveLineHeight)) {
                ops.add(new SetSpanOperation(start, length, new CustomLineHeightSpan(effectiveLineHeight)));
            }
            ops.add(new SetSpanOperation(start, length, new ReactTagSpan(textShadowNode.getReactTag())));
        }
    }

    public Spannable spannedFromShadowNode(ReactBaseTextShadowNode textShadowNode, String text, boolean supportsInlineViews, NativeViewHierarchyOptimizer nativeViewHierarchyOptimizer) {
        int i;
        int i2 = 0;
        Assertions.assertCondition(!supportsInlineViews || nativeViewHierarchyOptimizer != null, "nativeViewHierarchyOptimizer is required when inline views are supported");
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        ArrayList<SetSpanOperation> arrayList = new ArrayList();
        HashMap hashMap = supportsInlineViews ? new HashMap() : null;
        if (text != null) {
            spannableStringBuilder.append((CharSequence) TextTransform.apply(text, textShadowNode.mTextAttributes.getTextTransform()));
        }
        buildSpannedFromShadowNode(textShadowNode, spannableStringBuilder, arrayList, null, supportsInlineViews, hashMap, 0);
        textShadowNode.mContainsImages = false;
        textShadowNode.mInlineViews = hashMap;
        float f = Float.NaN;
        for (SetSpanOperation setSpanOperation : arrayList) {
            boolean z = setSpanOperation.what instanceof TextInlineImageSpan;
            if (z || (setSpanOperation.what instanceof TextInlineViewPlaceholderSpan)) {
                if (z) {
                    i = ((TextInlineImageSpan) setSpanOperation.what).getHeight();
                    textShadowNode.mContainsImages = true;
                } else {
                    TextInlineViewPlaceholderSpan textInlineViewPlaceholderSpan = (TextInlineViewPlaceholderSpan) setSpanOperation.what;
                    int height = textInlineViewPlaceholderSpan.getHeight();
                    ReactShadowNode reactShadowNode = hashMap.get(Integer.valueOf(textInlineViewPlaceholderSpan.getReactTag()));
                    nativeViewHierarchyOptimizer.handleForceViewToBeNonLayoutOnly(reactShadowNode);
                    reactShadowNode.setLayoutParent(textShadowNode);
                    i = height;
                }
                if (Float.isNaN(f) || i > f) {
                    f = i;
                }
            }
            setSpanOperation.execute(spannableStringBuilder, i2);
            i2++;
        }
        textShadowNode.mTextAttributes.setHeightOfTallestInlineViewOrImage(f);
        ReactTextViewManagerCallback reactTextViewManagerCallback = this.mReactTextViewManagerCallback;
        if (reactTextViewManagerCallback != null) {
            reactTextViewManagerCallback.onPostProcessSpannable(spannableStringBuilder);
        }
        return spannableStringBuilder;
    }

    public ReactBaseTextShadowNode() {
        this(null);
    }

    public ReactBaseTextShadowNode(ReactTextViewManagerCallback reactTextViewManagerCallback) {
        this.mIsColorSet = false;
        this.mIsBackgroundColorSet = false;
        this.mNumberOfLines = -1;
        this.mTextAlign = 0;
        this.mTextBreakStrategy = Build.VERSION.SDK_INT < 23 ? 0 : 1;
        int i = Build.VERSION.SDK_INT;
        this.mHyphenationFrequency = 0;
        int i2 = Build.VERSION.SDK_INT;
        this.mJustificationMode = 0;
        this.mTextShadowOffsetDx = 0.0f;
        this.mTextShadowOffsetDy = 0.0f;
        this.mTextShadowRadius = 0.0f;
        this.mTextShadowColor = DEFAULT_TEXT_SHADOW_COLOR;
        this.mIsUnderlineTextDecorationSet = false;
        this.mIsLineThroughTextDecorationSet = false;
        this.mIncludeFontPadding = true;
        this.mAdjustsFontSizeToFit = false;
        this.mMinimumFontScale = 0.0f;
        this.mFontStyle = -1;
        this.mFontWeight = -1;
        this.mFontFamily = null;
        this.mFontFeatureSettings = null;
        this.mContainsImages = false;
        this.mTextAttributes = new TextAttributes();
        this.mReactTextViewManagerCallback = reactTextViewManagerCallback;
    }

    private int getTextAlign() {
        int i = this.mTextAlign;
        if (getLayoutDirection() == YogaDirection.RTL) {
            if (i == 5) {
                return 3;
            }
            if (i != 3) {
                return i;
            }
            return 5;
        }
        return i;
    }

    @ReactProp(defaultInt = -1, name = ViewProps.NUMBER_OF_LINES)
    public void setNumberOfLines(int numberOfLines) {
        if (numberOfLines == 0) {
            numberOfLines = -1;
        }
        this.mNumberOfLines = numberOfLines;
        markUpdated();
    }

    @ReactProp(defaultFloat = Float.NaN, name = ViewProps.LINE_HEIGHT)
    public void setLineHeight(float lineHeight) {
        this.mTextAttributes.setLineHeight(lineHeight);
        markUpdated();
    }

    @ReactProp(defaultFloat = Float.NaN, name = ViewProps.LETTER_SPACING)
    public void setLetterSpacing(float letterSpacing) {
        this.mTextAttributes.setLetterSpacing(letterSpacing);
        markUpdated();
    }

    @ReactProp(defaultBoolean = true, name = ViewProps.ALLOW_FONT_SCALING)
    public void setAllowFontScaling(boolean allowFontScaling) {
        if (allowFontScaling != this.mTextAttributes.getAllowFontScaling()) {
            this.mTextAttributes.setAllowFontScaling(allowFontScaling);
            markUpdated();
        }
    }

    @ReactProp(defaultFloat = Float.NaN, name = ViewProps.MAX_FONT_SIZE_MULTIPLIER)
    public void setMaxFontSizeMultiplier(float maxFontSizeMultiplier) {
        if (maxFontSizeMultiplier != this.mTextAttributes.getMaxFontSizeMultiplier()) {
            this.mTextAttributes.setMaxFontSizeMultiplier(maxFontSizeMultiplier);
            markUpdated();
        }
    }

    @ReactProp(name = ViewProps.TEXT_ALIGN)
    public void setTextAlign(String textAlign) {
        if ("justify".equals(textAlign)) {
            if (Build.VERSION.SDK_INT >= 26) {
                this.mJustificationMode = 1;
            }
            this.mTextAlign = 3;
        } else {
            if (Build.VERSION.SDK_INT >= 26) {
                this.mJustificationMode = 0;
            }
            if (textAlign == null || "auto".equals(textAlign)) {
                this.mTextAlign = 0;
            } else if (ViewProps.LEFT.equals(textAlign)) {
                this.mTextAlign = 3;
            } else if (ViewProps.RIGHT.equals(textAlign)) {
                this.mTextAlign = 5;
            } else if ("center".equals(textAlign)) {
                this.mTextAlign = 1;
            } else {
                throw new JSApplicationIllegalArgumentException("Invalid textAlign: " + textAlign);
            }
        }
        markUpdated();
    }

    @ReactProp(defaultFloat = Float.NaN, name = ViewProps.FONT_SIZE)
    public void setFontSize(float fontSize) {
        this.mTextAttributes.setFontSize(fontSize);
        markUpdated();
    }

    @ReactProp(customType = "Color", name = ViewProps.COLOR)
    public void setColor(Integer color) {
        boolean z = color != null;
        this.mIsColorSet = z;
        if (z) {
            this.mColor = color.intValue();
        }
        markUpdated();
    }

    @ReactProp(customType = "Color", name = ViewProps.BACKGROUND_COLOR)
    public void setBackgroundColor(Integer color) {
        if (isVirtual()) {
            boolean z = color != null;
            this.mIsBackgroundColorSet = z;
            if (z) {
                this.mBackgroundColor = color.intValue();
            }
            markUpdated();
        }
    }

    @ReactProp(name = ViewProps.FONT_FAMILY)
    public void setFontFamily(String fontFamily) {
        this.mFontFamily = fontFamily;
        markUpdated();
    }

    @ReactProp(name = ViewProps.FONT_WEIGHT)
    public void setFontWeight(String fontWeightString) {
        int parseFontWeight = ReactTypefaceUtils.parseFontWeight(fontWeightString);
        if (parseFontWeight != this.mFontWeight) {
            this.mFontWeight = parseFontWeight;
            markUpdated();
        }
    }

    @ReactProp(name = ViewProps.FONT_VARIANT)
    public void setFontVariant(ReadableArray fontVariantArray) {
        String parseFontVariant = ReactTypefaceUtils.parseFontVariant(fontVariantArray);
        if (!TextUtils.equals(parseFontVariant, this.mFontFeatureSettings)) {
            this.mFontFeatureSettings = parseFontVariant;
            markUpdated();
        }
    }

    @ReactProp(name = ViewProps.FONT_STYLE)
    public void setFontStyle(String fontStyleString) {
        int parseFontStyle = ReactTypefaceUtils.parseFontStyle(fontStyleString);
        if (parseFontStyle != this.mFontStyle) {
            this.mFontStyle = parseFontStyle;
            markUpdated();
        }
    }

    @ReactProp(defaultBoolean = true, name = ViewProps.INCLUDE_FONT_PADDING)
    public void setIncludeFontPadding(boolean includepad) {
        this.mIncludeFontPadding = includepad;
    }

    @ReactProp(name = ViewProps.TEXT_DECORATION_LINE)
    public void setTextDecorationLine(String textDecorationLineString) {
        String[] split;
        this.mIsUnderlineTextDecorationSet = false;
        this.mIsLineThroughTextDecorationSet = false;
        if (textDecorationLineString != null) {
            for (String str : textDecorationLineString.split(" ")) {
                if ("underline".equals(str)) {
                    this.mIsUnderlineTextDecorationSet = true;
                } else if ("line-through".equals(str)) {
                    this.mIsLineThroughTextDecorationSet = true;
                }
            }
        }
        markUpdated();
    }

    @ReactProp(name = ViewProps.TEXT_BREAK_STRATEGY)
    public void setTextBreakStrategy(String textBreakStrategy) {
        if (Build.VERSION.SDK_INT < 23) {
            return;
        }
        if (textBreakStrategy == null || "highQuality".equals(textBreakStrategy)) {
            this.mTextBreakStrategy = 1;
        } else if ("simple".equals(textBreakStrategy)) {
            this.mTextBreakStrategy = 0;
        } else if ("balanced".equals(textBreakStrategy)) {
            this.mTextBreakStrategy = 2;
        } else {
            throw new JSApplicationIllegalArgumentException("Invalid textBreakStrategy: " + textBreakStrategy);
        }
        markUpdated();
    }

    @ReactProp(name = PROP_SHADOW_OFFSET)
    public void setTextShadowOffset(ReadableMap offsetMap) {
        this.mTextShadowOffsetDx = 0.0f;
        this.mTextShadowOffsetDy = 0.0f;
        if (offsetMap != null) {
            if (offsetMap.hasKey("width") && !offsetMap.isNull("width")) {
                this.mTextShadowOffsetDx = PixelUtil.toPixelFromDIP(offsetMap.getDouble("width"));
            }
            if (offsetMap.hasKey("height") && !offsetMap.isNull("height")) {
                this.mTextShadowOffsetDy = PixelUtil.toPixelFromDIP(offsetMap.getDouble("height"));
            }
        }
        markUpdated();
    }

    @ReactProp(defaultInt = 1, name = PROP_SHADOW_RADIUS)
    public void setTextShadowRadius(float textShadowRadius) {
        if (textShadowRadius != this.mTextShadowRadius) {
            this.mTextShadowRadius = textShadowRadius;
            markUpdated();
        }
    }

    @ReactProp(customType = "Color", defaultInt = DEFAULT_TEXT_SHADOW_COLOR, name = PROP_SHADOW_COLOR)
    public void setTextShadowColor(int textShadowColor) {
        if (textShadowColor != this.mTextShadowColor) {
            this.mTextShadowColor = textShadowColor;
            markUpdated();
        }
    }

    @ReactProp(name = PROP_TEXT_TRANSFORM)
    public void setTextTransform(String textTransform) {
        if (textTransform == null) {
            this.mTextAttributes.setTextTransform(TextTransform.UNSET);
        } else if (ViewProps.NONE.equals(textTransform)) {
            this.mTextAttributes.setTextTransform(TextTransform.NONE);
        } else if ("uppercase".equals(textTransform)) {
            this.mTextAttributes.setTextTransform(TextTransform.UPPERCASE);
        } else if ("lowercase".equals(textTransform)) {
            this.mTextAttributes.setTextTransform(TextTransform.LOWERCASE);
        } else if ("capitalize".equals(textTransform)) {
            this.mTextAttributes.setTextTransform(TextTransform.CAPITALIZE);
        } else {
            throw new JSApplicationIllegalArgumentException("Invalid textTransform: " + textTransform);
        }
        markUpdated();
    }

    @ReactProp(name = ViewProps.ADJUSTS_FONT_SIZE_TO_FIT)
    public void setAdjustFontSizeToFit(boolean adjustsFontSizeToFit) {
        if (adjustsFontSizeToFit != this.mAdjustsFontSizeToFit) {
            this.mAdjustsFontSizeToFit = adjustsFontSizeToFit;
            markUpdated();
        }
    }

    @ReactProp(name = ViewProps.MINIMUM_FONT_SCALE)
    public void setMinimumFontScale(float minimumFontScale) {
        if (minimumFontScale != this.mMinimumFontScale) {
            this.mMinimumFontScale = minimumFontScale;
            markUpdated();
        }
    }
}
