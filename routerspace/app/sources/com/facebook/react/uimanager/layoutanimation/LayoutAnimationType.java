package com.facebook.react.uimanager.layoutanimation;
/* loaded from: classes.dex */
enum LayoutAnimationType {
    CREATE,
    UPDATE,
    DELETE;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.facebook.react.uimanager.layoutanimation.LayoutAnimationType$1 */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$facebook$react$uimanager$layoutanimation$LayoutAnimationType;

        static {
            int[] iArr = new int[LayoutAnimationType.values().length];
            $SwitchMap$com$facebook$react$uimanager$layoutanimation$LayoutAnimationType = iArr;
            try {
                iArr[LayoutAnimationType.CREATE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$facebook$react$uimanager$layoutanimation$LayoutAnimationType[LayoutAnimationType.UPDATE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$facebook$react$uimanager$layoutanimation$LayoutAnimationType[LayoutAnimationType.DELETE.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    public static String toString(LayoutAnimationType type) {
        int i = AnonymousClass1.$SwitchMap$com$facebook$react$uimanager$layoutanimation$LayoutAnimationType[type.ordinal()];
        if (i != 1) {
            if (i == 2) {
                return "update";
            }
            if (i == 3) {
                return "delete";
            }
            throw new IllegalArgumentException("Unsupported LayoutAnimationType: " + type);
        }
        return "create";
    }
}
