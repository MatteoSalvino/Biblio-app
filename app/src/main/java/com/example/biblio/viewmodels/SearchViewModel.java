package com.example.biblio.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lrusso96.simplebiblio.core.Ebook;
import lrusso96.simplebiblio.core.Provider;
import lrusso96.simplebiblio.core.SimpleBiblio;
import lrusso96.simplebiblio.core.SimpleBiblioBuilder;
import lrusso96.simplebiblio.core.providers.feedbooks.Feedbooks;
import lrusso96.simplebiblio.core.providers.libgen.LibraryGenesis;
import lrusso96.simplebiblio.core.providers.standardebooks.StandardEbooks;

public class SearchViewModel extends ViewModel {
    private List<Ebook> result;
    private MutableLiveData<List<Ebook>> ebooks;
    private Map<String, Boolean> enabledProviders;
    private final String LOG_TAG = getClass().getName();

    public SearchViewModel() {
        enabledProviders = new HashMap<>();
        enabledProviders.put(Provider.FEEDBOOKS, true);
        enabledProviders.put(Provider.LIBGEN, true);
        enabledProviders.put(Provider.STANDARD_EBOOKS, true);
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
            if (isEnabled(x.getProviderName()))
                ret.add(x);
        }
        ebooks.postValue(ret);
    }

    public void refreshData(String query) {
        new Thread(() -> {
            Log.d(LOG_TAG, "refreshing data");
            SimpleBiblio sb = new SimpleBiblioBuilder().build();
            List<Ebook> ret = sb.searchAll(query);
            Log.d(LOG_TAG, String.format("ret has size: %d", ret.size()));
            if (ret.size() > 0) {
                result = ret;
                applyFilters();
            }
        }).start();
    }

    public boolean isEnabled(String provider_name) {
        Boolean enabled = enabledProviders.get(provider_name);
        if (enabled == null)
            enabled = true;
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
}