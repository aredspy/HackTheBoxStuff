package com.facebook.react.modules.core;
/* loaded from: classes.dex */
public interface PermissionAwareActivity {
    int checkPermission(String permission, int pid, int uid);

    int checkSelfPermission(String permission);

    void requestPermissions(String[] permissions, int requestCode, PermissionListener listener);

    boolean shouldShowRequestPermissionRationale(String permission);
}
