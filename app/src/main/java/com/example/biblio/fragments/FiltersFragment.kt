package com.example.biblio.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.biblio.databinding.FragmentFiltersBinding
import com.example.biblio.helpers.XFragment
import com.example.biblio.viewmodels.SearchViewModel
import lrusso96.simplebiblio.core.Provider
import lrusso96.simplebiblio.core.providers.feedbooks.Feedbooks
import lrusso96.simplebiblio.core.providers.libgen.LibraryGenesis

class FiltersFragment : XFragment(FiltersFragment::class.java) {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentFiltersBinding.inflate(inflater, container, false)
        val model = ViewModelProvider(activity!!).get(SearchViewModel::class.java)
        binding.filtersBackBtn.setOnClickListener { popBackStackImmediate() }
        binding.feedbooksCb.isChecked = model.isProviderVisible(Provider.FEEDBOOKS)
        binding.feedbooksCb.setOnCheckedChangeListener { _, isChecked -> model.setProviderVisibility(Feedbooks::class.java, isChecked) }
        binding.libgenCb.isChecked = model.isProviderVisible(Provider.LIBGEN)
        binding.libgenCb.setOnCheckedChangeListener { _, isChecked -> model.setProviderVisibility(LibraryGenesis::class.java, isChecked) }
        binding.englishCb.isChecked = model.isLanguageVisible("english")
        binding.englishCb.setOnCheckedChangeListener { _, isChecked -> model.showEnglish(isChecked) }
        binding.italianCb.isChecked = model.isLanguageVisible("italian")
        binding.italianCb.setOnCheckedChangeListener { _, isChecked -> model.showItalian(isChecked) }
        binding.otherCb.isChecked = model.isLanguageVisible("other")
        binding.otherCb.setOnCheckedChangeListener { _, isChecked -> model.showOther(isChecked) }
        binding.spanishCb.isChecked = model.isLanguageVisible("spanish")
        binding.spanishCb.setOnCheckedChangeListener { _, isChecked -> model.showSpanish(isChecked) }
        binding.filtersResetBtn.setOnClickListener {
            binding.libgenCb.isChecked = true
            binding.feedbooksCb.isChecked = true
            binding.italianCb.isChecked = true
            binding.englishCb.isChecked = true
            binding.otherCb.isChecked = true
            binding.spanishCb.isChecked = true
        }
        return binding.root
    }
}