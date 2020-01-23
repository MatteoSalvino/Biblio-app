package com.example.biblio.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.OnBackPressedCallback;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreference;

import com.example.biblio.R;
import com.example.biblio.helpers.LogHelper;
import com.example.biblio.helpers.ThemeHelper;
import com.mikepenz.aboutlibraries.LibsBuilder;

import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.util.Objects;

import static com.example.biblio.helpers.SharedPreferencesHelper.FEEDBOOKS_ENABLED_KEY;
import static com.example.biblio.helpers.SharedPreferencesHelper.LIBGEN_ENABLED_KEY;
import static com.example.biblio.helpers.SharedPreferencesHelper.LIBGEN_MAX_RESULTS_KEY;
import static com.example.biblio.helpers.SharedPreferencesHelper.LIBGEN_MIRROR_KEY;
import static com.example.biblio.helpers.SharedPreferencesHelper.LIBGEN_OVERRIDE_KEY;
import static com.example.biblio.helpers.SharedPreferencesHelper.STANDARD_EBOOKS_ENABLED_KEY;

public class SettingsFragment extends PreferenceFragmentCompat {
    public static final String TAG = "SettingsFragment";
    private final LogHelper logger = new LogHelper(getClass());
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = sharedPreferences.edit();

        SwitchPreference themePreference = findPreference("theme");
        if (themePreference != null) {
            themePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean themeOption = (boolean) newValue;
                ThemeHelper.applyTheme(themeOption);
                Objects.requireNonNull(getActivity()).recreate();
                return true;
            });
        }

        //Back pressed callback
        //SettingsFragment current = this;
        OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getFragmentManager().popBackStack();
            }
        };

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

        EditTextPreference libgen_mirror = findPreference(getResources().getString(R.string.libgen_mirror_pref));
        libgen_mirror.setOnPreferenceChangeListener((preference, newValue) -> {
            String mirror = newValue.toString();
            if (isValidMirror(mirror)) {
                editor.putString(LIBGEN_MIRROR_KEY, mirror);
                editor.apply();
            }
            return true;
        });

        SwitchPreference override_libgen = findPreference(getResources().getString(R.string.override_libgen_pref));
        override_libgen.setOnPreferenceChangeListener((preference, newValue) -> {
            editor.putBoolean(LIBGEN_OVERRIDE_KEY, (boolean) newValue);
            editor.apply();
            libgen_mirror.setEnabled((boolean) newValue);
            return true;
        });

        libgen_mirror.setEnabled(override_libgen.isChecked());

        SeekBarPreference libgen_max = findPreference(getResources().getString(R.string.libgen_max_pref));
        libgen_max.setOnPreferenceChangeListener((preference, newValue) -> {
            int val = ((int) newValue);
            int val_5 = val - (val % 5);
            new Handler().post(() -> libgen_max.setValue(val_5));
            editor.putInt(LIBGEN_MAX_RESULTS_KEY, val_5);
            return true;
        } );
    }

    private boolean isValidMirror(@NotNull String mirror) {
        try {
            logger.d(URI.create(mirror).toString());
            return true;
        } catch (IllegalArgumentException e) {
            logger.e(e.getMessage());
            return false;
        }
    }

    private void setEnabledListeners(int resource, String key) {
        SwitchPreference switchPreference = findPreference(getResources().getString(resource));
        if (switchPreference != null) {
            switchPreference.setOnPreferenceChangeListener((preference, enabled) -> {
                editor.putBoolean(key, (boolean) enabled);
                editor.apply();
                return true;
            });
        }
    }
}
