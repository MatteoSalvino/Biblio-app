package com.example.biblio;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

public class Introduction extends AppIntro {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Button mDoneBtn = findViewById(R.id.done);

        createSlideFragment("Search facility", "Search any books simply typing a short query in the search bar.", R.drawable.search, getResources().getColor(R.color.backgroundapp));
        createSlideFragment("Store books", "Store your favorite books in your device's local storage.", R.drawable.cloud_download, getResources().getColor(R.color.secondary));
        createSlideFragment("Refresh facility", "Swipe down in order to refresh data in Popular and Recent sections.", R.drawable.swipe_down, getResources().getColor(R.color.primary));

        setNavBarColor(R.color.colorPrimaryDark);

        setSeparatorColor(getResources().getColor(R.color.white));
        showSkipButton(false);

        mDoneBtn.setBackgroundColor(Color.TRANSPARENT);
        mDoneBtn.setText(getResources().getText(R.string.done_msg));
        mDoneBtn.setAllCaps(false);

        setProgressButtonEnabled(true);
        setFadeAnimation();
    }

    private void createSlideFragment(String title, String description, int image, int background) {
        SliderPage sliderPage = new SliderPage();
        sliderPage.setTitle(title);
        sliderPage.setDescription(description);
        sliderPage.setImageDrawable(image);
        sliderPage.setBgColor(background);
        addSlide(AppIntroFragment.newInstance(sliderPage));
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();
        finish();
    }
}
