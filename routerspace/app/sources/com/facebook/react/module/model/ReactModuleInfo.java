package com.facebook.react.module.model;
/* loaded from: classes.dex */
public class ReactModuleInfo {
    private final boolean mCanOverrideExistingModule;
    private String mClassName;
    private final boolean mHasConstants;
    private final boolean mIsCxxModule;
    private final boolean mIsTurboModule;
    private final String mName;
    private final boolean mNeedsEagerInit;

    public ReactModuleInfo(String name, String className, boolean canOverrideExistingModule, boolean needsEagerInit, boolean hasConstants, boolean isCxxModule, boolean isTurboModule) {
        this.mName = name;
        this.mClassName = className;
        this.mCanOverrideExistingModule = canOverrideExistingModule;
        this.mNeedsEagerInit = needsEagerInit;
        this.mHasConstants = hasConstants;
        this.mIsCxxModule = isCxxModule;
        this.mIsTurboModule = isTurboModule;
    }

    public String name() {
        return this.mName;
    }

    public String className() {
        return this.mClassName;
    }

    public boolean canOverrideExistingModule() {
        return this.mCanOverrideExistingModule;
    }

    public boolean needsEagerInit() {
        return this.mNeedsEagerInit;
    }

    public boolean hasConstants() {
        return this.mHasConstants;
    }

    public boolean isCxxModule() {
        return this.mIsCxxModule;
    }

    public boolean isTurboModule() {
        return this.mIsTurboModule;
    }
}
