package com.example.biblio.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.biblio.R;
import com.example.biblio.viewmodels.SearchViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.Objects;

import lrusso96.simplebiblio.core.Provider;
import lrusso96.simplebiblio.core.providers.feedbooks.Feedbooks;
import lrusso96.simplebiblio.core.providers.libgen.LibraryGenesis;

public class FiltersFragment extends Fragment {
    private MaterialButton mLowRatingBtn;
    private MaterialButton mMediumRatingBtn;
    private MaterialButton mMediumHighRatingBtn;
    private MaterialButton mHighRatingBtn;
    private MaterialCheckBox mLibgenesisCb;
    private MaterialCheckBox mFeedbooksCb;
    private MaterialCheckBox mItalianCb;
    private MaterialCheckBox mEnglishCb;
    private MaterialCheckBox mFrenchCb;
    private MaterialCheckBox mSpanishCb;
    private MaterialCheckBox mGermanCb;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.filters_fragment, container, false);

        SearchViewModel model = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(SearchViewModel.class);

        ImageView mBackBtn = view.findViewById(R.id.filters_back_btn);
        MaterialButton mResetBtn = view.findViewById(R.id.filters_reset_btn);
        mLowRatingBtn = view.findViewById(R.id.low_rating_btn);
        mMediumRatingBtn = view.findViewById(R.id.medium_rating_btn);
        mMediumHighRatingBtn = view.findViewById(R.id.medium_high_rating_btn);
        mHighRatingBtn = view.findViewById(R.id.high_rating_btn);
        mLibgenesisCb = view.findViewById(R.id.libgen_cb);
        mFeedbooksCb = view.findViewById(R.id.feedbooks_cb);
        mItalianCb = view.findViewById(R.id.italian_cb);
        mEnglishCb = view.findViewById(R.id.english_cb);
        mFrenchCb = view.findViewById(R.id.french_cb);
        mSpanishCb = view.findViewById(R.id.spanish_cb);
        mGermanCb = view.findViewById(R.id.german_cb);

        mBackBtn.setOnClickListener(x -> Objects.requireNonNull(getFragmentManager()).popBackStackImmediate());

        mLowRatingBtn.setOnClickListener(x -> {
            mLowRatingBtn.setBackgroundColor(getResources().getColor(R.color.add_button));
            mMediumRatingBtn.setBackgroundColor(getResources().getColor(R.color.add_button));
            mMediumHighRatingBtn.setBackgroundColor(getResources().getColor(R.color.add_button));
            mHighRatingBtn.setBackgroundColor(getResources().getColor(R.color.add_button));
        });

        mMediumRatingBtn.setOnClickListener(x -> {
            mLowRatingBtn.setBackgroundColor(getResources().getColor(R.color.disabled_button));
            mMediumRatingBtn.setBackgroundColor(getResources().getColor(R.color.add_button));
            mMediumHighRatingBtn.setBackgroundColor(getResources().getColor(R.color.add_button));
            mHighRatingBtn.setBackgroundColor(getResources().getColor(R.color.add_button));
        });


        mMediumHighRatingBtn.setOnClickListener(x -> {
            mLowRatingBtn.setBackgroundColor(getResources().getColor(R.color.disabled_button));
            mMediumRatingBtn.setBackgroundColor(getResources().getColor(R.color.disabled_button));
            mMediumHighRatingBtn.setBackgroundColor(getResources().getColor(R.color.add_button));
            mHighRatingBtn.setBackgroundColor(getResources().getColor(R.color.add_button));
        });

        mHighRatingBtn.setOnClickListener(x -> {
            mLowRatingBtn.setBackgroundColor(getResources().getColor(R.color.disabled_button));
            mMediumRatingBtn.setBackgroundColor(getResources().getColor(R.color.disabled_button));
            mMediumHighRatingBtn.setBackgroundColor(getResources().getColor(R.color.disabled_button));
            mHighRatingBtn.setBackgroundColor(getResources().getColor(R.color.add_button));
        });

        mFeedbooksCb.setChecked(model.isEnabled(Provider.FEEDBOOKS));
        mFeedbooksCb.setOnCheckedChangeListener((button, isChecked) -> {
            model.enableProvider(Feedbooks.class, isChecked);
        });

        mLibgenesisCb.setChecked(model.isEnabled(Provider.LIBGEN));
        mLibgenesisCb.setOnCheckedChangeListener((button, isChecked) -> {
            model.enableProvider(LibraryGenesis.class, isChecked);
        });


        mResetBtn.setOnClickListener(x -> {
            mLibgenesisCb.setChecked(true);
            mFeedbooksCb.setChecked(true);

            mLowRatingBtn.setBackgroundColor(getResources().getColor(R.color.add_button));
            mMediumRatingBtn.setBackgroundColor(getResources().getColor(R.color.add_button));
            mMediumHighRatingBtn.setBackgroundColor(getResources().getColor(R.color.add_button));
            mHighRatingBtn.setBackgroundColor(getResources().getColor(R.color.add_button));
            mItalianCb.setChecked(false);
            mEnglishCb.setChecked(false);
            mFrenchCb.setChecked(false);
            mSpanishCb.setChecked(false);
            mGermanCb.setChecked(false);
        });

        return view;
    }
}
