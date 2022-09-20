package com.facebook.react.shell;

import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.react.TurboReactPackage;
import com.facebook.react.animated.NativeAnimatedModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.module.model.ReactModuleInfo;
import com.facebook.react.module.model.ReactModuleInfoProvider;
import com.facebook.react.modules.accessibilityinfo.AccessibilityInfoModule;
import com.facebook.react.modules.appearance.AppearanceModule;
import com.facebook.react.modules.appstate.AppStateModule;
import com.facebook.react.modules.blob.BlobModule;
import com.facebook.react.modules.blob.FileReaderModule;
import com.facebook.react.modules.camera.ImageStoreManager;
import com.facebook.react.modules.clipboard.ClipboardModule;
import com.facebook.react.modules.datepicker.DatePickerDialogModule;
import com.facebook.react.modules.dialog.DialogModule;
import com.facebook.react.modules.fresco.FrescoModule;
import com.facebook.react.modules.i18nmanager.I18nManagerModule;
import com.facebook.react.modules.image.ImageLoaderModule;
import com.facebook.react.modules.intent.IntentModule;
import com.facebook.react.modules.network.NetworkingModule;
import com.facebook.react.modules.permissions.PermissionsModule;
import com.facebook.react.modules.share.ShareModule;
import com.facebook.react.modules.sound.SoundManagerModule;
import com.facebook.react.modules.statusbar.StatusBarModule;
import com.facebook.react.modules.storage.AsyncStorageModule;
import com.facebook.react.modules.toast.ToastModule;
import com.facebook.react.modules.vibration.VibrationModule;
import com.facebook.react.modules.websocket.WebSocketModule;
import com.facebook.react.turbomodule.core.interfaces.TurboModule;
import com.facebook.react.uimanager.ViewManager;
import com.facebook.react.views.drawer.ReactDrawerLayoutManager;
import com.facebook.react.views.image.ReactImageManager;
import com.facebook.react.views.modal.ReactModalHostManager;
import com.facebook.react.views.progressbar.ReactProgressBarViewManager;
import com.facebook.react.views.scroll.ReactHorizontalScrollContainerViewManager;
import com.facebook.react.views.scroll.ReactHorizontalScrollViewManager;
import com.facebook.react.views.scroll.ReactScrollViewManager;
import com.facebook.react.views.slider.ReactSliderManager;
import com.facebook.react.views.swiperefresh.SwipeRefreshLayoutManager;
import com.facebook.react.views.switchview.ReactSwitchManager;
import com.facebook.react.views.text.ReactRawTextManager;
import com.facebook.react.views.text.ReactTextViewManager;
import com.facebook.react.views.text.ReactVirtualTextViewManager;
import com.facebook.react.views.text.frescosupport.FrescoBasedReactTextInlineImageViewManager;
import com.facebook.react.views.textinput.ReactTextInputManager;
import com.facebook.react.views.unimplementedview.ReactUnimplementedViewManager;
import com.facebook.react.views.view.ReactViewManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
public class MainReactPackage extends TurboReactPackage {
    private MainPackageConfig mConfig;

    public MainReactPackage() {
    }

    public MainReactPackage(MainPackageConfig config) {
        this.mConfig = config;
    }

    @Override // com.facebook.react.TurboReactPackage
    public NativeModule getModule(String name, ReactApplicationContext context) {
        name.hashCode();
        char c = 65535;
        switch (name.hashCode()) {
            case -2115067288:
                if (name.equals(ToastModule.NAME)) {
                    c = 0;
                    break;
                }
                break;
            case -2033388651:
                if (name.equals(AsyncStorageModule.NAME)) {
                    c = 1;
                    break;
                }
                break;
            case -1962922905:
                if (name.equals(ImageStoreManager.NAME)) {
                    c = 2;
                    break;
                }
                break;
            case -1850625090:
                if (name.equals(SoundManagerModule.NAME)) {
                    c = 3;
                    break;
                }
                break;
            case -1654566518:
                if (name.equals(DialogModule.NAME)) {
                    c = 4;
                    break;
                }
                break;
            case -1344126773:
                if (name.equals(FileReaderModule.NAME)) {
                    c = 5;
                    break;
                }
                break;
            case -1062061717:
                if (name.equals(PermissionsModule.NAME)) {
                    c = 6;
                    break;
                }
                break;
            case -657277650:
                if (name.equals(ImageLoaderModule.NAME)) {
                    c = 7;
                    break;
                }
                break;
            case -570370161:
                if (name.equals(I18nManagerModule.NAME)) {
                    c = '\b';
                    break;
                }
                break;
            case -504784764:
                if (name.equals(AppearanceModule.NAME)) {
                    c = '\t';
                    break;
                }
                break;
            case -457866500:
                if (name.equals(AccessibilityInfoModule.NAME)) {
                    c = '\n';
                    break;
                }
                break;
            case -382654004:
                if (name.equals(StatusBarModule.NAME)) {
                    c = 11;
                    break;
                }
                break;
            case -254310125:
                if (name.equals("WebSocketModule")) {
                    c = '\f';
                    break;
                }
                break;
            case 163245714:
                if (name.equals(FrescoModule.NAME)) {
                    c = '\r';
                    break;
                }
                break;
            case 174691539:
                if (name.equals(DatePickerDialogModule.FRAGMENT_TAG)) {
                    c = 14;
                    break;
                }
                break;
            case 403570038:
                if (name.equals(ClipboardModule.NAME)) {
                    c = 15;
                    break;
                }
                break;
            case 563961875:
                if (name.equals(IntentModule.NAME)) {
                    c = 16;
                    break;
                }
                break;
            case 1221389072:
                if (name.equals(AppStateModule.NAME)) {
                    c = 17;
                    break;
                }
                break;
            case 1515242260:
                if (name.equals(NetworkingModule.NAME)) {
                    c = 18;
                    break;
                }
                break;
            case 1547941001:
                if (name.equals(BlobModule.NAME)) {
                    c = 19;
                    break;
                }
                break;
            case 1555425035:
                if (name.equals(ShareModule.NAME)) {
                    c = 20;
                    break;
                }
                break;
            case 1721274886:
                if (name.equals(NativeAnimatedModule.NAME)) {
                    c = 21;
                    break;
                }
                break;
            case 1922110066:
                if (name.equals(VibrationModule.NAME)) {
                    c = 22;
                    break;
                }
                break;
        }
        ImagePipelineConfig imagePipelineConfig = null;
        switch (c) {
            case 0:
                return new ToastModule(context);
            case 1:
                return new AsyncStorageModule(context);
            case 2:
                return new ImageStoreManager(context);
            case 3:
                return new SoundManagerModule(context);
            case 4:
                return new DialogModule(context);
            case 5:
                return new FileReaderModule(context);
            case 6:
                return new PermissionsModule(context);
            case 7:
                return new ImageLoaderModule(context);
            case '\b':
                return new I18nManagerModule(context);
            case '\t':
                return new AppearanceModule(context);
            case '\n':
                return new AccessibilityInfoModule(context);
            case 11:
                return new StatusBarModule(context);
            case '\f':
                return new WebSocketModule(context);
            case '\r':
                MainPackageConfig mainPackageConfig = this.mConfig;
                if (mainPackageConfig != null) {
                    imagePipelineConfig = mainPackageConfig.getFrescoConfig();
                }
                return new FrescoModule(context, true, imagePipelineConfig);
            case 14:
                return new DatePickerDialogModule(context);
            case 15:
                return new ClipboardModule(context);
            case 16:
                return new IntentModule(context);
            case 17:
                return new AppStateModule(context);
            case 18:
                return new NetworkingModule(context);
            case 19:
                return new BlobModule(context);
            case 20:
                return new ShareModule(context);
            case 21:
                return new NativeAnimatedModule(context);
            case 22:
                return new VibrationModule(context);
            default:
                return null;
        }
    }

    @Override // com.facebook.react.TurboReactPackage, com.facebook.react.ReactPackage
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ReactDrawerLayoutManager());
        arrayList.add(new ReactHorizontalScrollViewManager());
        arrayList.add(new ReactHorizontalScrollContainerViewManager());
        arrayList.add(new ReactProgressBarViewManager());
        arrayList.add(new ReactScrollViewManager());
        arrayList.add(new ReactSliderManager());
        arrayList.add(new ReactSwitchManager());
        arrayList.add(new SwipeRefreshLayoutManager());
        arrayList.add(new FrescoBasedReactTextInlineImageViewManager());
        arrayList.add(new ReactImageManager());
        arrayList.add(new ReactModalHostManager());
        arrayList.add(new ReactRawTextManager());
        arrayList.add(new ReactTextInputManager());
        arrayList.add(new ReactTextViewManager());
        arrayList.add(new ReactViewManager());
        arrayList.add(new ReactVirtualTextViewManager());
        arrayList.add(new ReactUnimplementedViewManager());
        return arrayList;
    }

    @Override // com.facebook.react.TurboReactPackage
    public ReactModuleInfoProvider getReactModuleInfoProvider() {
        try {
            return (ReactModuleInfoProvider) Class.forName("com.facebook.react.shell.MainReactPackage$$ReactModuleInfoProvider").newInstance();
        } catch (ClassNotFoundException unused) {
            Class[] clsArr = {AccessibilityInfoModule.class, AppearanceModule.class, AppStateModule.class, BlobModule.class, FileReaderModule.class, AsyncStorageModule.class, ClipboardModule.class, DatePickerDialogModule.class, DialogModule.class, FrescoModule.class, I18nManagerModule.class, ImageLoaderModule.class, ImageStoreManager.class, IntentModule.class, NativeAnimatedModule.class, NetworkingModule.class, PermissionsModule.class, ShareModule.class, StatusBarModule.class, SoundManagerModule.class, ToastModule.class, VibrationModule.class, WebSocketModule.class};
            final HashMap hashMap = new HashMap();
            for (int i = 0; i < 23; i++) {
                Class cls = clsArr[i];
                ReactModule reactModule = (ReactModule) cls.getAnnotation(ReactModule.class);
                hashMap.put(reactModule.name(), new ReactModuleInfo(reactModule.name(), cls.getName(), reactModule.canOverrideExistingModule(), reactModule.needsEagerInit(), reactModule.hasConstants(), reactModule.isCxxModule(), TurboModule.class.isAssignableFrom(cls)));
            }
            return new ReactModuleInfoProvider() { // from class: com.facebook.react.shell.MainReactPackage.1
                @Override // com.facebook.react.module.model.ReactModuleInfoProvider
                public Map<String, ReactModuleInfo> getReactModuleInfos() {
                    return hashMap;
                }
            };
        } catch (IllegalAccessException e) {
            throw new RuntimeException("No ReactModuleInfoProvider for CoreModulesPackage$$ReactModuleInfoProvider", e);
        } catch (InstantiationException e2) {
            throw new RuntimeException("No ReactModuleInfoProvider for CoreModulesPackage$$ReactModuleInfoProvider", e2);
        }
    }
}
