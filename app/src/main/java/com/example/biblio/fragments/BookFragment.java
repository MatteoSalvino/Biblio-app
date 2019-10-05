package com.example.biblio.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.biblio.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class BookFragment extends Fragment {
    private TextView mBookTitle;
    private TextView mBookAuthor;
    private ImageView mBookCover;
    private TextView mBookYear;
    private TextView mBookCategories;
    private TextView mBookRating;
    private TextView mBookDescription;
    private ImageView mBackBtn;
    private MaterialButton mDownloadBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.book_fragment, container, false);

        mBookTitle = view.findViewById(R.id.main_book_title);
        mBookAuthor = view.findViewById(R.id.main_book_author);
        mBookCover = view.findViewById(R.id.main_book_cover);
        mBookYear = view.findViewById(R.id.main_book_year);
        mBookCategories = view.findViewById(R.id.main_book_categories);
        mBookRating = view.findViewById(R.id.main_book_rating);
        mBookDescription = view.findViewById(R.id.main_book_desc);
        mBackBtn = view.findViewById(R.id.main_back_btn);
        mDownloadBtn = view.findViewById(R.id.main_download_btn);

        //Retrieve book's informations
        String book_title = getArguments().getString("book_title");
        String book_author = getArguments().getString("book_author");
        String book_cover = getArguments().getString("book_cover");
        Integer book_year = getArguments().getInt("book_year");
        ArrayList<String> book_categories = getArguments().getStringArrayList("book_categories");
        Double book_rating = getArguments().getDouble("book_rating");
        String book_description = getArguments().getString("book_desc");

        //Show retrieved informations

        mBookTitle.setText(book_title);
        mBookAuthor.setText(book_author);
        mBookCover.setImageResource(R.drawable.no_image);
        //mBookCover.setImageURI(Uri.parse(book_cover));
        mBookYear.setText(String.valueOf(book_year));

        String build_str = "";
        int bc_size = book_categories.size();
        for(int i = 0; i < bc_size; i++) {
            build_str.concat(book_categories.get(i));

            if(i < bc_size - 1)
                build_str.concat(", ");
            else
                build_str.concat("");
        }

        mBookCategories.setText(build_str);
        mBookRating.setText(String.valueOf(book_rating));
        mBookDescription.setText(book_description);


        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStackImmediate();
            }
        });

        return view;
    }
}
