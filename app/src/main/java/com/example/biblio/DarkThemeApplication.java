package com.example.biblio;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.example.biblio.helpers.ThemeHelper;

public class DarkThemeApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean themePref = preferences.getBoolean(getResources().getString(R.string.theme_pref), false);

        ThemeHelper.applyTheme(themePref);
    }
}
