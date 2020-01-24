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

import org.jetbrains.annotations.NotNull;

public class SignupActivity extends AppCompatActivity {
    private final LogHelper logger = new LogHelper(getClass());
    private SignupActivityBinding binding;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SignupActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.signupBtn.setOnClickListener(view -> {
            String name = binding.signupNameField.getEditText().getText().toString();
            String email = binding.signupEmailField.getEditText().getText().toString();
            String password = binding.signupPasswordField.getEditText().getText().toString();
            String password_confirmation = binding.signupPasswordConfirmationField.getEditText().getText().toString();

            if (isValidForm(name, email, password, password_confirmation)) {
                progressDialog = ProgressDialog.show(this, "", "", true);
                progressDialog.setContentView(R.layout.login_dialog_view);
                new Thread(() -> {
                    User user = new UserBuilder().setEmail(email).setPassword(password).setUsername(name).build();
                    boolean successful = user.signup();
                    runOnUiThread(() -> progressDialog.dismiss());
                    if (successful) {
                        logger.d("successful signup");
                        new SimpleBiblioHelper(getApplicationContext()).setCurrentUser(user);
                        setResult(Activity.RESULT_OK);
                        finish();
                    } else
                        logger.e("signup failed");
                }).start();
            } else {
                logger.d("The signup form is not valid.");
            }
        });
    }

    //todo: make more sophisticated
    private boolean isValidForm(@NotNull String name, String email, String password, String password_confirmation) {
        return !name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !password_confirmation.isEmpty()
                && (password.compareTo(password_confirmation) == 0) && binding.signupTermsCb.isChecked();
    }
}
