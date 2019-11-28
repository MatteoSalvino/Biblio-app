package com.example.biblio.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.biblio.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lrusso96.simplebiblio.core.Ebook;

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
    private List<Ebook> search_results;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.filters_fragment, container, false);

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
        MaterialButton mApplyFiltersBtn = view.findViewById(R.id.apply_filters_btn);

        search_results = new Gson().fromJson(Objects.requireNonNull(getArguments()).getString("search_data"), new TypeToken<ArrayList<Ebook>>() {
        }.getType());


        mBackBtn.setOnClickListener(view1 -> Objects.requireNonNull(getFragmentManager()).popBackStackImmediate());


        mLowRatingBtn.setOnClickListener(view12 -> {
            mLowRatingBtn.setBackgroundColor(getResources().getColor(R.color.add_button));
            mMediumRatingBtn.setBackgroundColor(getResources().getColor(R.color.add_button));
            mMediumHighRatingBtn.setBackgroundColor(getResources().getColor(R.color.add_button));
            mHighRatingBtn.setBackgroundColor(getResources().getColor(R.color.add_button));
        });

        mMediumRatingBtn.setOnClickListener(view13 -> {
            mLowRatingBtn.setBackgroundColor(getResources().getColor(R.color.disabled_button));
            mMediumRatingBtn.setBackgroundColor(getResources().getColor(R.color.add_button));
            mMediumHighRatingBtn.setBackgroundColor(getResources().getColor(R.color.add_button));
            mHighRatingBtn.setBackgroundColor(getResources().getColor(R.color.add_button));
        });


        mMediumHighRatingBtn.setOnClickListener(view14 -> {
            mLowRatingBtn.setBackgroundColor(getResources().getColor(R.color.disabled_button));
            mMediumRatingBtn.setBackgroundColor(getResources().getColor(R.color.disabled_button));
            mMediumHighRatingBtn.setBackgroundColor(getResources().getColor(R.color.add_button));
            mHighRatingBtn.setBackgroundColor(getResources().getColor(R.color.add_button));
        });

        mHighRatingBtn.setOnClickListener(view15 -> {
            mLowRatingBtn.setBackgroundColor(getResources().getColor(R.color.disabled_button));
            mMediumRatingBtn.setBackgroundColor(getResources().getColor(R.color.disabled_button));
            mMediumHighRatingBtn.setBackgroundColor(getResources().getColor(R.color.disabled_button));
            mHighRatingBtn.setBackgroundColor(getResources().getColor(R.color.add_button));
        });


        mLibgenesisCb.setOnClickListener(view16 -> {
            mFeedbooksCb.setChecked(false);
            mLibgenesisCb.setChecked(true);
        });

        mFeedbooksCb.setOnClickListener(view17 -> {
            //Add component dynamically
            //LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            //TextView tv = new TextView(getContext());
            //tv.setLayoutParams(lparams);
            //tv.setText("Feedbook selected");

            //mLinearLayout.addView(tv);

            mFeedbooksCb.setChecked(true);
            mLibgenesisCb.setChecked(false);
        });

        mResetBtn.setOnClickListener(view18 -> {
            mLibgenesisCb.setChecked(false);
            mFeedbooksCb.setChecked(false);
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

        mApplyFiltersBtn.setOnClickListener(view19 -> {
            String provider = mLibgenesisCb.isChecked() ? mLibgenesisCb.getText().toString() : mFeedbooksCb.getText().toString();
            Double rating = 0.0;
            ArrayList<String> languages = new ArrayList<>();

            if (mItalianCb.isChecked()) languages.add("it");
            if (mEnglishCb.isChecked()) languages.add("en");
            if (mFrenchCb.isChecked()) languages.add("fr");
            if (mSpanishCb.isChecked()) languages.add("es");
            if (mGermanCb.isChecked()) languages.add("de");


            //fixme: unused variable
            List<Ebook> filtered_results = filterResults(provider, rating, languages);
        });

        return view;
    }

    private List<Ebook> filterResults(String provider, Double rating, ArrayList<String> languages) {

        List<Ebook> filtered_results = new ArrayList<>();

        //todo: rating not managed yet
        for (Ebook elem : search_results) {
            if (elem.getProviderName().equals(provider) && languages.contains(elem.getLanguage()))
                filtered_results.add(elem);
        }
        return filtered_results;
    }
}
