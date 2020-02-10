package com.example.biblio.helpers

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel

@SuppressLint("Registered")
open class XActivity(clazz: Class<*>) : AppCompatActivity() {
    protected val uiScope = CoroutineScope(Dispatchers.Main)
    protected var logger = LogHelper(clazz)

    override fun onDestroy() {
        super.onDestroy()
        uiScope.cancel()
    }
}