package com.example.biblio.helpers;

import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Simple wrapper of logging methods: it overrides the TAG value of Util.Log with the current class
 * name.
 *
 * <pre>
 * {@code
 * public class MyActivity extends AppCompatActivity {
 *     private final LogHelper logger = new LogHelper(getClass());
 *     ...
 *
 *     logger.d("This is a debug line.");
 * }
 *
 * => D/MyActivity: This is a debug line.
 * }
 * </pre>
 */
public class LogHelper {
    private final String tag;

    public LogHelper(@NotNull Class clazz) {
        tag = clazz.getSimpleName();
    }

    public void d(@Nullable String msg) {
        Log.d(tag, lazy_null(msg));
    }

    public void d(@Nullable String msg, Throwable e) {
        Log.d(tag, lazy_null(msg), e);
    }

    public void e(@Nullable String msg) {
        Log.e(tag, lazy_null(msg));
    }

    public void e(@Nullable String msg, Throwable e) {
        Log.e(tag, lazy_null(msg), e);
    }

    public void v(@Nullable String msg) {
        Log.v(tag, lazy_null(msg));
    }

    public void v(@Nullable String msg, Throwable e) {
        Log.v(tag, lazy_null(msg), e);
    }

    public void w(@Nullable String msg) {
        Log.w(tag, lazy_null(msg));
    }

    public void w(@Nullable String msg, Throwable e) {
        Log.w(tag, lazy_null(msg), e);
    }

    @NotNull
    private String lazy_null(@Nullable String msg) {
        return msg != null ? msg : "";
    }
}
