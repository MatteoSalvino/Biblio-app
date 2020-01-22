package com.example.biblio.fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.example.biblio.R;
import com.example.biblio.helpers.LogHelper;
import com.example.biblio.helpers.ThemeHelper;
import com.mikepenz.aboutlibraries.LibsBuilder;

import java.util.Objects;

import static com.example.biblio.helpers.SharedPreferencesHelper.FEEDBOOKS_ENABLED_KEY;
import static com.example.biblio.helpers.SharedPreferencesHelper.LIBGEN_ENABLED_KEY;
import static com.example.biblio.helpers.SharedPreferencesHelper.STANDARD_EBOOKS_ENABLED_KEY;

public class SettingsFragment extends PreferenceFragmentCompat {
    public static final String TAG = "SettingsFragment";
    private SharedPreferences sharedPreferences;
    private final LogHelper logger = new LogHelper(getClass());


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

        Preference github = findPreference(getResources().getString(R.string.source_code_pref));
        if (github != null) {
            github.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/MatteoSalvino/Biblio-app"));
                startActivity(intent);
                return true;
            });
        }

        Preference dependencies = findPreference(getResources().getString(R.string.dependencies_pref));
        if (dependencies != null) {
            dependencies.setOnPreferenceClickListener(preference -> {
                new LibsBuilder()
                        .withAutoDetect(true)
                        .withActivityTitle(this.getResources().getString(R.string.open_source_libs))
                        .withAboutIconShown(true)
                        .withAboutVersionShown(true)
                        .start(getContext());
                return true;
            });
        }

        setEnabledListeners(R.string.libgen_enabled_pref, LIBGEN_ENABLED_KEY);
        setEnabledListeners(R.string.feedbooks_enabled_pref, FEEDBOOKS_ENABLED_KEY);
        setEnabledListeners(R.string.standard_ebooks_enabled_pref, STANDARD_EBOOKS_ENABLED_KEY);

        Preference external = findPreference(getResources().getString(R.string.external_readers_pref));
        external.setOnPreferenceClickListener(preference -> {
            try {
                String query = "ebook+reader";
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("http://play.google.com/store/search?q=%s&c=apps\n", query))));
            } catch (android.content.ActivityNotFoundException anfe) {
                logger.e(anfe.getMessage());
            }
            return true;
        });
    }

    private void setEnabledListeners(int resource, String key) {
        SwitchPreference switchPreference = findPreference(getResources().getString(resource));
        if (switchPreference != null) {
            switchPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean enabled = (boolean) newValue;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(key, enabled);
                editor.apply();
                return true;
            });
        }

    }
}
