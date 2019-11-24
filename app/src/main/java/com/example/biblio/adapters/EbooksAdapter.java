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


public class EbooksAdapter extends RecyclerView.Adapter<EbooksAdapter.EbooksViewHolder> {
    private final List<Ebook> mDataset;
    private final OnItemListener itemListener;
    private final RequestOptions option;
    private final Context mContext;

    public EbooksAdapter(List<Ebook> myDataset, OnItemListener listener, Context context) {
        this.mDataset = myDataset;
        this.itemListener = listener;
        this.mContext = context;
        option = new RequestOptions().centerCrop();
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
        Ebook elem = mDataset.get(position);
        holder.mBookTitle.setText(elem.getTitle());
        holder.mBookAuthor.setText(elem.getAuthor());
        int num_pages = elem.getPages();
        holder.mBookPages.setText(String.format("n° pages : %s", (num_pages == 0) ? "-" : String.valueOf(num_pages)));
        if (elem.getCover() != null)
            Glide.with(mContext).load(elem.getCover().toString()).placeholder(R.drawable.no_image).apply(option).into(holder.mBookCover);
        else
            Glide.with(mContext).load(R.drawable.no_image).apply(option).into(holder.mBookCover);

        holder.mBookSource.setText(mDataset.get(position).getSource());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    public interface OnItemListener {
        void onItemClick(int position);
    }

    public static class EbooksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView mBookTitle;
        final TextView mBookAuthor;
        final ImageView mBookCover;
        final TextView mBookPages;
        final TextView mBookSource;
        final OnItemListener itemListener;


        EbooksViewHolder(View v, OnItemListener listener) {
            super(v);
            mBookTitle = v.findViewById(R.id.book_title);
            mBookAuthor = v.findViewById(R.id.book_author);
            mBookCover = v.findViewById(R.id.book_cover);
            mBookPages = v.findViewById(R.id.book_pages);
            mBookSource = v.findViewById(R.id.book_source);
            this.itemListener = listener;

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            itemListener.onItemClick(getAdapterPosition());
        }
    }
}
