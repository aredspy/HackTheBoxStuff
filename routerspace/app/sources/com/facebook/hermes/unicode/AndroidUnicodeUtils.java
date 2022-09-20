package com.facebook.hermes.unicode;

import java.text.Collator;
import java.text.DateFormat;
import java.text.Normalizer;
import java.util.Locale;
/* loaded from: classes.dex */
public class AndroidUnicodeUtils {
    public static int localeCompare(String left, String right) {
        return Collator.getInstance().compare(left, right);
    }

    public static String dateFormat(double unixtimeMs, boolean formatDate, boolean formatTime) {
        DateFormat dateFormat;
        if (formatDate && formatTime) {
            dateFormat = DateFormat.getDateTimeInstance(2, 2);
        } else if (formatDate) {
            dateFormat = DateFormat.getDateInstance(2);
        } else if (formatTime) {
            dateFormat = DateFormat.getTimeInstance(2);
        } else {
            throw new RuntimeException("Bad dateFormat configuration");
        }
        return dateFormat.format(Long.valueOf((long) unixtimeMs)).toString();
    }

    public static String convertToCase(String input, int targetCase, boolean useCurrentLocale) {
        Locale locale = useCurrentLocale ? Locale.getDefault() : Locale.ENGLISH;
        if (targetCase != 0) {
            if (targetCase == 1) {
                return input.toLowerCase(locale);
            }
            throw new RuntimeException("Invalid target case");
        }
        return input.toUpperCase(locale);
    }

    public static String normalize(String input, int form) {
        if (form != 0) {
            if (form == 1) {
                return Normalizer.normalize(input, Normalizer.Form.NFD);
            }
            if (form == 2) {
                return Normalizer.normalize(input, Normalizer.Form.NFKC);
            }
            if (form == 3) {
                return Normalizer.normalize(input, Normalizer.Form.NFKD);
            }
            throw new RuntimeException("Invalid form");
        }
        return Normalizer.normalize(input, Normalizer.Form.NFC);
    }
}
