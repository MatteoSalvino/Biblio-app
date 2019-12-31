package com.example.biblio.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.biblio.R;
import com.example.biblio.databinding.FiltersFragmentBinding;
import com.example.biblio.viewmodels.SearchViewModel;

import java.util.Objects;

import lrusso96.simplebiblio.core.Provider;
import lrusso96.simplebiblio.core.providers.feedbooks.Feedbooks;
import lrusso96.simplebiblio.core.providers.libgen.LibraryGenesis;

public class FiltersFragment extends Fragment {
    private FiltersFragmentBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FiltersFragmentBinding.inflate(inflater, container, false);

        SearchViewModel model = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(SearchViewModel.class);

        binding.filtersBackBtn.setOnClickListener(x -> Objects.requireNonNull(getFragmentManager()).popBackStackImmediate());

        binding.lowRatingBtn.setOnClickListener(x -> updateButtonBackgroundColors(RATING.LOW));
        binding.mediumRatingBtn.setOnClickListener(x -> updateButtonBackgroundColors(RATING.MEDIUM));
        binding.mediumHighRatingBtn.setOnClickListener(x -> updateButtonBackgroundColors(RATING.MEDIUM_HIGH));
        binding.highRatingBtn.setOnClickListener(x -> updateButtonBackgroundColors(RATING.HIGH));

        binding.feedbooksCb.setChecked(model.isEnabled(Provider.FEEDBOOKS));
        binding.feedbooksCb.setOnCheckedChangeListener((button, isChecked) -> model.enableProvider(Feedbooks.class, isChecked));

        binding.libgenCb.setChecked(model.isEnabled(Provider.LIBGEN));
        binding.libgenCb.setOnCheckedChangeListener((button, isChecked) -> model.enableProvider(LibraryGenesis.class, isChecked));

        binding.filtersResetBtn.setOnClickListener(x -> {
            binding.libgenCb.setChecked(true);
            binding.feedbooksCb.setChecked(true);
            updateButtonBackgroundColors(RATING.LOW);
            binding.italianCb.setChecked(false);
            binding.englishCb.setChecked(false);
            binding.frenchCb.setChecked(false);
            binding.spanishCb.setChecked(false);
        });
        return binding.getRoot();
    }

    /**
     * Updates the background color of the rating buttons, according to the current minimum rating.
     * @param rating the minimum value to be accepted
     */
    private void updateButtonBackgroundColors(RATING rating) {
        int enabled = getResources().getColor(R.color.add_button);
        int disabled = getResources().getColor(R.color.disabled_button);
        int val = rating.getValue();
        int c = RATING.LOW.getValue() >= val ? enabled : disabled;
        binding.lowRatingBtn.setBackgroundColor(c);
        c = RATING.MEDIUM.getValue() >= val ? enabled : disabled;
        binding.mediumRatingBtn.setBackgroundColor(c);
        c = RATING.MEDIUM_HIGH.getValue() >= val ? enabled : disabled;
        binding.mediumHighRatingBtn.setBackgroundColor(c);
        c = RATING.HIGH.getValue() >= val ? enabled : disabled;
        binding.highRatingBtn.setBackgroundColor(c);
    }

    private enum RATING {
        LOW(0),
        MEDIUM(1),
        MEDIUM_HIGH(2),
        HIGH(3);

        private final int value;

        RATING(final int val) {
            value = val;
        }

        public int getValue() {
            return value;
        }
    }
}
