package com.facebook.react.views.text;

import java.text.BreakIterator;
/* loaded from: classes.dex */
public enum TextTransform {
    NONE,
    UPPERCASE,
    LOWERCASE,
    CAPITALIZE,
    UNSET;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.facebook.react.views.text.TextTransform$1 */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$facebook$react$views$text$TextTransform;

        static {
            int[] iArr = new int[TextTransform.values().length];
            $SwitchMap$com$facebook$react$views$text$TextTransform = iArr;
            try {
                iArr[TextTransform.UPPERCASE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$facebook$react$views$text$TextTransform[TextTransform.LOWERCASE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$facebook$react$views$text$TextTransform[TextTransform.CAPITALIZE.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    public static String apply(String text, TextTransform textTransform) {
        if (text == null) {
            return null;
        }
        int i = AnonymousClass1.$SwitchMap$com$facebook$react$views$text$TextTransform[textTransform.ordinal()];
        if (i == 1) {
            return text.toUpperCase();
        }
        if (i == 2) {
            return text.toLowerCase();
        }
        return i != 3 ? text : capitalize(text);
    }

    private static String capitalize(String text) {
        BreakIterator wordInstance = BreakIterator.getWordInstance();
        wordInstance.setText(text);
        StringBuilder sb = new StringBuilder(text.length());
        int first = wordInstance.first();
        while (true) {
            int i = first;
            first = wordInstance.next();
            if (first != -1) {
                String substring = text.substring(i, first);
                if (Character.isLetterOrDigit(substring.charAt(0))) {
                    sb.append(Character.toUpperCase(substring.charAt(0)));
                    sb.append(substring.substring(1).toLowerCase());
                } else {
                    sb.append(substring);
                }
            } else {
                return sb.toString();
            }
        }
    }
}
