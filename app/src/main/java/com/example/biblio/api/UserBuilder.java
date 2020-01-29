package com.example.biblio.api;

import android.net.Uri;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.jetbrains.annotations.NotNull;

import static com.example.biblio.api.SimpleBiblioCommons.byepassPwd;

public final class UserBuilder {
    String username;
    String email;
    String password;
    Uri photo;
    String oauthToken;

    public UserBuilder fromGoogleAccount(@NotNull GoogleSignInAccount account) {
        byepassPwd(this);
        username = account.getDisplayName();
        email = account.getEmail();
        oauthToken = account.getIdToken();
        photo = account.getPhotoUrl();
        return this;
    }

    public UserBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    public UserBuilder setEmail(String email) {
        this.email = email;
        return this;
    }

    public UserBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    public UserBuilder setPhoto(Uri photo) {
        this.photo = photo;
        return this;
    }

    public User build() {
        return new User(this);
    }
}
