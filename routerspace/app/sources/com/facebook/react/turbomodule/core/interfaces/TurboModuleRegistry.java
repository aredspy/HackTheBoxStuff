package com.facebook.react.turbomodule.core.interfaces;

import java.util.Collection;
import java.util.List;
/* loaded from: classes.dex */
public interface TurboModuleRegistry {
    List<String> getEagerInitModuleNames();

    TurboModule getModule(String moduleName);

    Collection<TurboModule> getModules();

    boolean hasModule(String moduleName);
}
