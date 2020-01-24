package com.example.biblio;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.biblio.api.User;
import com.example.biblio.api.UserBuilder;
import com.example.biblio.databinding.LoginActivityBinding;
import com.example.biblio.helpers.LogHelper;
import com.example.biblio.helpers.SimpleBiblioHelper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_UP = 1;
    private static final int RC_GOOGLE_SIGN_IN = 2;
    private final LogHelper logger = new LogHelper(getClass());
    private LoginActivityBinding binding;
    private ProgressDialog progressDialog;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LoginActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.loginBtn.setOnClickListener(view -> {
            String email = binding.emailField.getEditText().getText().toString().trim();
            String password = binding.passwordField.getEditText().getText().toString().trim();

            //todo: extract strings
            progressDialog = ProgressDialog.show(this, "Login process", "Please wait...", true);
            progressDialog.setContentView(R.layout.login_dialog_view);
            if (!EmailValidator.getInstance().isValid(email)) {
                //todo: extract string
                logger.d("Invalid email inserted");
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    showErrorMessage();
                });
                return;
            }
            new Thread(() -> {
                User user = new UserBuilder().setEmail(email).setPassword(password).build();
                boolean successful = user.login();
                runOnUiThread(() -> progressDialog.dismiss());
                if (successful) {
                    //todo: extract string
                    logger.d("successful login");
                    new SimpleBiblioHelper(getApplicationContext()).setCurrentUser(user);
                    setResult(Activity.RESULT_OK);
                    finish();
                } else {
                    //todo: extract string
                    logger.d("login failed");
                    runOnUiThread(this::showErrorMessage);
                }
            }).start();
        });

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(Objects.requireNonNull(this), gso);

        binding.googleLoginBtn.setOnClickListener(view -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
        });

        //todo: handle the result
        binding.signupSuggestionBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, SignupActivity.class);
            startActivityForResult(intent, RC_SIGN_UP);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setResult(resultCode);
        finish();
    }

    //todo: improve this
    private void showErrorMessage() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                //todo: extract string
                .setTitle("Error")
                .setMessage(R.string.login_error_msg)
                .setIcon(R.drawable.baseline_error_outline_24)
                .create();
        dialog.show();
    }
}
