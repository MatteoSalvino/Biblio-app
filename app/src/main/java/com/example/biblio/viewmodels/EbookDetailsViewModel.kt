package com.example.biblio.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.biblio.helpers.LogHelper
import lrusso96.simplebiblio.core.Ebook

class EbookDetailsViewModel : ViewModel() {
    private val logger = LogHelper(javaClass)
    val ebook: MutableLiveData<Ebook> = MutableLiveData()

    fun updateEbook(ebook: Ebook) {
        logger.d("posting new ebook: ${ebook.title}")
        this.ebook.postValue(ebook)
    }
}