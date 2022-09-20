package com.facebook.yoga;
/* loaded from: classes.dex */
public class YogaValue {
    public final YogaUnit unit;
    public final float value;
    static final YogaValue UNDEFINED = new YogaValue(Float.NaN, YogaUnit.UNDEFINED);
    static final YogaValue ZERO = new YogaValue(0.0f, YogaUnit.POINT);
    static final YogaValue AUTO = new YogaValue(Float.NaN, YogaUnit.AUTO);

    public YogaValue(float value, YogaUnit unit) {
        this.value = value;
        this.unit = unit;
    }

    public YogaValue(float value, int unit) {
        this(value, YogaUnit.fromInt(unit));
    }

    public boolean equals(Object other) {
        if (other instanceof YogaValue) {
            YogaValue yogaValue = (YogaValue) other;
            YogaUnit yogaUnit = this.unit;
            if (yogaUnit != yogaValue.unit) {
                return false;
            }
            return yogaUnit == YogaUnit.UNDEFINED || this.unit == YogaUnit.AUTO || Float.compare(this.value, yogaValue.value) == 0;
        }
        return false;
    }

    public int hashCode() {
        return Float.floatToIntBits(this.value) + this.unit.intValue();
    }

    /* renamed from: com.facebook.yoga.YogaValue$1 */
    /* loaded from: classes.dex */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$facebook$yoga$YogaUnit;

        static {
            int[] iArr = new int[YogaUnit.values().length];
            $SwitchMap$com$facebook$yoga$YogaUnit = iArr;
            try {
                iArr[YogaUnit.UNDEFINED.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$facebook$yoga$YogaUnit[YogaUnit.POINT.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$facebook$yoga$YogaUnit[YogaUnit.PERCENT.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$facebook$yoga$YogaUnit[YogaUnit.AUTO.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    public String toString() {
        int i = AnonymousClass1.$SwitchMap$com$facebook$yoga$YogaUnit[this.unit.ordinal()];
        if (i != 1) {
            if (i == 2) {
                return Float.toString(this.value);
            }
            if (i != 3) {
                if (i != 4) {
                    throw new IllegalStateException();
                }
                return "auto";
            }
            return this.value + "%";
        }
        return "undefined";
    }

    public static YogaValue parse(String s) {
        if (s == null) {
            return null;
        }
        if ("undefined".equals(s)) {
            return UNDEFINED;
        }
        if ("auto".equals(s)) {
            return AUTO;
        }
        if (s.endsWith("%")) {
            return new YogaValue(Float.parseFloat(s.substring(0, s.length() - 1)), YogaUnit.PERCENT);
        }
        return new YogaValue(Float.parseFloat(s), YogaUnit.POINT);
    }
}
