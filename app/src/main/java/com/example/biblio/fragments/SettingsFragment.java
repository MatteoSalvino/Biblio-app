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
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        SwitchPreference themePreference = findPreference("theme");
        if (themePreference != null) {
            themePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean themeOption = (boolean) newValue;
                ThemeHelper.applyTheme(themeOption);
                Objects.requireNonNull(getActivity()).recreate();
                return true;
            });
        }

        setEnabledListeners(R.string.libgen_enabled_pref, LIBGEN_ENABLED_KEY);
        setEnabledListeners(R.string.feedbooks_enabled_pref, FEEDBOOKS_ENABLED_KEY);
        setEnabledListeners(R.string.standard_ebooks_enabled_pref, STANDARD_EBOOKS_ENABLED_KEY);
    }

    private void setEnabledListeners(int resource, String key) {
        SwitchPreference switchPreference = findPreference(getResources().getString(resource));
        if (switchPreference != null) {
            switchPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean enabled = (boolean) newValue;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(LIBGEN_ENABLED_KEY, enabled);
                editor.apply();
                return true;
            });
        }

    }
}
