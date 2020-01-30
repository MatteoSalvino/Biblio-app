package com.example.biblio.helpers;

import android.os.Environment;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import lrusso96.simplebiblio.core.Ebook;

public class SDCardHelper {

    public static final String APP_ROOT_DIR = "Biblio";

    public static boolean isSDCardPresent() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static boolean findFile(@NotNull File dir, String filename, boolean removeOption) {
        File[] children = dir.listFiles();
        if (children != null) {
            for (File child : children) {
                if (filename.equals(child.getName())) {
                    if (removeOption)
                        return child.delete();
                }
            }
        }
        return false;
    }

    /**
     * @param ebook instance
     * @return its filename
     * @implNote Assume at least one available download
     */
    @NotNull
    public static String getFilename(@NotNull Ebook ebook) {
        return String.format("%s_%s_%s.%s", ebook.getTitle(), ebook.getAuthor(), ebook.getPublished(), ebook.getDownloads().get(0).getExtension());
    }
}
