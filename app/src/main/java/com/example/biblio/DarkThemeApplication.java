package com.example.biblio;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class DarkThemeApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        boolean themePref = sharedPreferences.getBoolean("theme", false);
        ThemeHelper.applyTheme(themePref);
    }
}
