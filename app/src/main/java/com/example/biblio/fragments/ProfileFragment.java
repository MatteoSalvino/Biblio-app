package com.example.biblio.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.biblio.R;
import com.example.biblio.EmailActivity;
import com.google.android.material.button.MaterialButton;

public class ProfileFragment extends Fragment {
    private MaterialButton mEmailLoginBtn;
    private MaterialButton mSignupSuggestionBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_fragment, container, false);

        mEmailLoginBtn = v.findViewById(R.id.email_login_btn);
        mSignupSuggestionBtn = v.findViewById(R.id.signup_suggestion_btn);

        mEmailLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), EmailActivity.class);
                startActivityForResult(i, 0);
                //startActivity(i);
            }
        });

        mSignupSuggestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), EmailActivity.class);
                i.putExtra("page start", 1);
                startActivity(i);
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == 200)
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LoggedProfileFragment(), "LoggedProfileFragment").commit();
    }
}
