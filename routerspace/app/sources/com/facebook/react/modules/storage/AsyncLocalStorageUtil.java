package com.facebook.react.modules.storage;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import com.facebook.react.bridge.ReadableArray;
import java.util.Arrays;
import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes.dex */
public class AsyncLocalStorageUtil {
    public static String buildKeySelection(int selectionCount) {
        String[] strArr = new String[selectionCount];
        Arrays.fill(strArr, "?");
        return "key IN (" + TextUtils.join(", ", strArr) + ")";
    }

    public static String[] buildKeySelectionArgs(ReadableArray keys, int start, int count) {
        String[] strArr = new String[count];
        for (int i = 0; i < count; i++) {
            strArr[i] = keys.getString(start + i);
        }
        return strArr;
    }

    public static String getItemImpl(SQLiteDatabase db, String key) {
        Cursor query = db.query("catalystLocalStorage", new String[]{"value"}, "key=?", new String[]{key}, null, null, null);
        try {
            if (query.moveToFirst()) {
                return query.getString(0);
            }
            return null;
        } finally {
            query.close();
        }
    }

    static boolean setItemImpl(SQLiteDatabase db, String key, String value) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("key", key);
        contentValues.put("value", value);
        return -1 != db.insertWithOnConflict("catalystLocalStorage", null, contentValues, 5);
    }

    public static boolean mergeImpl(SQLiteDatabase db, String key, String value) throws JSONException {
        String itemImpl = getItemImpl(db, key);
        if (itemImpl != null) {
            JSONObject jSONObject = new JSONObject(itemImpl);
            deepMergeInto(jSONObject, new JSONObject(value));
            value = jSONObject.toString();
        }
        return setItemImpl(db, key, value);
    }

    private static void deepMergeInto(JSONObject oldJSON, JSONObject newJSON) throws JSONException {
        Iterator<String> keys = newJSON.keys();
        while (keys.hasNext()) {
            String next = keys.next();
            JSONObject optJSONObject = newJSON.optJSONObject(next);
            JSONObject optJSONObject2 = oldJSON.optJSONObject(next);
            if (optJSONObject != null && optJSONObject2 != null) {
                deepMergeInto(optJSONObject2, optJSONObject);
                oldJSON.put(next, optJSONObject2);
            } else {
                oldJSON.put(next, newJSON.get(next));
            }
        }
    }
}
