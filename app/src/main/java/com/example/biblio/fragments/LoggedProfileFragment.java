package com.example.biblio.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.biblio.R;
import com.example.biblio.SettingsActivity;
import com.example.biblio.api.User;
import com.example.biblio.databinding.FragmentLoggedProfileBinding;
import com.example.biblio.helpers.SimpleBiblioHelper;
import com.example.biblio.helpers.XFragment;

public class LoggedProfileFragment extends XFragment {

    public LoggedProfileFragment() {
        super(LoggedProfileFragment.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentLoggedProfileBinding binding = FragmentLoggedProfileBinding.inflate(inflater, container, false);
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
            replaceWith(new ProfileFragment());
        });

        binding.loggedSettingsBtn.setOnClickListener(view -> startActivity(new Intent(getActivity(), SettingsActivity.class)));

        return binding.getRoot();
    }
}
