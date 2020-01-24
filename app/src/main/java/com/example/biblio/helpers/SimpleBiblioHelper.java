package com.example.biblio.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.example.biblio.api.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import lrusso96.simplebiblio.core.Ebook;

import static com.example.biblio.helpers.SharedPreferencesHelper.CURRENT_USER_KEY;
import static com.example.biblio.helpers.SharedPreferencesHelper.MY_EBOOKS_KEY;

public class SimpleBiblioHelper {
    private final Context context;
    private final SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    public SimpleBiblioHelper(Context context) {
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public User getCurrentUser() {
        return new Gson().fromJson(sharedPreferences.getString(CURRENT_USER_KEY, null), User.class);
    }

    public void setCurrentUser(User user) {
        editor.putString(CURRENT_USER_KEY, new Gson().toJson(user));
        editor.apply();
    }


    private ArrayList<Ebook> getMyEbooks() {
        String response = sharedPreferences.getString(MY_EBOOKS_KEY, "[]");
        return new Gson().fromJson(response, new TypeToken<ArrayList<Ebook>>() {
        }.getType());
    }

    public void removeEbook(Ebook ebook) {
        editor = sharedPreferences.edit();
        editor.putString(MY_EBOOKS_KEY, new Gson().toJson(getMyEbooks().remove(ebook)));
        editor.apply();
    }

    public void addEbookk(Ebook ebook) {
        //fixme: double call here!
        if (isFavorite(ebook)) return;
        editor.putString(MY_EBOOKS_KEY, new Gson().toJson(getMyEbooks().add(ebook)));
        editor.commit();
    }

    public boolean isFavorite(Ebook ebook) {
         return getMyEbooks().contains(ebook);
    }

}
