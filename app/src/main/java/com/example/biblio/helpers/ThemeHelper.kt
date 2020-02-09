package com.example.biblio.helpers

import androidx.appcompat.app.AppCompatDelegate

object ThemeHelper {
    @JvmStatic
    fun applyTheme(themeOption: Boolean) {
        AppCompatDelegate.setDefaultNightMode(when (themeOption) {
            true -> AppCompatDelegate.MODE_NIGHT_YES
            false -> AppCompatDelegate.MODE_NIGHT_NO
        })
    }
}