package com.example.biblio.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.biblio.adapters.EbooksAdapter;
import com.example.biblio.databinding.SwipeEbooksRvFragmentBinding;
import com.example.biblio.helpers.XFragment;
import com.example.biblio.viewmodels.EbookDetailsViewModel;
import com.example.biblio.viewmodels.SwipeEbooksViewModel;

import java.util.ArrayList;
import java.util.List;

import lrusso96.simplebiblio.core.Ebook;

public class SwipeEbooksFragment extends XFragment implements EbooksAdapter.OnItemListener {
    private final Class<? extends SwipeEbooksViewModel> mSwipeModel;
    private EbooksAdapter.OnItemListener mEbooksListener;
    private ArrayList<Ebook> mEbooks;
    private SwipeEbooksRvFragmentBinding binding;

    SwipeEbooksFragment(Class clazz, Class<? extends SwipeEbooksViewModel> model) {
        super(clazz);
        mSwipeModel = model;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = SwipeEbooksRvFragmentBinding.inflate(inflater, container, false);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        binding.ebooksRv.setLayoutManager(mLayoutManager);
        binding.ebooksRv.setHasFixedSize(true);
        binding.swipeContainer.setColorSchemeResources(android.R.color.holo_orange_light);

        mEbooksListener = this;
        SwipeEbooksViewModel model = new ViewModelProvider(getActivity()).get(mSwipeModel);

        binding.swipeContainer.setOnRefreshListener(model::refreshData);
        binding.swipeContainer.setRefreshing(true);
        final Observer<List<Ebook>> swipeObserver = ebooks -> {
            binding.swipeContainer.setRefreshing(true);
            logger.d("swiping");
            mEbooks = (ArrayList<Ebook>) ebooks;
            EbooksAdapter mAdapter = new EbooksAdapter(mEbooks, mEbooksListener, getContext());
            binding.ebooksRv.setAdapter(mAdapter);
            binding.swipeContainer.setRefreshing(false);
        };
        model.getEbooks().observe(getViewLifecycleOwner(), swipeObserver);
        return binding.getRoot();
    }

    @Override
    public void onItemClick(int position) {
        EbookDetailsViewModel model = new ViewModelProvider(getActivity()).get(EbookDetailsViewModel.class);
        model.setEbook(mEbooks.get(position));
        moveTo(new EbookDetailsFragment());
    }
}
