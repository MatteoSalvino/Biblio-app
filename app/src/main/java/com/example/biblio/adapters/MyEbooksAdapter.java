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
    private final List<Ebook> ebooks;
    private final OnItemListener itemListener;
    private final Context context;
    private final RequestOptions cropOptions = new RequestOptions().centerCrop();

    public MyEbooksAdapter(List<Ebook> ebooks, OnItemListener listener, Context context) {
        this.ebooks = ebooks;
        this.itemListener = listener;
        this.context = context;
    }

    @NotNull
    @Override
    public MyEbooksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_ebook, parent, false);

        return new MyEbooksViewHolder(v, itemListener);
    }

    @Override
    public void onBindViewHolder(MyEbooksViewHolder holder, int position) {
        Ebook elem = ebooks.get(position);
        holder.ebookTitle.setText(elem.getTitle());
        holder.ebookAuthor.setText(elem.getAuthor());
        int num_pages = elem.getPages();
        holder.ebookPages.setText(String.format("%s", (num_pages == 0) ? "-" : String.valueOf(num_pages)));
        if (elem.getCover() != null)
            Glide.with(context).load(elem.getCover().toString()).placeholder(R.drawable.no_image).apply(cropOptions).into(holder.ebookCover);
        else
            Glide.with(context).load(R.drawable.no_image).apply(cropOptions).into(holder.ebookCover);
    }

    @Override
    public int getItemCount() {
        return ebooks.size();
    }

    public interface OnItemListener {
        void onItemClick(int position);

        void onReadButtonClick(int position);
    }

    public static class MyEbooksViewHolder extends RecyclerView.ViewHolder {
        final TextView ebookTitle;
        final TextView ebookAuthor;
        final ImageView ebookCover;
        final TextView ebookPages;
        final MaterialButton readButton;
        final OnItemListener itemListener;

        MyEbooksViewHolder(View view, OnItemListener listener) {
            super(view);
            ebookTitle = view.findViewById(R.id.title);
            ebookAuthor = view.findViewById(R.id.author);
            ebookCover = view.findViewById(R.id.cover);
            ebookPages = view.findViewById(R.id.pages);
            readButton = view.findViewById(R.id.read_btn);
            this.itemListener = listener;
            readButton.setOnClickListener(x -> this.itemListener.onReadButtonClick(getAdapterPosition()));
            view.setOnClickListener(x -> itemListener.onItemClick((getAdapterPosition())));
        }
    }
}
