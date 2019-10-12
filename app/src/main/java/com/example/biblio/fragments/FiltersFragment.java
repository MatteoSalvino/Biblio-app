package com.example.biblio.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.biblio.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.List;

import lrusso96.simplebiblio.core.Ebook;

public class FiltersFragment extends Fragment {
    private LinearLayout mLinearLayout;
    private ImageView mBackBtn;
    private MaterialButton mResetBtn;
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
    private MaterialButton mApplyFiltersBtn;
    private List<Ebook> search_results;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.filters_fragment, container, false);

        mLinearLayout = view.findViewById(R.id.main_filters_layout);
        mBackBtn = view.findViewById(R.id.filters_back_btn);
        mResetBtn = view.findViewById(R.id.filters_reset_btn);
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
        mApplyFiltersBtn = view.findViewById(R.id.apply_filters_btn);

        search_results = new Gson().fromJson(getArguments().getString("search_data"), new TypeToken<ArrayList<Ebook>>() {}.getType());


        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStackImmediate();
            }
        });


        mLowRatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLowRatingBtn.setBackgroundColor(getResources().getColor(R.color.enableBtnColor));
                mMediumRatingBtn.setBackgroundColor(getResources().getColor(R.color.enableBtnColor));
                mMediumHighRatingBtn.setBackgroundColor(getResources().getColor(R.color.enableBtnColor));
                mHighRatingBtn.setBackgroundColor(getResources().getColor(R.color.enableBtnColor));
            }
        });

        mMediumRatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLowRatingBtn.setBackgroundColor(getResources().getColor(R.color.disableBtnColor));
                mMediumRatingBtn.setBackgroundColor(getResources().getColor(R.color.enableBtnColor));
                mMediumHighRatingBtn.setBackgroundColor(getResources().getColor(R.color.enableBtnColor));
                mHighRatingBtn.setBackgroundColor(getResources().getColor(R.color.enableBtnColor));
            }
        });


        mMediumHighRatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLowRatingBtn.setBackgroundColor(getResources().getColor(R.color.disableBtnColor));
                mMediumRatingBtn.setBackgroundColor(getResources().getColor(R.color.disableBtnColor));
                mMediumHighRatingBtn.setBackgroundColor(getResources().getColor(R.color.enableBtnColor));
                mHighRatingBtn.setBackgroundColor(getResources().getColor(R.color.enableBtnColor));
            }
        });

        mHighRatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLowRatingBtn.setBackgroundColor(getResources().getColor(R.color.disableBtnColor));
                mMediumRatingBtn.setBackgroundColor(getResources().getColor(R.color.disableBtnColor));
                mMediumHighRatingBtn.setBackgroundColor(getResources().getColor(R.color.disableBtnColor));
                mHighRatingBtn.setBackgroundColor(getResources().getColor(R.color.enableBtnColor));
            }
        });


        mLibgenesisCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFeedbooksCb.setChecked(false);
                mLibgenesisCb.setChecked(true);
            }
        });

        mFeedbooksCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Add component dynamically
                LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                TextView tv = new TextView(getContext());
                tv.setLayoutParams(lparams);
                tv.setText("Feedbook selected");

                mLinearLayout.addView(tv);

                mFeedbooksCb.setChecked(true);
                mLibgenesisCb.setChecked(false);
            }
        });

        mResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLibgenesisCb.setChecked(false);
                mFeedbooksCb.setChecked(false);
                mLowRatingBtn.setBackgroundColor(getResources().getColor(R.color.enableBtnColor));
                mMediumRatingBtn.setBackgroundColor(getResources().getColor(R.color.enableBtnColor));
                mMediumHighRatingBtn.setBackgroundColor(getResources().getColor(R.color.enableBtnColor));
                mHighRatingBtn.setBackgroundColor(getResources().getColor(R.color.enableBtnColor));
                mItalianCb.setChecked(false);
                mEnglishCb.setChecked(false);
                mFrenchCb.setChecked(false);
                mSpanishCb.setChecked(false);
                mGermanCb.setChecked(false);
            }
        });

        mApplyFiltersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String provider = mLibgenesisCb.isChecked() ? mLibgenesisCb.getText().toString() : mFeedbooksCb.getText().toString();
                Double rating = 0.0;
                ArrayList<String> languages = new ArrayList<>();

                if(mItalianCb.isChecked()) languages.add("it");
                if(mEnglishCb.isChecked()) languages.add("en");
                if(mFrenchCb.isChecked()) languages.add("fr");
                if(mSpanishCb.isChecked()) languages.add("es");
                if(mGermanCb.isChecked()) languages.add("de");


                List<Ebook> filtered_results = filterResults(provider, rating, languages);
            }
        });

        return view;
    }

    private List<Ebook> filterResults(String provider, Double rating, ArrayList<String> languages) {

        List<Ebook> filtered_results = new ArrayList<>();

        //Rating not managed yet
        for(Ebook elem : search_results) {
            if (elem.getProvider().equals(provider) && languages.contains(elem.getLanguage()))
                filtered_results.add(elem);
        }
        return filtered_results;
    }
}