package com.facebook.react.views.text;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.text.TextUtils;
import androidx.recyclerview.widget.ItemTouchHelper;
import com.facebook.react.bridge.ReadableArray;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class ReactTypefaceUtils {
    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static int parseFontWeight(String fontWeightString) {
        char c;
        if (fontWeightString != null) {
            fontWeightString.hashCode();
            switch (fontWeightString.hashCode()) {
                case -1039745817:
                    if (fontWeightString.equals("normal")) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                case 48625:
                    if (fontWeightString.equals("100")) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case 49586:
                    if (fontWeightString.equals("200")) {
                        c = 2;
                        break;
                    }
                    c = 65535;
                    break;
                case 50547:
                    if (fontWeightString.equals("300")) {
                        c = 3;
                        break;
                    }
                    c = 65535;
                    break;
                case 51508:
                    if (fontWeightString.equals("400")) {
                        c = 4;
                        break;
                    }
                    c = 65535;
                    break;
                case 52469:
                    if (fontWeightString.equals("500")) {
                        c = 5;
                        break;
                    }
                    c = 65535;
                    break;
                case 53430:
                    if (fontWeightString.equals("600")) {
                        c = 6;
                        break;
                    }
                    c = 65535;
                    break;
                case 54391:
                    if (fontWeightString.equals("700")) {
                        c = 7;
                        break;
                    }
                    c = 65535;
                    break;
                case 55352:
                    if (fontWeightString.equals("800")) {
                        c = '\b';
                        break;
                    }
                    c = 65535;
                    break;
                case 56313:
                    if (fontWeightString.equals("900")) {
                        c = '\t';
                        break;
                    }
                    c = 65535;
                    break;
                case 3029637:
                    if (fontWeightString.equals("bold")) {
                        c = '\n';
                        break;
                    }
                    c = 65535;
                    break;
                default:
                    c = 65535;
                    break;
            }
            switch (c) {
                case 0:
                case 4:
                    return 400;
                case 1:
                    return 100;
                case 2:
                    return ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION;
                case 3:
                    return 300;
                case 5:
                    return 500;
                case 6:
                    return 600;
                case 7:
                case '\n':
                    return TypefaceStyle.BOLD;
                case '\b':
                    return 800;
                case '\t':
                    return 900;
            }
        }
        return -1;
    }

    public static int parseFontStyle(String fontStyleString) {
        if (fontStyleString != null) {
            if ("italic".equals(fontStyleString)) {
                return 2;
            }
            return "normal".equals(fontStyleString) ? 0 : -1;
        }
        return -1;
    }

    public static String parseFontVariant(ReadableArray fontVariantArray) {
        if (fontVariantArray == null || fontVariantArray.size() == 0) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < fontVariantArray.size(); i++) {
            String string = fontVariantArray.getString(i);
            if (string != null) {
                string.hashCode();
                char c = 65535;
                switch (string.hashCode()) {
                    case -1195362251:
                        if (string.equals("proportional-nums")) {
                            c = 0;
                            break;
                        }
                        break;
                    case -1061392823:
                        if (string.equals("lining-nums")) {
                            c = 1;
                            break;
                        }
                        break;
                    case -771984547:
                        if (string.equals("tabular-nums")) {
                            c = 2;
                            break;
                        }
                        break;
                    case -659678800:
                        if (string.equals("oldstyle-nums")) {
                            c = 3;
                            break;
                        }
                        break;
                    case 1183323111:
                        if (string.equals("small-caps")) {
                            c = 4;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        arrayList.add("'pnum'");
                        continue;
                    case 1:
                        arrayList.add("'lnum'");
                        continue;
                    case 2:
                        arrayList.add("'tnum'");
                        continue;
                    case 3:
                        arrayList.add("'onum'");
                        continue;
                    case 4:
                        arrayList.add("'smcp'");
                        continue;
                }
            }
        }
        return TextUtils.join(", ", arrayList);
    }

    public static Typeface applyStyles(Typeface typeface, int style, int weight, String fontFamilyName, AssetManager assetManager) {
        TypefaceStyle typefaceStyle = new TypefaceStyle(style, weight);
        if (fontFamilyName == null) {
            if (typeface == null) {
                typeface = Typeface.DEFAULT;
            }
            return typefaceStyle.apply(typeface);
        }
        return ReactFontManager.getInstance().getTypeface(fontFamilyName, typefaceStyle, assetManager);
    }
}
