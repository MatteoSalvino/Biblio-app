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

import org.jetbrains.annotations.NotNull;

import java.util.List;

import lrusso96.simplebiblio.core.Ebook;

/**
 * RecyclerView adapter to manage and show ebooks.
 */
public class EbooksAdapter extends RecyclerView.Adapter<EbooksAdapter.EbooksViewHolder> {
    private final List<Ebook> ebooks;
    private final OnItemListener itemListener;
    private final RequestOptions cropOptions;
    private final Context context;

    public EbooksAdapter(List<Ebook> ebooks, OnItemListener listener, Context context) {
        this.ebooks = ebooks;
        this.itemListener = listener;
        this.context = context;
        cropOptions = new RequestOptions().centerCrop();
    }

    @NotNull
    @Override
    public EbooksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row, parent, false);

        return new EbooksViewHolder(v, itemListener);
    }

    @Override
    public void onBindViewHolder(EbooksViewHolder holder, int position) {
        Ebook elem = ebooks.get(position);
        holder.ebookTitle.setText(elem.getTitle());
        holder.ebookAuthor.setText(elem.getAuthor());
        int num_pages = elem.getPages();
        holder.ebookPages.setText(String.format("%s", (num_pages == 0) ? "-" : String.valueOf(num_pages)));
        if (elem.getCover() != null)
            Glide.with(context).load(elem.getCover().toString()).placeholder(R.drawable.no_image).apply(cropOptions).into(holder.ebookCover);
        else
            Glide.with(context).load(R.drawable.no_image).apply(cropOptions).into(holder.ebookCover);

        holder.ebookSource.setText(ebooks.get(position).getSource());
    }

    @Override
    public int getItemCount() {
        return ebooks.size();
    }

    public interface OnItemListener {
        void onItemClick(int position);
    }

    public static class EbooksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView ebookTitle;
        final TextView ebookAuthor;
        final ImageView ebookCover;
        final TextView ebookPages;
        final TextView ebookSource;
        final OnItemListener itemListener;

        EbooksViewHolder(View view, OnItemListener listener) {
            super(view);
            ebookTitle = view.findViewById(R.id.title);
            ebookAuthor = view.findViewById(R.id.author);
            ebookCover = view.findViewById(R.id.cover);
            ebookPages = view.findViewById(R.id.pages);
            ebookSource = view.findViewById(R.id.source);

            this.itemListener = listener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            itemListener.onItemClick(getAdapterPosition());
        }
    }
}
