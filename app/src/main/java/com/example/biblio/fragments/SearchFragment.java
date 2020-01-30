package com.example.biblio.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biblio.adapters.EbooksAdapter;
import com.example.biblio.databinding.SearchFragmentBinding;
import com.example.biblio.helpers.XFragment;
import com.example.biblio.viewmodels.EbookDetailsViewModel;
import com.example.biblio.viewmodels.SearchViewModel;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import lrusso96.simplebiblio.core.Ebook;

import static com.example.biblio.helpers.SharedPreferencesHelper.EAN_ENABLED_KEY;

public class SearchFragment extends XFragment implements EbooksAdapter.OnItemListener {
    private ArrayList<Ebook> mEbooks;
    private EbooksAdapter.OnItemListener adapterListener;
    private SearchFragmentBinding binding;
    private EbookDetailsViewModel ebook_model;

    public SearchFragment() {
        super(SearchFragment.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = SearchFragmentBinding.inflate(inflater, container, false);

        SearchViewModel model = new ViewModelProvider(getActivity()).get(SearchViewModel.class);
        ebook_model = new ViewModelProvider(getActivity()).get(EbookDetailsViewModel.class);

        binding.filtersBtn.setOnClickListener(view -> moveTo(new FiltersFragment()));
        binding.recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(mLayoutManager);
        adapterListener = this;

        RxTextView.textChanges(binding.searchBar.getSearchEditText())
                .debounce(750, TimeUnit.MILLISECONDS)
                .subscribe(textChanged -> {
                    logger.d("Stopped typing");
                    String query = binding.searchBar.getSearchEditText().getText().toString();
                    if (query.length() >= 5)
                        model.refreshData(query);
                    else
                        logger.d(String.format(Locale.getDefault(), "Query too short: %d chars inserted", query.length()));
                });

        final Observer<List<Ebook>> searchObserver = ebooks -> {
            mEbooks = (ArrayList<Ebook>) ebooks;
            EbooksAdapter mAdapter = new EbooksAdapter(mEbooks, adapterListener, getContext());
            binding.recyclerView.setAdapter(mAdapter);
        };
        model.getEbooks().observe(getViewLifecycleOwner(), searchObserver);

        binding.sortBtn.setOnClickListener(v -> {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
            alertDialog.setTitle("Set custom sorting");
            String[] items = {"Title", "Year"};
            alertDialog.setSingleChoiceItems(items, -1, (dialog, which) -> {
                switch (which) {
                    case 0:
                        model.sortByTitle();
                        break;
                    case 1:
                        model.sortByYear();
                        break;
                }
                dialog.dismiss();
            });
            AlertDialog alert = alertDialog.create();
            alert.setCanceledOnTouchOutside(true);
            alert.show();
        });

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        binding.scannerBtn.setOnClickListener(view -> IntentIntegrator.forSupportFragment(this).initiateScan());
        if (sharedPreferences.getBoolean(EAN_ENABLED_KEY, false))
            binding.scannerBtn.setVisibility(View.VISIBLE);
        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        String code = intentResult.getContents();
        if (code != null) {
            binding.searchBar.setText(code);
            binding.searchBar.enableSearch();
        }
    }

    @Override
    public void onItemClick(int position) {
        ebook_model.setEbook(mEbooks.get(position));
        moveTo(new EbookDetailsFragment());
    }
}
