package com.example.biblio.fragments;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.biblio.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.commons.validator.routines.EmailValidator;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginFragment extends Fragment {
    private TextInputLayout mEmailLayout;
    private TextInputLayout mPasswordLayout;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.login_fragment, container, false);

        mEmailLayout = v.findViewById(R.id.email_field);
        mPasswordLayout = v.findViewById(R.id.password_field);
        MaterialButton mLoginBtn = v.findViewById(R.id.login_btn);

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmailLayout.getEditText().getText().toString().trim();
                String password = mPasswordLayout.getEditText().getText().toString().trim();

                if (isValid(email, "email") && isValid(password, "password")) {
                    progressDialog = ProgressDialog.show(getActivity(), "Login process", "Logging in. Please wait...", true);
                    progressDialog.setContentView(R.layout.login_dialog_view);
                    login(email, password);
                } else
                    Log.d("onClick", "Wrong credentials");
            }
        });

        return v;
    }

    private boolean isValid(String param, String type) {
        if (type.equals("email"))
            return EmailValidator.getInstance().isValid(param);
        else return type.equals("password");

    }

    private void login(String email, String password) {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String URL = "http://10.0.3.2:3000/auth/login";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, response -> {
            progressDialog.dismiss();
            Log.d("onResponse", response);

            JsonParser parser = new JsonParser();
            JsonObject jsonObject = (JsonObject) parser.parse(response);
            String auth_token = jsonObject.get("auth_token").getAsString();

            try {
                String credentials = new JSONObject()
                        .put("email", email)
                        .put("password", password)
                        .put("auth_token", auth_token).toString();

                Log.d("credentials", credentials);


                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("credentials", credentials);
                editor.apply();

                getActivity().setResult(200);
                getActivity().finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.d("onErrorResponse", "Something goes wrong !");
                error.printStackTrace();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> postParams = new HashMap<String, String>();
                postParams.put("email", email);
                postParams.put("password", password);

                Log.d("getParams", postParams.toString());

                return postParams;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }
        };


        requestQueue.add(stringRequest);
        Log.d("login", "finished");
    }
}
