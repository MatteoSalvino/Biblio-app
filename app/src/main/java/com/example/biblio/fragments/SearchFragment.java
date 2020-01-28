package com.example.biblio.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biblio.R;
import com.example.biblio.adapters.EbooksAdapter;
import com.example.biblio.databinding.SearchFragmentBinding;
import com.example.biblio.helpers.LogHelper;
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

public class SearchFragment extends Fragment implements EbooksAdapter.OnItemListener {
    public static final String TAG = "SearchFragment";
    private final LogHelper logger = new LogHelper(getClass());
    private ArrayList<Ebook> mEbooks;
    private EbooksAdapter.OnItemListener adapterListener;
    private SearchFragmentBinding binding;
    private EbookDetailsViewModel ebook_model;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = SearchFragmentBinding.inflate(inflater, container, false);

        SearchViewModel model = new ViewModelProvider(getActivity()).get(SearchViewModel.class);
        ebook_model = new ViewModelProvider(getActivity()).get(EbookDetailsViewModel.class);

        binding.filtersBtn.setOnClickListener(view -> renderFragment(new FiltersFragment()));
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

        binding.scannerBtn.setOnClickListener(view -> {
            IntentIntegrator.forSupportFragment(this).initiateScan();
        });

        return binding.getRoot();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        //ToDo call search function with the scanned code as input
        binding.searchBar.getSearchEditText().setText(intentResult.getContents());
    }

    @Override
    public void onItemClick(int position) {
        Fragment to_render = new EbookDetailsFragment();
        ebook_model.setEbook(mEbooks.get(position));
        renderFragment(to_render);
    }

    private void renderFragment(Fragment to_render) {
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, to_render)
                .addToBackStack(null).commit();
    }
}
