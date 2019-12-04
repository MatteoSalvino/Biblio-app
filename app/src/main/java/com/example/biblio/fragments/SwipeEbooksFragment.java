package com.example.biblio.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.biblio.R;
import com.example.biblio.adapters.EbooksAdapter;
import com.example.biblio.databinding.SwipeEbooksRvFragmentBinding;
import com.example.biblio.viewmodels.SwipeEbooksViewModel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lrusso96.simplebiblio.core.Ebook;

public class SwipeEbooksFragment extends Fragment implements EbooksAdapter.OnItemListener {
    private EbooksAdapter.OnItemListener mEbooksListener;
    private ArrayList<Ebook> mEbooks;
    private final Class<? extends SwipeEbooksViewModel> mSwipeModel;
    private SwipeEbooksRvFragmentBinding binding;

    SwipeEbooksFragment(Class<? extends SwipeEbooksViewModel> clazz) {
        mSwipeModel = clazz;
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
        SwipeEbooksViewModel model = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(mSwipeModel);

        binding.swipeContainer.setOnRefreshListener(model::refreshData);
        binding.swipeContainer.setRefreshing(true);
        final Observer<List<Ebook>> swipeObserver = ebooks -> {
            binding.swipeContainer.setRefreshing(true);
            Log.d(getClass().getName(), "swiping");
            mEbooks = (ArrayList<Ebook>) ebooks;
            EbooksAdapter mAdapter = new EbooksAdapter(mEbooks, mEbooksListener, getContext());
            binding.ebooksRv.setAdapter(mAdapter);
            binding.swipeContainer.setRefreshing(false);
        };
        model.getEbooks().observe(this, swipeObserver);
        return binding.getRoot();
    }

    @Override
    public void onItemClick(int position) {
        Fragment to_render = new EbookDetailsFragment();
        Bundle args = new Bundle();

        args.putString("current", new Gson().toJson(mEbooks.get(position)));
        to_render.setArguments(args);

        getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container)
                .getFragmentManager().beginTransaction().replace(R.id.fragment_container, to_render)
                .addToBackStack(null).commit();
    }
}
