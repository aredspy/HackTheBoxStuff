package com.facebook.react.uimanager;

import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewParent;
import com.facebook.infer.annotation.Assertions;
/* loaded from: classes.dex */
public class RootViewUtil {
    public static RootView getRootView(View reactView) {
        while (!(reactView instanceof RootView)) {
            ViewParent parent = reactView.getParent();
            if (parent == null) {
                return null;
            }
            Assertions.assertCondition(parent instanceof View);
            reactView = (View) parent;
        }
        return (RootView) reactView;
    }

    public static Point getViewportOffset(View v) {
        v.getLocationInWindow(r0);
        Rect rect = new Rect();
        v.getWindowVisibleDisplayFrame(rect);
        int[] iArr = {iArr[0] - rect.left, iArr[1] - rect.top};
        return new Point(iArr[0], iArr[1]);
    }
}
