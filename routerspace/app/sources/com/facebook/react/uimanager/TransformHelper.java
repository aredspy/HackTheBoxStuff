package com.facebook.react.uimanager;

import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;
/* loaded from: classes.dex */
public class TransformHelper {
    private static ThreadLocal<double[]> sHelperMatrix = new ThreadLocal<double[]>() { // from class: com.facebook.react.uimanager.TransformHelper.1
        @Override // java.lang.ThreadLocal
        public double[] initialValue() {
            return new double[16];
        }
    };

    private static double convertToRadians(ReadableMap transformMap, String key) {
        double d;
        boolean z = true;
        if (transformMap.getType(key) == ReadableType.String) {
            String string = transformMap.getString(key);
            if (string.endsWith("rad")) {
                string = string.substring(0, string.length() - 3);
            } else if (string.endsWith("deg")) {
                string = string.substring(0, string.length() - 3);
                z = false;
            }
            d = Float.parseFloat(string);
        } else {
            d = transformMap.getDouble(key);
        }
        return z ? d : MatrixMathHelper.degreesToRadians(d);
    }

    public static void processTransform(ReadableArray transforms, double[] result) {
        double[] dArr = sHelperMatrix.get();
        MatrixMathHelper.resetIdentityMatrix(result);
        if (transforms.size() == 16 && transforms.getType(0) == ReadableType.Number) {
            for (int i = 0; i < transforms.size(); i++) {
                result[i] = transforms.getDouble(i);
            }
            return;
        }
        int size = transforms.size();
        for (int i2 = 0; i2 < size; i2++) {
            ReadableMap map = transforms.getMap(i2);
            String nextKey = map.keySetIterator().nextKey();
            MatrixMathHelper.resetIdentityMatrix(dArr);
            if ("matrix".equals(nextKey)) {
                ReadableArray array = map.getArray(nextKey);
                for (int i3 = 0; i3 < 16; i3++) {
                    dArr[i3] = array.getDouble(i3);
                }
            } else if ("perspective".equals(nextKey)) {
                MatrixMathHelper.applyPerspective(dArr, map.getDouble(nextKey));
            } else if ("rotateX".equals(nextKey)) {
                MatrixMathHelper.applyRotateX(dArr, convertToRadians(map, nextKey));
            } else if ("rotateY".equals(nextKey)) {
                MatrixMathHelper.applyRotateY(dArr, convertToRadians(map, nextKey));
            } else if ("rotate".equals(nextKey) || "rotateZ".equals(nextKey)) {
                MatrixMathHelper.applyRotateZ(dArr, convertToRadians(map, nextKey));
            } else if ("scale".equals(nextKey)) {
                double d = map.getDouble(nextKey);
                MatrixMathHelper.applyScaleX(dArr, d);
                MatrixMathHelper.applyScaleY(dArr, d);
            } else if (ViewProps.SCALE_X.equals(nextKey)) {
                MatrixMathHelper.applyScaleX(dArr, map.getDouble(nextKey));
            } else if (ViewProps.SCALE_Y.equals(nextKey)) {
                MatrixMathHelper.applyScaleY(dArr, map.getDouble(nextKey));
            } else {
                double d2 = 0.0d;
                if ("translate".equals(nextKey)) {
                    ReadableArray array2 = map.getArray(nextKey);
                    double d3 = array2.getDouble(0);
                    double d4 = array2.getDouble(1);
                    if (array2.size() > 2) {
                        d2 = array2.getDouble(2);
                    }
                    MatrixMathHelper.applyTranslate3D(dArr, d3, d4, d2);
                } else if (ViewProps.TRANSLATE_X.equals(nextKey)) {
                    MatrixMathHelper.applyTranslate2D(dArr, map.getDouble(nextKey), 0.0d);
                } else if (ViewProps.TRANSLATE_Y.equals(nextKey)) {
                    MatrixMathHelper.applyTranslate2D(dArr, 0.0d, map.getDouble(nextKey));
                } else if ("skewX".equals(nextKey)) {
                    MatrixMathHelper.applySkewX(dArr, convertToRadians(map, nextKey));
                } else if ("skewY".equals(nextKey)) {
                    MatrixMathHelper.applySkewY(dArr, convertToRadians(map, nextKey));
                } else {
                    throw new JSApplicationIllegalArgumentException("Unsupported transform type: " + nextKey);
                }
            }
            MatrixMathHelper.multiplyInto(result, result, dArr);
        }
    }
}
