package com.example.biblio.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.example.biblio.R;
import com.example.biblio.helpers.ThemeHelper;

import java.util.Objects;

import static com.example.biblio.helpers.SharedPreferencesHelper.FEEDBOOKS_ENABLED_KEY;
import static com.example.biblio.helpers.SharedPreferencesHelper.LIBGEN_ENABLED_KEY;
import static com.example.biblio.helpers.SharedPreferencesHelper.STANDARD_EBOOKS_ENABLED_KEY;

public class SettingsFragment extends PreferenceFragmentCompat {
    public static final String TAG = "SettingsFragment";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        SwitchPreference themePreference = findPreference("theme");
        if (themePreference != null) {
            themePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean themeOption = (boolean) newValue;
                ThemeHelper.applyTheme(themeOption);
                Objects.requireNonNull(getActivity()).recreate();
                return true;
            });
        }

        //fixme: extract key and refactor
        SwitchPreference libgenPreference = findPreference("libgen_enabled");
        if (libgenPreference != null) {
            libgenPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean enabled = (boolean) newValue;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(LIBGEN_ENABLED_KEY, enabled);
                editor.apply();
                return true;
            });
        }

        //fixme: extract key and refactor
        SwitchPreference feedbooksPreference = findPreference("feedbooks_enabled");
        if (feedbooksPreference != null) {
            feedbooksPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean enabled = (boolean) newValue;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(FEEDBOOKS_ENABLED_KEY, enabled);
                editor.apply();
                return true;
            });
        }

        //fixme: extract key and refactor
        SwitchPreference standardPreference = findPreference("standard_ebooks_enabled");
        if (standardPreference != null) {
            standardPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean enabled = (boolean) newValue;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(STANDARD_EBOOKS_ENABLED_KEY, enabled);
                editor.apply();
                return true;
            });
        }
    }
}
