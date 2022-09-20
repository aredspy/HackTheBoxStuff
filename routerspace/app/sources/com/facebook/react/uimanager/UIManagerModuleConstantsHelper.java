package com.facebook.react.uimanager;

import com.facebook.react.common.MapBuilder;
import com.facebook.systrace.SystraceMessage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
class UIManagerModuleConstantsHelper {
    private static final String BUBBLING_EVENTS_KEY = "bubblingEventTypes";
    private static final String DIRECT_EVENTS_KEY = "directEventTypes";

    UIManagerModuleConstantsHelper() {
    }

    public static Map<String, Object> createConstants(ViewManagerResolver resolver) {
        Map<String, Object> constants = UIManagerModuleConstants.getConstants();
        constants.put("ViewManagerNames", resolver.getViewManagerNames());
        constants.put("LazyViewManagersEnabled", true);
        return constants;
    }

    public static Map<String, Object> getDefaultExportableEventTypes() {
        return MapBuilder.of(BUBBLING_EVENTS_KEY, UIManagerModuleConstants.getBubblingEventTypeConstants(), DIRECT_EVENTS_KEY, UIManagerModuleConstants.getDirectEventTypeConstants());
    }

    public static Map<String, Object> createConstants(List<ViewManager> viewManagers, Map<String, Object> allBubblingEventTypes, Map<String, Object> allDirectEventTypes) {
        Map<String, Object> constants = UIManagerModuleConstants.getConstants();
        Map<? extends String, ? extends Object> bubblingEventTypeConstants = UIManagerModuleConstants.getBubblingEventTypeConstants();
        Map<? extends String, ? extends Object> directEventTypeConstants = UIManagerModuleConstants.getDirectEventTypeConstants();
        if (allBubblingEventTypes != null) {
            allBubblingEventTypes.putAll(bubblingEventTypeConstants);
        }
        if (allDirectEventTypes != null) {
            allDirectEventTypes.putAll(directEventTypeConstants);
        }
        for (ViewManager viewManager : viewManagers) {
            String name = viewManager.getName();
            SystraceMessage.beginSection(0L, "UIManagerModuleConstantsHelper.createConstants").arg("ViewManager", name).arg("Lazy", (Object) false).flush();
            try {
                Map<String, Object> createConstantsForViewManager = createConstantsForViewManager(viewManager, null, null, allBubblingEventTypes, allDirectEventTypes);
                if (!createConstantsForViewManager.isEmpty()) {
                    constants.put(name, createConstantsForViewManager);
                }
            } finally {
                SystraceMessage.endSection(0L);
            }
        }
        constants.put("genericBubblingEventTypes", bubblingEventTypeConstants);
        constants.put("genericDirectEventTypes", directEventTypeConstants);
        return constants;
    }

    public static Map<String, Object> createConstantsForViewManager(ViewManager viewManager, Map defaultBubblingEvents, Map defaultDirectEvents, Map cumulativeBubblingEventTypes, Map cumulativeDirectEventTypes) {
        HashMap newHashMap = MapBuilder.newHashMap();
        Map<String, Object> exportedCustomBubblingEventTypeConstants = viewManager.getExportedCustomBubblingEventTypeConstants();
        if (exportedCustomBubblingEventTypeConstants != null) {
            recursiveMerge(cumulativeBubblingEventTypes, exportedCustomBubblingEventTypeConstants);
            recursiveMerge(exportedCustomBubblingEventTypeConstants, defaultBubblingEvents);
            newHashMap.put(BUBBLING_EVENTS_KEY, exportedCustomBubblingEventTypeConstants);
        } else if (defaultBubblingEvents != null) {
            newHashMap.put(BUBBLING_EVENTS_KEY, defaultBubblingEvents);
        }
        Map<String, Object> exportedCustomDirectEventTypeConstants = viewManager.getExportedCustomDirectEventTypeConstants();
        if (exportedCustomDirectEventTypeConstants != null) {
            recursiveMerge(cumulativeDirectEventTypes, exportedCustomDirectEventTypeConstants);
            recursiveMerge(exportedCustomDirectEventTypeConstants, defaultDirectEvents);
            newHashMap.put(DIRECT_EVENTS_KEY, exportedCustomDirectEventTypeConstants);
        } else if (defaultDirectEvents != null) {
            newHashMap.put(DIRECT_EVENTS_KEY, defaultDirectEvents);
        }
        Map<String, Object> exportedViewConstants = viewManager.getExportedViewConstants();
        if (exportedViewConstants != null) {
            newHashMap.put("Constants", exportedViewConstants);
        }
        Map<String, Integer> commandsMap = viewManager.getCommandsMap();
        if (commandsMap != null) {
            newHashMap.put("Commands", commandsMap);
        }
        Map<String, String> nativeProps = viewManager.getNativeProps();
        if (!nativeProps.isEmpty()) {
            newHashMap.put("NativeProps", nativeProps);
        }
        return newHashMap;
    }

    private static void recursiveMerge(Map dest, Map source) {
        if (dest == null || source == null || source.isEmpty()) {
            return;
        }
        for (Object obj : source.keySet()) {
            Object obj2 = source.get(obj);
            Object obj3 = dest.get(obj);
            if (obj3 != null && (obj2 instanceof Map) && (obj3 instanceof Map)) {
                recursiveMerge((Map) obj3, (Map) obj2);
            } else {
                dest.put(obj, obj2);
            }
        }
    }
}
