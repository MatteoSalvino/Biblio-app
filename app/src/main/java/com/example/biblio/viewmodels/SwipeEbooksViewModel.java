package com.example.biblio.viewmodels;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;

import com.example.biblio.helpers.LogHelper;

import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.util.List;
import java.util.Locale;

import lrusso96.simplebiblio.core.Ebook;
import lrusso96.simplebiblio.core.SimpleBiblio;
import lrusso96.simplebiblio.core.SimpleBiblioBuilder;
import lrusso96.simplebiblio.core.providers.feedbooks.FeedbooksBuilder;
import lrusso96.simplebiblio.core.providers.libgen.LibraryGenesisBuilder;
import lrusso96.simplebiblio.core.providers.standardebooks.StandardEbooks;

import static com.example.biblio.helpers.SharedPreferencesHelper.FEEDBOOKS_ENABLED_KEY;
import static com.example.biblio.helpers.SharedPreferencesHelper.LIBGEN_ENABLED_KEY;
import static com.example.biblio.helpers.SharedPreferencesHelper.LIBGEN_MAX_RESULTS_KEY;
import static com.example.biblio.helpers.SharedPreferencesHelper.LIBGEN_MIRROR_KEY;
import static com.example.biblio.helpers.SharedPreferencesHelper.LIBGEN_OVERRIDE_KEY;
import static com.example.biblio.helpers.SharedPreferencesHelper.STANDARD_EBOOKS_ENABLED_KEY;

public abstract class SwipeEbooksViewModel extends AndroidViewModel {
    private final LogHelper logger = new LogHelper(getClass());
    private MutableLiveData<List<Ebook>> ebooks;
    private SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication().getApplicationContext());


    public SwipeEbooksViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Ebook>> getEbooks() {
        if (ebooks == null) {
            ebooks = new MutableLiveData<>();
            refreshData();
        }
        return ebooks;
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

    public void refreshData() {
        new Thread(() -> {
            logger.d("refreshing data");
            SimpleBiblio sb = buildBiblio();
            List<Ebook> ret = doRefresh(sb);
            logger.d(String.format(Locale.getDefault(), "ret has size: %d", ret.size()));
            ebooks.postValue(ret);
        }).start();
    }

    protected abstract List<Ebook> doRefresh(SimpleBiblio sb);
}