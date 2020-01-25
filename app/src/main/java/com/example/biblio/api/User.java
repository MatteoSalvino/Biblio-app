package com.example.biblio.api;

import com.example.biblio.helpers.LogHelper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lrusso96.simplebiblio.core.Ebook;

public final class User {
    private final int MAX_TRIES = 2;
    private final LogHelper logger = new LogHelper(this.getClass());
    String username;
    String email;
    String password;
    String token;
    String photo_uri;
    int total_downloads;
    int total_reviews;

    User(@NotNull UserBuilder builder) {
        this.email = builder.email;
        this.password = builder.password;
        this.username = builder.username;
        this.photo_uri = (builder.photo == null) ? null : builder.photo.toString();
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
                logger.e(e.getMessage());
            }
        }
        return null;
    }

    @Nullable
    public RatingResult getEbookStats(Ebook ebook) {
        for (int count = 0; count < MAX_TRIES; count++) {
            try {
                if (count == 0 || this.login())
                    return EbooksHandler.stats(this, ebook);
            } catch (UnhautorizedRequestException e) {
                logger.e(e.getMessage());
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
                logger.e(e.getMessage());
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

    public int getTotalReviews() {
        return total_reviews;
    }

    public String getPhotoUri() {
        return photo_uri;
    }
}
