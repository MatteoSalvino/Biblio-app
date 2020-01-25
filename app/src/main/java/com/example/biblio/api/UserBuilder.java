package com.example.biblio.api;

import android.net.Uri;

public final class UserBuilder {
    String username;
    String email;
    String password;
    Uri photo;

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
