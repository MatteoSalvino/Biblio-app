package com.example.biblio.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.example.biblio.helpers.LogHelper
import com.example.biblio.helpers.SharedPreferencesHelper.FEEDBOOKS_ENABLED_KEY
import com.example.biblio.helpers.SharedPreferencesHelper.LIBGEN_ENABLED_KEY
import com.example.biblio.helpers.SharedPreferencesHelper.LIBGEN_MAX_RESULTS_KEY
import com.example.biblio.helpers.SharedPreferencesHelper.LIBGEN_MIRROR_KEY
import com.example.biblio.helpers.SharedPreferencesHelper.LIBGEN_OVERRIDE_KEY
import com.example.biblio.helpers.SharedPreferencesHelper.STANDARD_EBOOKS_ENABLED_KEY
import com.example.biblio.helpers.SimpleBiblioHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import lrusso96.simplebiblio.core.Ebook
import lrusso96.simplebiblio.core.Provider
import lrusso96.simplebiblio.core.SimpleBiblio
import lrusso96.simplebiblio.core.SimpleBiblioBuilder
import lrusso96.simplebiblio.core.providers.feedbooks.Feedbooks
import lrusso96.simplebiblio.core.providers.feedbooks.FeedbooksBuilder
import lrusso96.simplebiblio.core.providers.libgen.LibraryGenesis
import lrusso96.simplebiblio.core.providers.libgen.LibraryGenesisBuilder
import lrusso96.simplebiblio.core.providers.standardebooks.StandardEbooks
import okhttp3.internal.toImmutableList
import java.net.URI
import java.util.*
import kotlin.collections.ArrayList

class SearchViewModel(application: Application?) : AndroidViewModel(application!!) {
    val ebooks: MutableLiveData<List<Ebook>> = MutableLiveData()
    private val filteredProviders: MutableMap<String, Boolean>
    private val filteredLanguages: MutableMap<String, Boolean>
    private val logger = LogHelper(javaClass)
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication<Application>().applicationContext)
    private var result: List<Ebook> = ArrayList()
    private var otherLanguage = true

    private fun applyFilters() {
        ebooks.postValue(result
                .filter { isProviderVisible(it.providerName) && isLanguageVisible(it.language) }
                .toImmutableList())
    }

    fun sortByTitle() {
        result = result.sortedBy { it.title }
        applyFilters()
    }

    fun sortByYear() {
        result = result.sortedBy { it.published }
        applyFilters()
    }

    //fixme: consider refactoring and move to repository
    private fun buildBiblio(): SimpleBiblio {
        val fixme = SimpleBiblioBuilder().build()
        val builder = SimpleBiblioBuilder()
        if (sharedPreferences.getBoolean(FEEDBOOKS_ENABLED_KEY, true)) builder.addProvider(FeedbooksBuilder(fixme).build())
        if (sharedPreferences.getBoolean(LIBGEN_ENABLED_KEY, true)) {
            val libgenBuilder = LibraryGenesisBuilder(fixme)
            if (sharedPreferences.getBoolean(LIBGEN_OVERRIDE_KEY, false)) {
                val mirror = sharedPreferences.getString(LIBGEN_MIRROR_KEY, "")
                if (!mirror.isNullOrBlank()) libgenBuilder.setMirror(URI.create(mirror))
            }
            libgenBuilder.setMaxResultsNumber(sharedPreferences.getInt(LIBGEN_MAX_RESULTS_KEY, 10))
            builder.addProvider(libgenBuilder.build())
        }
        if (sharedPreferences.getBoolean(STANDARD_EBOOKS_ENABLED_KEY, true)) builder.addProvider(StandardEbooks(fixme))
        return builder.build()
    }

    fun refreshData(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            logger.d("refreshing data")
            val ret = buildBiblio().searchAll(query)
            SimpleBiblioHelper.setLastSearchTS(getApplication<Application>().applicationContext)
            logger.d("ret has size: ${ret.size}")
            if (ret.size > 0) {
                result = ret
                applyFilters()
            }
        }
    }

    fun isProviderVisible(provider_name: String?): Boolean {
        val shouldShow = filteredProviders[provider_name]
        return shouldShow ?: true
    }

    fun isLanguageVisible(language: String?): Boolean {
        val lan = language?.toLowerCase(Locale.getDefault()) ?: return otherLanguage
        val filtered = filteredLanguages[lan]
        return filtered ?: otherLanguage
    }

    fun setProviderVisibility(provider: Class<out Provider?>, visible: Boolean) {
        filteredProviders[when (provider) {
            LibraryGenesis::class.java -> Provider.LIBGEN
            Feedbooks::class.java -> Provider.FEEDBOOKS
            StandardEbooks::class.java -> Provider.STANDARD_EBOOKS
            else -> {
                logger.e("unknown provider (maybe yous should add as option): ${provider.name}")
                ""
            }
        }] = visible
        applyFilters()
    }

    fun showEnglish(enabled: Boolean) {
        filteredLanguages["english"] = enabled
        filteredLanguages["en"] = enabled
        applyFilters()
    }

    fun showItalian(enabled: Boolean) {
        filteredLanguages["italian"] = enabled
        filteredLanguages["it"] = enabled
        applyFilters()
    }

    fun showOther(enabled: Boolean) {
        otherLanguage = enabled
        applyFilters()
    }

    fun showSpanish(enabled: Boolean) {
        filteredLanguages["spanish"] = enabled
        filteredLanguages["es"] = enabled
        applyFilters()
    }

    init {
        filteredProviders = HashMap()
        filteredProviders[Provider.FEEDBOOKS] = true
        filteredProviders[Provider.LIBGEN] = true
        filteredProviders[Provider.STANDARD_EBOOKS] = true
        //fixme: this is a temporary solution. Replace with Locale instead
        filteredLanguages = HashMap()
        filteredLanguages["italian"] = true
        filteredLanguages["it"] = true
        filteredLanguages["english"] = true
        filteredLanguages["en"] = true
        filteredLanguages["spanish"] = true
        filteredLanguages["es"] = true
    }
}