package com.example.biblio.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;

import java.util.List;

import lrusso96.simplebiblio.core.Ebook;
import lrusso96.simplebiblio.core.SimpleBiblio;

public class RecentViewModel extends SwipeEbooksViewModel {

    public RecentViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    protected List<Ebook> doRefresh(SimpleBiblio sb) {
        return sb.getAllRecent();
    }
}