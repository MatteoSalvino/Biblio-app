package com.example.biblio.api;

import com.example.biblio.helpers.LogHelper;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.Request;

import static com.example.biblio.api.SimpleBiblioCommons.AUTH_TOKEN_KEY;
import static com.example.biblio.api.SimpleBiblioCommons.CLIENT;
import static com.example.biblio.api.SimpleBiblioCommons.DOWNLOADS_KEY;
import static com.example.biblio.api.SimpleBiblioCommons.ENDPOINT;
import static com.example.biblio.api.SimpleBiblioCommons.REVIEWS_KEY;
import static com.example.biblio.api.SimpleBiblioCommons.USERNAME_KEY;
import static com.example.biblio.api.SimpleBiblioCommons.USER_KEY;
import static com.example.biblio.api.SimpleBiblioCommons.getAuthReqBuilder;
import static com.example.biblio.api.SimpleBiblioCommons.getMessage;
import static com.example.biblio.api.SimpleBiblioCommons.parseBody;

class AuthenticationHandler {
    private static final String USERNAME_PAR = "name";
    private static final String EMAIL_PAR = "email";
    private static final String PASSWORD_PAR = "password";
    private static final String PASSWORD_CONFIRMATION_PAR = "password_confirmation";
    private static final String GOOGLE_TOKEN_PAR = "google_token";
    private static final LogHelper logger = new LogHelper(AuthenticationHandler.class);

    @NotNull
    private static FormBody buildForm(@NotNull User user, boolean signup, boolean oauth) {
        FormBody.Builder builder = new FormBody.Builder()
                .add(EMAIL_PAR, user.email)
                .add(PASSWORD_PAR, user.password);
        if (signup) {
            builder.add(USERNAME_PAR, user.username)
                    .add(PASSWORD_CONFIRMATION_PAR, user.password);
        }
        if (oauth) builder.add(GOOGLE_TOKEN_PAR, user.oauth_token);
        return builder.build();
    }

    static boolean signup(@NotNull User user) {
        Request req = getAuthReqBuilder(user).url(String.format("%s/signup", ENDPOINT))
                .post(buildForm(user, true, false))
                .build();
        try {
            JSONObject result = parseBody(CLIENT.newCall(req).execute().body());
            logger.d(getMessage(result));
            user.token = getToken(result);
            logger.d("token succesfully retrieved on signup");
        } catch (IOException | BodyException | TokenException e) {
            logger.e(e.getMessage());
            return false;
        }
        return true;
    }

    static boolean login(@NotNull User user) {
        boolean enabled = user.oauth_token != null;
        Request req = getAuthReqBuilder(user).url(String.format("%s/auth/login", ENDPOINT))
                .post(buildForm(user, enabled, enabled))
                .build();
        try {
            JSONObject result = parseBody(CLIENT.newCall(req).execute().body());
            logger.d(getMessage(result));
            update(user, result);
            logger.d("token succesfully retrieved after login");
            return true;
        } catch (IOException | BodyException | TokenException e) {
            logger.e(e.getMessage());
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
            JSONObject u = result.getJSONObject(USER_KEY);
            user.username = u.getString(USERNAME_KEY);
            user.total_downloads = u.getInt(DOWNLOADS_KEY);
            user.total_reviews = u.getInt(REVIEWS_KEY);
        } catch (JSONException e) {
            logger.e(e.getMessage());
        }
    }
}