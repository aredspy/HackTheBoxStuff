package com.th3rdwave.safeareacontext;
/* loaded from: classes.dex */
class EdgeInsets {
    float bottom;
    float left;
    float right;
    float top;

    public EdgeInsets(float top, float right, float bottom, float left) {
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
    }

    public boolean equalsToEdgeInsets(EdgeInsets other) {
        if (this == other) {
            return true;
        }
        return this.top == other.top && this.right == other.right && this.bottom == other.bottom && this.left == other.left;
    }
}
