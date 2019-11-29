package com.example.biblio.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.biblio.EmailActivity;
import com.example.biblio.R;
import com.example.biblio.databinding.ProfileFragmentBinding;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ProfileFragmentBinding binding = ProfileFragmentBinding.inflate(inflater, container, false);

        binding.emailLoginBtn.setOnClickListener(view -> {
            Intent i = new Intent(getActivity(), EmailActivity.class);
            startActivityForResult(i, 0);
        });

        binding.signupSuggestionBtn.setOnClickListener(view -> {
            Intent i = new Intent(getActivity(), EmailActivity.class);
            i.putExtra("page start", 1);
            startActivity(i);
        });

        binding.settingsBtn.setOnClickListener(view -> loadFragment(new SettingsFragment(), "SettingsFragment"));
        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == 200)
            loadFragment(new LoggedProfileFragment(), "LoggedProfileFragment");
    }

    private void loadFragment(Fragment fragment, String tag) {
        FragmentActivity activity = getActivity();
        if (activity != null)
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, tag).commit();
    }
}
