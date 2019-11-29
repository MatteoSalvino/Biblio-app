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
import androidx.recyclerview.widget.RecyclerView;

import com.example.biblio.R;
import com.example.biblio.adapters.EbooksAdapter;
import com.example.biblio.databinding.SearchFragmentBinding;
import com.example.biblio.viewmodels.SearchViewModel;
import com.google.gson.Gson;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import lrusso96.simplebiblio.core.Ebook;

public class SearchFragment extends Fragment implements EbooksAdapter.OnItemListener {
    private ArrayList<Ebook> mEbooks;
    private EbooksAdapter.OnItemListener adapterListener;
    private SearchFragmentBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = SearchFragmentBinding.inflate(inflater, container, false);

        SearchViewModel model = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(SearchViewModel.class);

        //fixme: variable mSortBtn is never used
        binding.recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(mLayoutManager);
        adapterListener = this;

        RxTextView.textChanges(binding.searchBar.getSearchEditText())
                .debounce(750, TimeUnit.MILLISECONDS)
                .subscribe(textChanged -> {
                    Log.d("TextChanges", "Stopped typing.");
                    String query = binding.searchBar.getSearchEditText().getText().toString();

                    if (query.length() >= 5)
                        model.refreshData(query);
                    else
                        Log.d("QueryAlert", "Query too short !");
                });


        binding.filtersBtn.setOnClickListener(view1 -> {
            Fragment to_render = new FiltersFragment();
            getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container)
                    .getFragmentManager().beginTransaction().replace(R.id.fragment_container, to_render)
                    .addToBackStack(null).commit();
        });

        final Observer<List<Ebook>> searchObserver = ebooks -> {
            mEbooks = (ArrayList<Ebook>) ebooks;
            //todo: why this as listener?
            EbooksAdapter mAdapter = new EbooksAdapter(mEbooks, adapterListener, getContext());
            binding.recyclerView.setAdapter(mAdapter);
        };
        model.getEbooks().observe(this, searchObserver);
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

