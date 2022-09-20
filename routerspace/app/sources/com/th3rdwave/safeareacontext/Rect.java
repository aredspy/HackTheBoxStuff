package com.th3rdwave.safeareacontext;
/* loaded from: classes.dex */
class Rect {
    float height;
    float width;
    float x;
    float y;

    public Rect(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean equalsToRect(Rect other) {
        if (this == other) {
            return true;
        }
        return this.x == other.x && this.y == other.y && this.width == other.width && this.height == other.height;
    }
}
