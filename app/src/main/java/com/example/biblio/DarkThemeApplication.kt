package com.example.biblio

import android.app.Application
import androidx.preference.PreferenceManager
import com.example.biblio.helpers.ThemeHelper

class DarkThemeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val themePref = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(resources.getString(R.string.theme_pref), false)
        ThemeHelper.applyTheme(themePref)
    }
}