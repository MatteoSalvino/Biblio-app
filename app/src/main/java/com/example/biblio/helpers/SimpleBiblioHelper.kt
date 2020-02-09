package com.example.biblio.helpers

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.example.biblio.api.User
import com.example.biblio.helpers.SharedPreferencesHelper.CURRENT_USER_KEY
import com.example.biblio.helpers.SharedPreferencesHelper.LAST_SEARCH_TS_KEY
import com.example.biblio.helpers.SharedPreferencesHelper.MY_EBOOKS_KEY
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import lrusso96.simplebiblio.core.Ebook
import java.text.DateFormat
import java.util.*

/**
 * This class provides some utilities to store and retrieve data associated with the current user
 * within the app.
 */
object SimpleBiblioHelper {

    @JvmStatic
    fun getCurrentUser(context: Context) = Gson().fromJson(getSP(context).getString(CURRENT_USER_KEY, null), User::class.java)

    @JvmStatic
    fun setCurrentUser(user: User, context: Context) {
        getSP(context).edit { putString(CURRENT_USER_KEY, Gson().toJson(user)) }
    }

    @JvmStatic
    fun removeCurrentUser(context: Context) {
        getSP(context).edit { remove(CURRENT_USER_KEY) }
    }

    @JvmStatic
    fun getMyEbooks(context: Context): ArrayList<Ebook> {
        val response = getSP(context).getString(MY_EBOOKS_KEY, "[]")
        return Gson().fromJson(response, object : TypeToken<ArrayList<Ebook?>?>() {}.type)
    }

    @JvmStatic
    fun removeEbook(ebook: Ebook, context: Context) {
        val ebooks = getMyEbooks(context)
        ebooks.remove(ebook)
        getSP(context).edit { putString(MY_EBOOKS_KEY, Gson().toJson(ebooks)) }
    }

    @JvmStatic
    fun addEbook(ebook: Ebook, context: Context) {
        val ebooks = getMyEbooks(context)
        if (ebooks.contains(ebook)) return
        ebooks.add(ebook)
        getSP(context).edit { putString(MY_EBOOKS_KEY, Gson().toJson(ebooks)) }
    }

    @JvmStatic
    fun isFavorite(ebook: Ebook, context: Context): Boolean {
        return getMyEbooks(context).contains(ebook)
    }

    @JvmStatic
    fun getLastSearchTS(context: Context): Date? {
        return dateGson().fromJson(getSP(context).getString(LAST_SEARCH_TS_KEY, null), Date::class.java)
    }

    fun setLastSearchTS(context: Context) {
        getSP(context).edit { putString(LAST_SEARCH_TS_KEY, dateGson().toJson(Date())) }
    }

    private fun getSP(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    private fun dateGson(): Gson {
        return GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create()
    }
}