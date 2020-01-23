package com.example.biblio;

import android.app.Activity;
import android.app.AlertDialog;
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

import java.util.Objects;

import static com.example.biblio.helpers.SharedPreferencesHelper.CURRENT_USER_KEY;

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

            progressDialog = ProgressDialog.show(this, "Login process", "Please wait...", true);
            progressDialog.setContentView(R.layout.login_dialog_view);
            if (!EmailValidator.getInstance().isValid(email)) {
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
                    logger.d("successful login");
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(CURRENT_USER_KEY, new Gson().toJson(user));
                    editor.apply();
                    setResult(Activity.RESULT_OK);
                    finish();
                } else {
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
                .setTitle("Error")
                .setMessage("Login was not successful")
                .create();
        dialog.show();
    }
}
