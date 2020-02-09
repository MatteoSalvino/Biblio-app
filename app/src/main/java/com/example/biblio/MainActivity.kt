package com.example.biblio

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.example.biblio.databinding.ActivityMainBinding
import com.example.biblio.fragments.*
import com.example.biblio.helpers.SharedPreferencesHelper
import com.example.biblio.helpers.SimpleBiblioHelper
import com.example.biblio.helpers.XFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private val navListener = BottomNavigationView.OnNavigationItemSelectedListener { item: MenuItem ->
        val selectedFragment = getSelectedFragment(item)
        if (selectedFragment != null) {
            val previousFragment = supportFragmentManager.findFragmentByTag(selectedFragment.TAG)
            if (previousFragment != null && previousFragment.isVisible) {
                false
            } else {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, selectedFragment, selectedFragment.TAG).commit()
                true
            }
        } else false
    }

    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bottomNavigation.setOnNavigationItemSelectedListener(navListener)
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(baseContext)
        val isFirstStart = sharedPrefs.getBoolean(SharedPreferencesHelper.FIRST_START_KEY, true)
        val searchFragment = SearchFragment()
        if (isFirstStart) {
            val i = Intent(this, Introduction::class.java)
            runOnUiThread { startActivity(i) }
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, searchFragment, searchFragment.TAG).commit()
        } else if (savedInstanceState == null) supportFragmentManager.beginTransaction().replace(R.id.fragment_container, searchFragment, searchFragment.TAG).commit()
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish()
        }
        doubleBackToExitPressedOnce = true
        Toast.makeText(this, R.string.back_pressed_msg, Toast.LENGTH_SHORT).show()
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    private fun getSelectedFragment(item: MenuItem): XFragment? {
        var selectedFragment: XFragment? = null
        when (item.itemId) {
            R.id.nav_search -> selectedFragment = SearchFragment()
            R.id.nav_popular -> selectedFragment = PopularFragment()
            R.id.nav_recent -> selectedFragment = RecentFragment()
            R.id.nav_books -> selectedFragment = MyEbooksFragment()
            R.id.nav_profile -> selectedFragment = if (SimpleBiblioHelper.getCurrentUser(applicationContext) != null) {
                LoggedProfileFragment()
            } else
                ProfileFragment()
        }
        return selectedFragment
    }
}