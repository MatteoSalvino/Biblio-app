package com.example.biblio.fragments

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.biblio.adapters.EbooksAdapter
import com.example.biblio.databinding.FragmentSearchBinding
import com.example.biblio.helpers.SharedPreferencesHelper.EAN_ENABLED_KEY
import com.example.biblio.helpers.XFragment
import com.example.biblio.viewmodels.EbookDetailsViewModel
import com.example.biblio.viewmodels.SearchViewModel
import com.google.zxing.integration.android.IntentIntegrator
import com.jakewharton.rxbinding.widget.RxTextView
import lrusso96.simplebiblio.core.Ebook
import java.util.concurrent.TimeUnit

class SearchFragment : XFragment(SearchFragment::class.java), EbooksAdapter.OnItemListener {
    private lateinit var ebooks: List<Ebook>
    private lateinit var binding: FragmentSearchBinding
    private lateinit var ebookModel: EbookDetailsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentSearchBinding.inflate(inflater, container, false)
        val model = ViewModelProvider(activity!!).get(SearchViewModel::class.java)
        ebookModel = ViewModelProvider(activity!!).get(EbookDetailsViewModel::class.java)
        binding.filtersBtn.setOnClickListener { moveTo(FiltersFragment()) }
        binding.recyclerView.setHasFixedSize(true)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        binding.recyclerView.layoutManager = mLayoutManager
        RxTextView.textChanges(binding.searchBar.searchEditText)
                .debounce(750, TimeUnit.MILLISECONDS)
                .subscribe {
                    logger.d("Stopped typing")
                    val query = binding.searchBar.searchEditText.text.toString()
                    if (query.length >= 5) model.refreshData(query)
                    else logger.d("Query too short: ${query.length} chars inserted")
                }
        val searchObserver = Observer<List<Ebook>> { ebooks: List<Ebook> ->
            this.ebooks = ebooks
            val mAdapter = EbooksAdapter(ebooks, this, context!!)
            binding.recyclerView.adapter = mAdapter
        }
        model.ebooks.observe(viewLifecycleOwner, searchObserver)
        binding.sortBtn.setOnClickListener {
            val alertDialog = AlertDialog.Builder(context!!)
            alertDialog.setTitle("Set custom sorting")
            val items = arrayOf("Title", "Year")
            alertDialog.setSingleChoiceItems(items, -1) { dialog: DialogInterface, which: Int ->
                when (which) {
                    0 -> model.sortByTitle()
                    1 -> model.sortByYear()
                }
                dialog.dismiss()
            }
            val alert = alertDialog.create()
            alert.setCanceledOnTouchOutside(true)
            alert.show()
        }
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        binding.scannerBtn.setOnClickListener { IntentIntegrator.forSupportFragment(this).initiateScan() }
        if (sharedPreferences.getBoolean(EAN_ENABLED_KEY, false)) binding.scannerBtn.visibility = View.VISIBLE
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        val code = intentResult.contents
        if (code != null) {
            binding.searchBar.text = code
            binding.searchBar.enableSearch()
        }
    }

    override fun onItemClick(position: Int) {
        ebookModel.updateEbook(ebooks[position])
        moveTo(EbookDetailsFragment())
    }
}