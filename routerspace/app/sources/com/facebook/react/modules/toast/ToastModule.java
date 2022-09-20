package com.facebook.react.modules.toast;

import android.widget.Toast;
import com.facebook.fbreact.specs.NativeToastAndroidSpec;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.module.annotations.ReactModule;
import java.util.HashMap;
import java.util.Map;
@ReactModule(name = ToastModule.NAME)
/* loaded from: classes.dex */
public class ToastModule extends NativeToastAndroidSpec {
    private static final String DURATION_LONG_KEY = "LONG";
    private static final String DURATION_SHORT_KEY = "SHORT";
    private static final String GRAVITY_BOTTOM_KEY = "BOTTOM";
    private static final String GRAVITY_CENTER = "CENTER";
    private static final String GRAVITY_TOP_KEY = "TOP";
    public static final String NAME = "ToastAndroid";

    @Override // com.facebook.react.bridge.NativeModule
    public String getName() {
        return NAME;
    }

    public ToastModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override // com.facebook.fbreact.specs.NativeToastAndroidSpec
    public Map<String, Object> getTypedExportedConstants() {
        HashMap newHashMap = MapBuilder.newHashMap();
        newHashMap.put(DURATION_SHORT_KEY, 0);
        newHashMap.put(DURATION_LONG_KEY, 1);
        newHashMap.put(GRAVITY_TOP_KEY, 49);
        newHashMap.put(GRAVITY_BOTTOM_KEY, 81);
        newHashMap.put(GRAVITY_CENTER, 17);
        return newHashMap;
    }

    @Override // com.facebook.fbreact.specs.NativeToastAndroidSpec
    public void show(final String message, final double durationDouble) {
        final int i = (int) durationDouble;
        UiThreadUtil.runOnUiThread(new Runnable() { // from class: com.facebook.react.modules.toast.ToastModule.1
            @Override // java.lang.Runnable
            public void run() {
                Toast.makeText(ToastModule.this.getReactApplicationContext(), message, i).show();
            }
        });
    }

    @Override // com.facebook.fbreact.specs.NativeToastAndroidSpec
    public void showWithGravity(final String message, final double durationDouble, final double gravityDouble) {
        final int i = (int) durationDouble;
        final int i2 = (int) gravityDouble;
        UiThreadUtil.runOnUiThread(new Runnable() { // from class: com.facebook.react.modules.toast.ToastModule.2
            @Override // java.lang.Runnable
            public void run() {
                Toast makeText = Toast.makeText(ToastModule.this.getReactApplicationContext(), message, i);
                makeText.setGravity(i2, 0, 0);
                makeText.show();
            }
        });
    }

    @Override // com.facebook.fbreact.specs.NativeToastAndroidSpec
    public void showWithGravityAndOffset(final String message, final double durationDouble, final double gravityDouble, final double xOffsetDouble, final double yOffsetDouble) {
        final int i = (int) durationDouble;
        final int i2 = (int) gravityDouble;
        final int i3 = (int) xOffsetDouble;
        final int i4 = (int) yOffsetDouble;
        UiThreadUtil.runOnUiThread(new Runnable() { // from class: com.facebook.react.modules.toast.ToastModule.3
            @Override // java.lang.Runnable
            public void run() {
                Toast makeText = Toast.makeText(ToastModule.this.getReactApplicationContext(), message, i);
                makeText.setGravity(i2, i3, i4);
                makeText.show();
            }
        });
    }
}
