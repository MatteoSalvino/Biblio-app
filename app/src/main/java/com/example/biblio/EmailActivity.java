package com.example.biblio;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.biblio.adapters.PageAdapter;
import com.example.biblio.databinding.EmailActivityBinding;
import com.example.biblio.fragments.LoggedProfileFragment;
import com.example.biblio.fragments.LoginFragment;
import com.example.biblio.fragments.SignupFragment;
import com.google.android.material.tabs.TabLayout;

public class EmailActivity extends AppCompatActivity {
    private EmailActivityBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = EmailActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int page_start = getIntent().getIntExtra("page start", 0);
        binding.emailBackBtn.setOnClickListener(view -> onBackPressed());

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(LoginFragment.TITLE));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(SignupFragment.TITLE));
        binding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        PageAdapter pageAdapter = new PageAdapter(getSupportFragmentManager(), binding.tabLayout.getTabCount());
        binding.viewPager.setAdapter(pageAdapter);

        binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        binding.viewPager.setCurrentItem(page_start);
    }
}
