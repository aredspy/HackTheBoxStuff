package com.facebook.react.modules.datepicker;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.DatePicker;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
/* loaded from: classes.dex */
public class DismissableDatePickerDialog extends DatePickerDialog {
    public DismissableDatePickerDialog(Context context, DatePickerDialog.OnDateSetListener callback, int year, int monthOfYear, int dayOfMonth) {
        super(context, callback, year, monthOfYear, dayOfMonth);
        fixSpinner(context, year, monthOfYear, dayOfMonth);
    }

    public DismissableDatePickerDialog(Context context, int theme, DatePickerDialog.OnDateSetListener callback, int year, int monthOfYear, int dayOfMonth) {
        super(context, theme, callback, year, monthOfYear, dayOfMonth);
        fixSpinner(context, year, monthOfYear, dayOfMonth);
    }

    private void fixSpinner(Context context, int year, int month, int dayOfMonth) {
        if (Build.VERSION.SDK_INT == 24) {
            try {
                Class<?> cls = Class.forName("com.android.internal.R$styleable");
                TypedArray obtainStyledAttributes = context.obtainStyledAttributes(null, (int[]) cls.getField("DatePicker").get(null), 16843612, 0);
                int i = obtainStyledAttributes.getInt(cls.getField("DatePicker_datePickerMode").getInt(null), 2);
                obtainStyledAttributes.recycle();
                if (i != 2) {
                    return;
                }
                DatePicker datePicker = (DatePicker) findField(DatePickerDialog.class, DatePicker.class, "mDatePicker").get(this);
                Field findField = findField(DatePicker.class, Class.forName("android.widget.DatePickerSpinnerDelegate"), "mDelegate");
                if (findField.get(datePicker).getClass() == Class.forName("android.widget.DatePickerSpinnerDelegate")) {
                    return;
                }
                findField.set(datePicker, null);
                datePicker.removeAllViews();
                Method declaredMethod = DatePicker.class.getDeclaredMethod("createSpinnerUIDelegate", Context.class, AttributeSet.class, Integer.TYPE, Integer.TYPE);
                declaredMethod.setAccessible(true);
                findField.set(datePicker, declaredMethod.invoke(datePicker, context, null, 16843612, 0));
                datePicker.setCalendarViewShown(false);
                datePicker.init(year, month, dayOfMonth, this);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static Field findField(Class objectClass, Class fieldClass, String expectedName) {
        Field[] declaredFields;
        try {
            Field declaredField = objectClass.getDeclaredField(expectedName);
            declaredField.setAccessible(true);
            return declaredField;
        } catch (NoSuchFieldException unused) {
            for (Field field : objectClass.getDeclaredFields()) {
                if (field.getType() == fieldClass) {
                    field.setAccessible(true);
                    return field;
                }
            }
            return null;
        }
    }
}
