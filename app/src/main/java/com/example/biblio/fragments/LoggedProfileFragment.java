package com.example.biblio.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.example.biblio.R;
import com.google.android.material.button.MaterialButton;

import java.util.Objects;

public class LoggedProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.logged_profile_fragment, container, false);

        MaterialButton mLogoffBtn = v.findViewById(R.id.logoff_btn);
        mLogoffBtn.setOnClickListener(view -> {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getContext()));
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString("credentials", null);
            editor.putBoolean("validator", false);
            editor.apply();

            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment(), "ProfileFragment").commit();
        });

        return v;
    }
}
