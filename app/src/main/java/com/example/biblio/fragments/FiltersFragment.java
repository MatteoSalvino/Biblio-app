package com.example.biblio.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.biblio.R;
import com.example.biblio.databinding.FragmentFiltersBinding;
import com.example.biblio.helpers.XFragment;
import com.example.biblio.viewmodels.SearchViewModel;

import org.jetbrains.annotations.NotNull;

import lrusso96.simplebiblio.core.Provider;
import lrusso96.simplebiblio.core.providers.feedbooks.Feedbooks;
import lrusso96.simplebiblio.core.providers.libgen.LibraryGenesis;

public class FiltersFragment extends XFragment {
    private FragmentFiltersBinding binding;

    public FiltersFragment() {
        super(FiltersFragment.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFiltersBinding.inflate(inflater, container, false);

        SearchViewModel model = new ViewModelProvider(getActivity()).get(SearchViewModel.class);

        binding.filtersBackBtn.setOnClickListener(x -> popBackStackImmediate());

        binding.lowRatingBtn.setOnClickListener(x -> updateButtonBackgroundColors(RATING.LOW));
        binding.mediumRatingBtn.setOnClickListener(x -> updateButtonBackgroundColors(RATING.MEDIUM));
        binding.mediumHighRatingBtn.setOnClickListener(x -> updateButtonBackgroundColors(RATING.MEDIUM_HIGH));
        binding.highRatingBtn.setOnClickListener(x -> updateButtonBackgroundColors(RATING.HIGH));

        binding.feedbooksCb.setChecked(model.isProviderVisible(Provider.FEEDBOOKS));
        binding.feedbooksCb.setOnCheckedChangeListener((button, isChecked) -> model.setProviderVisibility(Feedbooks.class, isChecked));

        binding.libgenCb.setChecked(model.isProviderVisible(Provider.LIBGEN));
        binding.libgenCb.setOnCheckedChangeListener((button, isChecked) -> model.setProviderVisibility(LibraryGenesis.class, isChecked));

        binding.englishCb.setChecked(model.isLanguageVisible("english"));
        binding.englishCb.setOnCheckedChangeListener((button, isChecked) -> model.showEnglish(isChecked));
        binding.italianCb.setChecked(model.isLanguageVisible("italian"));
        binding.italianCb.setOnCheckedChangeListener((button, isChecked) -> model.showItalian(isChecked));
        binding.otherCb.setChecked(model.isLanguageVisible("other"));
        binding.otherCb.setOnCheckedChangeListener((button, isChecked) -> model.showOther(isChecked));
        binding.spanishCb.setChecked(model.isLanguageVisible("spanish"));
        binding.spanishCb.setOnCheckedChangeListener((button, isChecked) -> model.showSpanish(isChecked));

        binding.filtersResetBtn.setOnClickListener(x -> {
            binding.libgenCb.setChecked(true);
            binding.feedbooksCb.setChecked(true);
            updateButtonBackgroundColors(RATING.LOW);
            binding.italianCb.setChecked(true);
            binding.englishCb.setChecked(true);
            binding.otherCb.setChecked(true);
            binding.spanishCb.setChecked(true);
        });
        return binding.getRoot();
    }

    /**
     * Updates the background color of the rating buttons, according to the current minimum rating.
     *
     * @param rating the minimum value to be accepted
     */
    private void updateButtonBackgroundColors(@NotNull RATING rating) {
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
