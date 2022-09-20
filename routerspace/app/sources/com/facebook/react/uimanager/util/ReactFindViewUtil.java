package com.facebook.react.uimanager.util;

import android.view.View;
import android.view.ViewGroup;
import com.facebook.react.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
/* loaded from: classes.dex */
public class ReactFindViewUtil {
    private static final List<OnViewFoundListener> mOnViewFoundListeners = new ArrayList();
    private static final Map<OnMultipleViewsFoundListener, Set<String>> mOnMultipleViewsFoundListener = new HashMap();

    /* loaded from: classes.dex */
    public interface OnMultipleViewsFoundListener {
        void onViewFound(View view, String nativeId);
    }

    /* loaded from: classes.dex */
    public interface OnViewFoundListener {
        String getNativeId();

        void onViewFound(View view);
    }

    public static View findView(View root, String nativeId) {
        String nativeId2 = getNativeId(root);
        if (nativeId2 == null || !nativeId2.equals(nativeId)) {
            if (!(root instanceof ViewGroup)) {
                return null;
            }
            ViewGroup viewGroup = (ViewGroup) root;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View findView = findView(viewGroup.getChildAt(i), nativeId);
                if (findView != null) {
                    return findView;
                }
            }
            return null;
        }
        return root;
    }

    public static void findView(View root, OnViewFoundListener onViewFoundListener) {
        View findView = findView(root, onViewFoundListener.getNativeId());
        if (findView != null) {
            onViewFoundListener.onViewFound(findView);
        }
        addViewListener(onViewFoundListener);
    }

    public static void addViewListener(OnViewFoundListener onViewFoundListener) {
        mOnViewFoundListeners.add(onViewFoundListener);
    }

    public static void removeViewListener(OnViewFoundListener onViewFoundListener) {
        mOnViewFoundListeners.remove(onViewFoundListener);
    }

    public static void addViewsListener(OnMultipleViewsFoundListener listener, Set<String> ids) {
        mOnMultipleViewsFoundListener.put(listener, ids);
    }

    public static void removeViewsListener(OnMultipleViewsFoundListener listener) {
        mOnMultipleViewsFoundListener.remove(listener);
    }

    public static void notifyViewRendered(View view) {
        String nativeId = getNativeId(view);
        if (nativeId == null) {
            return;
        }
        Iterator<OnViewFoundListener> it = mOnViewFoundListeners.iterator();
        while (it.hasNext()) {
            OnViewFoundListener next = it.next();
            if (nativeId != null && nativeId.equals(next.getNativeId())) {
                next.onViewFound(view);
                it.remove();
            }
        }
        for (Map.Entry<OnMultipleViewsFoundListener, Set<String>> entry : mOnMultipleViewsFoundListener.entrySet()) {
            Set<String> value = entry.getValue();
            if (value != null && value.contains(nativeId)) {
                entry.getKey().onViewFound(view, nativeId);
            }
        }
    }

    private static String getNativeId(View view) {
        Object tag = view.getTag(R.id.view_tag_native_id);
        if (tag instanceof String) {
            return (String) tag;
        }
        return null;
    }
}
