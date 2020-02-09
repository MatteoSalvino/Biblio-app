package com.example.biblio.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.core.content.edit
import androidx.preference.*
import com.example.biblio.R
import com.example.biblio.helpers.LogHelper
import com.example.biblio.helpers.SharedPreferencesHelper.EAN_ENABLED_KEY
import com.example.biblio.helpers.SharedPreferencesHelper.FEEDBOOKS_ENABLED_KEY
import com.example.biblio.helpers.SharedPreferencesHelper.LIBGEN_ENABLED_KEY
import com.example.biblio.helpers.SharedPreferencesHelper.LIBGEN_MAX_RESULTS_KEY
import com.example.biblio.helpers.SharedPreferencesHelper.LIBGEN_MIRROR_KEY
import com.example.biblio.helpers.SharedPreferencesHelper.LIBGEN_OVERRIDE_KEY
import com.example.biblio.helpers.SharedPreferencesHelper.STANDARD_EBOOKS_ENABLED_KEY
import com.example.biblio.helpers.ThemeHelper.applyTheme
import com.mikepenz.aboutlibraries.LibsBuilder
import java.net.URI

class SettingsFragment : PreferenceFragmentCompat() {
    private val logger = LogHelper(javaClass)
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        val theme = findPreference<SwitchPreference>(resources.getString(R.string.theme_pref))
        theme?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _: Preference?, newValue: Any ->
            applyTheme(newValue as Boolean)
            activity?.recreate()
            true
        }

        val github = findPreference<Preference>(resources.getString(R.string.source_code_pref))
        github?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/MatteoSalvino/Biblio-app"))
            startActivity(intent)
            true
        }

        val dependencies = findPreference<Preference>(resources.getString(R.string.dependencies_pref))
        dependencies?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            LibsBuilder()
                    .withAutoDetect(true)
                    .withActivityTitle(this.resources.getString(R.string.open_source_libs))
                    .withAboutIconShown(true)
                    .withAboutVersionShown(true)
                    .start(context)
            true
        }

        setEnabledListeners(R.string.libgen_enabled_pref, LIBGEN_ENABLED_KEY)
        setEnabledListeners(R.string.feedbooks_enabled_pref, FEEDBOOKS_ENABLED_KEY)
        setEnabledListeners(R.string.standard_ebooks_enabled_pref, STANDARD_EBOOKS_ENABLED_KEY)

        val external = findPreference<Preference>(resources.getString(R.string.external_readers_pref))
        external?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            try {
                val query = "ebook+reader"
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(String.format("http://play.google.com/store/search?q=%s&c=apps\n", query))))
            } catch (anfException: ActivityNotFoundException) {
                logger.e(anfException.message)
            }
            true
        }

        val libgenMirror = findPreference<EditTextPreference>(resources.getString(R.string.libgen_mirror_pref))
        libgenMirror?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _: Preference?, newValue: Any ->
            val mirror = "$newValue"
            if (isValidMirror(mirror))
                sharedPreferences.edit { putString(LIBGEN_MIRROR_KEY, mirror) }
            true
        }

        val overrideLibgen = findPreference<SwitchPreference>(resources.getString(R.string.override_libgen_pref))
        overrideLibgen!!.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _: Preference?, newValue: Any ->
            if (newValue is Boolean) {
                sharedPreferences.edit { putBoolean(LIBGEN_OVERRIDE_KEY, newValue) }
                libgenMirror?.isEnabled = newValue
                true
            } else false
        }
        libgenMirror?.isEnabled = overrideLibgen.isChecked

        val libgenMax = findPreference<SeekBarPreference>(resources.getString(R.string.libgen_max_pref))
        libgenMax!!.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _: Preference?, newValue: Any ->
            val value = newValue as Int
            val value5 = value - value % 5
            Handler().post { libgenMax.value = value5 }
            sharedPreferences.edit { putInt(LIBGEN_MAX_RESULTS_KEY, value5) }
            true
        }

        val eanScanning = findPreference<SwitchPreference>(resources.getString(R.string.EAN_pref))
        eanScanning!!.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _: Preference?, newValue: Any ->
            sharedPreferences.edit { putBoolean(EAN_ENABLED_KEY, newValue as Boolean) }
            true
        }
    }

    private fun isValidMirror(mirror: String): Boolean {
        return try {
            logger.d("${URI.create(mirror)}")
            true
        } catch (e: IllegalArgumentException) {
            logger.e(e.message)
            false
        }
    }

    private fun setEnabledListeners(resource: Int, key: String) {
        val switchPreference = findPreference<SwitchPreference>(resources.getString(resource))
        switchPreference?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _: Preference?, enabled: Any ->
            sharedPreferences.edit { putBoolean(key, enabled as Boolean) }
            true
        }
    }

    companion object {
        const val TAG = "SettingsFragment"
    }
}