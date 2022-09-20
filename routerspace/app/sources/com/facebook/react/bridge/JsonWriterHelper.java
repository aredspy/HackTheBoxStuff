package com.facebook.react.bridge;

import android.util.JsonWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
public class JsonWriterHelper {
    public static void value(JsonWriter writer, Object value) throws IOException {
        if (value instanceof Map) {
            mapValue(writer, (Map) value);
        } else if (value instanceof List) {
            listValue(writer, (List) value);
        } else if (value instanceof ReadableMap) {
            readableMapValue(writer, (ReadableMap) value);
        } else if (value instanceof ReadableArray) {
            readableArrayValue(writer, (ReadableArray) value);
        } else if (value instanceof Dynamic) {
            dynamicValue(writer, (Dynamic) value);
        } else {
            objectValue(writer, value);
        }
    }

    /* renamed from: com.facebook.react.bridge.JsonWriterHelper$1 */
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

    private static void dynamicValue(JsonWriter writer, Dynamic value) throws IOException {
        switch (AnonymousClass1.$SwitchMap$com$facebook$react$bridge$ReadableType[value.getType().ordinal()]) {
            case 1:
                writer.nullValue();
                return;
            case 2:
                writer.value(value.asBoolean());
                return;
            case 3:
                writer.value(value.asDouble());
                return;
            case 4:
                writer.value(value.asString());
                return;
            case 5:
                readableMapValue(writer, value.asMap());
                return;
            case 6:
                readableArrayValue(writer, value.asArray());
                return;
            default:
                throw new IllegalArgumentException("Unknown data type: " + value.getType());
        }
    }

    private static void readableMapValue(JsonWriter writer, ReadableMap value) throws IOException {
        writer.beginObject();
        try {
            ReadableMapKeySetIterator keySetIterator = value.keySetIterator();
            while (keySetIterator.hasNextKey()) {
                String nextKey = keySetIterator.nextKey();
                writer.name(nextKey);
                switch (AnonymousClass1.$SwitchMap$com$facebook$react$bridge$ReadableType[value.getType(nextKey).ordinal()]) {
                    case 1:
                        writer.nullValue();
                        break;
                    case 2:
                        writer.value(value.getBoolean(nextKey));
                        break;
                    case 3:
                        writer.value(value.getDouble(nextKey));
                        break;
                    case 4:
                        writer.value(value.getString(nextKey));
                        break;
                    case 5:
                        readableMapValue(writer, value.getMap(nextKey));
                        break;
                    case 6:
                        readableArrayValue(writer, value.getArray(nextKey));
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown data type: " + value.getType(nextKey));
                }
            }
        } finally {
            writer.endObject();
        }
    }

    public static void readableArrayValue(JsonWriter writer, ReadableArray value) throws IOException {
        writer.beginArray();
        for (int i = 0; i < value.size(); i++) {
            try {
                switch (AnonymousClass1.$SwitchMap$com$facebook$react$bridge$ReadableType[value.getType(i).ordinal()]) {
                    case 1:
                        writer.nullValue();
                        break;
                    case 2:
                        writer.value(value.getBoolean(i));
                        break;
                    case 3:
                        writer.value(value.getDouble(i));
                        break;
                    case 4:
                        writer.value(value.getString(i));
                        break;
                    case 5:
                        readableMapValue(writer, value.getMap(i));
                        break;
                    case 6:
                        readableArrayValue(writer, value.getArray(i));
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown data type: " + value.getType(i));
                }
            } finally {
                writer.endArray();
            }
        }
    }

    private static void mapValue(JsonWriter writer, Map<?, ?> map) throws IOException {
        writer.beginObject();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            writer.name(entry.getKey().toString());
            value(writer, entry.getValue());
        }
        writer.endObject();
    }

    private static void listValue(JsonWriter writer, List<?> list) throws IOException {
        writer.beginArray();
        Iterator<?> it = list.iterator();
        while (it.hasNext()) {
            objectValue(writer, it.next());
        }
        writer.endArray();
    }

    private static void objectValue(JsonWriter writer, Object value) throws IOException {
        if (value == null) {
            writer.nullValue();
        } else if (value instanceof String) {
            writer.value((String) value);
        } else if (value instanceof Number) {
            writer.value((Number) value);
        } else if (value instanceof Boolean) {
            writer.value(((Boolean) value).booleanValue());
        } else {
            throw new IllegalArgumentException("Unknown value: " + value);
        }
    }
}
