package com.facebook.react.uimanager.common;
/* loaded from: classes.dex */
public class ViewUtil {
    public static int getUIManagerType(int reactTag) {
        return reactTag % 2 == 0 ? 2 : 1;
    }

    @Deprecated
    public static boolean isRootTag(int reactTag) {
        return reactTag % 10 == 1;
    }
}
