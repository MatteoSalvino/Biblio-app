package com.example.biblio.api;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

import static com.example.biblio.api.BodyException.NULL_BODY_MSG;

final class SimpleBiblioHelper {
    final static String ENDPOINT = "https://rocky-lake-33740.herokuapp.com";
    final static String AUTH_TOKEN_KEY = "auth_token";
    final static OkHttpClient CLIENT = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build();
    private final static String MESSAGE_KEY = "message";
    private final static Request.Builder REQ_BUILDER = new Request.Builder()
            .header("Accept", "application/json")
            .header("Content-Type", "application/x-www-form-urlencoded");

    static JSONObject parseBody(ResponseBody body) throws BodyException {
        if (body == null) throw new BodyException(NULL_BODY_MSG);
        try {
            return new JSONObject(body.string());
        } catch (IOException | JSONException e) {
            throw new BodyException(e.getMessage());
        }
    }

    @NotNull
    static Request.Builder getAuthReqBuilder(@NotNull User user) {
        if (user.token != null)
            return REQ_BUILDER.header("Authorization", user.token);
        return REQ_BUILDER;
    }

    static String getMessage(@NotNull JSONObject body) {
        try {
            return body.getString(MESSAGE_KEY);
        } catch (JSONException e) {
            return "";
        }
    }
}
