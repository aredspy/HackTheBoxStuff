package com.facebook.react;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.facebook.react.modules.core.PermissionAwareActivity;
import com.facebook.react.modules.core.PermissionListener;
/* loaded from: classes.dex */
public class ReactFragment extends Fragment implements PermissionAwareActivity {
    protected static final String ARG_COMPONENT_NAME = "arg_component_name";
    protected static final String ARG_LAUNCH_OPTIONS = "arg_launch_options";
    private PermissionListener mPermissionListener;
    private ReactDelegate mReactDelegate;

    public static ReactFragment newInstance(String componentName, Bundle launchOptions) {
        ReactFragment reactFragment = new ReactFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_COMPONENT_NAME, componentName);
        bundle.putBundle(ARG_LAUNCH_OPTIONS, launchOptions);
        reactFragment.setArguments(bundle);
        return reactFragment;
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        Bundle bundle;
        super.onCreate(savedInstanceState);
        String str = null;
        if (getArguments() != null) {
            str = getArguments().getString(ARG_COMPONENT_NAME);
            bundle = getArguments().getBundle(ARG_LAUNCH_OPTIONS);
        } else {
            bundle = null;
        }
        if (str == null) {
            throw new IllegalStateException("Cannot loadApp if component name is null");
        }
        this.mReactDelegate = new ReactDelegate(getActivity(), getReactNativeHost(), str, bundle);
    }

    protected ReactNativeHost getReactNativeHost() {
        return ((ReactApplication) getActivity().getApplication()).getReactNativeHost();
    }

    protected ReactDelegate getReactDelegate() {
        return this.mReactDelegate;
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mReactDelegate.loadApp();
        return this.mReactDelegate.getReactRootView();
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mReactDelegate.onHostResume();
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        this.mReactDelegate.onHostPause();
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        this.mReactDelegate.onHostDestroy();
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.mReactDelegate.onActivityResult(requestCode, resultCode, data, false);
    }

    public boolean onBackPressed() {
        return this.mReactDelegate.onBackPressed();
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return this.mReactDelegate.shouldShowDevMenuOrReload(keyCode, event);
    }

    @Override // androidx.fragment.app.Fragment
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionListener permissionListener = this.mPermissionListener;
        if (permissionListener == null || !permissionListener.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            return;
        }
        this.mPermissionListener = null;
    }

    @Override // com.facebook.react.modules.core.PermissionAwareActivity
    public int checkPermission(String permission, int pid, int uid) {
        return getActivity().checkPermission(permission, pid, uid);
    }

    @Override // com.facebook.react.modules.core.PermissionAwareActivity
    public int checkSelfPermission(String permission) {
        return getActivity().checkSelfPermission(permission);
    }

    @Override // com.facebook.react.modules.core.PermissionAwareActivity
    public void requestPermissions(String[] permissions, int requestCode, PermissionListener listener) {
        this.mPermissionListener = listener;
        requestPermissions(permissions, requestCode);
    }

    /* loaded from: classes.dex */
    public static class Builder {
        String mComponentName = null;
        Bundle mLaunchOptions = null;

        public Builder setComponentName(String componentName) {
            this.mComponentName = componentName;
            return this;
        }

        public Builder setLaunchOptions(Bundle launchOptions) {
            this.mLaunchOptions = launchOptions;
            return this;
        }

        public ReactFragment build() {
            return ReactFragment.newInstance(this.mComponentName, this.mLaunchOptions);
        }
    }
}
