package com.example.biblio.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.example.biblio.helpers.LogHelper
import com.example.biblio.helpers.SharedPreferencesHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lrusso96.simplebiblio.core.Ebook
import lrusso96.simplebiblio.core.SimpleBiblio
import lrusso96.simplebiblio.core.SimpleBiblioBuilder
import lrusso96.simplebiblio.core.providers.feedbooks.FeedbooksBuilder
import lrusso96.simplebiblio.core.providers.libgen.LibraryGenesisBuilder
import lrusso96.simplebiblio.core.providers.standardebooks.StandardEbooks
import org.apache.commons.lang3.StringUtils
import java.net.URI
import java.util.*

abstract class SwipeEbooksViewModel(application: Application) : AndroidViewModel(application) {
    private val logger = LogHelper(javaClass)
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication<Application>().applicationContext)
    private val ebooks: MutableLiveData<List<Ebook>> = MutableLiveData()

    fun getEbooks(): LiveData<List<Ebook>> {
        if (ebooks.value.isNullOrEmpty())
            refreshData()
        return ebooks
    }

    //fixme: consider refactoring and move to repository
    private fun buildBiblio(): SimpleBiblio {
        val fixme = SimpleBiblioBuilder().build()
        val builder = SimpleBiblioBuilder()
        if (sharedPreferences.getBoolean(SharedPreferencesHelper.FEEDBOOKS_ENABLED_KEY, true)) builder.addProvider(FeedbooksBuilder(fixme).build())
        if (sharedPreferences.getBoolean(SharedPreferencesHelper.LIBGEN_ENABLED_KEY, true)) {
            val libgen_builder = LibraryGenesisBuilder(fixme)
            if (sharedPreferences.getBoolean(SharedPreferencesHelper.LIBGEN_OVERRIDE_KEY, false)) {
                val mirror = sharedPreferences.getString(SharedPreferencesHelper.LIBGEN_MIRROR_KEY, "")
                if (!StringUtils.isEmpty(mirror)) libgen_builder.setMirror(URI.create(mirror))
            }
            libgen_builder.setMaxResultsNumber(sharedPreferences.getInt(SharedPreferencesHelper.LIBGEN_MAX_RESULTS_KEY, 10))
            builder.addProvider(libgen_builder.build())
        }
        if (sharedPreferences.getBoolean(SharedPreferencesHelper.STANDARD_EBOOKS_ENABLED_KEY, true)) builder.addProvider(StandardEbooks(fixme))
        return builder.build()
    }

    fun refreshData() {
        viewModelScope.launch(Dispatchers.IO) {
            logger.d("refreshing data")
            val ret = doRefresh(buildBiblio())
            logger.d("ret has size: ${ret.size}")
            ebooks.postValue(ret)
        }
    }

    protected abstract suspend fun doRefresh(sb: SimpleBiblio): List<Ebook>
}