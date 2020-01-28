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

import com.example.biblio.LoginActivity;
import com.example.biblio.R;
import com.example.biblio.databinding.ProfileFragmentBinding;
import com.example.biblio.helpers.LogHelper;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {
    public static final String TAG = "ProfileFragment";
    private static final int RC_SIGN_IN = 1;
    private final LogHelper logger = new LogHelper(getClass());
    private ProfileFragmentBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ProfileFragmentBinding.inflate(inflater, container, false);
        setUpButtons();

        binding.emailLoginBtn.setOnClickListener(view -> {
            Intent i = new Intent(getActivity(), LoginActivity.class);
            startActivityForResult(i, RC_SIGN_IN);
        });

        //todo: add to bar!
        binding.settingsBtn.setOnClickListener(view -> loadSettingsFragment());

        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK) {
            logger.d("successful login, loading LoggedProfileFragment...");
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new LoggedProfileFragment(), "LoggedProfileFragment")
                    .commit();
        }
    }

    private void loadSettingsFragment() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            Fragment fragment = new SettingsFragment();
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment, SettingsFragment.TAG).addToBackStack(SettingsFragment.TAG).commit();
        }
    }

    private void setUpButtons() {
        binding.infoTv.setVisibility(View.VISIBLE);
        binding.emailLoginBtn.setVisibility(View.VISIBLE);
    }
}
