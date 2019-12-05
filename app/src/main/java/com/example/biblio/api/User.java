package com.example.biblio.api;

import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lrusso96.simplebiblio.core.Ebook;

public final class User {
    String username;
    String email;
    String password;
    private final int MAX_TRIES = 2;
    String token;
    int total_downloads;

    private final String LOG_TAG = getClass().getName();

    User(@NotNull UserBuilder builder) {
        this.email = builder.email;
        this.password = builder.password;
        this.username = builder.username;
    }

    public boolean signup() {
        return AuthenticationHandler.signup(this);
    }

    public boolean login() {
        return AuthenticationHandler.login(this);
    }

    @Nullable
    public RatingResult rate(Ebook ebook, int rating) {
        for (int count = 0; count < MAX_TRIES; count++) {
            try {
                if (count == 0 || this.login())
                    return EbooksHandler.rate(this, ebook, rating);
            } catch (UnhautorizedRequestException e) {
                Log.e(LOG_TAG, "" + e.getMessage());
            }
        }
        return null;
    }

    @Nullable
    public RatingResult notifyDownload(Ebook ebook) {
        for (int count = 0; count < MAX_TRIES; count++) {
            try {
                if (count > 0) this.login();
                return EbooksHandler.notifyDownload(this, ebook);
            } catch (UnhautorizedRequestException e) {
                Log.e(LOG_TAG, "" + e.getMessage());
            }
        }
        return null;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public int getTotalDownloads() {
        return total_downloads;
    }
}
