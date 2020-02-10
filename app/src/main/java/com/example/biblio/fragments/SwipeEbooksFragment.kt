package com.example.biblio.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.biblio.adapters.EbooksAdapter
import com.example.biblio.databinding.FragmentSwipeEbooksBinding
import com.example.biblio.helpers.XFragment
import com.example.biblio.viewmodels.EbookDetailsViewModel
import com.example.biblio.viewmodels.SwipeEbooksViewModel
import lrusso96.simplebiblio.core.Ebook
import java.util.*

open class SwipeEbooksFragment internal constructor(clazz: Class<*>?, private val mSwipeModel: Class<out SwipeEbooksViewModel>) : XFragment(clazz!!), EbooksAdapter.OnItemListener {
    private lateinit var mEbooksListener: EbooksAdapter.OnItemListener
    private lateinit var mEbooks: ArrayList<Ebook>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val binding = FragmentSwipeEbooksBinding.inflate(inflater, container, false)
        val mLayoutManager = LinearLayoutManager(xContext)
        binding.ebooksRv.layoutManager = mLayoutManager
        binding.ebooksRv.setHasFixedSize(true)
        //binding.swipeContainer.setColorSchemeResources(android.R.color.);
        mEbooksListener = this
        val model = ViewModelProvider(activity!!)[mSwipeModel]
        binding.swipeContainer.setOnRefreshListener { model.refreshData() }
        binding.swipeContainer.isRefreshing = true
        val swipeObserver = Observer<List<Ebook>> { ebooks: List<Ebook> ->
            binding.swipeContainer.isRefreshing = true
            logger.d("swiping")
            mEbooks = ebooks as ArrayList<Ebook>
            val mAdapter = EbooksAdapter(mEbooks, mEbooksListener, xContext)
            binding.ebooksRv.adapter = mAdapter
            binding.swipeContainer.isRefreshing = false
        }
        model.getEbooks().observe(viewLifecycleOwner, swipeObserver)
        return binding.root
    }

    override fun onItemClick(position: Int) {
        val model = ViewModelProvider(activity!!).get(EbookDetailsViewModel::class.java)
        model.updateEbook(mEbooks[position])
        moveTo(EbookDetailsFragment())
    }
}