package com.example.biblio.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;

import com.example.biblio.EmailActivity;
import com.example.biblio.R;
import com.example.biblio.api.User;
import com.example.biblio.databinding.ProfileFragmentBinding;
import com.example.biblio.helpers.LogHelper;
import com.google.gson.Gson;

import java.util.Objects;

import static com.example.biblio.helpers.SharedPreferencesHelper.CURRENT_USER_KEY;

//todo: improve layout (e.g. showing stats if logged)
//todo: add google sign-in option
public class ProfileFragment extends Fragment {
    public static final String TAG = "ProfileFragment";
    private final LogHelper logger = new LogHelper(getClass());
    private ProfileFragmentBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ProfileFragmentBinding.inflate(inflater, container, false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getContext()));
        boolean logged = false;
        if (sharedPreferences.contains(CURRENT_USER_KEY)) {
            User user = new Gson().fromJson(sharedPreferences.getString(CURRENT_USER_KEY, null), User.class);
            logger.v(user.getEmail());
            logged = true;
        }
        showButtons(logged);

        binding.emailLoginBtn.setOnClickListener(view -> {
            Intent i = new Intent(getActivity(), EmailActivity.class);
            startActivityForResult(i, 0);
        });

        binding.signupSuggestionBtn.setOnClickListener(view -> {
            Intent i = new Intent(getActivity(), EmailActivity.class);
            i.putExtra("page start", 1);
            startActivity(i);
        });

        binding.logoffBtn.setOnClickListener(view -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(CURRENT_USER_KEY);
            editor.apply();
            showButtons(false);
        });

        binding.settingsBtn.setOnClickListener(view -> loadSettingsFragment());
        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK)
            showButtons(true);
    }

    private void loadSettingsFragment() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            Fragment fragment = new SettingsFragment();
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment, SettingsFragment.TAG).commit();
        }
    }

    private void showButtons(boolean logged) {
        if (logged) {
            binding.logoffBtn.setVisibility(View.VISIBLE);
            binding.signupSuggestionBtn.setVisibility(View.INVISIBLE);
            binding.emailLoginBtn.setVisibility(View.INVISIBLE);
            binding.googleLoginBtn.setVisibility(View.INVISIBLE);
        } else {
            binding.logoffBtn.setVisibility(View.INVISIBLE);
            binding.signupSuggestionBtn.setVisibility(View.VISIBLE);
            binding.emailLoginBtn.setVisibility(View.VISIBLE);
            binding.googleLoginBtn.setVisibility(View.VISIBLE);
        }
    }
}
