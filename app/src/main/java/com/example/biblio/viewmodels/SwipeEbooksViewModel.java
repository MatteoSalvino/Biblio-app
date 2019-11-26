package com.example.biblio.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import lrusso96.simplebiblio.core.Ebook;
import lrusso96.simplebiblio.core.SimpleBiblio;
import lrusso96.simplebiblio.core.SimpleBiblioBuilder;

public abstract class SwipeEbooksViewModel extends ViewModel {

    private MutableLiveData<List<Ebook>> ebooks;

    //todo: add a shared view model to handle filters

    public LiveData<List<Ebook>> getEbooks() {
        if (ebooks == null) {
            ebooks = new MutableLiveData<>();
            refreshData();
        }
        return ebooks;
    }

    public void refreshData() {
        new Thread(() -> {
            SimpleBiblio sb = new SimpleBiblioBuilder().build();
            List<Ebook> ret = doRefresh(sb);
            if(ret.size() > 0)
                ebooks.postValue(ret);
        }).start();
    }

    protected abstract List<Ebook> doRefresh(SimpleBiblio sb);

}