package com.th3rdwave.safeareacontext;

import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
/* loaded from: classes.dex */
class SafeAreaUtils {
    SafeAreaUtils() {
    }

    private static EdgeInsets getRootWindowInsetsCompat(View rootView) {
        if (Build.VERSION.SDK_INT >= 23) {
            WindowInsets rootWindowInsets = rootView.getRootWindowInsets();
            if (rootWindowInsets != null) {
                return new EdgeInsets(rootWindowInsets.getSystemWindowInsetTop(), rootWindowInsets.getSystemWindowInsetRight(), Math.min(rootWindowInsets.getSystemWindowInsetBottom(), rootWindowInsets.getStableInsetBottom()), rootWindowInsets.getSystemWindowInsetLeft());
            }
            return null;
        }
        Rect rect = new Rect();
        rootView.getWindowVisibleDisplayFrame(rect);
        return new EdgeInsets(rect.top, rootView.getWidth() - rect.right, rootView.getHeight() - rect.bottom, rect.left);
    }

    public static EdgeInsets getSafeAreaInsets(View view) {
        View rootView;
        EdgeInsets rootWindowInsetsCompat;
        if (view.getHeight() == 0 || (rootWindowInsetsCompat = getRootWindowInsetsCompat((rootView = view.getRootView()))) == null) {
            return null;
        }
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        rootWindowInsetsCompat.top = Math.max(rootWindowInsetsCompat.top - rect.top, 0.0f);
        rootWindowInsetsCompat.left = Math.max(rootWindowInsetsCompat.left - rect.left, 0.0f);
        rootWindowInsetsCompat.bottom = Math.max(Math.min((rect.top + view.getHeight()) - rootView.getHeight(), 0.0f) + rootWindowInsetsCompat.bottom, 0.0f);
        rootWindowInsetsCompat.right = Math.max(Math.min((rect.left + view.getWidth()) - rootView.getWidth(), 0.0f) + rootWindowInsetsCompat.right, 0.0f);
        return rootWindowInsetsCompat;
    }

    public static Rect getFrame(ViewGroup rootView, View view) {
        if (view.getParent() == null) {
            return null;
        }
        Rect rect = new Rect();
        view.getDrawingRect(rect);
        try {
            rootView.offsetDescendantRectToMyCoords(view, rect);
            return new Rect(rect.left, rect.top, view.getWidth(), view.getHeight());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }
}
