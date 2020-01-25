package com.example.biblio;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.biblio.api.User;
import com.example.biblio.api.UserBuilder;
import com.example.biblio.databinding.LoginActivityBinding;
import com.example.biblio.helpers.LogHelper;
import com.example.biblio.helpers.SimpleBiblioHelper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.apache.commons.validator.routines.EmailValidator;

import static com.example.biblio.helpers.GoogleHelper.getSignInIntent;


public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_UP = 1;
    private static final int RC_GOOGLE_SIGN_IN = 2;
    private final LogHelper logger = new LogHelper(getClass());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoginActivityBinding binding = LoginActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.loginBtn.setOnClickListener(view -> {
            String email = binding.emailField.getEditText().getText().toString().trim();
            String password = binding.passwordField.getEditText().getText().toString().trim();

            //todo: extract strings
            ProgressDialog progressDialog = ProgressDialog.show(this, "Login process", "Please wait...", true);
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
                runOnUiThread(progressDialog::dismiss);
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

        binding.googleLoginBtn.setOnClickListener(view -> startActivityForResult(getSignInIntent(this), RC_GOOGLE_SIGN_IN));

        //todo: handle the result
        binding.signupSuggestionBtn.setOnClickListener(view -> startActivityForResult(new Intent(this, SignupActivity.class), RC_SIGN_UP));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
            resultCode = RESULT_OK;
        }
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

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            //updateUI(account);
            if (account != null) {
                //fixme: handle more fields!
                User user = new UserBuilder().setEmail(account.getEmail()).setUsername(account.getDisplayName()).setPhoto(account.getPhotoUrl()).build();
                new SimpleBiblioHelper(getApplicationContext()).setCurrentUser(user);
                logger.d(String.format("%s - %s", account.getEmail(), account.getDisplayName()));
            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            logger.w("signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
    }
}
