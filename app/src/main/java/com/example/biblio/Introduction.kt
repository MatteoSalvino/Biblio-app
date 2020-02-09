package com.example.biblio

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.example.biblio.helpers.SharedPreferencesHelper.FIRST_START_KEY
import com.github.paolorotolo.appintro.AppIntro
import com.github.paolorotolo.appintro.AppIntroFragment
import com.github.paolorotolo.appintro.model.SliderPage

class Introduction : AppIntro() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createSlides()
        setNavBarColor(R.color.primary_dark)
        setSeparatorColor(ContextCompat.getColor(this, R.color.white))
        showSkipButton(false)
        val mDoneBtn = findViewById<Button>(R.id.done)
        mDoneBtn.setBackgroundColor(Color.TRANSPARENT)
        mDoneBtn.text = resources.getText(R.string.done_msg)
        mDoneBtn.isAllCaps = false
        isProgressButtonEnabled = true
        setFadeAnimation()
    }

    private fun createSlides() {
        createSlideFragment(resources.getString(R.string.introduction_search_title), resources.getString(R.string.introduction_search), R.drawable.search, ContextCompat.getColor(this, R.color.app_background))
        createSlideFragment(resources.getString(R.string.introduction_download_title), resources.getString(R.string.introduction_download), R.drawable.cloud_download, ContextCompat.getColor(this, R.color.secondary))
        createSlideFragment(resources.getString(R.string.introduction_refresh_title), resources.getString(R.string.introduction_refresh), R.drawable.swipe_down, ContextCompat.getColor(this, R.color.colorAccent))
    }

    private fun createSlideFragment(title: String, description: String, image: Int, background: Int) {
        val sliderPage = SliderPage()
        sliderPage.title = title
        sliderPage.description = description
        sliderPage.imageDrawable = image
        sliderPage.bgColor = background
        addSlide(AppIntroFragment.newInstance(sliderPage))
    }

    override fun onDonePressed(currentFragment: Fragment) {
        super.onDonePressed(currentFragment)
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(baseContext)
        sharedPrefs.edit { putBoolean(FIRST_START_KEY, false) }
        finish()
    }
}