package com.facebook.react.uimanager;

import android.content.Context;
import android.view.View;
import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.ColorPropConverter;
import com.facebook.react.bridge.Dynamic;
import com.facebook.react.bridge.DynamicFromObject;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.annotations.ReactPropGroup;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes.dex */
public class ViewManagersPropertyCache {
    private static final Map<Class, Map<String, PropSetter>> CLASS_PROPS_CACHE = new HashMap();
    private static final Map<String, PropSetter> EMPTY_PROPS_MAP = new HashMap();

    ViewManagersPropertyCache() {
    }

    public static void clear() {
        CLASS_PROPS_CACHE.clear();
        EMPTY_PROPS_MAP.clear();
    }

    /* loaded from: classes.dex */
    public static abstract class PropSetter {
        protected final Integer mIndex;
        protected final String mPropName;
        protected final String mPropType;
        protected final Method mSetter;
        private static final Object[] VIEW_MGR_ARGS = new Object[2];
        private static final Object[] VIEW_MGR_GROUP_ARGS = new Object[3];
        private static final Object[] SHADOW_ARGS = new Object[1];
        private static final Object[] SHADOW_GROUP_ARGS = new Object[2];

        protected abstract Object getValueOrDefault(Object value, Context context);

        private PropSetter(ReactProp prop, String defaultType, Method setter) {
            this.mPropName = prop.name();
            this.mPropType = !"__default_type__".equals(prop.customType()) ? prop.customType() : defaultType;
            this.mSetter = setter;
            this.mIndex = null;
        }

        private PropSetter(ReactPropGroup prop, String defaultType, Method setter, int index) {
            this.mPropName = prop.names()[index];
            this.mPropType = !"__default_type__".equals(prop.customType()) ? prop.customType() : defaultType;
            this.mSetter = setter;
            this.mIndex = Integer.valueOf(index);
        }

        public String getPropName() {
            return this.mPropName;
        }

        public String getPropType() {
            return this.mPropType;
        }

        public void updateViewProp(ViewManager viewManager, View viewToUpdate, Object value) {
            try {
                Integer num = this.mIndex;
                if (num == null) {
                    Object[] objArr = VIEW_MGR_ARGS;
                    objArr[0] = viewToUpdate;
                    objArr[1] = getValueOrDefault(value, viewToUpdate.getContext());
                    this.mSetter.invoke(viewManager, objArr);
                    Arrays.fill(objArr, (Object) null);
                    return;
                }
                Object[] objArr2 = VIEW_MGR_GROUP_ARGS;
                objArr2[0] = viewToUpdate;
                objArr2[1] = num;
                objArr2[2] = getValueOrDefault(value, viewToUpdate.getContext());
                this.mSetter.invoke(viewManager, objArr2);
                Arrays.fill(objArr2, (Object) null);
            } catch (Throwable th) {
                FLog.e(ViewManager.class, "Error while updating prop " + this.mPropName, th);
                throw new JSApplicationIllegalArgumentException("Error while updating property '" + this.mPropName + "' of a view managed by: " + viewManager.getName(), th);
            }
        }

        public void updateShadowNodeProp(ReactShadowNode nodeToUpdate, Object value) {
            try {
                Integer num = this.mIndex;
                if (num == null) {
                    Object[] objArr = SHADOW_ARGS;
                    objArr[0] = getValueOrDefault(value, nodeToUpdate.getThemedContext());
                    this.mSetter.invoke(nodeToUpdate, objArr);
                    Arrays.fill(objArr, (Object) null);
                    return;
                }
                Object[] objArr2 = SHADOW_GROUP_ARGS;
                objArr2[0] = num;
                objArr2[1] = getValueOrDefault(value, nodeToUpdate.getThemedContext());
                this.mSetter.invoke(nodeToUpdate, objArr2);
                Arrays.fill(objArr2, (Object) null);
            } catch (Throwable th) {
                FLog.e(ViewManager.class, "Error while updating prop " + this.mPropName, th);
                throw new JSApplicationIllegalArgumentException("Error while updating property '" + this.mPropName + "' in shadow node of type: " + nodeToUpdate.getViewClass(), th);
            }
        }
    }

    /* loaded from: classes.dex */
    public static class DynamicPropSetter extends PropSetter {
        public DynamicPropSetter(ReactProp prop, Method setter) {
            super(prop, "mixed", setter);
        }

        public DynamicPropSetter(ReactPropGroup prop, Method setter, int index) {
            super(prop, "mixed", setter, index);
        }

        @Override // com.facebook.react.uimanager.ViewManagersPropertyCache.PropSetter
        protected Object getValueOrDefault(Object value, Context context) {
            return value instanceof Dynamic ? value : new DynamicFromObject(value);
        }
    }

    /* loaded from: classes.dex */
    public static class IntPropSetter extends PropSetter {
        private final int mDefaultValue;

        public IntPropSetter(ReactProp prop, Method setter, int defaultValue) {
            super(prop, "number", setter);
            this.mDefaultValue = defaultValue;
        }

        public IntPropSetter(ReactPropGroup prop, Method setter, int index, int defaultValue) {
            super(prop, "number", setter, index);
            this.mDefaultValue = defaultValue;
        }

        @Override // com.facebook.react.uimanager.ViewManagersPropertyCache.PropSetter
        protected Object getValueOrDefault(Object value, Context context) {
            return Integer.valueOf(value == null ? this.mDefaultValue : Integer.valueOf(((Double) value).intValue()).intValue());
        }
    }

    /* loaded from: classes.dex */
    public static class DoublePropSetter extends PropSetter {
        private final double mDefaultValue;

        public DoublePropSetter(ReactProp prop, Method setter, double defaultValue) {
            super(prop, "number", setter);
            this.mDefaultValue = defaultValue;
        }

        public DoublePropSetter(ReactPropGroup prop, Method setter, int index, double defaultValue) {
            super(prop, "number", setter, index);
            this.mDefaultValue = defaultValue;
        }

        @Override // com.facebook.react.uimanager.ViewManagersPropertyCache.PropSetter
        protected Object getValueOrDefault(Object value, Context context) {
            return Double.valueOf(value == null ? this.mDefaultValue : ((Double) value).doubleValue());
        }
    }

    /* loaded from: classes.dex */
    public static class ColorPropSetter extends PropSetter {
        private final int mDefaultValue;

        public ColorPropSetter(ReactProp prop, Method setter) {
            this(prop, setter, 0);
        }

        public ColorPropSetter(ReactProp prop, Method setter, int defaultValue) {
            super(prop, "mixed", setter);
            this.mDefaultValue = defaultValue;
        }

        @Override // com.facebook.react.uimanager.ViewManagersPropertyCache.PropSetter
        protected Object getValueOrDefault(Object value, Context context) {
            if (value == null) {
                return Integer.valueOf(this.mDefaultValue);
            }
            return ColorPropConverter.getColor(value, context);
        }
    }

    /* loaded from: classes.dex */
    public static class BooleanPropSetter extends PropSetter {
        private final boolean mDefaultValue;

        public BooleanPropSetter(ReactProp prop, Method setter, boolean defaultValue) {
            super(prop, "boolean", setter);
            this.mDefaultValue = defaultValue;
        }

        @Override // com.facebook.react.uimanager.ViewManagersPropertyCache.PropSetter
        protected Object getValueOrDefault(Object value, Context context) {
            return value == null ? this.mDefaultValue : ((Boolean) value).booleanValue() ? Boolean.TRUE : Boolean.FALSE;
        }
    }

    /* loaded from: classes.dex */
    public static class FloatPropSetter extends PropSetter {
        private final float mDefaultValue;

        public FloatPropSetter(ReactProp prop, Method setter, float defaultValue) {
            super(prop, "number", setter);
            this.mDefaultValue = defaultValue;
        }

        public FloatPropSetter(ReactPropGroup prop, Method setter, int index, float defaultValue) {
            super(prop, "number", setter, index);
            this.mDefaultValue = defaultValue;
        }

        @Override // com.facebook.react.uimanager.ViewManagersPropertyCache.PropSetter
        protected Object getValueOrDefault(Object value, Context context) {
            return Float.valueOf(value == null ? this.mDefaultValue : Float.valueOf(((Double) value).floatValue()).floatValue());
        }
    }

    /* loaded from: classes.dex */
    public static class ArrayPropSetter extends PropSetter {
        public ArrayPropSetter(ReactProp prop, Method setter) {
            super(prop, "Array", setter);
        }

        @Override // com.facebook.react.uimanager.ViewManagersPropertyCache.PropSetter
        protected Object getValueOrDefault(Object value, Context context) {
            return (ReadableArray) value;
        }
    }

    /* loaded from: classes.dex */
    public static class MapPropSetter extends PropSetter {
        public MapPropSetter(ReactProp prop, Method setter) {
            super(prop, "Map", setter);
        }

        @Override // com.facebook.react.uimanager.ViewManagersPropertyCache.PropSetter
        protected Object getValueOrDefault(Object value, Context context) {
            return (ReadableMap) value;
        }
    }

    /* loaded from: classes.dex */
    public static class StringPropSetter extends PropSetter {
        public StringPropSetter(ReactProp prop, Method setter) {
            super(prop, "String", setter);
        }

        @Override // com.facebook.react.uimanager.ViewManagersPropertyCache.PropSetter
        protected Object getValueOrDefault(Object value, Context context) {
            return (String) value;
        }
    }

    /* loaded from: classes.dex */
    public static class BoxedBooleanPropSetter extends PropSetter {
        public BoxedBooleanPropSetter(ReactProp prop, Method setter) {
            super(prop, "boolean", setter);
        }

        @Override // com.facebook.react.uimanager.ViewManagersPropertyCache.PropSetter
        protected Object getValueOrDefault(Object value, Context context) {
            if (value != null) {
                return ((Boolean) value).booleanValue() ? Boolean.TRUE : Boolean.FALSE;
            }
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static class BoxedIntPropSetter extends PropSetter {
        public BoxedIntPropSetter(ReactProp prop, Method setter) {
            super(prop, "number", setter);
        }

        public BoxedIntPropSetter(ReactPropGroup prop, Method setter, int index) {
            super(prop, "number", setter, index);
        }

        @Override // com.facebook.react.uimanager.ViewManagersPropertyCache.PropSetter
        protected Object getValueOrDefault(Object value, Context context) {
            if (value != null) {
                if (value instanceof Double) {
                    return Integer.valueOf(((Double) value).intValue());
                }
                return (Integer) value;
            }
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static class BoxedColorPropSetter extends PropSetter {
        public BoxedColorPropSetter(ReactProp prop, Method setter) {
            super(prop, "mixed", setter);
        }

        @Override // com.facebook.react.uimanager.ViewManagersPropertyCache.PropSetter
        protected Object getValueOrDefault(Object value, Context context) {
            if (value != null) {
                return ColorPropConverter.getColor(value, context);
            }
            return null;
        }
    }

    static Map<String, String> getNativePropsForView(Class<? extends ViewManager> viewManagerTopClass, Class<? extends ReactShadowNode> shadowNodeTopClass) {
        HashMap hashMap = new HashMap();
        for (PropSetter propSetter : getNativePropSettersForViewManagerClass(viewManagerTopClass).values()) {
            hashMap.put(propSetter.getPropName(), propSetter.getPropType());
        }
        for (PropSetter propSetter2 : getNativePropSettersForShadowNodeClass(shadowNodeTopClass).values()) {
            hashMap.put(propSetter2.getPropName(), propSetter2.getPropType());
        }
        return hashMap;
    }

    public static Map<String, PropSetter> getNativePropSettersForViewManagerClass(Class<? extends ViewManager> cls) {
        if (cls == ViewManager.class) {
            return EMPTY_PROPS_MAP;
        }
        Map<Class, Map<String, PropSetter>> map = CLASS_PROPS_CACHE;
        Map<String, PropSetter> map2 = map.get(cls);
        if (map2 != null) {
            return map2;
        }
        HashMap hashMap = new HashMap(getNativePropSettersForViewManagerClass(cls.getSuperclass()));
        extractPropSettersFromViewManagerClassDefinition(cls, hashMap);
        map.put(cls, hashMap);
        return hashMap;
    }

    public static Map<String, PropSetter> getNativePropSettersForShadowNodeClass(Class<? extends ReactShadowNode> cls) {
        for (Class<?> cls2 : cls.getInterfaces()) {
            if (cls2 == ReactShadowNode.class) {
                return EMPTY_PROPS_MAP;
            }
        }
        Map<Class, Map<String, PropSetter>> map = CLASS_PROPS_CACHE;
        Map<String, PropSetter> map2 = map.get(cls);
        if (map2 != null) {
            return map2;
        }
        HashMap hashMap = new HashMap(getNativePropSettersForShadowNodeClass(cls.getSuperclass()));
        extractPropSettersFromShadowNodeClassDefinition(cls, hashMap);
        map.put(cls, hashMap);
        return hashMap;
    }

    private static PropSetter createPropSetter(ReactProp annotation, Method method, Class<?> propTypeClass) {
        if (propTypeClass == Dynamic.class) {
            return new DynamicPropSetter(annotation, method);
        }
        if (propTypeClass == Boolean.TYPE) {
            return new BooleanPropSetter(annotation, method, annotation.defaultBoolean());
        }
        if (propTypeClass == Integer.TYPE) {
            if ("Color".equals(annotation.customType())) {
                return new ColorPropSetter(annotation, method, annotation.defaultInt());
            }
            return new IntPropSetter(annotation, method, annotation.defaultInt());
        } else if (propTypeClass == Float.TYPE) {
            return new FloatPropSetter(annotation, method, annotation.defaultFloat());
        } else {
            if (propTypeClass == Double.TYPE) {
                return new DoublePropSetter(annotation, method, annotation.defaultDouble());
            }
            if (propTypeClass == String.class) {
                return new StringPropSetter(annotation, method);
            }
            if (propTypeClass == Boolean.class) {
                return new BoxedBooleanPropSetter(annotation, method);
            }
            if (propTypeClass == Integer.class) {
                if ("Color".equals(annotation.customType())) {
                    return new BoxedColorPropSetter(annotation, method);
                }
                return new BoxedIntPropSetter(annotation, method);
            } else if (propTypeClass == ReadableArray.class) {
                return new ArrayPropSetter(annotation, method);
            } else {
                if (propTypeClass == ReadableMap.class) {
                    return new MapPropSetter(annotation, method);
                }
                throw new RuntimeException("Unrecognized type: " + propTypeClass + " for method: " + method.getDeclaringClass().getName() + "#" + method.getName());
            }
        }
    }

    private static void createPropSetters(ReactPropGroup annotation, Method method, Class<?> propTypeClass, Map<String, PropSetter> props) {
        String[] names = annotation.names();
        int i = 0;
        if (propTypeClass == Dynamic.class) {
            while (i < names.length) {
                props.put(names[i], new DynamicPropSetter(annotation, method, i));
                i++;
            }
        } else if (propTypeClass == Integer.TYPE) {
            while (i < names.length) {
                props.put(names[i], new IntPropSetter(annotation, method, i, annotation.defaultInt()));
                i++;
            }
        } else if (propTypeClass == Float.TYPE) {
            while (i < names.length) {
                props.put(names[i], new FloatPropSetter(annotation, method, i, annotation.defaultFloat()));
                i++;
            }
        } else if (propTypeClass == Double.TYPE) {
            while (i < names.length) {
                props.put(names[i], new DoublePropSetter(annotation, method, i, annotation.defaultDouble()));
                i++;
            }
        } else if (propTypeClass == Integer.class) {
            while (i < names.length) {
                props.put(names[i], new BoxedIntPropSetter(annotation, method, i));
                i++;
            }
        } else {
            throw new RuntimeException("Unrecognized type: " + propTypeClass + " for method: " + method.getDeclaringClass().getName() + "#" + method.getName());
        }
    }

    private static void extractPropSettersFromViewManagerClassDefinition(Class<? extends ViewManager> cls, Map<String, PropSetter> props) {
        Method[] declaredMethods;
        for (Method method : cls.getDeclaredMethods()) {
            ReactProp reactProp = (ReactProp) method.getAnnotation(ReactProp.class);
            if (reactProp != null) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length != 2) {
                    throw new RuntimeException("Wrong number of args for prop setter: " + cls.getName() + "#" + method.getName());
                } else if (!View.class.isAssignableFrom(parameterTypes[0])) {
                    throw new RuntimeException("First param should be a view subclass to be updated: " + cls.getName() + "#" + method.getName());
                } else {
                    props.put(reactProp.name(), createPropSetter(reactProp, method, parameterTypes[1]));
                }
            }
            ReactPropGroup reactPropGroup = (ReactPropGroup) method.getAnnotation(ReactPropGroup.class);
            if (reactPropGroup != null) {
                Class<?>[] parameterTypes2 = method.getParameterTypes();
                if (parameterTypes2.length != 3) {
                    throw new RuntimeException("Wrong number of args for group prop setter: " + cls.getName() + "#" + method.getName());
                } else if (!View.class.isAssignableFrom(parameterTypes2[0])) {
                    throw new RuntimeException("First param should be a view subclass to be updated: " + cls.getName() + "#" + method.getName());
                } else if (parameterTypes2[1] != Integer.TYPE) {
                    throw new RuntimeException("Second argument should be property index: " + cls.getName() + "#" + method.getName());
                } else {
                    createPropSetters(reactPropGroup, method, parameterTypes2[2], props);
                }
            }
        }
    }

    private static void extractPropSettersFromShadowNodeClassDefinition(Class<? extends ReactShadowNode> cls, Map<String, PropSetter> props) {
        Method[] declaredMethods;
        for (Method method : cls.getDeclaredMethods()) {
            ReactProp reactProp = (ReactProp) method.getAnnotation(ReactProp.class);
            if (reactProp != null) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length != 1) {
                    throw new RuntimeException("Wrong number of args for prop setter: " + cls.getName() + "#" + method.getName());
                }
                props.put(reactProp.name(), createPropSetter(reactProp, method, parameterTypes[0]));
            }
            ReactPropGroup reactPropGroup = (ReactPropGroup) method.getAnnotation(ReactPropGroup.class);
            if (reactPropGroup != null) {
                Class<?>[] parameterTypes2 = method.getParameterTypes();
                if (parameterTypes2.length != 2) {
                    throw new RuntimeException("Wrong number of args for group prop setter: " + cls.getName() + "#" + method.getName());
                } else if (parameterTypes2[0] != Integer.TYPE) {
                    throw new RuntimeException("Second argument should be property index: " + cls.getName() + "#" + method.getName());
                } else {
                    createPropSetters(reactPropGroup, method, parameterTypes2[1], props);
                }
            }
        }
    }
}
