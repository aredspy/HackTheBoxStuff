package com.facebook.hermes.instrumentation;
/* loaded from: classes.dex */
public interface HermesMemoryDumper {
    String getId();

    String getInternalStorage();

    void setMetaData(String crashId);

    boolean shouldSaveSnapshot();
}
