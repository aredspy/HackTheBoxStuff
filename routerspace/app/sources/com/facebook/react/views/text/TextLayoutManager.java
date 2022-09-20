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
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableNativeMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.ReactAccessibilityDelegate;
import com.facebook.react.uimanager.ReactStylesDiffMap;
import com.facebook.react.uimanager.ViewProps;
import com.facebook.yoga.YogaConstants;
import com.facebook.yoga.YogaMeasureMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
/* loaded from: classes.dex */
public class TextLayoutManager {
    private static final boolean DEFAULT_INCLUDE_FONT_PADDING = true;
    private static final boolean ENABLE_MEASURE_LOGGING = false;
    private static final String INCLUDE_FONT_PADDING_KEY = "includeFontPadding";
    private static final String INLINE_VIEW_PLACEHOLDER = "0";
    private static final String MAXIMUM_NUMBER_OF_LINES_KEY = "maximumNumberOfLines";
    private static final String TAG = "TextLayoutManager";
    private static final String TEXT_BREAK_STRATEGY_KEY = "textBreakStrategy";
    private static final int spannableCacheSize = 100;
    private static final TextPaint sTextPaintInstance = new TextPaint(1);
    private static final Object sSpannableCacheLock = new Object();
    private static final LruCache<ReadableNativeMap, Spannable> sSpannableCache = new LruCache<>(100);
    private static final ConcurrentHashMap<Integer, Spannable> sTagToSpannableCache = new ConcurrentHashMap<>();

    public static boolean isRTL(ReadableMap attributedString) {
        ReadableArray array = attributedString.getArray("fragments");
        if (array.size() <= 0 || TextAttributeProps.getLayoutDirection(array.getMap(0).getMap("textAttributes").getString(ViewProps.LAYOUT_DIRECTION)) != 1) {
            return false;
        }
        return DEFAULT_INCLUDE_FONT_PADDING;
    }

    public static void setCachedSpannabledForTag(int reactTag, Spannable sp) {
        sTagToSpannableCache.put(Integer.valueOf(reactTag), sp);
    }

    public static void deleteCachedSpannableForTag(int reactTag) {
        sTagToSpannableCache.remove(Integer.valueOf(reactTag));
    }

    private static void buildSpannableFromFragment(Context context, ReadableArray fragments, SpannableStringBuilder sb, List<SetSpanOperation> ops) {
        int i;
        int i2 = 0;
        for (int size = fragments.size(); i2 < size; size = i) {
            ReadableMap map = fragments.getMap(i2);
            int length = sb.length();
            TextAttributeProps fromReadableMap = TextAttributeProps.fromReadableMap(new ReactStylesDiffMap(map.getMap("textAttributes")));
            sb.append((CharSequence) TextTransform.apply(map.getString("string"), fromReadableMap.mTextTransform));
            int length2 = sb.length();
            int i3 = map.hasKey("reactTag") ? map.getInt("reactTag") : -1;
            if (map.hasKey(ViewProps.IS_ATTACHMENT) && map.getBoolean(ViewProps.IS_ATTACHMENT)) {
                ops.add(new SetSpanOperation(sb.length() - 1, sb.length(), new TextInlineViewPlaceholderSpan(i3, (int) PixelUtil.toPixelFromSP(map.getDouble("width")), (int) PixelUtil.toPixelFromSP(map.getDouble("height")))));
            } else if (length2 >= length) {
                if (ReactAccessibilityDelegate.AccessibilityRole.LINK.equals(fromReadableMap.mAccessibilityRole)) {
                    ops.add(new SetSpanOperation(length, length2, new ReactClickableSpan(i3, fromReadableMap.mColor)));
                } else if (fromReadableMap.mIsColorSet) {
                    ops.add(new SetSpanOperation(length, length2, new ReactForegroundColorSpan(fromReadableMap.mColor)));
                }
                if (fromReadableMap.mIsBackgroundColorSet) {
                    ops.add(new SetSpanOperation(length, length2, new ReactBackgroundColorSpan(fromReadableMap.mBackgroundColor)));
                }
                if (!Float.isNaN(fromReadableMap.getLetterSpacing())) {
                    ops.add(new SetSpanOperation(length, length2, new CustomLetterSpacingSpan(fromReadableMap.getLetterSpacing())));
                }
                ops.add(new SetSpanOperation(length, length2, new ReactAbsoluteSizeSpan(fromReadableMap.mFontSize)));
                if (fromReadableMap.mFontStyle == -1 && fromReadableMap.mFontWeight == -1 && fromReadableMap.mFontFamily == null) {
                    i = size;
                } else {
                    i = size;
                    ops.add(new SetSpanOperation(length, length2, new CustomStyleSpan(fromReadableMap.mFontStyle, fromReadableMap.mFontWeight, fromReadableMap.mFontFeatureSettings, fromReadableMap.mFontFamily, context.getAssets())));
                }
                if (fromReadableMap.mIsUnderlineTextDecorationSet) {
                    ops.add(new SetSpanOperation(length, length2, new ReactUnderlineSpan()));
                }
                if (fromReadableMap.mIsLineThroughTextDecorationSet) {
                    ops.add(new SetSpanOperation(length, length2, new ReactStrikethroughSpan()));
                }
                if (fromReadableMap.mTextShadowOffsetDx != 0.0f || fromReadableMap.mTextShadowOffsetDy != 0.0f) {
                    ops.add(new SetSpanOperation(length, length2, new ShadowStyleSpan(fromReadableMap.mTextShadowOffsetDx, fromReadableMap.mTextShadowOffsetDy, fromReadableMap.mTextShadowRadius, fromReadableMap.mTextShadowColor)));
                }
                if (!Float.isNaN(fromReadableMap.getEffectiveLineHeight())) {
                    ops.add(new SetSpanOperation(length, length2, new CustomLineHeightSpan(fromReadableMap.getEffectiveLineHeight())));
                }
                ops.add(new SetSpanOperation(length, length2, new ReactTagSpan(i3)));
                i2++;
            }
            i = size;
            i2++;
        }
    }

    public static Spannable getOrCreateSpannableForText(Context context, ReadableMap attributedString, ReactTextViewManagerCallback reactTextViewManagerCallback) {
        Object obj = sSpannableCacheLock;
        synchronized (obj) {
            LruCache<ReadableNativeMap, Spannable> lruCache = sSpannableCache;
            Spannable spannable = lruCache.get((ReadableNativeMap) attributedString);
            if (spannable != null) {
                return spannable;
            }
            Spannable createSpannableFromAttributedString = createSpannableFromAttributedString(context, attributedString, reactTextViewManagerCallback);
            synchronized (obj) {
                lruCache.put((ReadableNativeMap) attributedString, createSpannableFromAttributedString);
            }
            return createSpannableFromAttributedString;
        }
    }

    private static Spannable createSpannableFromAttributedString(Context context, ReadableMap attributedString, ReactTextViewManagerCallback reactTextViewManagerCallback) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        ArrayList<SetSpanOperation> arrayList = new ArrayList();
        buildSpannableFromFragment(context, attributedString.getArray("fragments"), spannableStringBuilder, arrayList);
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

    /* JADX WARN: Code restructure failed: missing block: B:33:0x009d, code lost:
        if (r3 > r21) goto L34;
     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x00b1, code lost:
        if (r1 > r23) goto L41;
     */
    /* JADX WARN: Code restructure failed: missing block: B:74:0x0147, code lost:
        if (r6 != false) goto L75;
     */
    /* JADX WARN: Removed duplicated region for block: B:37:0x00a5  */
    /* JADX WARN: Removed duplicated region for block: B:45:0x00bd  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static long measureText(android.content.Context r18, com.facebook.react.bridge.ReadableMap r19, com.facebook.react.bridge.ReadableMap r20, float r21, com.facebook.yoga.YogaMeasureMode r22, float r23, com.facebook.yoga.YogaMeasureMode r24, com.facebook.react.views.text.ReactTextViewManagerCallback r25, float[] r26) {
        /*
            Method dump skipped, instructions count: 384
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.facebook.react.views.text.TextLayoutManager.measureText(android.content.Context, com.facebook.react.bridge.ReadableMap, com.facebook.react.bridge.ReadableMap, float, com.facebook.yoga.YogaMeasureMode, float, com.facebook.yoga.YogaMeasureMode, com.facebook.react.views.text.ReactTextViewManagerCallback, float[]):long");
    }

    public static WritableArray measureLines(Context context, ReadableMap attributedString, ReadableMap paragraphAttributes, float width) {
        TextPaint textPaint = sTextPaintInstance;
        Spannable orCreateSpannableForText = getOrCreateSpannableForText(context, attributedString, null);
        return FontMetricsUtil.getFontMetrics(orCreateSpannableForText, createLayout(orCreateSpannableForText, BoringLayout.isBoring(orCreateSpannableForText, textPaint), width, YogaMeasureMode.EXACTLY, paragraphAttributes.hasKey("includeFontPadding") ? paragraphAttributes.getBoolean("includeFontPadding") : DEFAULT_INCLUDE_FONT_PADDING, TextAttributeProps.getTextBreakStrategy(paragraphAttributes.getString("textBreakStrategy"))), textPaint, context);
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
