package com.facebook.react.bridge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes.dex */
public class JavaOnlyArray implements ReadableArray, WritableArray {
    private final List mBackingList;

    public static JavaOnlyArray from(List list) {
        return new JavaOnlyArray(list);
    }

    public static JavaOnlyArray of(Object... values) {
        return new JavaOnlyArray(values);
    }

    public static JavaOnlyArray deepClone(ReadableArray ary) {
        JavaOnlyArray javaOnlyArray = new JavaOnlyArray();
        int size = ary.size();
        for (int i = 0; i < size; i++) {
            switch (AnonymousClass1.$SwitchMap$com$facebook$react$bridge$ReadableType[ary.getType(i).ordinal()]) {
                case 1:
                    javaOnlyArray.pushNull();
                    break;
                case 2:
                    javaOnlyArray.pushBoolean(ary.getBoolean(i));
                    break;
                case 3:
                    javaOnlyArray.pushDouble(ary.getDouble(i));
                    break;
                case 4:
                    javaOnlyArray.pushString(ary.getString(i));
                    break;
                case 5:
                    javaOnlyArray.pushMap(JavaOnlyMap.deepClone(ary.getMap(i)));
                    break;
                case 6:
                    javaOnlyArray.pushArray(deepClone(ary.getArray(i)));
                    break;
            }
        }
        return javaOnlyArray;
    }

    /* renamed from: com.facebook.react.bridge.JavaOnlyArray$1 */
    /* loaded from: classes.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$facebook$react$bridge$ReadableType;

        static {
            int[] iArr = new int[ReadableType.values().length];
            $SwitchMap$com$facebook$react$bridge$ReadableType = iArr;
            try {
                iArr[ReadableType.Null.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$facebook$react$bridge$ReadableType[ReadableType.Boolean.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$facebook$react$bridge$ReadableType[ReadableType.Number.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$facebook$react$bridge$ReadableType[ReadableType.String.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$facebook$react$bridge$ReadableType[ReadableType.Map.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$facebook$react$bridge$ReadableType[ReadableType.Array.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
        }
    }

    private JavaOnlyArray(Object... values) {
        this.mBackingList = Arrays.asList(values);
    }

    private JavaOnlyArray(List list) {
        this.mBackingList = new ArrayList(list);
    }

    public JavaOnlyArray() {
        this.mBackingList = new ArrayList();
    }

    @Override // com.facebook.react.bridge.ReadableArray
    public int size() {
        return this.mBackingList.size();
    }

    @Override // com.facebook.react.bridge.ReadableArray
    public boolean isNull(int index) {
        return this.mBackingList.get(index) == null;
    }

    @Override // com.facebook.react.bridge.ReadableArray
    public double getDouble(int index) {
        return ((Number) this.mBackingList.get(index)).doubleValue();
    }

    @Override // com.facebook.react.bridge.ReadableArray
    public int getInt(int index) {
        return ((Number) this.mBackingList.get(index)).intValue();
    }

    @Override // com.facebook.react.bridge.ReadableArray
    public String getString(int index) {
        return (String) this.mBackingList.get(index);
    }

    @Override // com.facebook.react.bridge.ReadableArray
    public ReadableArray getArray(int index) {
        return (ReadableArray) this.mBackingList.get(index);
    }

    @Override // com.facebook.react.bridge.ReadableArray
    public boolean getBoolean(int index) {
        return ((Boolean) this.mBackingList.get(index)).booleanValue();
    }

    @Override // com.facebook.react.bridge.ReadableArray
    public ReadableMap getMap(int index) {
        return (ReadableMap) this.mBackingList.get(index);
    }

    @Override // com.facebook.react.bridge.ReadableArray
    public Dynamic getDynamic(int index) {
        return DynamicFromArray.create(this, index);
    }

    @Override // com.facebook.react.bridge.ReadableArray
    public ReadableType getType(int index) {
        Object obj = this.mBackingList.get(index);
        if (obj == null) {
            return ReadableType.Null;
        }
        if (obj instanceof Boolean) {
            return ReadableType.Boolean;
        }
        if ((obj instanceof Double) || (obj instanceof Float) || (obj instanceof Integer)) {
            return ReadableType.Number;
        }
        if (obj instanceof String) {
            return ReadableType.String;
        }
        if (obj instanceof ReadableArray) {
            return ReadableType.Array;
        }
        if (!(obj instanceof ReadableMap)) {
            return null;
        }
        return ReadableType.Map;
    }

    @Override // com.facebook.react.bridge.WritableArray
    public void pushBoolean(boolean value) {
        this.mBackingList.add(Boolean.valueOf(value));
    }

    @Override // com.facebook.react.bridge.WritableArray
    public void pushDouble(double value) {
        this.mBackingList.add(Double.valueOf(value));
    }

    @Override // com.facebook.react.bridge.WritableArray
    public void pushInt(int value) {
        this.mBackingList.add(new Double(value));
    }

    @Override // com.facebook.react.bridge.WritableArray
    public void pushString(String value) {
        this.mBackingList.add(value);
    }

    @Override // com.facebook.react.bridge.WritableArray
    public void pushArray(ReadableArray array) {
        this.mBackingList.add(array);
    }

    @Override // com.facebook.react.bridge.WritableArray
    public void pushMap(ReadableMap map) {
        this.mBackingList.add(map);
    }

    @Override // com.facebook.react.bridge.WritableArray
    public void pushNull() {
        this.mBackingList.add(null);
    }

    @Override // com.facebook.react.bridge.ReadableArray
    public ArrayList<Object> toArrayList() {
        return new ArrayList<>(this.mBackingList);
    }

    public String toString() {
        return this.mBackingList.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        List list = this.mBackingList;
        List list2 = ((JavaOnlyArray) o).mBackingList;
        return list == null ? list2 == null : list.equals(list2);
    }

    public int hashCode() {
        List list = this.mBackingList;
        if (list != null) {
            return list.hashCode();
        }
        return 0;
    }
}
