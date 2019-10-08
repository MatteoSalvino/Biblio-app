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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.biblio.R;
import com.google.android.material.button.MaterialButton;

import org.w3c.dom.Text;

import java.util.ArrayList;

import lrusso96.simplebiblio.core.Ebook;

public class MyBooksAdapter extends ArrayAdapter<Ebook> {
    private Context mContext;
    private ArrayList<Ebook> myBooks;



    public MyBooksAdapter(@NonNull Context context, ArrayList<Ebook> list) {
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

            Ebook current = myBooks.get(position);
            RequestOptions option = new RequestOptions().centerCrop();

            ImageView mBookCover = listItem.findViewById(R.id.mybook_cover);
            Glide.with(getContext()).load(myBooks.get(position).getCover().toString()).placeholder(R.drawable.no_image).apply(option).into(mBookCover);

            TextView mBookTitle = listItem.findViewById(R.id.mybook_title);
            mBookTitle.setText(current.getTitle());

            TextView mBookAuthor = listItem.findViewById(R.id.mybook_author);
            mBookAuthor.setText(current.getAuthor());

            TextView mBookPages = listItem.findViewById(R.id.mybook_pages);
            mBookPages.setText("n° pages : " + (current.getPages() == 0 ?  "-" :  String.valueOf(current.getPages())));

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
