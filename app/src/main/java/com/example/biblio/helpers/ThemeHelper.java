package com.example.biblio.helpers;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeHelper {
    public static void applyTheme(boolean themeOption) {
        if (themeOption)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }
}
