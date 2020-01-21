package com.example.biblio.helpers;

import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LogHelper {
    private final String tag;

    public LogHelper(@NotNull Class clazz) {
        tag = clazz.getName();
    }

    public void d(@Nullable String msg) {
        Log.d(tag, lazy_null(msg));
    }

    public void e(@Nullable String msg) {
        Log.e(tag, lazy_null(msg));
    }

    public void v(@Nullable String msg) {
        Log.v(tag, lazy_null(msg));
    }

    public void w(@Nullable String msg) {
        Log.w(tag, lazy_null(msg));
    }

    @NotNull
    private String lazy_null(@Nullable String msg) {
        return msg != null ? msg : "";
    }
}
