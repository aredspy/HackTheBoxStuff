package com.facebook.systrace;

import android.os.Trace;
/* loaded from: classes.dex */
public class Systrace {
    public static final long TRACE_TAG_REACT_APPS = 0;
    public static final long TRACE_TAG_REACT_FRESCO = 0;
    public static final long TRACE_TAG_REACT_JAVA_BRIDGE = 0;
    public static final long TRACE_TAG_REACT_JS_VM_CALLS = 0;
    public static final long TRACE_TAG_REACT_VIEW = 0;

    public static void beginAsyncSection(long tag, final String sectionName, final int cookie) {
    }

    public static void beginAsyncSection(long tag, final String sectionName, final int cookie, final long startNanos) {
    }

    public static void endAsyncFlow(long tag, final String sectionName, final int cookie) {
    }

    public static void endAsyncSection(long tag, final String sectionName, final int cookie) {
    }

    public static void endAsyncSection(long tag, final String sectionName, final int cookie, final long endNanos) {
    }

    public static boolean isTracing(long tag) {
        return false;
    }

    public static void registerListener(TraceListener listener) {
    }

    public static void startAsyncFlow(long tag, final String sectionName, final int cookie) {
    }

    public static void stepAsyncFlow(long tag, final String sectionName, final int cookie) {
    }

    public static void traceCounter(long tag, final String counterName, final int counterValue) {
    }

    public static void traceInstant(long tag, final String title, EventScope scope) {
    }

    public static void unregisterListener(TraceListener listener) {
    }

    /* loaded from: classes.dex */
    public enum EventScope {
        THREAD('t'),
        PROCESS('p'),
        GLOBAL('g');
        
        private final char mCode;

        EventScope(char code) {
            this.mCode = code;
        }

        public char getCode() {
            return this.mCode;
        }
    }

    public static void beginSection(long tag, final String sectionName) {
        Trace.beginSection(sectionName);
    }

    public static void endSection(long tag) {
        Trace.endSection();
    }
}
