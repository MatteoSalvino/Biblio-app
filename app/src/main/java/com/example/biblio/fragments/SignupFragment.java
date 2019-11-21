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

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.biblio.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignupFragment extends Fragment {
    private TextInputLayout mNameLayout;
    private TextInputLayout mEmailLayout;
    private TextInputLayout mPasswordLayout;
    private TextInputLayout mPasswordConfirmationLayout;
    private MaterialCheckBox mTermsCheckBox;
    private MaterialButton mSignupBtn;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.signup_fragment, container, false);

        mNameLayout = v.findViewById(R.id.signup_name_field);
        mEmailLayout = v.findViewById(R.id.signup_email_field);
        mPasswordLayout = v.findViewById(R.id.signup_password_field);
        mPasswordConfirmationLayout = v.findViewById(R.id.signup_password_confirmation_field);
        mTermsCheckBox = v.findViewById(R.id.signup_terms_cb);
        mSignupBtn = v.findViewById(R.id.signup_btn);

        mSignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = mNameLayout.getEditText().getText().toString();
                String email = mEmailLayout.getEditText().getText().toString();
                String password = mPasswordLayout.getEditText().getText().toString();
                String password_confirmation = mPasswordConfirmationLayout.getEditText().getText().toString();

                if(isValidForm(name, email, password, password_confirmation)){
                    progressDialog = ProgressDialog.show(getActivity(), "", "", true);
                    progressDialog.setContentView(R.layout.login_dialog_view);
                    signup(name, email, password, password_confirmation);
                } else {
                    Log.d("FormValidation", "The signup form is not valid.");
                }
            }
        });

        return v;
    }

    private boolean isValidForm(String name, String email, String password, String password_confirmation) {
        //To make more sophisticated
        return !name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !password_confirmation.isEmpty()
                && (password.compareTo(password_confirmation) == 0) && mTermsCheckBox.isChecked();
    }

    private void signup(String name, String email, String password, String password_confirmation) {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String URL = "http://10.0.3.2:3000/signup";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> postParams = new HashMap<String, String>();
                postParams.put("name", name);
                postParams.put("email", email);
                postParams.put("password", password);
                postParams.put("password_confirmation", password_confirmation);

                Log.d("getParams", postParams.toString());

                return postParams;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.add(stringRequest);
    }
}
