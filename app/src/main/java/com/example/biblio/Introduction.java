package com.example.biblio;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

import static com.example.biblio.helpers.SharedPreferencesHelper.FIRST_START_KEY;

public class Introduction extends AppIntro {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createSlides();
        setNavBarColor(R.color.primary_dark);
        setSeparatorColor(getResources().getColor(R.color.white));
        showSkipButton(false);

        Button mDoneBtn = findViewById(R.id.done);
        mDoneBtn.setBackgroundColor(Color.TRANSPARENT);
        mDoneBtn.setText(getResources().getText(R.string.done_msg));
        mDoneBtn.setAllCaps(false);

        setProgressButtonEnabled(true);
        setFadeAnimation();
    }

    private void createSlides() {
        Resources resources = getResources();
        createSlideFragment(resources.getString(R.string.introduction_search_title), resources.getString(R.string.introduction_search), R.drawable.search, resources.getColor(R.color.app_background));
        createSlideFragment(resources.getString(R.string.introduction_download_title), resources.getString(R.string.introduction_download), R.drawable.cloud_download, resources.getColor(R.color.secondary));
        createSlideFragment(resources.getString(R.string.introduction_refresh_title), resources.getString(R.string.introduction_refresh), R.drawable.swipe_down, resources.getColor(R.color.colorAccent));
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
        editor.putBoolean(FIRST_START_KEY, false);
        editor.apply();
        finish();
    }
}
