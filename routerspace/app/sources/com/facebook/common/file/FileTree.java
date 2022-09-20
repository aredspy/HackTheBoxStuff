package com.facebook.common.file;

import java.io.File;
/* loaded from: classes.dex */
public class FileTree {
    public static void walkFileTree(File directory, FileTreeVisitor visitor) {
        visitor.preVisitDirectory(directory);
        File[] listFiles = directory.listFiles();
        if (listFiles != null) {
            for (File file : listFiles) {
                if (file.isDirectory()) {
                    walkFileTree(file, visitor);
                } else {
                    visitor.visitFile(file);
                }
            }
        }
        visitor.postVisitDirectory(directory);
    }

    public static boolean deleteContents(File directory) {
        File[] listFiles = directory.listFiles();
        boolean z = true;
        if (listFiles != null) {
            for (File file : listFiles) {
                z &= deleteRecursively(file);
            }
        }
        return z;
    }

    public static boolean deleteRecursively(File file) {
        if (file.isDirectory()) {
            deleteContents(file);
        }
        return file.delete();
    }
}
