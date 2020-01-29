package com.example.biblio.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.biblio.LoginActivity;
import com.example.biblio.databinding.ProfileFragmentBinding;
import com.example.biblio.helpers.XFragment;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends XFragment {
    private static final int RC_SIGN_IN = 1;
    private ProfileFragmentBinding binding;

    public ProfileFragment() {
        super(ProfileFragment.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ProfileFragmentBinding.inflate(inflater, container, false);
        setUpButtons();

        binding.emailLoginBtn.setOnClickListener(view -> {
            Intent i = new Intent(getActivity(), LoginActivity.class);
            startActivityForResult(i, RC_SIGN_IN);
        });

        binding.settingsBtn.setOnClickListener(view -> moveTo(new SettingsFragment(), SettingsFragment.TAG));
        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK)
            replaceWith(new LoggedProfileFragment());
    }

    private void setUpButtons() {
        binding.infoTv.setVisibility(View.VISIBLE);
        binding.emailLoginBtn.setVisibility(View.VISIBLE);
    }
}
