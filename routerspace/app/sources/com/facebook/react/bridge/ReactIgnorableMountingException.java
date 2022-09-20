package com.facebook.react.bridge;
/* loaded from: classes.dex */
public class ReactIgnorableMountingException extends RuntimeException {
    public ReactIgnorableMountingException(String m) {
        super(m);
    }

    public ReactIgnorableMountingException(String m, Throwable e) {
        super(m, e);
    }

    public ReactIgnorableMountingException(Throwable e) {
        super(e);
    }

    public static boolean isIgnorable(Throwable e) {
        while (e != null) {
            if (e instanceof ReactIgnorableMountingException) {
                return true;
            }
            e = e.getCause();
        }
        return false;
    }
}
