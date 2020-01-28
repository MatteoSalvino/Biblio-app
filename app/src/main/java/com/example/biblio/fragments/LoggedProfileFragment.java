package com.example.biblio.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.biblio.R;
import com.example.biblio.api.User;
import com.example.biblio.databinding.LoggedProfileFragmentBinding;
import com.example.biblio.helpers.SimpleBiblioHelper;

public class LoggedProfileFragment extends Fragment {
    public static final String TAG = "LoggedProfileFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        com.example.biblio.databinding.LoggedProfileFragmentBinding binding = LoggedProfileFragmentBinding.inflate(inflater, container, false);
        User current = SimpleBiblioHelper.getCurrentUser(getContext());

        //Load current user's data
        binding.loggedUsernameTv.setText(current.getUsername());
        binding.loggedEmailTv.setText(current.getEmail());
        binding.loggedDownloadTv.setText(String.valueOf(current.getTotalDownloads()));
        binding.loggedReviewsTv.setText(String.valueOf(current.getTotalReviews()));

        if (current.getPhotoUri() != null)
            Glide.with(getContext()).load(current.getPhotoUri())
                    .placeholder(R.drawable.account_circle_outline)
                    .apply(new RequestOptions().centerInside().circleCrop())
                    .into(binding.loggedPhoto);

        binding.loggedLastSearchTv.setText(SimpleBiblioHelper.getLastSearchTS(getContext()));

        binding.logoutBtn.setOnClickListener(view -> {
            SimpleBiblioHelper.removeCurrentUser(getContext());
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .remove(this)
                    .replace(R.id.fragment_container, new ProfileFragment(), ProfileFragment.TAG)
                    .commit();
        });

        binding.loggedSettingsBtn.setOnClickListener(view -> {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new SettingsFragment(), SettingsFragment.TAG)
                    .addToBackStack(SettingsFragment.TAG).commit();
        });

        return binding.getRoot();
    }
}
