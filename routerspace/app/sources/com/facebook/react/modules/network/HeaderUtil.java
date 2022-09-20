package com.facebook.react.modules.network;
/* loaded from: classes.dex */
public class HeaderUtil {
    public static String stripHeaderName(String name) {
        StringBuilder sb = new StringBuilder(name.length());
        int length = name.length();
        boolean z = false;
        for (int i = 0; i < length; i++) {
            char charAt = name.charAt(i);
            if (charAt <= ' ' || charAt >= 127) {
                z = true;
            } else {
                sb.append(charAt);
            }
        }
        return z ? sb.toString() : name;
    }

    public static String stripHeaderValue(String value) {
        StringBuilder sb = new StringBuilder(value.length());
        int length = value.length();
        boolean z = false;
        for (int i = 0; i < length; i++) {
            char charAt = value.charAt(i);
            if ((charAt <= 31 || charAt >= 127) && charAt != '\t') {
                z = true;
            } else {
                sb.append(charAt);
            }
        }
        return z ? sb.toString() : value;
    }
}
