package com.facebook.common.file;

import java.io.File;
/* loaded from: classes.dex */
public interface FileTreeVisitor {
    void postVisitDirectory(File directory);

    void preVisitDirectory(File directory);

    void visitFile(File file);
}
