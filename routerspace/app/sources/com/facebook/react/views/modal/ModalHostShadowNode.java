package com.facebook.react.views.modal;

import android.graphics.Point;
import com.facebook.react.uimanager.LayoutShadowNode;
import com.facebook.react.uimanager.ReactShadowNodeImpl;
/* loaded from: classes.dex */
class ModalHostShadowNode extends LayoutShadowNode {
    @Override // com.facebook.react.uimanager.ReactShadowNodeImpl
    public void addChildAt(ReactShadowNodeImpl child, int i) {
        super.addChildAt(child, i);
        Point modalHostSize = ModalHostHelper.getModalHostSize(getThemedContext());
        child.setStyleWidth(modalHostSize.x);
        child.setStyleHeight(modalHostSize.y);
    }
}
