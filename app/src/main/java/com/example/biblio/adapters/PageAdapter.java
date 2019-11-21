package com.example.biblio.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.biblio.fragments.LoginFragment;
import com.example.biblio.fragments.SignupFragment;

public class PageAdapter extends FragmentStatePagerAdapter {
    private int tab_counter;

    public PageAdapter(FragmentManager fm, int tab_counter) {
        super(fm);
        this.tab_counter = tab_counter;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0 :
                return new LoginFragment();
            case 1 :
                return new SignupFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tab_counter;
    }
}
