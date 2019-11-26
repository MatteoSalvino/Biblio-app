package com.example.biblio.viewmodels;

import java.util.List;

import lrusso96.simplebiblio.core.Ebook;
import lrusso96.simplebiblio.core.SimpleBiblio;

public class PopularViewModel extends SwipeEbooksViewModel {

    @Override
    protected List<Ebook> doRefresh(SimpleBiblio sb) {
        return sb.getAllPopular();
    }
}