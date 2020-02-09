package com.example.biblio.helpers

import android.util.Log

/**
 * Simple wrapper of logging methods: it overrides the TAG value of Util.Log with the current class
 * name.
 *
 * <pre>
 * `public class MyActivity extends AppCompatActivity {
 * private final LogHelper logger = new LogHelper(getClass());
 * ...
 *
 * logger.d("This is a debug line.");
 * }
 *
 * => D/MyActivity: This is a debug line.
` *
</pre> *
 */
class LogHelper(clazz: Class<*>) {
    private val tag: String = clazz.simpleName

    fun d(msg: String?) {
        Log.d(tag, lazyNull(msg))
    }

    fun d(msg: String?, e: Throwable?) {
        Log.d(tag, lazyNull(msg), e)
    }

    fun e(msg: String?) {
        Log.e(tag, lazyNull(msg))
    }

    fun e(msg: String?, e: Throwable?) {
        Log.e(tag, lazyNull(msg), e)
    }

    fun v(msg: String?) {
        Log.v(tag, lazyNull(msg))
    }

    fun v(msg: String?, e: Throwable?) {
        Log.v(tag, lazyNull(msg), e)
    }

    fun w(msg: String?) {
        Log.w(tag, lazyNull(msg))
    }

    fun w(msg: String?, e: Throwable?) {
        Log.w(tag, lazyNull(msg), e)
    }

    private fun lazyNull(msg: String?): String {
        return msg ?: ""
    }
}