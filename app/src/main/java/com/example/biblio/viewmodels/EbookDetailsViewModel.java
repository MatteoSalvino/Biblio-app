package com.example.biblio.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.biblio.helpers.LogHelper;

import org.jetbrains.annotations.NotNull;

import lrusso96.simplebiblio.core.Ebook;

public class EbookDetailsViewModel extends ViewModel {
    private final LogHelper logger = new LogHelper(getClass());
    private MutableLiveData<Ebook> ebook;

    public EbookDetailsViewModel() {
        if (ebook == null)
            ebook = new MutableLiveData<>();
    }

    public LiveData<Ebook> getEbook() {
        return ebook;
    }

    public void setEbook(@NotNull Ebook ebook) {
        logger.d(String.format("posting new ebook: %s", ebook.getTitle()));
        this.ebook.postValue(ebook);
    }
}