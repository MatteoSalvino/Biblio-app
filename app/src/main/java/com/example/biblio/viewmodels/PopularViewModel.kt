package com.example.biblio.viewmodels

import android.app.Application
import lrusso96.simplebiblio.core.Ebook
import lrusso96.simplebiblio.core.SimpleBiblio

class PopularViewModel(application: Application) : SwipeEbooksViewModel(application) {
    override suspend fun doRefresh(sb: SimpleBiblio): List<Ebook> = sb.allPopular
}