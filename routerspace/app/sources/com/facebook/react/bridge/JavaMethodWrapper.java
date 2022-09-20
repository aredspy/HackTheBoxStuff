package com.facebook.react.bridge;

import com.facebook.debug.holder.PrinterHolder;
import com.facebook.debug.tags.ReactDebugOverlayTags;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.NativeModule;
import com.facebook.systrace.SystraceMessage;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
/* loaded from: classes.dex */
public class JavaMethodWrapper implements NativeModule.NativeMethod {
    private ArgumentExtractor[] mArgumentExtractors;
    private Object[] mArguments;
    private boolean mArgumentsProcessed = false;
    private int mJSArgumentsNeeded;
    private final Method mMethod;
    private final JavaModuleWrapper mModuleWrapper;
    private final int mParamLength;
    private final Class[] mParameterTypes;
    private String mSignature;
    private String mType;
    private static final ArgumentExtractor<Boolean> ARGUMENT_EXTRACTOR_BOOLEAN = new ArgumentExtractor<Boolean>() { // from class: com.facebook.react.bridge.JavaMethodWrapper.1
        @Override // com.facebook.react.bridge.JavaMethodWrapper.ArgumentExtractor
        public Boolean extractArgument(JSInstance jsInstance, ReadableArray jsArguments, int atIndex) {
            return Boolean.valueOf(jsArguments.getBoolean(atIndex));
        }
    };
    private static final ArgumentExtractor<Double> ARGUMENT_EXTRACTOR_DOUBLE = new ArgumentExtractor<Double>() { // from class: com.facebook.react.bridge.JavaMethodWrapper.2
        @Override // com.facebook.react.bridge.JavaMethodWrapper.ArgumentExtractor
        public Double extractArgument(JSInstance jsInstance, ReadableArray jsArguments, int atIndex) {
            return Double.valueOf(jsArguments.getDouble(atIndex));
        }
    };
    private static final ArgumentExtractor<Float> ARGUMENT_EXTRACTOR_FLOAT = new ArgumentExtractor<Float>() { // from class: com.facebook.react.bridge.JavaMethodWrapper.3
        @Override // com.facebook.react.bridge.JavaMethodWrapper.ArgumentExtractor
        public Float extractArgument(JSInstance jsInstance, ReadableArray jsArguments, int atIndex) {
            return Float.valueOf((float) jsArguments.getDouble(atIndex));
        }
    };
    private static final ArgumentExtractor<Integer> ARGUMENT_EXTRACTOR_INTEGER = new ArgumentExtractor<Integer>() { // from class: com.facebook.react.bridge.JavaMethodWrapper.4
        @Override // com.facebook.react.bridge.JavaMethodWrapper.ArgumentExtractor
        public Integer extractArgument(JSInstance jsInstance, ReadableArray jsArguments, int atIndex) {
            return Integer.valueOf((int) jsArguments.getDouble(atIndex));
        }
    };
    private static final ArgumentExtractor<String> ARGUMENT_EXTRACTOR_STRING = new ArgumentExtractor<String>() { // from class: com.facebook.react.bridge.JavaMethodWrapper.5
        @Override // com.facebook.react.bridge.JavaMethodWrapper.ArgumentExtractor
        public String extractArgument(JSInstance jsInstance, ReadableArray jsArguments, int atIndex) {
            return jsArguments.getString(atIndex);
        }
    };
    private static final ArgumentExtractor<ReadableArray> ARGUMENT_EXTRACTOR_ARRAY = new ArgumentExtractor<ReadableArray>() { // from class: com.facebook.react.bridge.JavaMethodWrapper.6
        @Override // com.facebook.react.bridge.JavaMethodWrapper.ArgumentExtractor
        public ReadableArray extractArgument(JSInstance jsInstance, ReadableArray jsArguments, int atIndex) {
            return jsArguments.getArray(atIndex);
        }
    };
    private static final ArgumentExtractor<Dynamic> ARGUMENT_EXTRACTOR_DYNAMIC = new ArgumentExtractor<Dynamic>() { // from class: com.facebook.react.bridge.JavaMethodWrapper.7
        @Override // com.facebook.react.bridge.JavaMethodWrapper.ArgumentExtractor
        public Dynamic extractArgument(JSInstance jsInstance, ReadableArray jsArguments, int atIndex) {
            return DynamicFromArray.create(jsArguments, atIndex);
        }
    };
    private static final ArgumentExtractor<ReadableMap> ARGUMENT_EXTRACTOR_MAP = new ArgumentExtractor<ReadableMap>() { // from class: com.facebook.react.bridge.JavaMethodWrapper.8
        @Override // com.facebook.react.bridge.JavaMethodWrapper.ArgumentExtractor
        public ReadableMap extractArgument(JSInstance jsInstance, ReadableArray jsArguments, int atIndex) {
            return jsArguments.getMap(atIndex);
        }
    };
    private static final ArgumentExtractor<Callback> ARGUMENT_EXTRACTOR_CALLBACK = new ArgumentExtractor<Callback>() { // from class: com.facebook.react.bridge.JavaMethodWrapper.9
        @Override // com.facebook.react.bridge.JavaMethodWrapper.ArgumentExtractor
        public Callback extractArgument(JSInstance jsInstance, ReadableArray jsArguments, int atIndex) {
            if (jsArguments.isNull(atIndex)) {
                return null;
            }
            return new CallbackImpl(jsInstance, (int) jsArguments.getDouble(atIndex));
        }
    };
    private static final ArgumentExtractor<Promise> ARGUMENT_EXTRACTOR_PROMISE = new ArgumentExtractor<Promise>() { // from class: com.facebook.react.bridge.JavaMethodWrapper.10
        @Override // com.facebook.react.bridge.JavaMethodWrapper.ArgumentExtractor
        public int getJSArgumentsNeeded() {
            return 2;
        }

        @Override // com.facebook.react.bridge.JavaMethodWrapper.ArgumentExtractor
        public Promise extractArgument(JSInstance jsInstance, ReadableArray jsArguments, int atIndex) {
            return new PromiseImpl((Callback) JavaMethodWrapper.ARGUMENT_EXTRACTOR_CALLBACK.extractArgument(jsInstance, jsArguments, atIndex), (Callback) JavaMethodWrapper.ARGUMENT_EXTRACTOR_CALLBACK.extractArgument(jsInstance, jsArguments, atIndex + 1));
        }
    };
    private static final boolean DEBUG = PrinterHolder.getPrinter().shouldDisplayLogMessage(ReactDebugOverlayTags.BRIDGE_CALLS);

    /* loaded from: classes.dex */
    public static abstract class ArgumentExtractor<T> {
        public abstract T extractArgument(JSInstance jsInstance, ReadableArray jsArguments, int atIndex);

        public int getJSArgumentsNeeded() {
            return 1;
        }

        private ArgumentExtractor() {
        }
    }

    private static char paramTypeToChar(Class paramClass) {
        char commonTypeToChar = commonTypeToChar(paramClass);
        if (commonTypeToChar != 0) {
            return commonTypeToChar;
        }
        if (paramClass == Callback.class) {
            return 'X';
        }
        if (paramClass == Promise.class) {
            return 'P';
        }
        if (paramClass == ReadableMap.class) {
            return 'M';
        }
        if (paramClass == ReadableArray.class) {
            return 'A';
        }
        if (paramClass == Dynamic.class) {
            return 'Y';
        }
        throw new RuntimeException("Got unknown param class: " + paramClass.getSimpleName());
    }

    private static char returnTypeToChar(Class returnClass) {
        char commonTypeToChar = commonTypeToChar(returnClass);
        if (commonTypeToChar != 0) {
            return commonTypeToChar;
        }
        if (returnClass == Void.TYPE) {
            return 'v';
        }
        if (returnClass == WritableMap.class) {
            return 'M';
        }
        if (returnClass == WritableArray.class) {
            return 'A';
        }
        throw new RuntimeException("Got unknown return class: " + returnClass.getSimpleName());
    }

    private static char commonTypeToChar(Class typeClass) {
        if (typeClass == Boolean.TYPE) {
            return 'z';
        }
        if (typeClass == Boolean.class) {
            return 'Z';
        }
        if (typeClass == Integer.TYPE) {
            return 'i';
        }
        if (typeClass == Integer.class) {
            return 'I';
        }
        if (typeClass == Double.TYPE) {
            return 'd';
        }
        if (typeClass == Double.class) {
            return 'D';
        }
        if (typeClass == Float.TYPE) {
            return 'f';
        }
        if (typeClass == Float.class) {
            return 'F';
        }
        return typeClass == String.class ? 'S' : (char) 0;
    }

    public JavaMethodWrapper(JavaModuleWrapper module, Method method, boolean isSync) {
        this.mType = BaseJavaModule.METHOD_TYPE_ASYNC;
        this.mModuleWrapper = module;
        this.mMethod = method;
        method.setAccessible(true);
        Class<?>[] parameterTypes = method.getParameterTypes();
        this.mParameterTypes = parameterTypes;
        int length = parameterTypes.length;
        this.mParamLength = length;
        if (isSync) {
            this.mType = BaseJavaModule.METHOD_TYPE_SYNC;
        } else if (length <= 0 || parameterTypes[length - 1] != Promise.class) {
        } else {
            this.mType = BaseJavaModule.METHOD_TYPE_PROMISE;
        }
    }

    private void processArguments() {
        if (this.mArgumentsProcessed) {
            return;
        }
        SystraceMessage.Builder beginSection = SystraceMessage.beginSection(0L, "processArguments");
        beginSection.arg("method", this.mModuleWrapper.getName() + "." + this.mMethod.getName()).flush();
        try {
            this.mArgumentsProcessed = true;
            this.mArgumentExtractors = buildArgumentExtractors(this.mParameterTypes);
            this.mSignature = buildSignature(this.mMethod, this.mParameterTypes, this.mType.equals(BaseJavaModule.METHOD_TYPE_SYNC));
            this.mArguments = new Object[this.mParameterTypes.length];
            this.mJSArgumentsNeeded = calculateJSArgumentsNeeded();
        } finally {
            SystraceMessage.endSection(0L).flush();
        }
    }

    public Method getMethod() {
        return this.mMethod;
    }

    public String getSignature() {
        if (!this.mArgumentsProcessed) {
            processArguments();
        }
        return (String) Assertions.assertNotNull(this.mSignature);
    }

    private String buildSignature(Method method, Class[] paramTypes, boolean isSync) {
        StringBuilder sb = new StringBuilder(paramTypes.length + 2);
        if (isSync) {
            sb.append(returnTypeToChar(method.getReturnType()));
            sb.append('.');
        } else {
            sb.append("v.");
        }
        for (int i = 0; i < paramTypes.length; i++) {
            Class cls = paramTypes[i];
            if (cls == Promise.class) {
                boolean z = true;
                if (i != paramTypes.length - 1) {
                    z = false;
                }
                Assertions.assertCondition(z, "Promise must be used as last parameter only");
            }
            sb.append(paramTypeToChar(cls));
        }
        return sb.toString();
    }

    private ArgumentExtractor[] buildArgumentExtractors(Class[] paramTypes) {
        ArgumentExtractor[] argumentExtractorArr = new ArgumentExtractor[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i += argumentExtractorArr[i].getJSArgumentsNeeded()) {
            Class cls = paramTypes[i];
            if (cls == Boolean.class || cls == Boolean.TYPE) {
                argumentExtractorArr[i] = ARGUMENT_EXTRACTOR_BOOLEAN;
            } else if (cls == Integer.class || cls == Integer.TYPE) {
                argumentExtractorArr[i] = ARGUMENT_EXTRACTOR_INTEGER;
            } else if (cls == Double.class || cls == Double.TYPE) {
                argumentExtractorArr[i] = ARGUMENT_EXTRACTOR_DOUBLE;
            } else if (cls == Float.class || cls == Float.TYPE) {
                argumentExtractorArr[i] = ARGUMENT_EXTRACTOR_FLOAT;
            } else if (cls == String.class) {
                argumentExtractorArr[i] = ARGUMENT_EXTRACTOR_STRING;
            } else if (cls == Callback.class) {
                argumentExtractorArr[i] = ARGUMENT_EXTRACTOR_CALLBACK;
            } else if (cls == Promise.class) {
                argumentExtractorArr[i] = ARGUMENT_EXTRACTOR_PROMISE;
                boolean z = true;
                if (i != paramTypes.length - 1) {
                    z = false;
                }
                Assertions.assertCondition(z, "Promise must be used as last parameter only");
            } else if (cls == ReadableMap.class) {
                argumentExtractorArr[i] = ARGUMENT_EXTRACTOR_MAP;
            } else if (cls == ReadableArray.class) {
                argumentExtractorArr[i] = ARGUMENT_EXTRACTOR_ARRAY;
            } else if (cls == Dynamic.class) {
                argumentExtractorArr[i] = ARGUMENT_EXTRACTOR_DYNAMIC;
            } else {
                throw new RuntimeException("Got unknown argument class: " + cls.getSimpleName());
            }
        }
        return argumentExtractorArr;
    }

    private int calculateJSArgumentsNeeded() {
        int i = 0;
        for (ArgumentExtractor argumentExtractor : (ArgumentExtractor[]) Assertions.assertNotNull(this.mArgumentExtractors)) {
            i += argumentExtractor.getJSArgumentsNeeded();
        }
        return i;
    }

    private String getAffectedRange(int startIndex, int jsArgumentsNeeded) {
        if (jsArgumentsNeeded <= 1) {
            return "" + startIndex;
        }
        return "" + startIndex + "-" + ((startIndex + jsArgumentsNeeded) - 1);
    }

    @Override // com.facebook.react.bridge.NativeModule.NativeMethod
    public void invoke(JSInstance jsInstance, ReadableArray parameters) {
        String str = this.mModuleWrapper.getName() + "." + this.mMethod.getName();
        SystraceMessage.beginSection(0L, "callJavaModuleMethod").arg("method", str).flush();
        int i = 0;
        if (DEBUG) {
            PrinterHolder.getPrinter().logMessage(ReactDebugOverlayTags.BRIDGE_CALLS, "JS->Java: %s.%s()", this.mModuleWrapper.getName(), this.mMethod.getName());
        }
        try {
            if (!this.mArgumentsProcessed) {
                processArguments();
            }
            if (this.mArguments == null || this.mArgumentExtractors == null) {
                throw new Error("processArguments failed");
            }
            if (this.mJSArgumentsNeeded != parameters.size()) {
                throw new NativeArgumentsParseException(str + " got " + parameters.size() + " arguments, expected " + this.mJSArgumentsNeeded);
            }
            int i2 = 0;
            while (true) {
                try {
                    ArgumentExtractor[] argumentExtractorArr = this.mArgumentExtractors;
                    if (i < argumentExtractorArr.length) {
                        this.mArguments[i] = argumentExtractorArr[i].extractArgument(jsInstance, parameters, i2);
                        i2 += this.mArgumentExtractors[i].getJSArgumentsNeeded();
                        i++;
                    } else {
                        try {
                            this.mMethod.invoke(this.mModuleWrapper.getModule(), this.mArguments);
                            return;
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException("Could not invoke " + str, e);
                        } catch (IllegalArgumentException e2) {
                            throw new RuntimeException("Could not invoke " + str, e2);
                        } catch (InvocationTargetException e3) {
                            if (e3.getCause() instanceof RuntimeException) {
                                throw ((RuntimeException) e3.getCause());
                            }
                            throw new RuntimeException("Could not invoke " + str, e3);
                        }
                    }
                } catch (UnexpectedNativeTypeException e4) {
                    throw new NativeArgumentsParseException(e4.getMessage() + " (constructing arguments for " + str + " at argument index " + getAffectedRange(i2, this.mArgumentExtractors[i].getJSArgumentsNeeded()) + ")", e4);
                }
            }
        } finally {
            SystraceMessage.endSection(0L).flush();
        }
    }

    @Override // com.facebook.react.bridge.NativeModule.NativeMethod
    public String getType() {
        return this.mType;
    }
}
