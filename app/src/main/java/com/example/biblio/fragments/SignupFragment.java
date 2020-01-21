package com.example.biblio.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
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
import com.example.biblio.api.User;
import com.example.biblio.api.UserBuilder;
import com.example.biblio.databinding.SignupFragmentBinding;
import com.example.biblio.helpers.LogHelper;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import static com.example.biblio.helpers.SharedPreferencesHelper.CURRENT_USER_KEY;

public class SignupFragment extends Fragment {
    public static final String TITLE = "Signup";
    private final LogHelper logger = new LogHelper(getClass());
    private SignupFragmentBinding binding;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = SignupFragmentBinding.inflate(inflater, container, false);

        binding.signupBtn.setOnClickListener(view -> {
            String name = binding.signupNameField.getEditText().getText().toString();
            String email = binding.signupEmailField.getEditText().getText().toString();
            String password = binding.signupPasswordField.getEditText().getText().toString();
            String password_confirmation = binding.signupPasswordConfirmationField.getEditText().getText().toString();

            if (isValidForm(name, email, password, password_confirmation)) {
                progressDialog = ProgressDialog.show(getActivity(), "", "", true);
                progressDialog.setContentView(R.layout.login_dialog_view);
                new Thread(() -> {
                    User user = new UserBuilder().setEmail(email).setPassword(password).setUsername(name).build();
                    boolean successful = user.signup();
                    getActivity().runOnUiThread(() -> progressDialog.dismiss());
                    if (successful) {
                        logger.d("successful signup");
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(CURRENT_USER_KEY, new Gson().toJson(user));
                        editor.apply();
                        getActivity().setResult(Activity.RESULT_OK);
                        getActivity().finish();
                    } else
                        logger.e("signup failed");
                }).start();
            } else {
                logger.d("The signup form is not valid.");
            }
        });
        return binding.getRoot();
    }

    //todo: make more sophisticated
    private boolean isValidForm(@NotNull String name, String email, String password, String password_confirmation) {
        return !name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !password_confirmation.isEmpty()
                && (password.compareTo(password_confirmation) == 0) && binding.signupTermsCb.isChecked();
    }
}
