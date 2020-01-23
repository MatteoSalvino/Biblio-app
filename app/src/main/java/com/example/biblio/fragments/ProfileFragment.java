package com.example.biblio.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.biblio.LoginActivity;
import com.example.biblio.R;
import com.example.biblio.databinding.ProfileFragmentBinding;
import com.example.biblio.helpers.LogHelper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {
    public static final String TAG = "ProfileFragment";
    private static final int RC_SIGN_IN = 1;
    private static final int RC_GOOGLE_SIGN_IN = 2;
    private final LogHelper logger = new LogHelper(getClass());
    private ProfileFragmentBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ProfileFragmentBinding.inflate(inflater, container, false);
        setUpButtons();

        binding.emailLoginBtn.setOnClickListener(view -> {
            Intent i = new Intent(getActivity(), LoginActivity.class);
            startActivityForResult(i, RC_SIGN_IN);
        });

        //todo: add to bar!
        binding.settingsBtn.setOnClickListener(view -> loadSettingsFragment());

        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK) {
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, new LoggedProfileFragment(), "LoggedProfileFragment").commit();
            //showButtons(resultCode == Activity.RESULT_OK);
            // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        } else if (requestCode == RC_GOOGLE_SIGN_IN && resultCode == RESULT_OK) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            //updateUI(account);
            if (account != null)
                logger.d(String.format("%s - %s", account.getEmail(), account.getDisplayName()));
            setUpButtons();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            logger.w("signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
    }

    /*
    private void googleSignOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener(getActivity(),
                task -> setUpButtons());
    }
     */

    private void loadSettingsFragment() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            Fragment fragment = new SettingsFragment();
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment, SettingsFragment.TAG).addToBackStack(SettingsFragment.TAG).commit();
        }
    }

    private void setUpButtons() {
        binding.infoTv.setVisibility(View.VISIBLE);
        binding.emailLoginBtn.setVisibility(View.VISIBLE);
    }
}
