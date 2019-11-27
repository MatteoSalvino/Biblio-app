package com.example.biblio;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.example.biblio.fragments.LoggedProfileFragment;
import com.example.biblio.fragments.MyEbooksFragment;
import com.example.biblio.fragments.PopularFragment;
import com.example.biblio.fragments.ProfileFragment;
import com.example.biblio.fragments.RecentFragment;
import com.example.biblio.fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean isFirstStart = sharedPrefs.getBoolean("firstStart", true);

        if (isFirstStart) {
            //Launch introduction activity
            Intent i = new Intent(MainActivity.this, Introduction.class);

            runOnUiThread(() -> startActivity(i));

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SearchFragment(), "SearchFragment").commit();
        } else {
            if (savedInstanceState == null)
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SearchFragment(), "SearchFragment").commit();
        }
    }

    //todo: design a suitable menu to put more than 5 items!
    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;
                String TAG = "";
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                Boolean validCredentials = sp.getBoolean("validator", false);

                switch (item.getItemId()) {
                    case R.id.nav_search:
                        selectedFragment = new SearchFragment();
                        TAG = "SearchFragment";
                        break;
                    case R.id.nav_popular:
                        selectedFragment = new PopularFragment();
                        TAG = "PopularFragment";
                        break;
                    case R.id.nav_recent:
                        selectedFragment = new RecentFragment();
                        TAG = "RecentFragment";
                        break;
                    case R.id.nav_books:
                        selectedFragment = new MyEbooksFragment();
                        TAG = "MyEBooksFragment";
                        break;
                    case R.id.nav_profile:
                        //Show custom user's fragment
                        if (validCredentials) {
                            selectedFragment = new LoggedProfileFragment();
                            TAG = "LoggedProfileFragment";
                        } else {
                            selectedFragment = new ProfileFragment();
                            TAG = "ProfileFragment";
                        }
                        break;
                    /*
                    case R.id.nav_settings:
                        selectedFragment = new SettingsFragment();
                        TAG = "SettingsFragment";
                        break;
                    */
                }


                if (selectedFragment != null) {
                    Fragment previous_fragment = getSupportFragmentManager().findFragmentByTag(TAG);
                    if (previous_fragment != null && previous_fragment.isVisible()) {
                        return false;

                    } else {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment, TAG).commit();
                        return true;
                    }
                }
                return false;
            };

    @Override
    protected void onStart() {
        super.onStart();
        checkCredentials();
    }

    private void checkCredentials() {
        Thread threadA = new Thread() {
            public void run() {
                ThreadB threadB = new ThreadB(getApplicationContext());
                JSONObject jsonResponse;

                try {
                    jsonResponse = threadB.execute().get(10, TimeUnit.SECONDS);
                    if (jsonResponse != null && !jsonResponse.isNull("auth_token")) {
                        Log.d("checkCredentials", "Valid credentials");
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = sp.edit();

                        editor.putBoolean("validator", true);
                        editor.apply();
                    } else
                        Log.d("checkCredentials", "Credentials not valid");
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    e.printStackTrace();
                }
            }
        };
        threadA.start();
    }

    private class ThreadB extends AsyncTask<Void, Void, JSONObject> {
        private Context mContext;

        public ThreadB(Context context) {
            mContext = context;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            final RequestFuture<JSONObject> futureRequest = RequestFuture.newFuture();
            RequestQueue mQueue = VolleyRequestQueue.getInstance(mContext.getApplicationContext())
                    .getRequestQueue();
            String url = "http://10.0.3.2:3000/auth/login";

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            JSONObject response = null;

            try {
                String values = sharedPreferences.getString("credentials", null);
                response = (values == null) ? null : new JSONObject(values);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (response != null) {
                final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, response, futureRequest, futureRequest);
                mQueue.add(jsonRequest);

                try {
                    return futureRequest.get(10, TimeUnit.SECONDS);
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
    }
}

