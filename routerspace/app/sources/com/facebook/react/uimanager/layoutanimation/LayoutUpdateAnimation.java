package com.facebook.react.uimanager.layoutanimation;

import android.view.View;
import android.view.animation.Animation;
/* loaded from: classes.dex */
class LayoutUpdateAnimation extends AbstractLayoutAnimation {
    private static final boolean USE_TRANSLATE_ANIMATION = false;

    @Override // com.facebook.react.uimanager.layoutanimation.AbstractLayoutAnimation
    boolean isValid() {
        return this.mDurationMs > 0;
    }

    @Override // com.facebook.react.uimanager.layoutanimation.AbstractLayoutAnimation
    Animation createAnimationImpl(View view, int x, int y, int width, int height) {
        boolean z = false;
        boolean z2 = (view.getX() == ((float) x) && view.getY() == ((float) y)) ? false : true;
        if (view.getWidth() != width || view.getHeight() != height) {
            z = true;
        }
        if (z2 || z) {
            return new PositionAndSizeAnimation(view, x, y, width, height);
        }
        return null;
    }
}
