package com.example.biblio.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.biblio.R;
import com.example.biblio.adapters.EbooksAdapter;
import com.example.biblio.viewmodels.SwipeEbooksViewModel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lrusso96.simplebiblio.core.Ebook;

public class SwipeEbooksFragment extends Fragment implements EbooksAdapter.OnItemListener {
    private RecyclerView mRecyclerView;
    private EbooksAdapter.OnItemListener mEbooksListener;
    private ArrayList<Ebook> mEbooks;
    private Class<? extends SwipeEbooksViewModel> mSwipeModel;


    SwipeEbooksFragment(Class<? extends SwipeEbooksViewModel> clazz) {
        mSwipeModel= clazz;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.swipe_ebooks_rv_fragment, container, false);

        mRecyclerView = view.findViewById(R.id.ebooks_rv);
        mRecyclerView.setHasFixedSize(true);

        SwipeRefreshLayout mSwipeContainer = view.findViewById(R.id.swipeContainer);
        mSwipeContainer.setColorSchemeResources(android.R.color.holo_orange_light);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mEbooksListener = this;
        SwipeEbooksViewModel model = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(mSwipeModel);

        mSwipeContainer.setOnRefreshListener(model::refreshData);
        mSwipeContainer.setRefreshing(true);
        final Observer<List<Ebook>> popularObserver = ebooks -> {
            mSwipeContainer.setRefreshing(true);
            mEbooks= (ArrayList<Ebook>) ebooks;
            EbooksAdapter mAdapter = new EbooksAdapter(mEbooks, mEbooksListener, getContext());
            mRecyclerView.setAdapter(mAdapter);
            mSwipeContainer.setRefreshing(false);
        };
        model.getEbooks().observe(this, popularObserver);
        return view;
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
