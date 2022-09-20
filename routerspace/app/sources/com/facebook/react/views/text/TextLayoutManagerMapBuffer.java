package com.facebook.react.views.text;

import android.content.Context;
import android.os.Build;
import android.text.BoringLayout;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.LruCache;
import com.facebook.react.bridge.ReactNoCrashSoftException;
import com.facebook.react.bridge.ReactSoftExceptionLogger;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.common.mapbuffer.ReadableMapBuffer;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.ReactAccessibilityDelegate;
import com.facebook.yoga.YogaConstants;
import com.facebook.yoga.YogaMeasureMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
/* loaded from: classes.dex */
public class TextLayoutManagerMapBuffer {
    public static final short AS_KEY_CACHE_ID = 3;
    public static final short AS_KEY_FRAGMENTS = 2;
    public static final short AS_KEY_HASH = 0;
    public static final short AS_KEY_STRING = 1;
    private static final boolean DEFAULT_INCLUDE_FONT_PADDING = true;
    private static final boolean ENABLE_MEASURE_LOGGING = false;
    public static final short FR_KEY_HEIGHT = 4;
    public static final short FR_KEY_IS_ATTACHMENT = 2;
    public static final short FR_KEY_REACT_TAG = 1;
    public static final short FR_KEY_STRING = 0;
    public static final short FR_KEY_TEXT_ATTRIBUTES = 5;
    public static final short FR_KEY_WIDTH = 3;
    private static final String INLINE_VIEW_PLACEHOLDER = "0";
    public static final short PA_KEY_ADJUST_FONT_SIZE_TO_FIT = 3;
    public static final short PA_KEY_ELLIPSIZE_MODE = 1;
    public static final short PA_KEY_INCLUDE_FONT_PADDING = 4;
    public static final short PA_KEY_MAX_NUMBER_OF_LINES = 0;
    public static final short PA_KEY_TEXT_BREAK_STRATEGY = 2;
    private static final String TAG = "TextLayoutManagerMapBuffer";
    private static final short spannableCacheSize = 100;
    private static final TextPaint sTextPaintInstance = new TextPaint(1);
    private static final Object sSpannableCacheLock = new Object();
    private static final LruCache<ReadableMapBuffer, Spannable> sSpannableCache = new LruCache<>(100);
    private static final ConcurrentHashMap<Integer, Spannable> sTagToSpannableCache = new ConcurrentHashMap<>();

    public static void setCachedSpannabledForTag(int reactTag, Spannable sp) {
        sTagToSpannableCache.put(Integer.valueOf(reactTag), sp);
    }

    public static void deleteCachedSpannableForTag(int reactTag) {
        sTagToSpannableCache.remove(Integer.valueOf(reactTag));
    }

    public static boolean isRTL(ReadableMapBuffer attributedString) {
        ReadableMapBuffer mapBuffer = attributedString.getMapBuffer((short) 2);
        if (mapBuffer.getCount() != 0 && TextAttributeProps.getLayoutDirection(mapBuffer.getMapBuffer((short) 0).getMapBuffer((short) 5).getString((short) 21)) == 1) {
            return DEFAULT_INCLUDE_FONT_PADDING;
        }
        return false;
    }

    private static void buildSpannableFromFragment(Context context, ReadableMapBuffer fragments, SpannableStringBuilder sb, List<SetSpanOperation> ops) {
        short count = fragments.getCount();
        for (short s = 0; s < count; s = (short) (s + 1)) {
            ReadableMapBuffer mapBuffer = fragments.getMapBuffer(s);
            int length = sb.length();
            TextAttributeProps fromReadableMapBuffer = TextAttributeProps.fromReadableMapBuffer(mapBuffer.getMapBuffer((short) 5));
            sb.append((CharSequence) TextTransform.apply(mapBuffer.getString((short) 0), fromReadableMapBuffer.mTextTransform));
            int length2 = sb.length();
            int i = mapBuffer.hasKey((short) 1) ? mapBuffer.getInt((short) 1) : -1;
            if (mapBuffer.hasKey((short) 2) && mapBuffer.getBoolean((short) 2)) {
                ops.add(new SetSpanOperation(sb.length() - 1, sb.length(), new TextInlineViewPlaceholderSpan(i, (int) PixelUtil.toPixelFromSP(mapBuffer.getDouble((short) 3)), (int) PixelUtil.toPixelFromSP(mapBuffer.getDouble((short) 4)))));
            } else if (length2 >= length) {
                if (ReactAccessibilityDelegate.AccessibilityRole.LINK.equals(fromReadableMapBuffer.mAccessibilityRole)) {
                    ops.add(new SetSpanOperation(length, length2, new ReactClickableSpan(i, fromReadableMapBuffer.mColor)));
                } else if (fromReadableMapBuffer.mIsColorSet) {
                    ops.add(new SetSpanOperation(length, length2, new ReactForegroundColorSpan(fromReadableMapBuffer.mColor)));
                }
                if (fromReadableMapBuffer.mIsBackgroundColorSet) {
                    ops.add(new SetSpanOperation(length, length2, new ReactBackgroundColorSpan(fromReadableMapBuffer.mBackgroundColor)));
                }
                if (!Float.isNaN(fromReadableMapBuffer.getLetterSpacing())) {
                    ops.add(new SetSpanOperation(length, length2, new CustomLetterSpacingSpan(fromReadableMapBuffer.getLetterSpacing())));
                }
                ops.add(new SetSpanOperation(length, length2, new ReactAbsoluteSizeSpan(fromReadableMapBuffer.mFontSize)));
                if (fromReadableMapBuffer.mFontStyle != -1 || fromReadableMapBuffer.mFontWeight != -1 || fromReadableMapBuffer.mFontFamily != null) {
                    ops.add(new SetSpanOperation(length, length2, new CustomStyleSpan(fromReadableMapBuffer.mFontStyle, fromReadableMapBuffer.mFontWeight, fromReadableMapBuffer.mFontFeatureSettings, fromReadableMapBuffer.mFontFamily, context.getAssets())));
                }
                if (fromReadableMapBuffer.mIsUnderlineTextDecorationSet) {
                    ops.add(new SetSpanOperation(length, length2, new ReactUnderlineSpan()));
                }
                if (fromReadableMapBuffer.mIsLineThroughTextDecorationSet) {
                    ops.add(new SetSpanOperation(length, length2, new ReactStrikethroughSpan()));
                }
                if (fromReadableMapBuffer.mTextShadowOffsetDx != 0.0f || fromReadableMapBuffer.mTextShadowOffsetDy != 0.0f) {
                    ops.add(new SetSpanOperation(length, length2, new ShadowStyleSpan(fromReadableMapBuffer.mTextShadowOffsetDx, fromReadableMapBuffer.mTextShadowOffsetDy, fromReadableMapBuffer.mTextShadowRadius, fromReadableMapBuffer.mTextShadowColor)));
                }
                if (!Float.isNaN(fromReadableMapBuffer.getEffectiveLineHeight())) {
                    ops.add(new SetSpanOperation(length, length2, new CustomLineHeightSpan(fromReadableMapBuffer.getEffectiveLineHeight())));
                }
                ops.add(new SetSpanOperation(length, length2, new ReactTagSpan(i)));
            }
        }
    }

    public static Spannable getOrCreateSpannableForText(Context context, ReadableMapBuffer attributedString, ReactTextViewManagerCallback reactTextViewManagerCallback) {
        Object obj = sSpannableCacheLock;
        synchronized (obj) {
            LruCache<ReadableMapBuffer, Spannable> lruCache = sSpannableCache;
            Spannable spannable = lruCache.get(attributedString);
            if (spannable != null) {
                return spannable;
            }
            Spannable createSpannableFromAttributedString = createSpannableFromAttributedString(context, attributedString, reactTextViewManagerCallback);
            synchronized (obj) {
                lruCache.put(attributedString, createSpannableFromAttributedString);
            }
            return createSpannableFromAttributedString;
        }
    }

    private static Spannable createSpannableFromAttributedString(Context context, ReadableMapBuffer attributedString, ReactTextViewManagerCallback reactTextViewManagerCallback) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        ArrayList<SetSpanOperation> arrayList = new ArrayList();
        buildSpannableFromFragment(context, attributedString.getMapBuffer((short) 2), spannableStringBuilder, arrayList);
        int i = 0;
        for (SetSpanOperation setSpanOperation : arrayList) {
            setSpanOperation.execute(spannableStringBuilder, i);
            i++;
        }
        if (reactTextViewManagerCallback != null) {
            reactTextViewManagerCallback.onPostProcessSpannable(spannableStringBuilder);
        }
        return spannableStringBuilder;
    }

    private static Layout createLayout(Spannable text, BoringLayout.Metrics boring, float width, YogaMeasureMode widthYogaMeasureMode, boolean includeFontPadding, int textBreakStrategy) {
        int i;
        int length = text.length();
        boolean z = (widthYogaMeasureMode == YogaMeasureMode.UNDEFINED || width < 0.0f) ? DEFAULT_INCLUDE_FONT_PADDING : false;
        TextPaint textPaint = sTextPaintInstance;
        float desiredWidth = boring == null ? Layout.getDesiredWidth(text, textPaint) : Float.NaN;
        if (boring == null && (z || (!YogaConstants.isUndefined(desiredWidth) && desiredWidth <= width))) {
            int ceil = (int) Math.ceil(desiredWidth);
            if (Build.VERSION.SDK_INT < 23) {
                return new StaticLayout(text, textPaint, ceil, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, includeFontPadding);
            }
            return StaticLayout.Builder.obtain(text, 0, length, textPaint, ceil).setAlignment(Layout.Alignment.ALIGN_NORMAL).setLineSpacing(0.0f, 1.0f).setIncludePad(includeFontPadding).setBreakStrategy(textBreakStrategy).setHyphenationFrequency(1).build();
        } else if (boring != null && (z || boring.width <= width)) {
            int i2 = boring.width;
            if (boring.width < 0) {
                String str = TAG;
                ReactSoftExceptionLogger.logSoftException(str, new ReactNoCrashSoftException("Text width is invalid: " + boring.width));
                i = 0;
            } else {
                i = i2;
            }
            return BoringLayout.make(text, textPaint, i, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, boring, includeFontPadding);
        } else if (Build.VERSION.SDK_INT < 23) {
            return new StaticLayout(text, textPaint, (int) width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, includeFontPadding);
        } else {
            StaticLayout.Builder hyphenationFrequency = StaticLayout.Builder.obtain(text, 0, length, textPaint, (int) width).setAlignment(Layout.Alignment.ALIGN_NORMAL).setLineSpacing(0.0f, 1.0f).setIncludePad(includeFontPadding).setBreakStrategy(textBreakStrategy).setHyphenationFrequency(1);
            if (Build.VERSION.SDK_INT >= 28) {
                hyphenationFrequency.setUseLineSpacingFromFallbacks(DEFAULT_INCLUDE_FONT_PADDING);
            }
            return hyphenationFrequency.build();
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:39:0x00a4, code lost:
        if (r11 > r21) goto L40;
     */
    /* JADX WARN: Code restructure failed: missing block: B:46:0x00b8, code lost:
        if (r1 > r23) goto L47;
     */
    /* JADX WARN: Removed duplicated region for block: B:43:0x00ac  */
    /* JADX WARN: Removed duplicated region for block: B:51:0x00c4  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static long measureText(android.content.Context r18, com.facebook.react.common.mapbuffer.ReadableMapBuffer r19, com.facebook.react.common.mapbuffer.ReadableMapBuffer r20, float r21, com.facebook.yoga.YogaMeasureMode r22, float r23, com.facebook.yoga.YogaMeasureMode r24, com.facebook.react.views.text.ReactTextViewManagerCallback r25, float[] r26) {
        /*
            Method dump skipped, instructions count: 397
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.facebook.react.views.text.TextLayoutManagerMapBuffer.measureText(android.content.Context, com.facebook.react.common.mapbuffer.ReadableMapBuffer, com.facebook.react.common.mapbuffer.ReadableMapBuffer, float, com.facebook.yoga.YogaMeasureMode, float, com.facebook.yoga.YogaMeasureMode, com.facebook.react.views.text.ReactTextViewManagerCallback, float[]):long");
    }

    public static WritableArray measureLines(Context context, ReadableMapBuffer attributedString, ReadableMapBuffer paragraphAttributes, float width) {
        TextPaint textPaint = sTextPaintInstance;
        Spannable orCreateSpannableForText = getOrCreateSpannableForText(context, attributedString, null);
        return FontMetricsUtil.getFontMetrics(orCreateSpannableForText, createLayout(orCreateSpannableForText, BoringLayout.isBoring(orCreateSpannableForText, textPaint), width, YogaMeasureMode.EXACTLY, paragraphAttributes.hasKey((short) 4) ? paragraphAttributes.getBoolean((short) 4) : DEFAULT_INCLUDE_FONT_PADDING, TextAttributeProps.getTextBreakStrategy(paragraphAttributes.getString((short) 2))), textPaint, context);
    }

    /* loaded from: classes.dex */
    public static class SetSpanOperation {
        protected int end;
        protected int start;
        protected ReactSpan what;

        public SetSpanOperation(int start, int end, ReactSpan what) {
            this.start = start;
            this.end = end;
            this.what = what;
        }

        public void execute(Spannable sb, int priority) {
            int i = this.start;
            sb.setSpan(this.what, i, this.end, ((priority << 16) & 16711680) | ((i == 0 ? 18 : 34) & (-16711681)));
        }
    }
}
