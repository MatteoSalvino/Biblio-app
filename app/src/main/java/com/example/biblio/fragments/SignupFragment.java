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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.biblio.R;
import com.example.biblio.databinding.SignupFragmentBinding;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignupFragment extends Fragment {
    private SignupFragmentBinding binding;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = SignupFragmentBinding.inflate(inflater, container, false);

        binding.signupBtn.setOnClickListener(view -> {
            String name = binding.signupNameField.getEditText().getText().toString();
            String email = binding.signupEmailField.getEditText().getText().toString();
            String password = binding.signupPasswordField.getEditText().getText().toString();
            String password_confirmation = binding.signupPasswordConfirmationField.getEditText().getText().toString();

            if (isValidForm(name, email, password, password_confirmation)) {
                progressDialog = ProgressDialog.show(getActivity(), "", "", true);
                progressDialog.setContentView(R.layout.login_dialog_view);
                signup(name, email, password, password_confirmation);
            } else {
                Log.d("FormValidation", "The signup form is not valid.");
            }
        });

        return binding.getRoot();
    }

    //todo: make more sophisticated
    private boolean isValidForm(String name, String email, String password, String password_confirmation) {
        return !name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !password_confirmation.isEmpty()
                && (password.compareTo(password_confirmation) == 0) && binding.signupTermsCb.isChecked();
    }

    private void signup(String name, String email, String password, String password_confirmation) {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String URL = "http://10.0.3.2:3000/signup";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, response -> {
            progressDialog.dismiss();
            Log.d("onResponse", response);

            JsonObject jsonObject = (JsonObject) JsonParser.parseString(response);
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
        }, error -> {
            progressDialog.dismiss();
            Log.d("onErrorResponse", "Something goes wrong !");
            error.printStackTrace();
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> postParams = new HashMap<>();
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
