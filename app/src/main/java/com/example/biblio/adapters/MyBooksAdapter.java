package com.example.biblio.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.biblio.R;
import com.example.biblio.models.Book;
import com.google.android.material.button.MaterialButton;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MyBooksAdapter extends ArrayAdapter<Book> {
    private Context mContext;
    private ArrayList<Book> myBooks;



    public MyBooksAdapter(@NonNull Context context, ArrayList<Book> list) {
        super(context, R.layout.mybook_row, list);
        mContext = context;
        myBooks = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItem = convertView;

        if(listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.mybook_row, parent, false);

            Book current = myBooks.get(position);

            ImageView mBookCover = listItem.findViewById(R.id.mybook_cover);
            mBookCover.setImageResource(R.drawable.no_image);
            //mBookCover.setImageURI(Uri.parse(current.getCover_image()));

            TextView mBookTitle = listItem.findViewById(R.id.mybook_title);
            mBookTitle.setText(current.getTitle());

            TextView mBookAuthor = listItem.findViewById(R.id.mybook_author);
            mBookAuthor.setText(current.getAuthor());

            TextView mBookRating = listItem.findViewById(R.id.mybook_rating);
            mBookRating.setText(String.valueOf(current.getRating()));

            MaterialButton mReadBtn = listItem.findViewById(R.id.read_btn);
            mReadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(), "button clicked !", Toast.LENGTH_SHORT).show();
                }
            });
        }

        return listItem;
    }

}
