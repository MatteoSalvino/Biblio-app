package com.example.biblio.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.example.biblio.R;
import com.example.biblio.api.User;
import com.example.biblio.databinding.LoggedProfileFragmentBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Objects;

import static com.example.biblio.helpers.SharedPreferencesHelper.CURRENT_USER_KEY;

public class LoggedProfileFragment extends Fragment {
    public static final String TAG = "LoggedProfileFragment";
    private LoggedProfileFragmentBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LoggedProfileFragmentBinding.inflate(inflater, container, false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getContext()));
        User current = new Gson().fromJson(sharedPreferences.getString(CURRENT_USER_KEY, null), new TypeToken<User>() {
        }.getType());

        //Load current user's data
        binding.loggedUsernameTv.setText(current.getUsername());
        binding.loggedEmailTv.setText(current.getEmail());
        binding.loggedDownloadTv.setText(String.valueOf(current.getTotalDownloads()));


        binding.logoutBtn.setOnClickListener(view -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(CURRENT_USER_KEY);
            editor.apply();
            getFragmentManager().beginTransaction().remove(this).replace(R.id.fragment_container, new ProfileFragment(), "ProfileFragment").commit();
        });

        binding.loggedSettingsBtn.setOnClickListener(view -> {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new SettingsFragment(), SettingsFragment.TAG).addToBackStack(SettingsFragment.TAG).commit();
        });

        return binding.getRoot();
    }
}
