package com.example.biblio;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.biblio.api.User;
import com.example.biblio.api.UserBuilder;
import com.example.biblio.databinding.SignupActivityBinding;
import com.example.biblio.helpers.LogHelper;
import com.example.biblio.helpers.SimpleBiblioHelper;

import org.apache.commons.lang3.StringUtils;

public class SignupActivity extends AppCompatActivity {
    private final LogHelper logger = new LogHelper(getClass());
    private SignupActivityBinding binding;
    private String username, email, password, passwordConfirmation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SignupActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.signupBtn.setOnClickListener(view -> {
            username = binding.signupNameField.getEditText().getText().toString();
            email = binding.signupEmailField.getEditText().getText().toString();
            password = binding.signupPasswordField.getEditText().getText().toString();
            passwordConfirmation = binding.signupPasswordConfirmationField.getEditText().getText().toString();
            trySignup();
        });
    }

    /**
     * Checks wheter ot not the current signup form is valid and tries to signup.
     * If successful, terminates the activity and stores the credentials.
     */
    private void trySignup() {
        if (!isValidForm()) {
            logger.d("The signup form is not valid.");
            return;
        }
        ProgressDialog progressDialog = ProgressDialog.show(this, "", "", true);
        progressDialog.setContentView(R.layout.login_dialog_view);
        new Thread(() -> {
            User user = new UserBuilder()
                    .setEmail(email)
                    .setPassword(password)
                    .setUsername(username)
                    .build();
            boolean successful = user.signup();
            runOnUiThread(progressDialog::dismiss);
            if (successful) {
                logger.d("successful signup");
                SimpleBiblioHelper.setCurrentUser(user, getApplicationContext());
                setResult(Activity.RESULT_OK);
                finish();
            } else
                logger.e("signup failed");
        }).start();
    }

    /**
     * Simple method to check whether the form has been properly filled.
     * This does not check user credentials, since is only a local method.
     *
     * @return true if valid, else false
     */
    private boolean isValidForm() {
        boolean blank = StringUtils.isBlank(username) || StringUtils.isBlank(email) || StringUtils.isBlank(password);
        boolean match = password.equals(passwordConfirmation);
        boolean checked = binding.signupTermsCb.isChecked();
        return !blank && checked && match;
    }
}
