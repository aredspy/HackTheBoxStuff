package com.facebook.react.views.text;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.SparseArray;
import androidx.core.content.res.ResourcesCompat;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes.dex */
public class ReactFontManager {
    private static final String[] EXTENSIONS = {"", "_bold", "_italic", "_bold_italic"};
    private static final String[] FILE_EXTENSIONS = {".ttf", ".otf"};
    private static final String FONTS_ASSET_PATH = "fonts/";
    private static ReactFontManager sReactFontManagerInstance;
    private final Map<String, AssetFontFamily> mFontCache = new HashMap();
    private final Map<String, Typeface> mCustomTypefaceCache = new HashMap();

    private ReactFontManager() {
    }

    public static ReactFontManager getInstance() {
        if (sReactFontManagerInstance == null) {
            sReactFontManagerInstance = new ReactFontManager();
        }
        return sReactFontManagerInstance;
    }

    public Typeface getTypeface(String fontFamilyName, int style, AssetManager assetManager) {
        return getTypeface(fontFamilyName, new TypefaceStyle(style), assetManager);
    }

    public Typeface getTypeface(String fontFamilyName, int weight, boolean italic, AssetManager assetManager) {
        return getTypeface(fontFamilyName, new TypefaceStyle(weight, italic), assetManager);
    }

    public Typeface getTypeface(String fontFamilyName, int style, int weight, AssetManager assetManager) {
        return getTypeface(fontFamilyName, new TypefaceStyle(style, weight), assetManager);
    }

    public Typeface getTypeface(String fontFamilyName, TypefaceStyle typefaceStyle, AssetManager assetManager) {
        if (this.mCustomTypefaceCache.containsKey(fontFamilyName)) {
            return typefaceStyle.apply(this.mCustomTypefaceCache.get(fontFamilyName));
        }
        AssetFontFamily assetFontFamily = this.mFontCache.get(fontFamilyName);
        if (assetFontFamily == null) {
            assetFontFamily = new AssetFontFamily();
            this.mFontCache.put(fontFamilyName, assetFontFamily);
        }
        int nearestStyle = typefaceStyle.getNearestStyle();
        Typeface typefaceForStyle = assetFontFamily.getTypefaceForStyle(nearestStyle);
        if (typefaceForStyle != null) {
            return typefaceForStyle;
        }
        Typeface createAssetTypeface = createAssetTypeface(fontFamilyName, nearestStyle, assetManager);
        assetFontFamily.setTypefaceForStyle(nearestStyle, createAssetTypeface);
        return createAssetTypeface;
    }

    public void addCustomFont(Context context, String fontFamily, int fontId) {
        Typeface font = ResourcesCompat.getFont(context, fontId);
        if (font != null) {
            this.mCustomTypefaceCache.put(fontFamily, font);
        }
    }

    public void setTypeface(String fontFamilyName, int style, Typeface typeface) {
        if (typeface != null) {
            AssetFontFamily assetFontFamily = this.mFontCache.get(fontFamilyName);
            if (assetFontFamily == null) {
                assetFontFamily = new AssetFontFamily();
                this.mFontCache.put(fontFamilyName, assetFontFamily);
            }
            assetFontFamily.setTypefaceForStyle(style, typeface);
        }
    }

    private static Typeface createAssetTypeface(String fontFamilyName, int style, AssetManager assetManager) {
        String[] strArr;
        String str = EXTENSIONS[style];
        for (String str2 : FILE_EXTENSIONS) {
            try {
                return Typeface.createFromAsset(assetManager, FONTS_ASSET_PATH + fontFamilyName + str + str2);
            } catch (RuntimeException unused) {
            }
        }
        return Typeface.create(fontFamilyName, style);
    }

    /* loaded from: classes.dex */
    public static class AssetFontFamily {
        private SparseArray<Typeface> mTypefaceSparseArray;

        private AssetFontFamily() {
            this.mTypefaceSparseArray = new SparseArray<>(4);
        }

        public Typeface getTypefaceForStyle(int style) {
            return this.mTypefaceSparseArray.get(style);
        }

        public void setTypefaceForStyle(int style, Typeface typeface) {
            this.mTypefaceSparseArray.put(style, typeface);
        }
    }
}
