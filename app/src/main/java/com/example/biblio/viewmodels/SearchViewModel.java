package com.example.biblio.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.biblio.helpers.LogHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

import lrusso96.simplebiblio.core.Ebook;
import lrusso96.simplebiblio.core.Provider;
import lrusso96.simplebiblio.core.SimpleBiblio;
import lrusso96.simplebiblio.core.SimpleBiblioBuilder;
import lrusso96.simplebiblio.core.providers.feedbooks.Feedbooks;
import lrusso96.simplebiblio.core.providers.libgen.LibraryGenesis;
import lrusso96.simplebiblio.core.providers.standardebooks.StandardEbooks;

public class SearchViewModel extends ViewModel {
    private final Map<String, Boolean> enabledProviders;
    private final Map<String, Boolean> enabledLanguages;
    private final LogHelper logger = new LogHelper(getClass());
    private List<Ebook> result;
    private MutableLiveData<List<Ebook>> ebooks;

    public SearchViewModel() {
        enabledProviders = new HashMap<>();
        enabledProviders.put(Provider.FEEDBOOKS, true);
        enabledProviders.put(Provider.LIBGEN, true);
        enabledProviders.put(Provider.STANDARD_EBOOKS, true);

        //fixme: this is a temporary solution. Replace with Locale instead
        enabledLanguages = new HashMap<>();
        enabledLanguages.put("italian", true);
        enabledLanguages.put("it", true);
        enabledLanguages.put("english", true);
        enabledLanguages.put("en", true);
        enabledLanguages.put("spanish", true);
        enabledLanguages.put("es", true);
        enabledLanguages.put("french", true);
        enabledLanguages.put("fr", true);
    }

    public LiveData<List<Ebook>> getEbooks() {
        if (ebooks == null) {
            ebooks = new MutableLiveData<>();
            result = new ArrayList<>();
        }
        return ebooks;
    }

    private void applyFilters() {
        if (result == null || result.isEmpty())
            return;
        List<Ebook> ret = new ArrayList<>();
        for (Ebook x : result) {
            if (isProviderEnabled(x.getProviderName()) && isLanguageEnabled(x.getLanguage()))
                ret.add(x);
        }
        ebooks.postValue(ret);
    }

    public void sortByTitle() {
        if (result == null)
            return;
        Collections.sort(result, (e1, e2) -> {
            if (e1.getTitle() == null)
                return 1;
            if (e2.getTitle() == null)
                return -1;
            return e1.getTitle().compareTo(e2.getTitle());
        });
        ebooks.postValue(result);
    }

    public void sortByYear() {
        if (result == null)
            return;
        Collections.sort(result, (e1, e2) -> {
            if (e1.getPublished() == null)
                return 1;
            if (e2.getPublished() == null)
                return -1;
            return e1.getPublished().compareTo(e2.getPublished());
        });
        ebooks.postValue(result);
    }


    public void refreshData(String query) {
        new Thread(() -> {
            logger.d("refreshing data");
            SimpleBiblio sb = new SimpleBiblioBuilder().build();
            List<Ebook> ret = sb.searchAll(query);
            logger.d(String.format(Locale.getDefault(), "ret has size: %d", ret.size()));
            if (ret.size() > 0) {
                result = ret;
                applyFilters();
            }
        }).start();
    }

    public boolean isProviderEnabled(String provider_name) {
        Boolean enabled = enabledProviders.get(provider_name);
        if (enabled == null)
            enabled = true;
        return enabled;
    }

    public boolean isLanguageEnabled(@Nullable String language) {
        if (language == null)
            return false;
        language = language.toLowerCase();
        Boolean enabled = enabledLanguages.get(language);
        if (enabled == null)
            enabled = false;
        return enabled;
    }

    public void enableProvider(Class<? extends Provider> provider, boolean enabled) {
        if (provider == LibraryGenesis.class) {
            enabledProviders.put(Provider.LIBGEN, enabled);
            applyFilters();
        } else if (provider == Feedbooks.class) {
            enabledProviders.put(Provider.FEEDBOOKS, enabled);
            applyFilters();
        } else if (provider == StandardEbooks.class) {
            enabledProviders.put(Provider.STANDARD_EBOOKS, enabled);
            applyFilters();
        }
    }

    public void enableEnglish(boolean enabled) {
        enabledLanguages.put("english", enabled);
        enabledLanguages.put("en", enabled);
        applyFilters();
    }

    public void enableItalian(boolean enabled) {
        enabledLanguages.put("italian", enabled);
        enabledLanguages.put("it", enabled);
        applyFilters();
    }

    public void enableFrench(boolean enabled) {
        enabledLanguages.put("french", enabled);
        enabledLanguages.put("fr", enabled);
        applyFilters();
    }

    public void enableSpanish(boolean enabled) {
        enabledLanguages.put("spanish", enabled);
        enabledLanguages.put("es", enabled);
        applyFilters();
    }


}