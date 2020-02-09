package com.example.biblio.helpers

import android.app.Activity
import com.example.biblio.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

object GoogleHelper {
    /**
     * Configure sign-in to request the user's ID, email address, and basic profile and an id token.
     *
     * @param activity instance of Activity
     * @return Google sign-in Intent
     */
    @JvmStatic
    fun getSignInIntent(activity: Activity) = getClient(activity).signInIntent

    /**
     * Signs out the current Google user.
     *
     * @param activity instance of Activity
     */
    fun signOut(activity: Activity) {
        getClient(activity).signOut()
    }

    /**
     * @implNote ID and basic profile are included in DEFAULT_SIGN_IN.
     */
    private fun getClient(activity: Activity): GoogleSignInClient {
        val id = activity.resources.getString(R.string.default_web_client_id)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(id)
                .build()
        // Build a GoogleSignInClient with the options specified by gso.
        return GoogleSignIn.getClient(activity, gso)
    }
}