package com.example.biblio.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.biblio.helpers.LogHelper;

import java.util.List;
import java.util.Locale;

import lrusso96.simplebiblio.core.Ebook;
import lrusso96.simplebiblio.core.SimpleBiblio;
import lrusso96.simplebiblio.core.SimpleBiblioBuilder;

public abstract class SwipeEbooksViewModel extends AndroidViewModel {
    private final LogHelper logger = new LogHelper(getClass());
    private MutableLiveData<List<Ebook>> ebooks;
    private SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication().getApplicationContext());

    public LiveData<List<Ebook>> getEbooks() {
        if (ebooks == null) {
            ebooks = new MutableLiveData<>();
            refreshData();
        }
        return ebooks;
    }

    public void refreshData() {
        new Thread(() -> {
            logger.d("refreshing data");
            SimpleBiblio sb = new SimpleBiblioBuilder().build();
            List<Ebook> ret = doRefresh(sb);
            logger.d(String.format(Locale.getDefault(), "ret has size: %d", ret.size()));
            if (ret.size() > 0)
                ebooks.postValue(ret);
        }).start();
    }

    protected abstract List<Ebook> doRefresh(SimpleBiblio sb);
}