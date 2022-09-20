package com.facebook.common.util;

import javax.annotation.Nullable;
/* loaded from: classes.dex */
public enum TriState {
    YES,
    NO,
    UNSET;

    public boolean isSet() {
        return this != UNSET;
    }

    public static TriState valueOf(boolean bool) {
        return bool ? YES : NO;
    }

    public static TriState valueOf(@Nullable Boolean bool) {
        return bool != null ? valueOf(bool.booleanValue()) : UNSET;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.facebook.common.util.TriState$1 */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$facebook$common$util$TriState;

        static {
            int[] iArr = new int[TriState.values().length];
            $SwitchMap$com$facebook$common$util$TriState = iArr;
            try {
                iArr[TriState.YES.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$facebook$common$util$TriState[TriState.NO.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$facebook$common$util$TriState[TriState.UNSET.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    public boolean asBoolean() {
        int i = AnonymousClass1.$SwitchMap$com$facebook$common$util$TriState[ordinal()];
        if (i != 1) {
            if (i == 2) {
                return false;
            }
            if (i == 3) {
                throw new IllegalStateException("No boolean equivalent for UNSET");
            }
            throw new IllegalStateException("Unrecognized TriState value: " + this);
        }
        return true;
    }

    public boolean asBoolean(boolean defaultValue) {
        int i = AnonymousClass1.$SwitchMap$com$facebook$common$util$TriState[ordinal()];
        if (i != 1) {
            if (i == 2) {
                return false;
            }
            if (i == 3) {
                return defaultValue;
            }
            throw new IllegalStateException("Unrecognized TriState value: " + this);
        }
        return true;
    }

    @Nullable
    public Boolean asBooleanObject() {
        int i = AnonymousClass1.$SwitchMap$com$facebook$common$util$TriState[ordinal()];
        if (i != 1) {
            if (i == 2) {
                return Boolean.FALSE;
            }
            if (i == 3) {
                return null;
            }
            throw new IllegalStateException("Unrecognized TriState value: " + this);
        }
        return Boolean.TRUE;
    }

    public int getDbValue() {
        int i = AnonymousClass1.$SwitchMap$com$facebook$common$util$TriState[ordinal()];
        int i2 = 1;
        if (i != 1) {
            i2 = 2;
            if (i != 2) {
                return 3;
            }
        }
        return i2;
    }

    public static TriState fromDbValue(int value) {
        if (value != 1) {
            if (value == 2) {
                return NO;
            }
            return UNSET;
        }
        return YES;
    }
}
