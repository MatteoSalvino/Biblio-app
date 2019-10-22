package com.example.biblio.helpers;

import android.os.Environment;

import java.io.File;

public class CheckForSDCardHelper {

    public static boolean isSDCardPresent() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static boolean findFile(File dir, String filename, boolean removeOption) {
        File[] children = dir.listFiles();

        if(children != null) {

            for (File child : children) {
                //Log.d("findFile", child.getName());
                if (filename.equals(child.getName())) {
                    if (removeOption)
                        return child.delete();
                }
            }
        }
        return false;
    }
}
