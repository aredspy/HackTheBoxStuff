package com.facebook.react.uimanager;

import com.facebook.infer.annotation.Assertions;
import java.lang.reflect.Array;
/* loaded from: classes.dex */
public class MatrixMathHelper {
    private static final double EPSILON = 1.0E-5d;

    public static double degreesToRadians(double degrees) {
        return (degrees * 3.141592653589793d) / 180.0d;
    }

    /* loaded from: classes.dex */
    public static class MatrixDecompositionContext {
        double[] perspective = new double[4];
        double[] scale = new double[3];
        double[] skew = new double[3];
        double[] translation = new double[3];
        double[] rotationDegrees = new double[3];

        private static void resetArray(double[] arr) {
            for (int i = 0; i < arr.length; i++) {
                arr[i] = 0.0d;
            }
        }

        public void reset() {
            resetArray(this.perspective);
            resetArray(this.scale);
            resetArray(this.skew);
            resetArray(this.translation);
            resetArray(this.rotationDegrees);
        }
    }

    private static boolean isZero(double d) {
        return !Double.isNaN(d) && Math.abs(d) < 1.0E-5d;
    }

    public static void multiplyInto(double[] out, double[] a, double[] b) {
        double d = a[0];
        double d2 = a[1];
        double d3 = a[2];
        double d4 = a[3];
        double d5 = a[4];
        double d6 = a[5];
        double d7 = a[6];
        double d8 = a[7];
        double d9 = a[8];
        double d10 = a[9];
        double d11 = a[10];
        double d12 = a[11];
        double d13 = a[12];
        double d14 = a[13];
        double d15 = a[14];
        double d16 = a[15];
        double d17 = b[0];
        double d18 = b[1];
        double d19 = b[2];
        double d20 = b[3];
        out[0] = (d17 * d) + (d18 * d5) + (d19 * d9) + (d20 * d13);
        out[1] = (d17 * d2) + (d18 * d6) + (d19 * d10) + (d20 * d14);
        out[2] = (d17 * d3) + (d18 * d7) + (d19 * d11) + (d20 * d15);
        out[3] = (d17 * d4) + (d18 * d8) + (d19 * d12) + (d20 * d16);
        double d21 = b[4];
        double d22 = b[5];
        double d23 = b[6];
        double d24 = b[7];
        out[4] = (d21 * d) + (d22 * d5) + (d23 * d9) + (d24 * d13);
        out[5] = (d21 * d2) + (d22 * d6) + (d23 * d10) + (d24 * d14);
        out[6] = (d21 * d3) + (d22 * d7) + (d23 * d11) + (d24 * d15);
        out[7] = (d21 * d4) + (d22 * d8) + (d23 * d12) + (d24 * d16);
        double d25 = b[8];
        double d26 = b[9];
        double d27 = b[10];
        double d28 = b[11];
        out[8] = (d25 * d) + (d26 * d5) + (d27 * d9) + (d28 * d13);
        out[9] = (d25 * d2) + (d26 * d6) + (d27 * d10) + (d28 * d14);
        out[10] = (d25 * d3) + (d26 * d7) + (d27 * d11) + (d28 * d15);
        out[11] = (d25 * d4) + (d26 * d8) + (d27 * d12) + (d28 * d16);
        double d29 = b[12];
        double d30 = b[13];
        double d31 = b[14];
        double d32 = b[15];
        out[12] = (d * d29) + (d5 * d30) + (d9 * d31) + (d13 * d32);
        out[13] = (d2 * d29) + (d6 * d30) + (d10 * d31) + (d14 * d32);
        out[14] = (d3 * d29) + (d7 * d30) + (d11 * d31) + (d15 * d32);
        out[15] = (d29 * d4) + (d30 * d8) + (d31 * d12) + (d32 * d16);
    }

    public static void decomposeMatrix(double[] transformMatrix, MatrixDecompositionContext ctx) {
        Assertions.assertCondition(transformMatrix.length == 16);
        double[] dArr = ctx.perspective;
        double[] dArr2 = ctx.scale;
        double[] dArr3 = ctx.skew;
        double[] dArr4 = ctx.translation;
        double[] dArr5 = ctx.rotationDegrees;
        if (isZero(transformMatrix[15])) {
            return;
        }
        double[][] dArr6 = (double[][]) Array.newInstance(double.class, 4, 4);
        double[] dArr7 = new double[16];
        for (int i = 0; i < 4; i++) {
            for (int i2 = 0; i2 < 4; i2++) {
                int i3 = (i * 4) + i2;
                double d = transformMatrix[i3] / transformMatrix[15];
                dArr6[i][i2] = d;
                if (i2 == 3) {
                    d = 0.0d;
                }
                dArr7[i3] = d;
            }
        }
        dArr7[15] = 1.0d;
        if (isZero(determinant(dArr7))) {
            return;
        }
        if (!isZero(dArr6[0][3]) || !isZero(dArr6[1][3]) || !isZero(dArr6[2][3])) {
            multiplyVectorByMatrix(new double[]{dArr6[0][3], dArr6[1][3], dArr6[2][3], dArr6[3][3]}, transpose(inverse(dArr7)), dArr);
        } else {
            dArr[2] = 0.0d;
            dArr[1] = 0.0d;
            dArr[0] = 0.0d;
            dArr[3] = 1.0d;
        }
        for (int i4 = 0; i4 < 3; i4++) {
            dArr4[i4] = dArr6[3][i4];
        }
        double[][] dArr8 = (double[][]) Array.newInstance(double.class, 3, 3);
        for (int i5 = 0; i5 < 3; i5++) {
            dArr8[i5][0] = dArr6[i5][0];
            dArr8[i5][1] = dArr6[i5][1];
            dArr8[i5][2] = dArr6[i5][2];
        }
        dArr2[0] = v3Length(dArr8[0]);
        dArr8[0] = v3Normalize(dArr8[0], dArr2[0]);
        dArr3[0] = v3Dot(dArr8[0], dArr8[1]);
        dArr8[1] = v3Combine(dArr8[1], dArr8[0], 1.0d, -dArr3[0]);
        dArr2[1] = v3Length(dArr8[1]);
        dArr8[1] = v3Normalize(dArr8[1], dArr2[1]);
        dArr3[0] = dArr3[0] / dArr2[1];
        dArr3[1] = v3Dot(dArr8[0], dArr8[2]);
        dArr8[2] = v3Combine(dArr8[2], dArr8[0], 1.0d, -dArr3[1]);
        dArr3[2] = v3Dot(dArr8[1], dArr8[2]);
        dArr8[2] = v3Combine(dArr8[2], dArr8[1], 1.0d, -dArr3[2]);
        dArr2[2] = v3Length(dArr8[2]);
        dArr8[2] = v3Normalize(dArr8[2], dArr2[2]);
        dArr3[1] = dArr3[1] / dArr2[2];
        dArr3[2] = dArr3[2] / dArr2[2];
        if (v3Dot(dArr8[0], v3Cross(dArr8[1], dArr8[2])) < 0.0d) {
            for (int i6 = 0; i6 < 3; i6++) {
                dArr2[i6] = dArr2[i6] * (-1.0d);
                double[] dArr9 = dArr8[i6];
                dArr9[0] = dArr9[0] * (-1.0d);
                double[] dArr10 = dArr8[i6];
                dArr10[1] = dArr10[1] * (-1.0d);
                double[] dArr11 = dArr8[i6];
                dArr11[2] = dArr11[2] * (-1.0d);
            }
        }
        dArr5[0] = roundTo3Places((-Math.atan2(dArr8[2][1], dArr8[2][2])) * 57.29577951308232d);
        dArr5[1] = roundTo3Places((-Math.atan2(-dArr8[2][0], Math.sqrt((dArr8[2][1] * dArr8[2][1]) + (dArr8[2][2] * dArr8[2][2])))) * 57.29577951308232d);
        dArr5[2] = roundTo3Places((-Math.atan2(dArr8[1][0], dArr8[0][0])) * 57.29577951308232d);
    }

    public static double determinant(double[] matrix) {
        double d = matrix[0];
        double d2 = matrix[1];
        double d3 = matrix[2];
        double d4 = matrix[3];
        double d5 = matrix[4];
        double d6 = matrix[5];
        double d7 = matrix[6];
        double d8 = matrix[7];
        double d9 = matrix[8];
        double d10 = matrix[9];
        double d11 = matrix[10];
        double d12 = matrix[11];
        double d13 = matrix[12];
        double d14 = matrix[13];
        double d15 = matrix[14];
        double d16 = matrix[15];
        double d17 = d4 * d7;
        double d18 = d3 * d8;
        double d19 = d4 * d6;
        double d20 = d2 * d8;
        double d21 = d3 * d6;
        double d22 = d2 * d7;
        double d23 = d4 * d5;
        double d24 = d8 * d;
        double d25 = d3 * d5;
        double d26 = d7 * d;
        double d27 = d2 * d5;
        double d28 = d * d6;
        return ((((((((((((((((((((((((d17 * d10) * d13) - ((d18 * d10) * d13)) - ((d19 * d11) * d13)) + ((d20 * d11) * d13)) + ((d21 * d12) * d13)) - ((d22 * d12) * d13)) - ((d17 * d9) * d14)) + ((d18 * d9) * d14)) + ((d23 * d11) * d14)) - ((d24 * d11) * d14)) - ((d25 * d12) * d14)) + ((d26 * d12) * d14)) + ((d19 * d9) * d15)) - ((d20 * d9) * d15)) - ((d23 * d10) * d15)) + ((d24 * d10) * d15)) + ((d27 * d12) * d15)) - ((d12 * d28) * d15)) - ((d21 * d9) * d16)) + ((d22 * d9) * d16)) + ((d25 * d10) * d16)) - ((d26 * d10) * d16)) - ((d27 * d11) * d16)) + (d28 * d11 * d16);
    }

    public static double[] inverse(double[] matrix) {
        double determinant = determinant(matrix);
        if (isZero(determinant)) {
            return matrix;
        }
        double d = matrix[0];
        double d2 = matrix[1];
        double d3 = matrix[2];
        double d4 = matrix[3];
        double d5 = matrix[4];
        double d6 = matrix[5];
        double d7 = matrix[6];
        double d8 = matrix[7];
        double d9 = matrix[8];
        double d10 = matrix[9];
        double d11 = matrix[10];
        double d12 = matrix[11];
        double d13 = matrix[12];
        double d14 = matrix[13];
        double d15 = matrix[14];
        double d16 = matrix[15];
        double d17 = d7 * d12;
        double d18 = d8 * d11;
        double d19 = d8 * d10;
        double d20 = d6 * d12;
        double d21 = d7 * d10;
        double d22 = ((((d17 * d14) - (d18 * d14)) + (d19 * d15)) - (d20 * d15)) - (d21 * d16);
        double d23 = d6 * d11;
        double d24 = d4 * d11;
        double d25 = d3 * d12;
        double d26 = d4 * d10;
        double d27 = d2 * d12;
        double d28 = d3 * d10;
        double d29 = (((d24 * d14) - (d25 * d14)) - (d26 * d15)) + (d27 * d15) + (d28 * d16);
        double d30 = d2 * d11;
        double d31 = d3 * d8;
        double d32 = d4 * d7;
        double d33 = d4 * d6;
        double d34 = d2 * d8;
        double d35 = d3 * d6;
        double d36 = d2 * d7;
        double d37 = (d18 * d13) - (d17 * d13);
        double d38 = d8 * d9;
        double d39 = d5 * d12;
        double d40 = d7 * d9;
        double d41 = (d37 - (d38 * d15)) + (d39 * d15) + (d40 * d16);
        double d42 = d5 * d11;
        double d43 = (d25 * d13) - (d24 * d13);
        double d44 = d4 * d9;
        double d45 = d * d12;
        double d46 = d3 * d9;
        double d47 = d * d11;
        double d48 = d4 * d5;
        double d49 = d8 * d;
        double d50 = d3 * d5;
        double d51 = d7 * d;
        double d52 = d6 * d9;
        double d53 = ((((d20 * d13) - (d19 * d13)) + (d38 * d14)) - (d39 * d14)) - (d52 * d16);
        double d54 = d5 * d10;
        double d55 = d2 * d9;
        double d56 = d * d10;
        double d57 = d2 * d5;
        double d58 = d * d6;
        return new double[]{(d22 + (d23 * d16)) / determinant, (d29 - (d30 * d16)) / determinant, ((((((d31 * d14) - (d32 * d14)) + (d33 * d15)) - (d34 * d15)) - (d35 * d16)) + (d36 * d16)) / determinant, ((((((d32 * d10) - (d31 * d10)) - (d33 * d11)) + (d34 * d11)) + (d35 * d12)) - (d36 * d12)) / determinant, (d41 - (d42 * d16)) / determinant, ((((d43 + (d44 * d15)) - (d45 * d15)) - (d46 * d16)) + (d47 * d16)) / determinant, ((((((d32 * d13) - (d31 * d13)) - (d48 * d15)) + (d49 * d15)) + (d50 * d16)) - (d51 * d16)) / determinant, ((((((d31 * d9) - (d32 * d9)) + (d48 * d11)) - (d49 * d11)) - (d50 * d12)) + (d51 * d12)) / determinant, (d53 + (d54 * d16)) / determinant, ((((((d26 * d13) - (d27 * d13)) - (d44 * d14)) + (d45 * d14)) + (d55 * d16)) - (d56 * d16)) / determinant, ((((((d34 * d13) - (d33 * d13)) + (d48 * d14)) - (d49 * d14)) - (d57 * d16)) + (d16 * d58)) / determinant, ((((((d33 * d9) - (d34 * d9)) - (d48 * d10)) + (d49 * d10)) + (d57 * d12)) - (d12 * d58)) / determinant, ((((((d21 * d13) - (d23 * d13)) - (d40 * d14)) + (d42 * d14)) + (d52 * d15)) - (d54 * d15)) / determinant, ((((((d30 * d13) - (d28 * d13)) + (d46 * d14)) - (d47 * d14)) - (d55 * d15)) + (d56 * d15)) / determinant, ((((((d35 * d13) - (d13 * d36)) - (d50 * d14)) + (d14 * d51)) + (d57 * d15)) - (d15 * d58)) / determinant, ((((((d36 * d9) - (d35 * d9)) + (d50 * d10)) - (d51 * d10)) - (d57 * d11)) + (d58 * d11)) / determinant};
    }

    public static double[] transpose(double[] m) {
        return new double[]{m[0], m[4], m[8], m[12], m[1], m[5], m[9], m[13], m[2], m[6], m[10], m[14], m[3], m[7], m[11], m[15]};
    }

    public static void multiplyVectorByMatrix(double[] v, double[] m, double[] result) {
        double d = v[0];
        double d2 = v[1];
        double d3 = v[2];
        double d4 = v[3];
        result[0] = (m[0] * d) + (m[4] * d2) + (m[8] * d3) + (m[12] * d4);
        result[1] = (m[1] * d) + (m[5] * d2) + (m[9] * d3) + (m[13] * d4);
        result[2] = (m[2] * d) + (m[6] * d2) + (m[10] * d3) + (m[14] * d4);
        result[3] = (d * m[3]) + (d2 * m[7]) + (d3 * m[11]) + (d4 * m[15]);
    }

    public static double v3Length(double[] a) {
        return Math.sqrt((a[0] * a[0]) + (a[1] * a[1]) + (a[2] * a[2]));
    }

    public static double[] v3Normalize(double[] vector, double norm) {
        if (isZero(norm)) {
            norm = v3Length(vector);
        }
        double d = 1.0d / norm;
        return new double[]{vector[0] * d, vector[1] * d, vector[2] * d};
    }

    public static double v3Dot(double[] a, double[] b) {
        return (a[0] * b[0]) + (a[1] * b[1]) + (a[2] * b[2]);
    }

    public static double[] v3Combine(double[] a, double[] b, double aScale, double bScale) {
        return new double[]{(a[0] * aScale) + (b[0] * bScale), (a[1] * aScale) + (b[1] * bScale), (aScale * a[2]) + (bScale * b[2])};
    }

    public static double[] v3Cross(double[] a, double[] b) {
        return new double[]{(a[1] * b[2]) - (a[2] * b[1]), (a[2] * b[0]) - (a[0] * b[2]), (a[0] * b[1]) - (a[1] * b[0])};
    }

    public static double roundTo3Places(double n) {
        return Math.round(n * 1000.0d) * 0.001d;
    }

    public static double[] createIdentityMatrix() {
        double[] dArr = new double[16];
        resetIdentityMatrix(dArr);
        return dArr;
    }

    public static void resetIdentityMatrix(double[] matrix) {
        matrix[14] = 0.0d;
        matrix[13] = 0.0d;
        matrix[12] = 0.0d;
        matrix[11] = 0.0d;
        matrix[9] = 0.0d;
        matrix[8] = 0.0d;
        matrix[7] = 0.0d;
        matrix[6] = 0.0d;
        matrix[4] = 0.0d;
        matrix[3] = 0.0d;
        matrix[2] = 0.0d;
        matrix[1] = 0.0d;
        matrix[15] = 1.0d;
        matrix[10] = 1.0d;
        matrix[5] = 1.0d;
        matrix[0] = 1.0d;
    }

    public static void applyPerspective(double[] m, double perspective) {
        m[11] = (-1.0d) / perspective;
    }

    public static void applyScaleX(double[] m, double factor) {
        m[0] = factor;
    }

    public static void applyScaleY(double[] m, double factor) {
        m[5] = factor;
    }

    public static void applyScaleZ(double[] m, double factor) {
        m[10] = factor;
    }

    public static void applyTranslate2D(double[] m, double x, double y) {
        m[12] = x;
        m[13] = y;
    }

    public static void applyTranslate3D(double[] m, double x, double y, double z) {
        m[12] = x;
        m[13] = y;
        m[14] = z;
    }

    public static void applySkewX(double[] m, double radians) {
        m[4] = Math.tan(radians);
    }

    public static void applySkewY(double[] m, double radians) {
        m[1] = Math.tan(radians);
    }

    public static void applyRotateX(double[] m, double radians) {
        m[5] = Math.cos(radians);
        m[6] = Math.sin(radians);
        m[9] = -Math.sin(radians);
        m[10] = Math.cos(radians);
    }

    public static void applyRotateY(double[] m, double radians) {
        m[0] = Math.cos(radians);
        m[2] = -Math.sin(radians);
        m[8] = Math.sin(radians);
        m[10] = Math.cos(radians);
    }

    public static void applyRotateZ(double[] m, double radians) {
        m[0] = Math.cos(radians);
        m[1] = Math.sin(radians);
        m[4] = -Math.sin(radians);
        m[5] = Math.cos(radians);
    }
}
