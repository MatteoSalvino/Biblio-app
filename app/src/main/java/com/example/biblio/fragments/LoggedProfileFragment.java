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
import com.example.biblio.databinding.LoggedProfileFragmentBinding;

import java.util.Objects;

import static com.example.biblio.helpers.SharedPreferencesHelper.CURRENT_USER_KEY;

public class LoggedProfileFragment extends Fragment {
    public static final String TAG = "LoggedProfileFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        com.example.biblio.databinding.LoggedProfileFragmentBinding binding = LoggedProfileFragmentBinding.inflate(inflater, container, false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getContext()));

        binding.profileLogoffBtn.setOnClickListener(view -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(CURRENT_USER_KEY);
            editor.apply();
            getFragmentManager().beginTransaction().remove(this).replace(R.id.fragment_container, new ProfileFragment(), "ProfileFragment").commit();
        });

        return binding.getRoot();
    }
}
