package com.example.biblio.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.biblio.R;
import com.google.android.material.button.MaterialButton;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import lrusso96.simplebiblio.core.Ebook;

public class MyEbooksAdapter extends RecyclerView.Adapter<MyEbooksAdapter.MyEbooksViewHolder> {
    private final List<Ebook> mDataset;
    private final OnItemListener itemListener;
    private final Context mContext;

    public MyEbooksAdapter(List<Ebook> myDataset, OnItemListener listener, Context context) {
        this.mDataset = myDataset;
        this.itemListener = listener;
        this.mContext = context;
    }

    @NotNull
    @Override
    public MyEbooksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_ebook_row, parent, false);

        return new MyEbooksViewHolder(v, itemListener);
    }

    @Override
    public void onBindViewHolder(MyEbooksViewHolder holder, int position) {
        Ebook elem = mDataset.get(position);
        holder.mEbookTitle.setText(elem.getTitle());
        holder.mEbookAuthor.setText(elem.getAuthor());
        int num_pages = elem.getPages();
        holder.mEbookPages.setText(String.format("n° pages : %s", (num_pages == 0) ? "-" : String.valueOf(num_pages)));
        RequestOptions centerCrop = new RequestOptions().centerCrop();
        if (elem.getCover() != null)
            Glide.with(mContext).load(elem.getCover().toString()).placeholder(R.drawable.no_image).apply(centerCrop).into(holder.mEbookCover);
        else
            Glide.with(mContext).load(R.drawable.no_image).apply(centerCrop).into(holder.mEbookCover);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    public interface OnItemListener {
        void onItemClick(int position);

        void onReadButtonClick(int position);
    }

    public static class MyEbooksViewHolder extends RecyclerView.ViewHolder {
        final TextView mEbookTitle;
        final TextView mEbookAuthor;
        final ImageView mEbookCover;
        final TextView mEbookPages;
        final MaterialButton mReadButton;
        final OnItemListener itemListener;

        MyEbooksViewHolder(View v, OnItemListener listener) {
            super(v);
            mEbookTitle = v.findViewById(R.id.my_ebook_title);
            mEbookAuthor = v.findViewById(R.id.my_ebook_author);
            mEbookCover = v.findViewById(R.id.my_ebook_cover);
            mEbookPages = v.findViewById(R.id.my_ebook_pages);
            mReadButton = v.findViewById(R.id.read_btn);
            this.itemListener = listener;
            mReadButton.setOnClickListener(x -> this.itemListener.onReadButtonClick(getAdapterPosition()));
            v.setOnClickListener(x -> itemListener.onItemClick((getAdapterPosition())));
        }
    }
}
