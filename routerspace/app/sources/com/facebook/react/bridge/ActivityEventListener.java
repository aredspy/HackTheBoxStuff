package com.facebook.react.bridge;

import android.app.Activity;
import android.content.Intent;
/* loaded from: classes.dex */
public interface ActivityEventListener {
    void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data);

    void onNewIntent(Intent intent);
}
