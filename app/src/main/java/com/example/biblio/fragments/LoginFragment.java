package com.example.biblio.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
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
import com.example.biblio.api.UserBuilder;
import com.example.biblio.databinding.LoginFragmentBinding;
import com.google.gson.Gson;

import org.apache.commons.validator.routines.EmailValidator;
import org.jetbrains.annotations.NotNull;

import static com.example.biblio.helpers.SharedPreferencesHelper.CURRENT_USER_KEY;

public class LoginFragment extends Fragment {
    public static final String TITLE = "Login";
    private final String LOG_TAG = getClass().getName();
    private ProgressDialog progressDialog;
    private LoginFragmentBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LoginFragmentBinding.inflate(inflater, container, false);

        binding.loginBtn.setOnClickListener(view -> {
            String email = binding.emailField.getEditText().getText().toString().trim();
            String password = binding.passwordField.getEditText().getText().toString().trim();

            if (isValid(email, "email") && isValid(password, "password")) {
                progressDialog = ProgressDialog.show(getActivity(), "Login process", "Please wait...", true);
                progressDialog.setContentView(R.layout.login_dialog_view);
                new Thread(() -> {
                    User user = new UserBuilder().setEmail(email).setPassword(password).build();
                    boolean successful = user.login();
                    getActivity().runOnUiThread(() -> progressDialog.dismiss());
                    if (successful) {
                        Log.d(LOG_TAG, "successful login");
                        //todo: check SP behaviour
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(CURRENT_USER_KEY, new Gson().toJson(user));
                        editor.apply();
                        //fixme: consider launching the activity here instead of returning to prev fragment
                        getActivity().setResult(Activity.RESULT_OK);
                        getActivity().finish();
                    } else {
                        Log.d(LOG_TAG, "login failed");
                        //todo: show some error message
                        //todo: should return to previous fragment with different code?
                    }
                }).start();
            } else
                Log.d(LOG_TAG, "Wrong credentials");
        });
        return binding.getRoot();
    }

    private boolean isValid(String param, @NotNull String type) {
        if (type.equals("email"))
            return EmailValidator.getInstance().isValid(param);
        else return type.equals("password");
    }
}
