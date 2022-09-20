package com.facebook.common.callercontext;

import android.os.Parcel;
import android.os.Parcelable;
import com.facebook.common.internal.Objects;
import com.facebook.common.internal.Preconditions;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class ContextChain implements Parcelable {
    public static final Parcelable.Creator<ContextChain> CREATOR = new Parcelable.Creator<ContextChain>() { // from class: com.facebook.common.callercontext.ContextChain.1
        @Override // android.os.Parcelable.Creator
        public ContextChain createFromParcel(Parcel in) {
            return new ContextChain(in);
        }

        @Override // android.os.Parcelable.Creator
        public ContextChain[] newArray(int size) {
            return new ContextChain[size];
        }
    };
    private static final char PARENT_SEPARATOR = '/';
    public static final String TAG_INFRA = "i";
    public static final String TAG_PRODUCT = "p";
    public static final String TAG_PRODUCT_AND_INFRA = "pi";
    private static boolean sUseDeepEquals = false;
    @Nullable
    private Map<String, Object> mExtraData;
    private final int mLevel;
    private final String mName;
    @Nullable
    private final ContextChain mParent;
    @Nullable
    private String mSerializedString;
    private final String mTag;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public static void setUseDeepEquals(boolean useDeepEquals) {
        sUseDeepEquals = useDeepEquals;
    }

    public ContextChain(final String tag, final String name, @Nullable final Map<String, String> extraData, @Nullable final ContextChain parent) {
        this.mTag = tag;
        this.mName = name;
        this.mLevel = parent != null ? parent.mLevel + 1 : 0;
        this.mParent = parent;
        Map<String, Object> extraData2 = parent != null ? parent.getExtraData() : null;
        if (extraData2 != null) {
            this.mExtraData = new HashMap(extraData2);
        }
        if (extraData != null) {
            if (this.mExtraData == null) {
                this.mExtraData = new HashMap();
            }
            this.mExtraData.putAll(extraData);
        }
    }

    public ContextChain(final String tag, final String name, @Nullable final ContextChain parent) {
        this(tag, name, null, parent);
    }

    protected ContextChain(Parcel in) {
        this.mTag = in.readString();
        this.mName = in.readString();
        this.mLevel = in.readInt();
        this.mParent = (ContextChain) in.readParcelable(ContextChain.class.getClassLoader());
    }

    public String getName() {
        return this.mName;
    }

    public String getTag() {
        return this.mTag;
    }

    @Nullable
    public Map<String, Object> getExtraData() {
        return this.mExtraData;
    }

    @Nullable
    public ContextChain getParent() {
        return this.mParent;
    }

    public ContextChain getRootContextChain() {
        ContextChain contextChain = this.mParent;
        return contextChain == null ? this : contextChain.getRootContextChain();
    }

    @Nullable
    public String getStringExtra(String key) {
        Object obj;
        Map<String, Object> map = this.mExtraData;
        if (map == null || (obj = map.get(key)) == null) {
            return null;
        }
        return String.valueOf(obj);
    }

    public void putObjectExtra(String key, Object value) {
        if (this.mExtraData == null) {
            this.mExtraData = new HashMap();
        }
        this.mExtraData.put(key, value);
    }

    public String toString() {
        if (this.mSerializedString == null) {
            this.mSerializedString = this.mTag + ":" + this.mName;
            if (this.mParent != null) {
                this.mSerializedString = this.mParent.toString() + PARENT_SEPARATOR + this.mSerializedString;
            }
        }
        return this.mSerializedString;
    }

    public String[] toStringArray() {
        int i = this.mLevel;
        String[] strArr = new String[i + 1];
        ContextChain contextChain = this;
        while (i >= 0) {
            Preconditions.checkNotNull(contextChain, "ContextChain level mismatch, this should not happen.");
            strArr[i] = contextChain.mTag + ":" + contextChain.mName;
            contextChain = contextChain.mParent;
            i += -1;
        }
        return strArr;
    }

    public boolean equals(@Nullable Object obj) {
        if (sUseDeepEquals) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            ContextChain contextChain = (ContextChain) obj;
            if (Objects.equal(this.mTag, contextChain.mTag) && Objects.equal(this.mName, contextChain.mName) && this.mLevel == contextChain.mLevel) {
                ContextChain contextChain2 = this.mParent;
                ContextChain contextChain3 = contextChain.mParent;
                if (contextChain2 == contextChain3) {
                    return true;
                }
                if (contextChain2 != null && contextChain2.equals(contextChain3)) {
                    return true;
                }
            }
            return false;
        }
        return super.equals(obj);
    }

    public int hashCode() {
        if (sUseDeepEquals) {
            int hashCode = super.hashCode() * 31;
            String str = this.mTag;
            int i = 0;
            int hashCode2 = (hashCode + (str != null ? str.hashCode() : 0)) * 31;
            String str2 = this.mName;
            int hashCode3 = (((hashCode2 + (str2 != null ? str2.hashCode() : 0)) * 31) + this.mLevel) * 31;
            ContextChain contextChain = this.mParent;
            if (contextChain != null) {
                i = contextChain.hashCode();
            }
            return hashCode3 + i;
        }
        return super.hashCode();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mTag);
        dest.writeString(this.mName);
        dest.writeInt(this.mLevel);
        dest.writeParcelable(this.mParent, flags);
    }
}
