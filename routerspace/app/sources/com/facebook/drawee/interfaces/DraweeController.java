package com.facebook.drawee.interfaces;

import android.graphics.drawable.Animatable;
import android.view.MotionEvent;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public interface DraweeController {
    Animatable getAnimatable();

    String getContentDescription();

    @Nullable
    DraweeHierarchy getHierarchy();

    boolean isSameImageRequest(DraweeController other);

    void onAttach();

    void onDetach();

    boolean onTouchEvent(MotionEvent event);

    void onViewportVisibilityHint(boolean isVisibleInViewportHint);

    void setContentDescription(String contentDescription);

    void setHierarchy(@Nullable DraweeHierarchy hierarchy);
}
