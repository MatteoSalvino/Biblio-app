package com.example.biblio;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.example.biblio.databinding.ActivityMainBinding;
import com.example.biblio.fragments.MyEbooksFragment;
import com.example.biblio.fragments.PopularFragment;
import com.example.biblio.fragments.ProfileFragment;
import com.example.biblio.fragments.RecentFragment;
import com.example.biblio.fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import static com.example.biblio.helpers.SharedPreferencesHelper.FIRST_START_KEY;

public class MainActivity extends AppCompatActivity {
    //todo: design a suitable menu to put more than 5 items!
    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;
                String TAG = "";
                switch (item.getItemId()) {
                    case R.id.nav_search:
                        selectedFragment = new SearchFragment();
                        TAG = SearchFragment.TAG;
                        break;
                    case R.id.nav_popular:
                        selectedFragment = new PopularFragment();
                        TAG = PopularFragment.TAG;
                        break;
                    case R.id.nav_recent:
                        selectedFragment = new RecentFragment();
                        TAG = RecentFragment.TAG;
                        break;
                    case R.id.nav_books:
                        selectedFragment = new MyEbooksFragment();
                        TAG = MyEbooksFragment.TAG;
                        break;
                    case R.id.nav_profile:
                        selectedFragment = new ProfileFragment();
                        TAG = ProfileFragment.TAG;
                        break;
                }
                if (selectedFragment != null) {
                    Fragment previous_fragment = getSupportFragmentManager().findFragmentByTag(TAG);
                    if (previous_fragment != null && previous_fragment.isVisible()) {
                        return false;
                    } else {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment, TAG).commit();
                        return true;
                    }
                }
                return false;
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bottomNavigation.setOnNavigationItemSelectedListener(navListener);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        //fixme: move key to SharedPreferencesHelper
        boolean isFirstStart = sharedPrefs.getBoolean(FIRST_START_KEY, true);
        if (isFirstStart) {
            Intent i = new Intent(MainActivity.this, Introduction.class);
            runOnUiThread(() -> startActivity(i));
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SearchFragment(), SearchFragment.TAG).commit();
        } else if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SearchFragment(), SearchFragment.TAG).commit();
    }
}
