package com.example.biblio.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.example.biblio.api.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import lrusso96.simplebiblio.core.Ebook;

import static com.example.biblio.helpers.SharedPreferencesHelper.CURRENT_USER_KEY;
import static com.example.biblio.helpers.SharedPreferencesHelper.LAST_SEARCH_TS_KEY;
import static com.example.biblio.helpers.SharedPreferencesHelper.MY_EBOOKS_KEY;

/**
 * This class provides some utilities to store and retrieve data associated with the current user
 * within the app.
 */
public class SimpleBiblioHelper {

    public static User getCurrentUser(Context context) {
        return new Gson().fromJson(getSP(context).getString(CURRENT_USER_KEY, null), User.class);
    }

    public static void setCurrentUser(User user, Context context) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(CURRENT_USER_KEY, new Gson().toJson(user));
        editor.apply();
    }

    public static void removeCurrentUser(Context context) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.remove(CURRENT_USER_KEY);
        editor.apply();
    }

    public static ArrayList<Ebook> getMyEbooks(Context context) {
        String response = getSP(context).getString(MY_EBOOKS_KEY, "[]");
        return new Gson().fromJson(response, new TypeToken<ArrayList<Ebook>>() {
        }.getType());
    }

    public static void removeEbook(Ebook ebook, Context context) {
        SharedPreferences.Editor editor = getEditor(context);
        ArrayList<Ebook> ebooks = getMyEbooks(context);
        ebooks.remove(ebook);
        editor.putString(MY_EBOOKS_KEY, new Gson().toJson(ebooks));
        editor.apply();
    }

    public static void addEbook(Ebook ebook, Context context) {
        SharedPreferences sharedPreferences = getSP(context);
        ArrayList<Ebook> ebooks = getMyEbooks(context);
        if (ebooks.contains(ebook)) return;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        ebooks.add(ebook);
        editor.putString(MY_EBOOKS_KEY, new Gson().toJson(ebooks));
        editor.apply();
    }

    public static boolean isFavorite(Ebook ebook, Context context) {
        return getMyEbooks(context).contains(ebook);
    }

    public static Date getLastSearchTS(Context context) {
        return dateGson().fromJson(getSP(context).getString(LAST_SEARCH_TS_KEY, null), Date.class);
    }

    public static void setLastSearchTS(Context context) {
        String ts = dateGson().toJson(new Date());
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(LAST_SEARCH_TS_KEY, ts);
        editor.apply();
    }

    private static SharedPreferences getSP(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        return getSP(context).edit();
    }

    @NotNull
    private static Gson dateGson() {
        return new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
    }

}
