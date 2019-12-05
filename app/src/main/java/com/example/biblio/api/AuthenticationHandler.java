package com.example.biblio.api;

import android.util.Log;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.example.biblio.api.SimpleBiblioHelper.AUTH_TOKEN_KEY;
import static com.example.biblio.api.SimpleBiblioHelper.CLIENT;
import static com.example.biblio.api.SimpleBiblioHelper.DOWNLOADS_KEY;
import static com.example.biblio.api.SimpleBiblioHelper.ENDPOINT;
import static com.example.biblio.api.SimpleBiblioHelper.USERNAME_KEY;
import static com.example.biblio.api.SimpleBiblioHelper.USER_KEY;
import static com.example.biblio.api.SimpleBiblioHelper.getAuthReqBuilder;
import static com.example.biblio.api.SimpleBiblioHelper.getMessage;
import static com.example.biblio.api.SimpleBiblioHelper.parseBody;

class AuthenticationHandler {
    private static final String USERNAME_PAR = "name";
    private static final String EMAIL_PAR = "email";
    private static final String PASSWORD_PAR = "password";
    private static final String PASSWORD_CONFIRMATION_PAR = "password_confirmation";

    private static final String LOG_TAG = AuthenticationHandler.class.getName();

    static boolean signup(@NotNull User user) {
        RequestBody formBody = new FormBody.Builder()
                .add(USERNAME_PAR, user.username)
                .add(EMAIL_PAR, user.email)
                .add(PASSWORD_PAR, user.password)
                .add(PASSWORD_CONFIRMATION_PAR, user.password)
                .build();
        Request req = getAuthReqBuilder(user).url(String.format("%s/signup", ENDPOINT))
                .post(formBody)
                .build();
        try {
            JSONObject result = parseBody(CLIENT.newCall(req).execute().body());
            Log.d(LOG_TAG, getMessage(result));
            user.token = getToken(result);
            Log.d(LOG_TAG, "token succesfully retrieved on signup");
        } catch (IOException | BodyException | TokenException e) {
            Log.e(LOG_TAG, "" + e.getMessage());
            return false;
        }
        return true;
    }

    static boolean login(@NotNull User user) {
        RequestBody formBody = new FormBody.Builder()
                .add(EMAIL_PAR, user.email)
                .add(PASSWORD_PAR, user.password)
                .build();
        Request req = getAuthReqBuilder(user).url(String.format("%s/auth/login", ENDPOINT))
                .post(formBody)
                .build();
        try {
            JSONObject result = parseBody(CLIENT.newCall(req).execute().body());
            Log.d(LOG_TAG, getMessage(result));
            update(user, result);
            Log.d(LOG_TAG, "token succesfully retrieved after login");
            return true;
        } catch (IOException | BodyException | TokenException e) {
            Log.e(LOG_TAG, "" + e.getMessage());
            return false;
        }
    }

    @NotNull
    private static String getToken(@NotNull JSONObject result) throws TokenException {
        try {
            return result.getString(AUTH_TOKEN_KEY);
        } catch (JSONException e) {
            throw new TokenException("no token received after signup");
        }
    }

    private static void update(@NotNull User user, @NotNull JSONObject result) throws TokenException {
        try {
            user.token = getToken(result);
            user.username = result.getJSONObject(USER_KEY).getString(USERNAME_KEY);
            user.total_downloads = result.getInt(DOWNLOADS_KEY);
            Log.d(LOG_TAG, String.format("updated total downloads:%d", user.total_downloads));
        } catch (JSONException e) {
            Log.e(LOG_TAG, "" + e.getMessage());
        }
    }
}