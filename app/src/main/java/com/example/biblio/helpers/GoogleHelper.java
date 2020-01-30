package com.example.biblio.helpers;

import android.app.Activity;
import android.content.Intent;

import com.example.biblio.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import org.jetbrains.annotations.NotNull;

public class GoogleHelper {

    /**
     * Configure sign-in to request the user's ID, email address, and basic profile and an id token.
     *
     * @param activity instance of Activity
     * @return Google sign-in Intent
     */
    @NotNull
    public static Intent getSignInIntent(Activity activity) {
        return getClient(activity).getSignInIntent();
    }

    /**
     * Signs out the current Google user.
     *
     * @param activity instance of Activity
     */
    public static void signOut(@NotNull Activity activity) {
        getClient(activity).signOut();
    }

    /**
     * @implNote ID and basic profile are included in DEFAULT_SIGN_IN.
     */
    @NotNull
    private static GoogleSignInClient getClient(@NotNull Activity activity) {
        String id = activity.getResources().getString(R.string.default_web_client_id);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(id)
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        return GoogleSignIn.getClient(activity, gso);
    }
}
