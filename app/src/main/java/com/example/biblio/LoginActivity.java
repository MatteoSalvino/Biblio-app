package com.example.biblio;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.example.biblio.api.User;
import com.example.biblio.api.UserBuilder;
import com.example.biblio.databinding.LoginActivityBinding;
import com.example.biblio.helpers.LogHelper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.gson.Gson;

import org.apache.commons.validator.routines.EmailValidator;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.example.biblio.helpers.SharedPreferencesHelper.CURRENT_USER_KEY;

public class LoginActivity extends AppCompatActivity {
    private LoginActivityBinding binding;
    private final LogHelper logger = new LogHelper(getClass());
    private ProgressDialog progressDialog;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_UP = 1;
    private static final int RC_GOOGLE_SIGN_IN = 2;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LoginActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.loginBtn.setOnClickListener(view -> {
            String email = binding.emailField.getEditText().getText().toString().trim();
            String password = binding.passwordField.getEditText().getText().toString().trim();

            if (isValid(email, "email") && isValid(password, "password")) {
                progressDialog = ProgressDialog.show(this, "Login process", "Please wait...", true);
                progressDialog.setContentView(R.layout.login_dialog_view);
                new Thread(() -> {
                    User user = new UserBuilder().setEmail(email).setPassword(password).build();
                    boolean successful = user.login();
                    runOnUiThread(() -> progressDialog.dismiss());
                    if (successful) {
                        logger.d("successful login");
                        //todo: check SP behaviour
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(CURRENT_USER_KEY, new Gson().toJson(user));
                        editor.apply();
                        //fixme: consider launching the activity here instead of returning to prev fragment
                        setResult(Activity.RESULT_OK);
                        finish();
                    } else {
                        logger.d("login failed");
                        //todo: show some error message
                        //todo: should return to previous fragment with different code?
                    }
                }).start();
            } else
                logger.d("Wrong credentials");
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

    private boolean isValid(String param, @NotNull String type) {
        if (type.equals("email"))
            return EmailValidator.getInstance().isValid(param);
        else return type.equals("password");
    }
}
