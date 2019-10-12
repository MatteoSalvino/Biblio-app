package com.example.biblio;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import com.example.biblio.fragments.MyBooksFragment;
import com.example.biblio.fragments.PopularFragment;
import com.example.biblio.fragments.RecentFragment;
import com.example.biblio.fragments.SearchFragment;
import com.example.biblio.fragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        if(savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SearchFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_search :
                            selectedFragment = new SearchFragment();
                            break;
                        case R.id.nav_popular :
                            selectedFragment = new PopularFragment();
                            break;
                        case R.id.nav_recent :
                            selectedFragment = new RecentFragment();
                            break;
                        case R.id.nav_books :
                            selectedFragment = new MyBooksFragment();
                            break;
                        case R.id.nav_settings :
                            selectedFragment = new SettingsFragment();
                            break;
                    }

                    if (selectedFragment != null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                        return true;
                    }
                    return false;
                }
            };
}
