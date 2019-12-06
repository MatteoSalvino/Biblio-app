package com.example.biblio.api;

import com.example.biblio.helpers.LogHelper;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import lrusso96.simplebiblio.core.Ebook;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;

import static com.example.biblio.api.SimpleBiblioHelper.CLIENT;
import static com.example.biblio.api.SimpleBiblioHelper.ENDPOINT;
import static com.example.biblio.api.SimpleBiblioHelper.getAuthReqBuilder;
import static com.example.biblio.api.SimpleBiblioHelper.getMessage;
import static com.example.biblio.api.SimpleBiblioHelper.getProviderId;
import static com.example.biblio.api.SimpleBiblioHelper.parseBody;

class EbooksHandler {
    private static final String EBOOK_PAR = "ebook";
    private static final String PROVIDER_PAR = "provider";
    private static final String RATING_PAR = "rating";

    private static final LogHelper logger = new LogHelper(EbooksHandler.class);

    @NotNull
    static RatingResult rate(User user, @NotNull Ebook ebook, int rating) throws UnhautorizedRequestException {
        RequestBody formBody = new FormBody.Builder()
                .add(EBOOK_PAR, Integer.toString(ebook.getId()))
                .add(PROVIDER_PAR, Integer.toString(getProviderId(ebook.getProviderName())))
                .add(RATING_PAR, Integer.toString(rating))
                .build();
        Request req = getAuthReqBuilder(user)
                .url(String.format("%s/ebooks/rate", ENDPOINT))
                .post(formBody)
                .build();
        try {
            JSONObject result = parseBody(CLIENT.newCall(req).execute().body());
            return parseRating(result);
        } catch (IOException | BodyException e) {
            //fixme: add generic exception here!
            throw new UnhautorizedRequestException(e.getMessage());
        }
    }

    @NotNull
    static RatingResult notifyDownload(User user, @NotNull Ebook ebook) throws UnhautorizedRequestException {
        RequestBody formBody = new FormBody.Builder()
                .add(EBOOK_PAR, Integer.toString(ebook.getId()))
                .add(PROVIDER_PAR, Integer.toString(getProviderId(ebook.getProviderName())))
                .build();
        Request req = getAuthReqBuilder(user)
                .url(String.format("%s/ebooks/download", ENDPOINT))
                .post(formBody)
                .build();
        try {
            JSONObject result = parseBody(CLIENT.newCall(req).execute().body());
            return extractDownloadRating(result);
        } catch (IOException | BodyException e) {
            //fixme: add generic exception here!
            throw new UnhautorizedRequestException(e.getMessage());
        }
    }

    @NotNull
    private static RatingResult extractDownloadRating(@NotNull JSONObject result) throws UnhautorizedRequestException {
        String tag = "ebook";
        try {
            JSONObject ebook = result.getJSONObject(tag);
            return parseRating(ebook);
        } catch (JSONException e) {
            throw new UnhautorizedRequestException(getMessage(result));
        }
    }

    //fixme: is it always unhautorized? (maybe check status code!)
    //fixme: why different keys?
    private static RatingResult parseRating(@NotNull JSONObject result) throws UnhautorizedRequestException {
        try {
            int ratings = result.getInt("ratings");
            double rating_avg = result.getDouble("rating_avg");
            return new RatingResult(ratings, rating_avg);
        } catch (JSONException e) {
            throw new UnhautorizedRequestException(e.getMessage());
        }
    }
}
