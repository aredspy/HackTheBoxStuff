package com.facebook.react.views.text;

import android.os.Build;
import android.text.TextUtils;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.mapbuffer.ReadableMapBuffer;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.ReactAccessibilityDelegate;
import com.facebook.react.uimanager.ReactStylesDiffMap;
import com.facebook.react.uimanager.ViewProps;
import java.util.ArrayList;
import java.util.Iterator;
/* loaded from: classes.dex */
public class TextAttributeProps {
    private static final int DEFAULT_BREAK_STRATEGY;
    private static final int DEFAULT_JUSTIFICATION_MODE = 0;
    private static final int DEFAULT_TEXT_SHADOW_COLOR = 1426063360;
    private static final String PROP_SHADOW_COLOR = "textShadowColor";
    private static final String PROP_SHADOW_OFFSET = "textShadowOffset";
    private static final String PROP_SHADOW_OFFSET_HEIGHT = "height";
    private static final String PROP_SHADOW_OFFSET_WIDTH = "width";
    private static final String PROP_SHADOW_RADIUS = "textShadowRadius";
    private static final String PROP_TEXT_TRANSFORM = "textTransform";
    public static final short TA_KEY_ACCESSIBILITY_ROLE = 22;
    public static final short TA_KEY_ALIGNMENT = 12;
    public static final short TA_KEY_ALLOW_FONT_SCALING = 9;
    public static final short TA_KEY_BACKGROUND_COLOR = 1;
    public static final short TA_KEY_BEST_WRITING_DIRECTION = 13;
    public static final short TA_KEY_FONT_FAMILY = 3;
    public static final short TA_KEY_FONT_SIZE = 4;
    public static final short TA_KEY_FONT_SIZE_MULTIPLIER = 5;
    public static final short TA_KEY_FONT_STYLE = 7;
    public static final short TA_KEY_FONT_VARIANT = 8;
    public static final short TA_KEY_FONT_WEIGHT = 6;
    public static final short TA_KEY_FOREGROUND_COLOR = 0;
    public static final short TA_KEY_IS_HIGHLIGHTED = 20;
    public static final short TA_KEY_LAYOUT_DIRECTION = 21;
    public static final short TA_KEY_LETTER_SPACING = 10;
    public static final short TA_KEY_LINE_HEIGHT = 11;
    public static final short TA_KEY_OPACITY = 2;
    public static final short TA_KEY_TEXT_DECORATION_COLOR = 14;
    public static final short TA_KEY_TEXT_DECORATION_LINE = 15;
    public static final short TA_KEY_TEXT_DECORATION_LINE_PATTERN = 17;
    public static final short TA_KEY_TEXT_DECORATION_LINE_STYLE = 16;
    public static final short TA_KEY_TEXT_SHADOW_COLOR = 19;
    public static final short TA_KEY_TEXT_SHADOW_RAIDUS = 18;
    public static final int UNSET = -1;
    protected int mBackgroundColor;
    protected int mColor;
    protected float mLineHeight = Float.NaN;
    protected boolean mIsColorSet = false;
    protected boolean mAllowFontScaling = true;
    protected boolean mIsBackgroundColorSet = false;
    protected int mNumberOfLines = -1;
    protected int mFontSize = -1;
    protected float mFontSizeInput = -1.0f;
    protected float mLineHeightInput = -1.0f;
    protected float mLetterSpacingInput = Float.NaN;
    protected int mTextAlign = 0;
    protected int mLayoutDirection = -1;
    protected TextTransform mTextTransform = TextTransform.NONE;
    protected float mTextShadowOffsetDx = 0.0f;
    protected float mTextShadowOffsetDy = 0.0f;
    protected float mTextShadowRadius = 1.0f;
    protected int mTextShadowColor = 1426063360;
    protected boolean mIsUnderlineTextDecorationSet = false;
    protected boolean mIsLineThroughTextDecorationSet = false;
    protected boolean mIncludeFontPadding = true;
    protected ReactAccessibilityDelegate.AccessibilityRole mAccessibilityRole = null;
    protected boolean mIsAccessibilityRoleSet = false;
    protected int mFontStyle = -1;
    protected int mFontWeight = -1;
    protected String mFontFamily = null;
    protected String mFontFeatureSettings = null;
    protected boolean mContainsImages = false;
    protected float mHeightOfTallestInlineImage = Float.NaN;

    static {
        int i = Build.VERSION.SDK_INT;
        int i2 = 0;
        if (Build.VERSION.SDK_INT >= 23) {
            i2 = 1;
        }
        DEFAULT_BREAK_STRATEGY = i2;
    }

    private TextAttributeProps() {
    }

    public static TextAttributeProps fromReadableMapBuffer(ReadableMapBuffer props) {
        TextAttributeProps textAttributeProps = new TextAttributeProps();
        Iterator<ReadableMapBuffer.MapBufferEntry> it = props.iterator();
        while (it.hasNext()) {
            ReadableMapBuffer.MapBufferEntry next = it.next();
            short key = next.getKey();
            if (key == 0) {
                textAttributeProps.setColor(Integer.valueOf(next.getInt(0)));
            } else if (key == 1) {
                textAttributeProps.setBackgroundColor(Integer.valueOf(next.getInt(0)));
            } else if (key == 3) {
                textAttributeProps.setFontFamily(next.getString());
            } else if (key == 4) {
                textAttributeProps.setFontSize((float) next.getDouble(-1.0d));
            } else if (key == 15) {
                textAttributeProps.setTextDecorationLine(next.getString());
            } else if (key == 18) {
                textAttributeProps.setTextShadowRadius(next.getInt(1));
            } else if (key == 19) {
                textAttributeProps.setTextShadowColor(next.getInt(1426063360));
            } else if (key == 21) {
                textAttributeProps.setLayoutDirection(next.getString());
            } else if (key != 22) {
                switch (key) {
                    case 6:
                        textAttributeProps.setFontWeight(next.getString());
                        continue;
                    case 7:
                        textAttributeProps.setFontStyle(next.getString());
                        continue;
                    case 8:
                        textAttributeProps.setFontVariant(next.getReadableMapBuffer());
                        continue;
                    case 9:
                        textAttributeProps.setAllowFontScaling(next.getBoolean(true));
                        continue;
                    case 10:
                        textAttributeProps.setLetterSpacing((float) next.getDouble(Double.NaN));
                        continue;
                    case 11:
                        textAttributeProps.setLineHeight((float) next.getDouble(-1.0d));
                        continue;
                }
            } else {
                textAttributeProps.setAccessibilityRole(next.getString());
            }
        }
        return textAttributeProps;
    }

    public static TextAttributeProps fromReadableMap(ReactStylesDiffMap props) {
        TextAttributeProps textAttributeProps = new TextAttributeProps();
        textAttributeProps.setNumberOfLines(getIntProp(props, ViewProps.NUMBER_OF_LINES, -1));
        textAttributeProps.setLineHeight(getFloatProp(props, ViewProps.LINE_HEIGHT, -1.0f));
        textAttributeProps.setLetterSpacing(getFloatProp(props, ViewProps.LETTER_SPACING, Float.NaN));
        textAttributeProps.setAllowFontScaling(getBooleanProp(props, ViewProps.ALLOW_FONT_SCALING, true));
        textAttributeProps.setFontSize(getFloatProp(props, ViewProps.FONT_SIZE, -1.0f));
        ReadableMap readableMap = null;
        textAttributeProps.setColor(props.hasKey(ViewProps.COLOR) ? Integer.valueOf(props.getInt(ViewProps.COLOR, 0)) : null);
        textAttributeProps.setColor(props.hasKey(ViewProps.FOREGROUND_COLOR) ? Integer.valueOf(props.getInt(ViewProps.FOREGROUND_COLOR, 0)) : null);
        textAttributeProps.setBackgroundColor(props.hasKey(ViewProps.BACKGROUND_COLOR) ? Integer.valueOf(props.getInt(ViewProps.BACKGROUND_COLOR, 0)) : null);
        textAttributeProps.setFontFamily(getStringProp(props, ViewProps.FONT_FAMILY));
        textAttributeProps.setFontWeight(getStringProp(props, ViewProps.FONT_WEIGHT));
        textAttributeProps.setFontStyle(getStringProp(props, ViewProps.FONT_STYLE));
        textAttributeProps.setFontVariant(getArrayProp(props, ViewProps.FONT_VARIANT));
        textAttributeProps.setIncludeFontPadding(getBooleanProp(props, ViewProps.INCLUDE_FONT_PADDING, true));
        textAttributeProps.setTextDecorationLine(getStringProp(props, ViewProps.TEXT_DECORATION_LINE));
        if (props.hasKey("textShadowOffset")) {
            readableMap = props.getMap("textShadowOffset");
        }
        textAttributeProps.setTextShadowOffset(readableMap);
        textAttributeProps.setTextShadowRadius(getIntProp(props, "textShadowRadius", 1));
        textAttributeProps.setTextShadowColor(getIntProp(props, "textShadowColor", 1426063360));
        textAttributeProps.setTextTransform(getStringProp(props, "textTransform"));
        textAttributeProps.setLayoutDirection(getStringProp(props, ViewProps.LAYOUT_DIRECTION));
        textAttributeProps.setAccessibilityRole(getStringProp(props, ViewProps.ACCESSIBILITY_ROLE));
        return textAttributeProps;
    }

    public static int getTextAlignment(ReactStylesDiffMap props, boolean isRTL) {
        String string = props.hasKey(ViewProps.TEXT_ALIGN) ? props.getString(ViewProps.TEXT_ALIGN) : null;
        if ("justify".equals(string)) {
            return 3;
        }
        if (string == null || "auto".equals(string)) {
            return 0;
        }
        if (ViewProps.LEFT.equals(string)) {
            if (!isRTL) {
                return 3;
            }
        } else if (!ViewProps.RIGHT.equals(string)) {
            if ("center".equals(string)) {
                return 1;
            }
            throw new JSApplicationIllegalArgumentException("Invalid textAlign: " + string);
        } else if (isRTL) {
            return 3;
        }
        return 5;
    }

    public static int getJustificationMode(ReactStylesDiffMap props) {
        if (!"justify".equals(props.hasKey(ViewProps.TEXT_ALIGN) ? props.getString(ViewProps.TEXT_ALIGN) : null) || Build.VERSION.SDK_INT < 26) {
            return DEFAULT_JUSTIFICATION_MODE;
        }
        return 1;
    }

    private static boolean getBooleanProp(ReactStylesDiffMap mProps, String name, boolean defaultValue) {
        return mProps.hasKey(name) ? mProps.getBoolean(name, defaultValue) : defaultValue;
    }

    private static String getStringProp(ReactStylesDiffMap mProps, String name) {
        if (mProps.hasKey(name)) {
            return mProps.getString(name);
        }
        return null;
    }

    private static int getIntProp(ReactStylesDiffMap mProps, String name, int defaultvalue) {
        return mProps.hasKey(name) ? mProps.getInt(name, defaultvalue) : defaultvalue;
    }

    private static float getFloatProp(ReactStylesDiffMap mProps, String name, float defaultvalue) {
        return mProps.hasKey(name) ? mProps.getFloat(name, defaultvalue) : defaultvalue;
    }

    private static ReadableArray getArrayProp(ReactStylesDiffMap mProps, String name) {
        if (mProps.hasKey(name)) {
            return mProps.getArray(name);
        }
        return null;
    }

    public float getEffectiveLineHeight() {
        return !Float.isNaN(this.mLineHeight) && !Float.isNaN(this.mHeightOfTallestInlineImage) && (this.mHeightOfTallestInlineImage > this.mLineHeight ? 1 : (this.mHeightOfTallestInlineImage == this.mLineHeight ? 0 : -1)) > 0 ? this.mHeightOfTallestInlineImage : this.mLineHeight;
    }

    private void setNumberOfLines(int numberOfLines) {
        if (numberOfLines == 0) {
            numberOfLines = -1;
        }
        this.mNumberOfLines = numberOfLines;
    }

    private void setLineHeight(float lineHeight) {
        float f;
        this.mLineHeightInput = lineHeight;
        if (lineHeight == -1.0f) {
            this.mLineHeight = Float.NaN;
            return;
        }
        if (this.mAllowFontScaling) {
            f = PixelUtil.toPixelFromSP(lineHeight);
        } else {
            f = PixelUtil.toPixelFromDIP(lineHeight);
        }
        this.mLineHeight = f;
    }

    private void setLetterSpacing(float letterSpacing) {
        this.mLetterSpacingInput = letterSpacing;
    }

    public float getLetterSpacing() {
        float f;
        if (this.mAllowFontScaling) {
            f = PixelUtil.toPixelFromSP(this.mLetterSpacingInput);
        } else {
            f = PixelUtil.toPixelFromDIP(this.mLetterSpacingInput);
        }
        int i = this.mFontSize;
        if (i > 0) {
            return f / i;
        }
        throw new IllegalArgumentException("FontSize should be a positive value. Current value: " + this.mFontSize);
    }

    private void setAllowFontScaling(boolean allowFontScaling) {
        if (allowFontScaling != this.mAllowFontScaling) {
            this.mAllowFontScaling = allowFontScaling;
            setFontSize(this.mFontSizeInput);
            setLineHeight(this.mLineHeightInput);
            setLetterSpacing(this.mLetterSpacingInput);
        }
    }

    private void setFontSize(float fontSize) {
        double d;
        this.mFontSizeInput = fontSize;
        if (fontSize != -1.0f) {
            if (this.mAllowFontScaling) {
                d = Math.ceil(PixelUtil.toPixelFromSP(fontSize));
            } else {
                d = Math.ceil(PixelUtil.toPixelFromDIP(fontSize));
            }
            fontSize = (float) d;
        }
        this.mFontSize = (int) fontSize;
    }

    private void setColor(Integer color) {
        boolean z = color != null;
        this.mIsColorSet = z;
        if (z) {
            this.mColor = color.intValue();
        }
    }

    private void setBackgroundColor(Integer color) {
        boolean z = color != null;
        this.mIsBackgroundColorSet = z;
        if (z) {
            this.mBackgroundColor = color.intValue();
        }
    }

    private void setFontFamily(String fontFamily) {
        this.mFontFamily = fontFamily;
    }

    private void setFontVariant(ReadableArray fontVariant) {
        this.mFontFeatureSettings = ReactTypefaceUtils.parseFontVariant(fontVariant);
    }

    private void setFontVariant(ReadableMapBuffer fontVariant) {
        if (fontVariant == null || fontVariant.getCount() == 0) {
            this.mFontFeatureSettings = null;
            return;
        }
        ArrayList arrayList = new ArrayList();
        Iterator<ReadableMapBuffer.MapBufferEntry> it = fontVariant.iterator();
        while (it.hasNext()) {
            String string = it.next().getString();
            if (string != null) {
                string.hashCode();
                char c = 65535;
                switch (string.hashCode()) {
                    case -1195362251:
                        if (string.equals("proportional-nums")) {
                            c = 0;
                            break;
                        }
                        break;
                    case -1061392823:
                        if (string.equals("lining-nums")) {
                            c = 1;
                            break;
                        }
                        break;
                    case -771984547:
                        if (string.equals("tabular-nums")) {
                            c = 2;
                            break;
                        }
                        break;
                    case -659678800:
                        if (string.equals("oldstyle-nums")) {
                            c = 3;
                            break;
                        }
                        break;
                    case 1183323111:
                        if (string.equals("small-caps")) {
                            c = 4;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        arrayList.add("'pnum'");
                        continue;
                    case 1:
                        arrayList.add("'lnum'");
                        continue;
                    case 2:
                        arrayList.add("'tnum'");
                        continue;
                    case 3:
                        arrayList.add("'onum'");
                        continue;
                    case 4:
                        arrayList.add("'smcp'");
                        continue;
                }
            }
        }
        this.mFontFeatureSettings = TextUtils.join(", ", arrayList);
    }

    private void setFontWeight(String fontWeightString) {
        this.mFontWeight = ReactTypefaceUtils.parseFontWeight(fontWeightString);
    }

    private void setFontStyle(String fontStyleString) {
        this.mFontStyle = ReactTypefaceUtils.parseFontStyle(fontStyleString);
    }

    private void setIncludeFontPadding(boolean includepad) {
        this.mIncludeFontPadding = includepad;
    }

    private void setTextDecorationLine(String textDecorationLineString) {
        String[] split;
        this.mIsUnderlineTextDecorationSet = false;
        this.mIsLineThroughTextDecorationSet = false;
        if (textDecorationLineString != null) {
            for (String str : textDecorationLineString.split("-")) {
                if ("underline".equals(str)) {
                    this.mIsUnderlineTextDecorationSet = true;
                } else if ("strikethrough".equals(str)) {
                    this.mIsLineThroughTextDecorationSet = true;
                }
            }
        }
    }

    private void setTextShadowOffset(ReadableMap offsetMap) {
        this.mTextShadowOffsetDx = 0.0f;
        this.mTextShadowOffsetDy = 0.0f;
        if (offsetMap != null) {
            if (offsetMap.hasKey("width") && !offsetMap.isNull("width")) {
                this.mTextShadowOffsetDx = PixelUtil.toPixelFromDIP(offsetMap.getDouble("width"));
            }
            if (!offsetMap.hasKey("height") || offsetMap.isNull("height")) {
                return;
            }
            this.mTextShadowOffsetDy = PixelUtil.toPixelFromDIP(offsetMap.getDouble("height"));
        }
    }

    public static int getLayoutDirection(String layoutDirection) {
        if (layoutDirection == null || "undefined".equals(layoutDirection)) {
            return -1;
        }
        if ("rtl".equals(layoutDirection)) {
            return 1;
        }
        if ("ltr".equals(layoutDirection)) {
            return 0;
        }
        throw new JSApplicationIllegalArgumentException("Invalid layoutDirection: " + layoutDirection);
    }

    private void setLayoutDirection(String layoutDirection) {
        this.mLayoutDirection = getLayoutDirection(layoutDirection);
    }

    private void setTextShadowRadius(float textShadowRadius) {
        if (textShadowRadius != this.mTextShadowRadius) {
            this.mTextShadowRadius = textShadowRadius;
        }
    }

    private void setTextShadowColor(int textShadowColor) {
        if (textShadowColor != this.mTextShadowColor) {
            this.mTextShadowColor = textShadowColor;
        }
    }

    private void setTextTransform(String textTransform) {
        if (textTransform == null || ViewProps.NONE.equals(textTransform)) {
            this.mTextTransform = TextTransform.NONE;
        } else if ("uppercase".equals(textTransform)) {
            this.mTextTransform = TextTransform.UPPERCASE;
        } else if ("lowercase".equals(textTransform)) {
            this.mTextTransform = TextTransform.LOWERCASE;
        } else if ("capitalize".equals(textTransform)) {
            this.mTextTransform = TextTransform.CAPITALIZE;
        } else {
            throw new JSApplicationIllegalArgumentException("Invalid textTransform: " + textTransform);
        }
    }

    private void setAccessibilityRole(String accessibilityRole) {
        if (accessibilityRole != null) {
            this.mIsAccessibilityRoleSet = accessibilityRole != null;
            this.mAccessibilityRole = ReactAccessibilityDelegate.AccessibilityRole.fromValue(accessibilityRole);
        }
    }

    public static int getTextBreakStrategy(String textBreakStrategy) {
        int i = DEFAULT_BREAK_STRATEGY;
        if (textBreakStrategy != null) {
            textBreakStrategy.hashCode();
            if (textBreakStrategy.equals("balanced")) {
                return 2;
            }
            return !textBreakStrategy.equals("simple") ? 1 : 0;
        }
        return i;
    }
}
