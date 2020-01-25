package com.example.biblio.fragments;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.biblio.R;
import com.example.biblio.api.User;
import com.example.biblio.databinding.LoggedProfileFragmentBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Objects;

import static com.example.biblio.helpers.SharedPreferencesHelper.CURRENT_USER_KEY;
import static com.example.biblio.helpers.SharedPreferencesHelper.LAST_SEARCH_TS_KEY;

public class LoggedProfileFragment extends Fragment {
    public static final String TAG = "LoggedProfileFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        com.example.biblio.databinding.LoggedProfileFragmentBinding binding = LoggedProfileFragmentBinding.inflate(inflater, container, false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getContext()));
        User current = new Gson().fromJson(sharedPreferences.getString(CURRENT_USER_KEY, null), new TypeToken<User>() {
        }.getType());

        assert current != null;

        //Load current user's data
        binding.loggedUsernameTv.setText(current.getUsername());
        binding.loggedEmailTv.setText(current.getEmail());
        binding.loggedDownloadTv.setText(String.valueOf(current.getTotalDownloads()));
        binding.loggedReviewsTv.setText(String.valueOf(current.getTotalDownloads()));


        if (current.getPhotoUri() != null) {
            Glide.with(getContext()).load(current.getPhotoUri())
                    .placeholder(R.drawable.account_circle_outline).apply(new RequestOptions().centerInside().circleCrop()).into(binding.loggedPhoto);
        }


        if (sharedPreferences.contains(LAST_SEARCH_TS_KEY)) {
            //todo: 0,0 as default?
            String default_ts = "0,0";
            String[] timestamp = sharedPreferences.getString(LAST_SEARCH_TS_KEY, default_ts)
                    .split(",");
            binding.loggedLastSearchTv.setText(String.format("%s %s", timestamp[0], timestamp[1]));
        }

        binding.logoutBtn.setOnClickListener(view -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(CURRENT_USER_KEY);
            editor.apply();
            getFragmentManager()
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
