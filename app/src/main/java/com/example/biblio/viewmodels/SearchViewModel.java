package com.example.biblio.viewmodels;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;

import com.example.biblio.helpers.LogHelper;

import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import lrusso96.simplebiblio.core.providers.feedbooks.FeedbooksBuilder;
import lrusso96.simplebiblio.core.providers.libgen.LibraryGenesis;
import lrusso96.simplebiblio.core.providers.libgen.LibraryGenesisBuilder;
import lrusso96.simplebiblio.core.providers.standardebooks.StandardEbooks;

import static com.example.biblio.helpers.SharedPreferencesHelper.FEEDBOOKS_ENABLED_KEY;
import static com.example.biblio.helpers.SharedPreferencesHelper.LAST_SEARCH_TS_KEY;
import static com.example.biblio.helpers.SharedPreferencesHelper.LIBGEN_ENABLED_KEY;
import static com.example.biblio.helpers.SharedPreferencesHelper.LIBGEN_MAX_RESULTS_KEY;
import static com.example.biblio.helpers.SharedPreferencesHelper.LIBGEN_MIRROR_KEY;
import static com.example.biblio.helpers.SharedPreferencesHelper.LIBGEN_OVERRIDE_KEY;
import static com.example.biblio.helpers.SharedPreferencesHelper.STANDARD_EBOOKS_ENABLED_KEY;

public class SearchViewModel extends AndroidViewModel {
    private final Map<String, Boolean> filteredProviders;
    private final Map<String, Boolean> filteredLanguages;
    private final LogHelper logger = new LogHelper(getClass());
    private final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication().getApplicationContext());
    private List<Ebook> result;
    private List<Ebook> current;
    private MutableLiveData<List<Ebook>> ebooks;

    public SearchViewModel(Application application) {
        super(application);
        filteredProviders = new HashMap<>();
        filteredProviders.put(Provider.FEEDBOOKS, true);
        filteredProviders.put(Provider.LIBGEN, true);
        filteredProviders.put(Provider.STANDARD_EBOOKS, true);

        //fixme: this is a temporary solution. Replace with Locale instead
        filteredLanguages = new HashMap<>();
        filteredLanguages.put("italian", true);
        filteredLanguages.put("it", true);
        filteredLanguages.put("english", true);
        filteredLanguages.put("en", true);
        filteredLanguages.put("spanish", true);
        filteredLanguages.put("es", true);
        filteredLanguages.put("french", true);
        filteredLanguages.put("fr", true);
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
        current = new ArrayList<>();
        for (Ebook x : result) {
            if (isProviderVisible(x.getProviderName()) && isLanguageVisible(x.getLanguage()))
                current.add(x);
        }
        ebooks.postValue(current);
    }

    public void sortByTitle() {
        if (current == null || current.isEmpty())
            return;
        Collections.sort(current, (e1, e2) -> {
            if (e1.getTitle() == null)
                return 1;
            if (e2.getTitle() == null)
                return -1;
            return e1.getTitle().compareTo(e2.getTitle());
        });
        ebooks.postValue(current);
    }

    public void sortByYear() {
        if (current == null || current.isEmpty())
            return;
        Collections.sort(current, (e1, e2) -> {
            if (e1.getPublished() == null)
                return 1;
            if (e2.getPublished() == null)
                return -1;
            return e1.getPublished().compareTo(e2.getPublished());
        });
        ebooks.postValue(current);
    }

    //fixme: consider refactoring and move to repository
    private SimpleBiblio buildBiblio() {
        SimpleBiblio fixme = new SimpleBiblioBuilder().build();
        SimpleBiblioBuilder builder = new SimpleBiblioBuilder();
        if (sharedPreferences.getBoolean(FEEDBOOKS_ENABLED_KEY, true))
            builder.addProvider(new FeedbooksBuilder(fixme).build());
        if (sharedPreferences.getBoolean(LIBGEN_ENABLED_KEY, true)) {
            LibraryGenesisBuilder libgen_builder = new LibraryGenesisBuilder(fixme);
            if (sharedPreferences.getBoolean(LIBGEN_OVERRIDE_KEY, false)) {
                String mirror = sharedPreferences.getString(LIBGEN_MIRROR_KEY, "");
                if (!StringUtils.isEmpty(mirror))
                    libgen_builder.setMirror(URI.create(mirror));
            }
            libgen_builder.setMaxResultsNumber(sharedPreferences.getInt(LIBGEN_MAX_RESULTS_KEY, 10));
            builder.addProvider(libgen_builder.build());
        }
        if (sharedPreferences.getBoolean(STANDARD_EBOOKS_ENABLED_KEY, true))
            builder.addProvider(new StandardEbooks(fixme));
        return builder.build();
    }

    public void refreshData(String query) {
        new Thread(() -> {
            logger.d("refreshing data");
            SimpleBiblio sb = buildBiblio();
            List<Ebook> ret = sb.searchAll(query);

            String timestamp = new SimpleDateFormat("dd-MM-yyyy,HH:mm").format(new Date());
            logger.d(timestamp);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(LAST_SEARCH_TS_KEY, timestamp).apply();

            logger.d(String.format(Locale.getDefault(), "ret has size: %d", ret.size()));
            if (ret.size() > 0) {
                result = ret;
                applyFilters();
            }
        }).start();
    }

    public boolean isProviderVisible(String provider_name) {
        Boolean shouldShow = filteredProviders.get(provider_name);
        if (shouldShow == null)
            shouldShow = true;
        return shouldShow;
    }

    public boolean isLanguageVisible(@Nullable String language) {
        if (language == null)
            return false;
        language = language.toLowerCase();
        Boolean filtered = filteredLanguages.get(language);
        if (filtered == null)
            filtered = false;
        return filtered;
    }

    public void setProviderVisibility(Class<? extends Provider> provider, boolean visible) {
        if (provider == LibraryGenesis.class) {
            filteredProviders.put(Provider.LIBGEN, visible);
            applyFilters();
        } else if (provider == Feedbooks.class) {
            filteredProviders.put(Provider.FEEDBOOKS, visible);
            applyFilters();
        } else if (provider == StandardEbooks.class) {
            filteredProviders.put(Provider.STANDARD_EBOOKS, visible);
            applyFilters();
        }
    }

    public void showEnglish(boolean enabled) {
        filteredLanguages.put("english", enabled);
        filteredLanguages.put("en", enabled);
        applyFilters();
    }

    public void showItalian(boolean enabled) {
        filteredLanguages.put("italian", enabled);
        filteredLanguages.put("it", enabled);
        applyFilters();
    }

    public void showFrench(boolean enabled) {
        filteredLanguages.put("french", enabled);
        filteredLanguages.put("fr", enabled);
        applyFilters();
    }

    public void showSpanish(boolean enabled) {
        filteredLanguages.put("spanish", enabled);
        filteredLanguages.put("es", enabled);
        applyFilters();
    }
}