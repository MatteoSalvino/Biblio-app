package com.example.biblio.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import lrusso96.simplebiblio.core.Ebook;
import lrusso96.simplebiblio.core.SimpleBiblio;
import lrusso96.simplebiblio.core.SimpleBiblioBuilder;

public class RecentViewModel extends ViewModel {

    private MutableLiveData<List<Ebook>> ebooks;

    public LiveData<List<Ebook>> getEbooks() {
        if (ebooks == null) {
            ebooks = new MutableLiveData<>();
            loadData();
        }
        return ebooks;
    }

    private void loadData() {
        new Thread(() -> {
            SimpleBiblio sb = new SimpleBiblioBuilder().build();
            ebooks.postValue(sb.getAllRecent());
        }).start();
    }
}