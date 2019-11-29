package com.example.biblio.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import lrusso96.simplebiblio.core.Ebook;
import lrusso96.simplebiblio.core.SimpleBiblio;
import lrusso96.simplebiblio.core.SimpleBiblioBuilder;

public abstract class SwipeEbooksViewModel extends ViewModel {

    private MutableLiveData<List<Ebook>> ebooks;

    public LiveData<List<Ebook>> getEbooks() {
        if (ebooks == null) {
            ebooks = new MutableLiveData<>();
            refreshData();
        }
        return ebooks;
    }

    public void refreshData() {
        new Thread(() -> {
            Log.d(getClass().getName(), "refreshing data");
            SimpleBiblio sb = new SimpleBiblioBuilder().build();
            List<Ebook> ret = doRefresh(sb);
            Log.d(getClass().getName(), String.format("ret has size: %d", ret.size()));
            if (ret.size() > 0)
                ebooks.postValue(ret);
        }).start();
    }

    protected abstract List<Ebook> doRefresh(SimpleBiblio sb);
}