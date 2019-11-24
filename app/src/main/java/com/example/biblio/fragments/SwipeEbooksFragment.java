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

import lrusso96.simplebiblio.core.Ebook;

public class SwipeEbooksFragment extends Fragment implements EbooksAdapter.OnItemListener {
    private RecyclerView mPopularRecycleView;
    private EbooksAdapter.OnItemListener adapterListener;
    private ArrayList<Ebook> popularList;
    private Class<? extends SwipeEbooksViewModel> clazz;


    public SwipeEbooksFragment(Class<? extends SwipeEbooksViewModel> clazz) {
        this.clazz = clazz;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.swipe_ebooks_rv_fragment, container, false);

        mPopularRecycleView = view.findViewById(R.id.ebooks_rv);
        mPopularRecycleView.setHasFixedSize(true);

        SwipeRefreshLayout mSwipeContainer = view.findViewById(R.id.swipeContainer);
        mSwipeContainer.setColorSchemeResources(android.R.color.holo_orange_light);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mPopularRecycleView.setLayoutManager(mLayoutManager);

        adapterListener = this;
        SwipeEbooksViewModel model2 = ViewModelProviders.of(getActivity()).get(clazz);

        mSwipeContainer.setOnRefreshListener(model2::refreshData);
        mSwipeContainer.setRefreshing(true);
        final Observer<List<Ebook>> popularObserver = ebooks -> {
            mSwipeContainer.setRefreshing(true);
            popularList = (ArrayList<Ebook>) ebooks;
            EbooksAdapter mAdapter = new EbooksAdapter(popularList, adapterListener, getContext());
            mPopularRecycleView.setAdapter(mAdapter);
            mSwipeContainer.setRefreshing(false);
        };
        model2.getEbooks().observe(this, popularObserver);
        return view;
    }

    @Override
    public void onItemClick(int position) {
        Fragment to_render = new BookFragment();
        Bundle args = new Bundle();

        args.putString("current", new Gson().toJson(popularList.get(position)));
        to_render.setArguments(args);

        getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container)
                .getFragmentManager().beginTransaction().replace(R.id.fragment_container, to_render)
                .addToBackStack(null).commit();
    }
}
