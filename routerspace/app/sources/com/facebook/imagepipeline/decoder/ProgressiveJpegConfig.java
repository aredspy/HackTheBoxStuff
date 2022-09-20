package com.facebook.imagepipeline.decoder;

import com.facebook.imagepipeline.image.QualityInfo;
/* loaded from: classes.dex */
public interface ProgressiveJpegConfig {
    int getNextScanNumberToDecode(int scanNumber);

    QualityInfo getQualityInfo(int scanNumber);
}
