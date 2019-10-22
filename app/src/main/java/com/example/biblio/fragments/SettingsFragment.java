package com.example.biblio.fragments;


import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.example.biblio.R;
import com.example.biblio.helpers.ThemeHelper;

import java.util.Objects;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        SwitchPreference switchPreference = findPreference("theme");

        if (switchPreference != null) {
            switchPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean themeOption = (boolean) newValue;
                //Toast.makeText(getContext(), "night mode : " +themeOption, Toast.LENGTH_LONG).show();

                ThemeHelper.applyTheme(themeOption);
                Objects.requireNonNull(getActivity()).recreate();
                return true;
            });
        }
    }
}
