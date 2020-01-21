package com.example.biblio.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import java.util.Objects;

import static com.example.biblio.helpers.SharedPreferencesHelper.CURRENT_USER_KEY;

//todo: improve layout (e.g. showing stats if logged)
public class ProfileFragment extends Fragment {
    public static final String TAG = "ProfileFragment";
    private static final int RC_SIGN_IN = 1;
    private static final int RC_GOOGLE_SIGN_IN = 2;
    private final LogHelper logger = new LogHelper(getClass());
    private ProfileFragmentBinding binding;
    private GoogleSignInClient mGoogleSignInClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ProfileFragmentBinding.inflate(inflater, container, false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getContext()));
        boolean logged = false;
        //fixme: if logged we should show LoggedProfileFragment (?)
        if (sharedPreferences.contains(CURRENT_USER_KEY)) {
            User user = new Gson().fromJson(sharedPreferences.getString(CURRENT_USER_KEY, null), User.class);
            logger.v(user.getEmail());
            logged = true;
        }
        showButtons(logged);

        binding.emailLoginBtn.setOnClickListener(view -> {
            Intent i = new Intent(getActivity(), EmailActivity.class);
            startActivityForResult(i, RC_SIGN_IN);
        });

        binding.signupSuggestionBtn.setOnClickListener(view -> {
            Intent i = new Intent(getActivity(), EmailActivity.class);
            i.putExtra("page start", 1);
            startActivity(i);
        });

        binding.settingsBtn.setOnClickListener(view -> loadSettingsFragment());

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(Objects.requireNonNull(getActivity()), gso);

        binding.googleLoginBtn.setOnClickListener(view -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
        });

        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, new LoggedProfileFragment(), "LoggedProfileFragment").commit();
            //showButtons(resultCode == Activity.RESULT_OK);
            // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        } else if (requestCode == RC_GOOGLE_SIGN_IN) {
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
            showButtons(true);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            logger.w("signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
    }

    private void googleSignOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener(getActivity(),
                task -> showButtons(false));
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
            //binding.logoffBtn.setVisibility(View.VISIBLE);
            binding.infoTv.setVisibility(View.INVISIBLE);
            binding.signupSuggestionBtn.setVisibility(View.INVISIBLE);
            binding.emailLoginBtn.setVisibility(View.INVISIBLE);
            binding.googleLoginBtn.setVisibility(View.INVISIBLE);
        } else {
            binding.infoTv.setVisibility(View.VISIBLE);
            //binding.logoffBtn.setVisibility(View.INVISIBLE);
            binding.signupSuggestionBtn.setVisibility(View.VISIBLE);
            binding.emailLoginBtn.setVisibility(View.VISIBLE);
            binding.googleLoginBtn.setVisibility(View.VISIBLE);
        }
    }
}
