package com.facebook.react;

import com.facebook.react.bridge.ModuleHolder;
import com.facebook.react.bridge.ModuleSpec;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.module.model.ReactModuleInfo;
import com.facebook.react.module.model.ReactModuleInfoProvider;
import com.facebook.react.uimanager.ViewManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.inject.Provider;
/* loaded from: classes.dex */
public abstract class TurboReactPackage implements ReactPackage {
    public abstract NativeModule getModule(String name, final ReactApplicationContext reactContext);

    public abstract ReactModuleInfoProvider getReactModuleInfoProvider();

    @Override // com.facebook.react.ReactPackage
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        throw new UnsupportedOperationException("In case of TurboModules, createNativeModules is not supported. NativeModuleRegistry should instead use getModuleList or getModule method");
    }

    public Iterable<ModuleHolder> getNativeModuleIterator(final ReactApplicationContext reactContext) {
        final Iterator<Map.Entry<String, ReactModuleInfo>> it = getReactModuleInfoProvider().getReactModuleInfos().entrySet().iterator();
        return new Iterable<ModuleHolder>() { // from class: com.facebook.react.TurboReactPackage.1
            @Override // java.lang.Iterable
            public Iterator<ModuleHolder> iterator() {
                return new Iterator<ModuleHolder>() { // from class: com.facebook.react.TurboReactPackage.1.1
                    Map.Entry<String, ReactModuleInfo> nextEntry = null;

                    /* JADX WARN: Removed duplicated region for block: B:4:0x000a  */
                    /*
                        Code decompiled incorrectly, please refer to instructions dump.
                        To view partially-correct add '--show-bad-code' argument
                    */
                    private void findNext() {
                        /*
                            r3 = this;
                        L0:
                            com.facebook.react.TurboReactPackage$1 r0 = com.facebook.react.TurboReactPackage.AnonymousClass1.this
                            java.util.Iterator r0 = r2
                            boolean r0 = r0.hasNext()
                            if (r0 == 0) goto L28
                            com.facebook.react.TurboReactPackage$1 r0 = com.facebook.react.TurboReactPackage.AnonymousClass1.this
                            java.util.Iterator r0 = r2
                            java.lang.Object r0 = r0.next()
                            java.util.Map$Entry r0 = (java.util.Map.Entry) r0
                            java.lang.Object r1 = r0.getValue()
                            com.facebook.react.module.model.ReactModuleInfo r1 = (com.facebook.react.module.model.ReactModuleInfo) r1
                            boolean r2 = com.facebook.react.config.ReactFeatureFlags.useTurboModules
                            if (r2 == 0) goto L25
                            boolean r1 = r1.isTurboModule()
                            if (r1 == 0) goto L25
                            goto L0
                        L25:
                            r3.nextEntry = r0
                            return
                        L28:
                            r0 = 0
                            r3.nextEntry = r0
                            return
                        */
                        throw new UnsupportedOperationException("Method not decompiled: com.facebook.react.TurboReactPackage.AnonymousClass1.C00051.findNext():void");
                    }

                    @Override // java.util.Iterator
                    public boolean hasNext() {
                        if (this.nextEntry == null) {
                            findNext();
                        }
                        return this.nextEntry != null;
                    }

                    @Override // java.util.Iterator
                    public ModuleHolder next() {
                        if (this.nextEntry == null) {
                            findNext();
                        }
                        Map.Entry<String, ReactModuleInfo> entry = this.nextEntry;
                        if (entry == null) {
                            throw new NoSuchElementException("ModuleHolder not found");
                        }
                        findNext();
                        return new ModuleHolder(entry.getValue(), new ModuleHolderProvider(entry.getKey(), reactContext));
                    }

                    @Override // java.util.Iterator
                    public void remove() {
                        throw new UnsupportedOperationException("Cannot remove native modules from the list");
                    }
                };
            }
        };
    }

    protected List<ModuleSpec> getViewManagers(ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }

    @Override // com.facebook.react.ReactPackage
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        List<ModuleSpec> viewManagers = getViewManagers(reactContext);
        if (viewManagers == null || viewManagers.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList arrayList = new ArrayList();
        for (ModuleSpec moduleSpec : viewManagers) {
            arrayList.add((ViewManager) moduleSpec.getProvider().get());
        }
        return arrayList;
    }

    /* loaded from: classes.dex */
    public class ModuleHolderProvider implements Provider<NativeModule> {
        private final String mName;
        private final ReactApplicationContext mReactContext;

        public ModuleHolderProvider(String name, ReactApplicationContext reactContext) {
            TurboReactPackage.this = this$0;
            this.mName = name;
            this.mReactContext = reactContext;
        }

        @Override // javax.inject.Provider
        public NativeModule get() {
            return TurboReactPackage.this.getModule(this.mName, this.mReactContext);
        }
    }
}
