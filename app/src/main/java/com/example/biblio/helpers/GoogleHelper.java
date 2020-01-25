package com.example.biblio.helpers;

import android.app.Activity;
import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import org.jetbrains.annotations.NotNull;

public class GoogleHelper {

    /*
     * Configure sign-in to request the user's ID, email address, and basic profile.
     * ID and basic profile are included in DEFAULT_SIGN_IN.
     */
    private static GoogleSignInClient getClient(@NotNull Activity activity) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        return GoogleSignIn.getClient(activity, gso);
    }

    public static Intent getSignInIntent(Activity activity) {
        return getClient(activity).getSignInIntent();
    }

    public static void signOut(@NotNull Activity activity) {
        getClient(activity).signOut();
    }
}
