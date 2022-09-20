package com.facebook.react.uimanager;

import com.facebook.yoga.YogaConstants;
import java.util.Arrays;
/* loaded from: classes.dex */
public class Spacing {
    public static final int ALL = 8;
    public static final int BOTTOM = 3;
    public static final int END = 5;
    public static final int HORIZONTAL = 6;
    public static final int LEFT = 0;
    public static final int RIGHT = 2;
    public static final int START = 4;
    public static final int TOP = 1;
    public static final int VERTICAL = 7;
    private static final int[] sFlagsMap = {1, 2, 4, 8, 16, 32, 64, 128, 256};
    private final float mDefaultValue;
    private boolean mHasAliasesSet;
    private final float[] mSpacing;
    private int mValueFlags;

    public Spacing() {
        this(0.0f);
    }

    public Spacing(float defaultValue) {
        this.mValueFlags = 0;
        this.mDefaultValue = defaultValue;
        this.mSpacing = newFullSpacingArray();
    }

    public Spacing(Spacing original) {
        this.mValueFlags = 0;
        this.mDefaultValue = original.mDefaultValue;
        float[] fArr = original.mSpacing;
        this.mSpacing = Arrays.copyOf(fArr, fArr.length);
        this.mValueFlags = original.mValueFlags;
        this.mHasAliasesSet = original.mHasAliasesSet;
    }

    public boolean set(int spacingType, float value) {
        boolean z = false;
        if (!FloatUtil.floatsEqual(this.mSpacing[spacingType], value)) {
            this.mSpacing[spacingType] = value;
            if (YogaConstants.isUndefined(value)) {
                this.mValueFlags = (~sFlagsMap[spacingType]) & this.mValueFlags;
            } else {
                this.mValueFlags = sFlagsMap[spacingType] | this.mValueFlags;
            }
            int i = this.mValueFlags;
            int[] iArr = sFlagsMap;
            if ((iArr[8] & i) != 0 || (iArr[7] & i) != 0 || (i & iArr[6]) != 0) {
                z = true;
            }
            this.mHasAliasesSet = z;
            return true;
        }
        return false;
    }

    public float get(int spacingType) {
        float f = (spacingType == 4 || spacingType == 5) ? Float.NaN : this.mDefaultValue;
        int i = this.mValueFlags;
        if (i == 0) {
            return f;
        }
        int[] iArr = sFlagsMap;
        if ((iArr[spacingType] & i) != 0) {
            return this.mSpacing[spacingType];
        }
        if (this.mHasAliasesSet) {
            char c = (spacingType == 1 || spacingType == 3) ? (char) 7 : (char) 6;
            if ((iArr[c] & i) != 0) {
                return this.mSpacing[c];
            }
            if ((i & iArr[8]) != 0) {
                return this.mSpacing[8];
            }
        }
        return f;
    }

    public float getRaw(int spacingType) {
        return this.mSpacing[spacingType];
    }

    public void reset() {
        Arrays.fill(this.mSpacing, Float.NaN);
        this.mHasAliasesSet = false;
        this.mValueFlags = 0;
    }

    float getWithFallback(int spacingType, int fallbackType) {
        return (this.mValueFlags & sFlagsMap[spacingType]) != 0 ? this.mSpacing[spacingType] : get(fallbackType);
    }

    private static float[] newFullSpacingArray() {
        return new float[]{Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN};
    }
}
